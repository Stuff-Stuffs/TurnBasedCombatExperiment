package io.github.stuff_stuffs.turnbasedcombat.common;

import com.mojang.brigadier.CommandDispatcher;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleActions;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage.*;
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
        BattleDamageSource source = new BattleDamageSource(null, DamageComposition.builder().set(DamageType.PHYSICAL, 1).set(DamageType.MAGIC, 2).build(), null);
        DamagePacket damagePacket = new DamagePacket(source, 100);
        DamageResistances screen = DamageResistances.builder().set(DamageType.PHYSICAL, 0.5).set(DamageType.MAGIC, 1).build();
        final DamagePacket packet = damagePacket.screen(screen);
        System.out.println("" + packet);
    }

    public static int getMaxTurnTime() {
        return 5;
    }

    public static int getMaxTurnCount() {
        return 100;
    }
}
