package me.chris.eruption.profile.listeners;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Flag;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.TrapDoor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.chris.eruption.arena.arena.Arena;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.events.types.oitc.OITCEvent;
import me.chris.eruption.events.types.oitc.OITCPlayer;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.kit.PlayerKit;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchState;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.util.other.Clickable;
import me.chris.eruption.util.other.Style;

public class PlayerListener implements Listener {

    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @EventHandler
    public void onPlayerInteractSoup(final PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        if (!player.isDead() && player.getItemInHand().getType() == Material.MUSHROOM_SOUP && player.getHealth() < 19.0) {
            final double newHealth = (player.getHealth() + 7.0 > 20.0) ? 20.0 : (player.getHealth() + 7.0);
            player.setHealth(newHealth);
            player.getItemInHand().setType(Material.BOWL);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0];
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (playerData.isRenaming()) {
            event.getPlayer().sendMessage(Style.RED + "A kit name cannot start with \"/\".");
            event.getPlayer().sendMessage(Style.RED + "Event cancelled.");
            playerData.setActive(false);
            playerData.setRename(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            if (!event.getItem().hasItemMeta()
                    || !event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
                return;
            }

            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());

            if (playerData.getPlayerState() == PlayerState.FIGHTING) {
                Player player = event.getPlayer();
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
            }
        }
    }

    @EventHandler
    public void onRegenerate(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
            return;
        }

        Player player = (Player) event.getEntity();

        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match.getKit().getFlag().equals(Flag.BUILD)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.plugin.getPlayerManager().createPlayerData(player);
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        player.performCommand("help");
        /*Bukkit.getServer().getOnlinePlayers().forEach(x -> {
            NameTagHandler.addToTeam(profile, x, ChatColor.BLUE, false);
            NameTagHandler.addToTeam(x, profile, ChatColor.BLUE, false);
        });*/
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (playerData == null) {
            return;
        }

        switch (playerData.getPlayerState()) {
            case FIGHTING:
                this.plugin.getMatchManager().removeFighter(player, playerData, false);
                break;
            case SPECTATING:

                if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                    this.plugin.getEventManager().removeSpectator(player, this.plugin.getEventManager().getEventPlaying(player));
                } else {
                    this.plugin.getMatchManager().removeSpectator(player);
                }

