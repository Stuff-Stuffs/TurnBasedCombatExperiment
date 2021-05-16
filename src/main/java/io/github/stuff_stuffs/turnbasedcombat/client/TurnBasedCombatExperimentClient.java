package io.github.stuff_stuffs.turnbasedcombat.client;

import com.mojang.brigadier.arguments.BoolArgumentType;
import io.github.stuff_stuffs.turnbasedcombat.client.command.DebugRendererArgument;
import io.github.stuff_stuffs.turnbasedcombat.client.network.Network;
import io.github.stuff_stuffs.turnbasedcombat.client.render.Render;
import io.github.stuff_stuffs.turnbasedcombat.client.render.debug.DebugRenderers;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleEntityComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.ClientBattleWorldComponent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;

@Environment(EnvType.CLIENT)
public class TurnBasedCombatExperimentClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Network.init();
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            final BattleEntityComponent battleEntity = Components.BATTLE_ENTITY_COMPONENT_KEY.getNullable(entity);
            if (battleEntity != null && battleEntity.isInBattle()) {
                final ClientBattleWorldComponent battleWorld = (ClientBattleWorldComponent) Components.BATTLE_WORLD_COMPONENT_KEY.get(world);
                battleWorld.demote(battleEntity.getBattleHandle());
            }
        });
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("debugRenderer").then(ClientCommandManager.argument("renderer", DebugRendererArgument.debugRendererArgument()).then(ClientCommandManager.argument("on", BoolArgumentType.bool()).executes(context -> {
            String renderer = context.getArgument("renderer", String.class);
            boolean on = BoolArgumentType.getBool(context, "on");
            DebugRenderers.set(renderer, on);
            return 0;
        }))));
        Render.init();
    }
}
