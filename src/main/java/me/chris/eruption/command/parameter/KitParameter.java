package me.chris.eruption.command.parameter;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.vaperion.blade.argument.Argument;
import me.vaperion.blade.argument.ArgumentProvider;
import me.vaperion.blade.context.Context;
import me.vaperion.blade.exception.BladeExitMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KitParameter implements ArgumentProvider<Kit> {
    @Override
    public @Nullable Kit provide(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
        Kit kit = EruptionPlugin.getInstance().getKitManager().getKit(argument.getString());

        if(kit == null){
            throw new BladeExitMessage("A kit with the name " + argument.getString() + " does not exists.");
        }

        return kit;

    }

    @Override
    public @NotNull List<String> suggest(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
       final List<String> kits = new ArrayList<>();
        for(Kit kit : EruptionPlugin.getInstance().getKitManager().getKits()){
            kits.add(kit.getName());
        }

        return kits;
    }
}
