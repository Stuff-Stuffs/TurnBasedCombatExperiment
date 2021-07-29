package io.github.stuff_stuffs.turnbasedcombat.common;

import com.mojang.brigadier.CommandDispatcher;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleActionRegistry;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.ParticipantJoinBattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.damage.BattleDamageType;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.world.BattleBounds;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.world.ServerBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.EntityTypes;
import io.github.stuff_stuffs.turnbasedcombat.common.network.Network;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.BattleWorldSupplier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class TurnBasedCombatExperiment implements ModInitializer {
    public static final String MOD_ID = "turn_based_combat";

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        EntityTypes.init();
        Network.init();
        BattleDamageType.init();
        BattleActionRegistry.init();
        BattleParticipantStat.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> register(dispatcher));
    }

    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("battleCreate").then(CommandManager.argument("entities", EntityArgumentType.entities()).executes(context -> {
            final Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
            final ServerWorld world = context.getSource().getWorld();
            final int x = (int) context.getSource().getPosition().x;
            final int y = (int) context.getSource().getPosition().y;
            final int z = (int) context.getSource().getPosition().z;
            final BattleBounds bounds = new BattleBounds(x - 10, y - 4, z - 10, x + 10, y + 4, z + 10);
            final BattleHandle handle = ((ServerBattleWorld) ((BattleWorldSupplier) world).tbcex_getBattleWorld()).createBattle(bounds);
            final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle);
            if (battle == null) {
                throw new RuntimeException();
            }
            for (final Entity entity : entities) {
                if (entity instanceof BattleEntity battleEntity) {
                    final BattleAction<?> action = new ParticipantJoinBattleAction(BattleParticipantHandle.UNIVERSAL.apply(handle), new BattleParticipantState(new BattleParticipantHandle(handle, entity.getUuid()), battleEntity));
                    battle.push(action);
                } else {
                    context.getSource().sendError(new LiteralText("Entity: " + entity.getUuidAsString() + " is not instanceof BattleEntity, excluding it from battle"));
                }
            }
            return 0;
        })));
    }
}