                break;
            case EDITING:
                this.plugin.getEditorManager().removeEditor(player.getUniqueId());
                break;
            case QUEUE:
                if (party == null) {
                    this.plugin.getQueueManager().removePlayerFromQueue(player);
                } else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                    this.plugin.getQueueManager().removePartyFromQueue(party);
                }
                break;
            case EVENT:
                PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
                if (practiceEvent != null) {
                    practiceEvent.leave(player);
                }
                break;
        }

        this.plugin.getTournamentManager().leaveTournament(player);
        this.plugin.getPartyManager().leaveParty(player);

        this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
        this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());
        this.plugin.getPlayerManager().removePlayerData(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (playerData == null) {
            return;
        }

        switch (playerData.getPlayerState()) {
            case FIGHTING:
                this.plugin.getMatchManager().removeFighter(player, playerData, false);
                break;
            case SPECTATING:
                if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                    this.plugin.getEventManager().removeSpectator(player, this.plugin.getEventManager().getEventPlaying(player));
                } else {
                    this.plugin.getMatchManager().removeSpectator(player);
                }
                break;
            case EDITING:
                this.plugin.getEditorManager().removeEditor(player.getUniqueId());
                break;
            case QUEUE:
                if (party == null) {
                    this.plugin.getQueueManager().removePlayerFromQueue(player);
                } else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                    this.plugin.getQueueManager().removePartyFromQueue(party);
                }
                break;
            case EVENT:
                PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
                if (practiceEvent != null) { // A redundant check, but just in case
                    practiceEvent.leave(player);
                }
                break;
        }

        this.plugin.getTournamentManager().leaveTournament(player);
        this.plugin.getPartyManager().leaveParty(player);

        this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
        this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());
        this.plugin.getPlayerManager().removePlayerData(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (playerData.getPlayerState() == PlayerState.SPECTATING) {
            event.setCancelled(true);
        }

        if (event.getAction().name().endsWith("_BLOCK")) {
            if (event.getClickedBlock().getType().name().contains("SIGN") && event.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                if (ChatColor.stripColor(sign.getLine(1)).equals("[Soup]")) {
                    event.setCancelled(true);

                    Inventory inventory = this.plugin.getServer().createInventory(null, 54,
                            ChatColor.DARK_GRAY + "Soup Refill");

                    for (int i = 0; i < 54; i++) {
                        inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                    }
                    event.getPlayer().openInventory(inventory);
                }
            }else if(event.getClickedBlock().getType().name().contains("TRAP") || event.getClickedBlock().getType().name().contains("FENCE") && (event.getClickedBlock().getState() instanceof TrapDoor)){
                event.setCancelled(true);
            }
            if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
            }
        }

        if (event.getAction().name().startsWith("RIGHT_")) {
            ItemStack item = event.getItem();
            Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

            switch (playerData.getPlayerState()) {
                case LOADING:
                    player.sendMessage(ChatColor.RED + "Please wait until your profile data is loaded.");
                    break;
                case FIGHTING:
                    if (item == null) {
                        return;
                    }
                    Match match = this.plugin.getMatchManager().getMatch(playerData);

                    if (item.getType() == Material.POTION && item.getDurability() == 16421) {
                        if (match.getMatchState() == MatchState.STARTING) {
                            event.setCancelled(true);
                            player.updateInventory();
                        }
                        return;
                    }

                    if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
                        if (event.getItem().equals(this.plugin.getHotbarManager().getDefaultBook())) {
                            event.setCancelled(true);

                            player.getInventory().setArmorContents(match.getKit().getArmor());
                            player.getInventory().setContents(match.getKit().getContents());
                            player.updateInventory();
                            player.sendMessage(Style.GRAY + "You equipped the default kit.");
                            return;
                        }
                    }

                    if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
                        final String displayName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());

                        for (PlayerKit kit : playerData.getKits(match.getKit())) {
                            if (kit != null) {
                                if (ChatColor.stripColor(kit.getName()).equals(displayName)) {
                                    event.setCancelled(true);

                                    player.getInventory().setArmorContents(match.getKit().getArmor());
                                    player.getInventory().setContents(kit.getContents());
                                    player.updateInventory();
                                    player.sendMessage(Style.GRAY + "You equipped your custom kit.");
                                    return;
                                }
                            }
                        }
                    }

                    if (item.getType() == Material.ENDER_PEARL) {
                        if (match.getMatchState() == MatchState.STARTING) {
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.RED + "You can't throw enderpearls while the match is starting");
                            player.updateInventory();
                        }
                    }
                    break;
                case SPAWN:
                    if (item == null) {
                        return;
                    }

                    switch (item.getType()) {
                        case FENCE_GATE:
                        case TRAP_DOOR:
                            event.setCancelled(true);
                            break;
                        case EMERALD:
                            if(item.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "View Leaderboards" + ChatColor.GRAY + " (Right-Click)")){
                                player.performCommand("leaderboards");
                            }
                            //EruptionPlugin.getInstance().getLeaderboardsManager().openInventory(profile);
                            break;

                        case DIAMOND_SWORD:
                            if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                player.sendMessage(ChatColor.RED + "You are not the leader of this party.");
                                return;
                            }

                            player.openInventory(this.plugin.getInventoryManager().getRankedInventory().getCurrentPage());
                            break;
                        case IRON_SWORD:
                            if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                player.sendMessage(ChatColor.RED + "You are not the leader of this party.");
                                return;
                            }

                            player.openInventory(this.plugin.getInventoryManager().getUnrankedInventory().getCurrentPage());
                            break;
                        case NAME_TAG:
                            this.plugin.getPartyManager().createParty(player);
                            break;
                        case BOOK:
                            //new SelectLadderKitMenu().openMenu(player);
                            break;
                        case PAPER:
                            player.performCommand("party info");
                            break;
                        case WATCH:
                            player.performCommand("settings");
                            break;
                        case GOLD_AXE:
                            if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                player.sendMessage(ChatColor.RED + "You are not the leader of this party.");
                                return;
                            }
                            player.openInventory(this.plugin.getInventoryManager().getPartyEventInventory().getCurrentPage());
                            break;
                        case SKULL_ITEM:
                            if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                player.sendMessage(ChatColor.RED + "You are not the leader of this party.");
                                return;
                            }
                            player.openInventory(this.plugin.getInventoryManager().getPartyInventory().getCurrentPage());
                            break;
                        case NETHER_STAR:
                            this.plugin.getPartyManager().leaveParty(player);
                            this.plugin.getTournamentManager().leaveTournament(player);
                            break;
                    }
                    break;
                case QUEUE:
                    if (item == null) {
                        return;
                    }
                    if (item.getType() == Material.INK_SACK && item.getDurability() == (short) 1) {
                        if (party == null) {
                            this.plugin.getQueueManager().removePlayerFromQueue(player);
                        } else {
                            this.plugin.getQueueManager().removePartyFromQueue(party);
                        }
                    }
                    break;
                case EVENT:
                    if (item == null) {
                        return;
                    }
                    if (item.getType() == Material.NETHER_STAR) {
                        PracticeEvent eventPlaying = this.plugin.getEventManager().getEventPlaying(player);

                        if (eventPlaying != null) {
                            eventPlaying.leave(player);
                        }
                    }
                    break;
                case SPECTATING:
                    if (item == null) {
                        return;
                    }
                    if (item.getType() == Material.NETHER_STAR) {

                        if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                            this.plugin.getEventManager().removeSpectator(player, this.plugin.getEventManager().getEventPlaying(player));
                        } else if (party == null) {
                            this.plugin.getMatchManager().removeSpectator(player);
                        } else {
                            this.plugin.getPartyManager().leaveParty(player);
                        }
                    }
                    break;
                case EDITING:
                    if (event.getClickedBlock() == null) {
                        return;
                    }
                    switch (event.getClickedBlock().getType()) {
                        case WALL_SIGN:
                        case SIGN:
                        case SIGN_POST:
                            this.plugin.getEditorManager().removeEditor(player.getUniqueId());
                            this.plugin.getPlayerManager().sendToSpawnAndReset(player);
                            break;
                        case CHEST:
                            Kit kit = this.plugin.getKitManager()
                                    .getKit(this.plugin.getEditorManager().getEditingKit(player.getUniqueId()));
                            //Check if the edit kit contents are empty before opening the menus.
                            if (kit.getKitEditContents()[0] != null) {
                                Inventory editorInventory = this.plugin.getServer().createInventory(null, 36);

                                editorInventory.setContents(kit.getKitEditContents());
                                player.openInventory(editorInventory);
                                event.setCancelled(true);
                            }
                            break;
                        case ANVIL:
                            player.openInventory(
                                    this.plugin.getInventoryManager().getEditingKitInventory(player.getUniqueId()).getCurrentPage());
                            event.setCancelled(true);
                            break;
                    }
                    break;
            }
        }
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getEntity() instanceof Player && event.getDamager() instanceof EnderPearl) {
            Player player = (Player) event.getEntity();
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            Match match = EruptionPlugin.getInstance().getMatchManager().getMatch(playerData);

            if (match != null) {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        Material drop = event.getItemDrop().getItemStack().getType();

        switch (playerData.getPlayerState()) {
            case FIGHTING:
                if (drop == Material.ENCHANTED_BOOK) {
                    event.setCancelled(true);
                } else if (drop == Material.GLASS_BOTTLE) {
                    event.getItemDrop().remove();
                } else if (drop == Material.DIAMOND_SWORD || drop == Material.IRON_AXE || drop == Material.DIAMOND_SPADE || drop == Material.BOW) {
                    player.sendMessage(ChatColor.RED + "You can't drop your weapon if it is in slot 1.");
                    event.setCancelled(true);
                } else {
                    Match match = this.plugin.getMatchManager().getMatch(event.getPlayer().getUniqueId());

                    this.plugin.getMatchManager().addDroppedItem(match, event.getItemDrop());
                }
                break;
            default:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        Material drop = event.getItem().getType();

        switch (playerData.getPlayerState()) {
            case FIGHTING:


                if (drop.getId() == 373) {
                    this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, new Runnable() {
                        public void run() {
                            //profile.setItemInHand(new ItemStack(Material.AIR));
                            //profile.updateInventory();
                        }
                    }, 1L);
                }
                break;
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

            if (match.getEntitiesToRemove().contains(event.getItem())) {
                match.removeEntityToRemove(event.getItem());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        String chatMessage = event.getMessage();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (party != null) {
            if (chatMessage.startsWith("!") || chatMessage.startsWith("@")) {
                event.setCancelled(true);

                String message = ChatColor.RED.toString() + ChatColor.BOLD+ "PARTY " + ChatColor.DARK_GRAY + "» " + ChatColor.WHITE + player.getName() + ": " + ChatColor.RED + chatMessage.replaceFirst("!", "").replaceFirst("@", "");

                party.broadcast(message);
            }
        }

        if (playerData.isRenaming()) {
            event.setCancelled(true);

            if (event.getMessage().length() > 16) {
                event.getPlayer().sendMessage(Style.RED + "A kit name cannot be more than 16 characters long.");
                return;
            }

            if (!playerData.isInMatch()) {
                //new KitManagementMenu(playerData.getSelectedLadder()).openMenu(event.getPlayer());
            }

            //playerData.getSelectedKit().setName(event.getMessage());
            playerData.setActive(false);
            playerData.setRename(false);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        switch (playerData.getPlayerState()) {

            case EVENT:
                PracticeEvent currentEvent = this.plugin.getEventManager().getEventPlaying(player);

                if (currentEvent != null) {

                    if (currentEvent instanceof OITCEvent) {
                        event.setRespawnLocation(player.getLocation());
                        currentEvent.onDeath().accept(player);
                    }
                }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        Player player=event.getEntity();
        PlayerData playerData=this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        //player.getWorld().strikeLightningEffect(player.getLocation());

        switch (playerData.getPlayerState()) {
            case FIGHTING:
                this.plugin.getMatchManager().removeFighter(player, playerData, true);
                //player.getWorld().strikeLightningEffect(player.getLocation());
                break;
            case EVENT:
                PracticeEvent currentEvent=this.plugin.getEventManager().getEventPlaying(player);

                if (currentEvent != null) {

                    if (currentEvent instanceof OITCEvent) {
                        OITCEvent oitcEvent=(OITCEvent) currentEvent;
                        OITCPlayer oitcKiller=oitcEvent.getPlayer(player.getKiller());
                        OITCPlayer oitcPlayer=oitcEvent.getPlayer(player);
                        oitcPlayer.setLastKiller(oitcKiller);
                        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> player.spigot().respawn(), 1L);
                        break;
                    }
                }
        }
        event.getDrops().clear();
        event.setDeathMessage(null);
        event.setDroppedExp(0);
    }




    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

            if (match.getKit().getFlag().equals(Flag.SUMO) || this.plugin.getEventManager().getEventPlaying(player) != null) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) event.getEntity().getShooter();
            PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());

            if (shooterData.getPlayerState() == PlayerState.FIGHTING) {
                Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());

                match.addEntityToRemove(event.getEntity());
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) event.getEntity().getShooter();
            PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());

            if (shooterData != null) {
                if (shooterData.getPlayerState() == PlayerState.FIGHTING) {
                    Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());

                    match.removeEntityToRemove(event.getEntity());

                    if (event.getEntityType() == EntityType.ARROW) {
                        event.getEntity().remove();
                    }
                }
            }
        }
    }

    private void sendRematch(Player player, Player selected, Kit kit, Arena arena) {
        this.plugin.getMatchManager().createMatchRequest(player, selected, arena, kit.getName(), false);

        player.closeInventory();

        String requestGetString = ChatColor.RED + player.getName() + ChatColor.WHITE + " has requested a duel with the kit " + ChatColor.RED + kit.getName() + ChatColor.WHITE + " on the arena " + ChatColor.RED + arena.getName() + ChatColor.WHITE+". " + ChatColor.GRAY + "[Click to Accept]";
        String requestSendString = ChatColor.WHITE + "Sent a duel request to " + ChatColor.RED + selected.getName() + ChatColor.WHITE + " with the kit " + ChatColor.RED + kit.getName() + ChatColor.WHITE + " on the arena " + ChatColor.RED + arena.getName() + ChatColor.WHITE + ".";

        Clickable requestMessage = new Clickable(
                requestGetString,
                ChatColor.GRAY + "Click to accept duel",
                "/accept " + player.getName() + " " + kit.getName());

        requestMessage.sendToPlayer(selected);
        player.sendMessage(requestSendString);
    }
}
