package io.github.stuff_stuffs.turnbasedcombat.common;

import com.mojang.brigadier.CommandDispatcher;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleActions;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooserTypeRegistry;
import io.github.stuff_stuffs.turnbasedcombat.common.command.BattleStartCommand;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.EntityTypes;
import io.github.stuff_stuffs.turnbasedcombat.common.network.Network;
import io.github.stuff_stuffs.turnbasedcombat.common.persistant.BattlePersistentState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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
            public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
                BattleStartCommand.register(dispatcher);
            }
        });
    }

    public static int getMaxTurnTime() {
        return 30;
    }

    public static int getMaxTurnCount() {
        return 100;
    }
}
