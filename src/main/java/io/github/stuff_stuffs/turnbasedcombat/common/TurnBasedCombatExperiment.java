package io.github.stuff_stuffs.turnbasedcombat.common;

import com.mojang.brigadier.CommandDispatcher;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleActions;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.LeaveBattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage.DamageType;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat.EntityStatType;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooserTypeRegistry;
import io.github.stuff_stuffs.turnbasedcombat.common.command.BattleStartCommand;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.EntityTypes;
import io.github.stuff_stuffs.turnbasedcombat.common.network.Network;
import io.github.stuff_stuffs.turnbasedcombat.common.persistant.BattlePersistentState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public class TurnBasedCombatExperiment implements ModInitializer {
    public static final String MOD_ID = "turn_based_combat";

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        EntityTypes.init();
        TurnChooserTypeRegistry.init();
        Network.init();
        BattleActions.init();
        ServerTickEvents.END_WORLD_TICK.register(world -> BattlePersistentState.get(world.getPersistentStateManager()).getData().tick());
        CommandRegistrationCallback.EVENT.register(new CommandRegistrationCallback() {
            @Override
            public void register(final CommandDispatcher<ServerCommandSource> dispatcher, final boolean dedicated) {
                BattleStartCommand.register(dispatcher);
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            Battle battle = BattlePersistentState.get(handler.player.getServerWorld().getPersistentStateManager()).getData().getBattle((BattleEntity) handler.player);
            if(battle!=null) {
                battle.push(new LeaveBattleAction(BattleParticipantHandle.UNIVERSAL.apply(battle.getBattleId()), new BattleParticipantHandle(battle.getBattleId(), handler.player.getUuid())));
            }
        });
        DamageType.init();
        EntityStatType.init();
    }

    public static int getMaxTurnTime() {
        return 5;
    }

    public static int getMaxTurnCount() {
        return 100;
    }
}
