package me.chris.eruption.command.parameter;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.vaperion.blade.argument.Argument;
import me.vaperion.blade.argument.ArgumentProvider;
import me.vaperion.blade.context.Context;
import me.vaperion.blade.exception.BladeExitMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class KitParameter implements ArgumentProvider<Kit> {

    @Override
    public @Nullable Kit provide(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
        Kit kit = EruptionPlugin.getInstance().getKitManager().getKit(argument.getString());

        if (kit == null) {
            throw new BladeExitMessage("A kit with the name " + argument.getString() + " does not exists.");
        }

        return kit;
    }

    @Override
    public @NotNull List<String> suggest(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
        return EruptionPlugin.getInstance().getKitManager().getKits()
                .stream()
                .map(Kit::getName)
                .collect(Collectors.toList());
    }
}
