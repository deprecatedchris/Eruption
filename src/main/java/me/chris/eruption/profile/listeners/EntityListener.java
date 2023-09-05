package me.chris.eruption.profile.listeners;

import me.chris.eruption.events.types.sumo.SumoEvent;
import me.chris.eruption.kit.Flag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.events.types.oitc.OITCEvent;
import me.chris.eruption.events.types.oitc.OITCPlayer;
import me.chris.eruption.events.types.sumo.SumoPlayer;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchState;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;

public class EntityListener implements Listener {
    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

            switch (playerData.getPlayerState()) {
                case FIGHTING:
                    Match match = this.plugin.getMatchManager().getMatch(playerData);
                    if (match.getMatchState() != MatchState.FIGHTING) {
                        if (match.getKit().getName().equals("Boxing")) {
                            if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                                e.setCancelled(true);
                                return;
                            }

                            if (!e.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM)) {
                                e.setDamage(0.0D);
                            }
                            return;
                        }
                    }
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        this.plugin.getMatchManager().removeFighter(player, playerData, true);
                    }
                    break;
                case EVENT:
                    PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);

                    if (event != null) {

                        if (event instanceof SumoEvent) {
                            SumoEvent sumoEvent = (SumoEvent) event;
                            SumoPlayer sumoPlayer = sumoEvent.getPlayer(player);

                            if (sumoPlayer != null && sumoPlayer.getState() == SumoPlayer.SumoState.FIGHTING) {
                                e.setCancelled(false);
                            }
                        } else if (event instanceof OITCEvent) {
                            OITCEvent oitcEvent = (OITCEvent) event;
                            OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);

                            if (oitcPlayer != null && oitcPlayer.getState() == OITCPlayer.OITCState.FIGHTING) {
                                e.setCancelled(false);
                            } else {
                                e.setCancelled(true);
                            }
                        }
                    }
                    break;
                default:
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        e.getEntity().teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
                    }
                    e.setCancelled(true);
                    break;
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

        if (!(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
            return;
        }

        Player entity = (Player) e.getEntity();

        Player damager;

        if (e.getDamager() instanceof Player) {
            damager = (Player) e.getDamager();
        } else if (e.getDamager() instanceof Projectile) {
            damager = (Player) ((Projectile) e.getDamager()).getShooter();
        } else {
            return;
        }

        PlayerData entityData = this.plugin.getPlayerManager().getPlayerData(entity.getUniqueId());
        PlayerData damagerData = this.plugin.getPlayerManager().getPlayerData(damager.getUniqueId());

        boolean isEventEntity = this.plugin.getEventManager().getEventPlaying(entity) != null;
        boolean isEventDamager = this.plugin.getEventManager().getEventPlaying(damager) != null;

        PracticeEvent eventDamager = this.plugin.getEventManager().getEventPlaying(damager);
        PracticeEvent eventEntity = this.plugin.getEventManager().getEventPlaying(entity);

        if (!entity.canSee(damager) && damager.canSee(entity)) {
            e.setCancelled(true);
            return;
        }

        if (isEventDamager && eventDamager instanceof SumoEvent && ((SumoEvent) eventDamager).getPlayer(damager).getState() != SumoPlayer.SumoState.FIGHTING || isEventEntity && eventDamager instanceof SumoEvent && ((SumoEvent) eventEntity).getPlayer(entity).getState() != SumoPlayer.SumoState.FIGHTING || !isEventDamager && damagerData.getPlayerState() != PlayerState.FIGHTING || !isEventEntity && entityData.getPlayerState() != PlayerState.FIGHTING) {
            e.setCancelled(true);
            return;
        }

        if (isEventDamager && eventDamager instanceof OITCEvent || isEventEntity && eventEntity instanceof OITCEvent || !isEventDamager && damagerData.getPlayerState() != PlayerState.FIGHTING || !isEventEntity && entityData.getPlayerState() != PlayerState.FIGHTING) {

            if (isEventEntity && isEventDamager && eventEntity instanceof OITCEvent && eventDamager instanceof OITCEvent) {

                OITCEvent oitcEvent = (OITCEvent) eventDamager;
                OITCPlayer oitcKiller = oitcEvent.getPlayer(damager);
                OITCPlayer oitcPlayer = oitcEvent.getPlayer(entity);

                if (oitcKiller.getState() != OITCPlayer.OITCState.FIGHTING || oitcPlayer.getState() != OITCPlayer.OITCState.FIGHTING) {
                    e.setCancelled(true);
                    return;
                }

                if (e.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) e.getDamager();

                    if (arrow.getShooter() instanceof Player) {

                        if (damager != entity) {
                            oitcPlayer.setLastKiller(oitcKiller);
                            e.setDamage(0.0D);
                            eventEntity.onDeath().accept(entity);
                        }
                    }
                }
            }


            return;
        }

        if (entityData == null || damagerData == null) {
            e.setCancelled(true);
            return;
        }

        if (entityData.getPlayerState() == PlayerState.EVENT && eventEntity instanceof SumoEvent || damagerData.getPlayerState() == PlayerState.EVENT && eventDamager instanceof SumoEvent) {
            e.setDamage(0.0D);
            return;
        }

        Match match = this.plugin.getMatchManager().getMatch(entityData);

        if (match == null) {
            e.setDamage(0.0D);
            return;
        }

        if (damagerData.getTeamID() == entityData.getTeamID() && !match.isFFA()) {
            e.setCancelled(true);
            return;
        }

        if (match.getKit().getFlag().equals(Flag.PARKOUR)) {
            e.setCancelled(true);
            return;
        }

        if (match.getKit().getName().equals("Sumo")) {
            e.setDamage(0.0D);
            return;
        }

        if (match.getKit().getFlag().equals(Flag.SPLEEF) || match.getKit().getFlag().equals(Flag.SUMO)) {
            e.setDamage(0.0D);
        }

        if (e.getDamager() instanceof Player) {
            if (!match.getMatchState().equals(MatchState.FIGHTING)) {
                return;
            }

            damagerData.setCombo(damagerData.getCombo() + 1);
            damagerData.setHits(damagerData.getHits() + 1);

            if (damagerData.getCombo() > damagerData.getLongestCombo()) {
                damagerData.setLongestCombo(damagerData.getCombo());
            }

            entityData.setCombo(0);

            if (match.getKit().getName().contains("Boxing")) {
                if (damagerData.getHits() >= 100) {
                    this.plugin.getMatchManager().removeFighter(entity, entityData, false);
                }

                e.setDamage(0.0D);
            } else if (match.getKit().getFlag().equals(Flag.STICK_FIGHT)) {
                e.setDamage(0.0D);
            } else if (match.getKit().getFlag().equals(Flag.SPLEEF)) {
                e.setCancelled(true);
            }
        } else if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();

            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();

                if (!entity.getName().equals(shooter.getName())) {
                    double health = Math.ceil(entity.getHealth() - e.getFinalDamage()) / 2.0D;

                    if (health > 0.0D) {
                        shooter.sendMessage(entity.getDisplayName() + ChatColor.WHITE + " has been shot " + ChatColor.GRAY + " (" + ChatColor.RED + health + "❤" + ChatColor.GRAY + ")");
                    }
                }
            }
        }

    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }
        for (PotionEffect effect : e.getEntity().getEffects()) {
            if (effect.getType().equals(PotionEffectType.HEAL)) {
                Player shooter = (Player) e.getEntity().getShooter();
                PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
                shooterData.setThrownPots(shooterData.getThrownPots() + 1);

                if (e.getIntensity(shooter) <= 0.5D) {
                    shooterData.setMissedPots(shooterData.getMissedPots() + 1);
                }
                break;
            }
        }
    }
}
