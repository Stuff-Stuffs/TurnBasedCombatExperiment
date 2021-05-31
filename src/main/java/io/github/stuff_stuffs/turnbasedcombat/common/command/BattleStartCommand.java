package io.github.stuff_stuffs.turnbasedcombat.common.command;

import com.mojang.brigadier.CommandDispatcher;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.data.ServerBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.persistant.BattlePersistentState;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;

import java.util.Collection;

public final class BattleStartCommand {
    private BattleStartCommand() {
    }

    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("battleCreate").then(CommandManager.argument("entities", EntityArgumentType.entities()).executes(context -> {
            final Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
            final ServerWorld world = context.getSource().getWorld();
            final ServerBattleWorld battleWorld = BattlePersistentState.get(world.getPersistentStateManager()).getData();
            final BattleHandle handle = battleWorld.create();
            for (final Entity entity : entities) {
                if (entity instanceof BattleEntity battleEntity) {
                    battleWorld.join(battleEntity, handle);
                } else {
                    context.getSource().sendError(new LiteralText("Entity: " + entity.getUuidAsString() + " is not instanceof BattleEntity, excluding it from battle"));
                }
            }
            return 0;
        })));
    }
}
