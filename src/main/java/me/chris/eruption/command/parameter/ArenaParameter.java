package me.chris.eruption.command.parameter;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.arena.arena.Arena;
import me.vaperion.blade.argument.Argument;
import me.vaperion.blade.argument.ArgumentProvider;
import me.vaperion.blade.context.Context;
import me.vaperion.blade.exception.BladeExitMessage;
import me.vaperion.blade.exception.BladeUsageMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ArenaParameter implements ArgumentProvider<Arena> {
    @Override
    public @Nullable Arena provide(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
        final Arena arena = EruptionPlugin.getInstance().getArenaManager().getArena(argument.getString());

        if(arena == null){
            throw new BladeExitMessage("An arena with the name " + argument.getString() + " exist.");
        }

        return arena;
    }

    @Override
    public @NotNull List<String> suggest(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
        final List<String> arenas = new ArrayList<>();

        for(Arena arena : EruptionPlugin.getInstance().getArenaManager().getArenas().values()){
            arenas.add(arena.getName());
        }

        return arenas;
    }
}
