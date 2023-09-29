package me.chris.eruption.command.parameter;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.arena.arena.Arena;
import me.vaperion.blade.argument.Argument;
import me.vaperion.blade.argument.ArgumentProvider;
import me.vaperion.blade.context.Context;
import me.vaperion.blade.exception.BladeExitMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class ArenaParameter implements ArgumentProvider<Arena> {
    @Override
    public @Nullable Arena provide(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
        final Arena arena = EruptionPlugin.getInstance().getArenaManager().getArena(argument.getString());

        if (arena == null) {
            throw new BladeExitMessage("An arena with the name " + argument.getString() + " exist.");
        }

        return arena;
    }

    @Override
    public @NotNull List<String> suggest(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
        return EruptionPlugin.getInstance().getArenaManager().getArenas().values()
                .stream()
                .map(Arena::getName)
                .collect(Collectors.toList());
    }
}
