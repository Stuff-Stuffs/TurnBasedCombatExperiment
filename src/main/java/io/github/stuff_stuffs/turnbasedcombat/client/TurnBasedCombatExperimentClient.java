package io.github.stuff_stuffs.turnbasedcombat.client;

import com.mojang.brigadier.arguments.BoolArgumentType;
import io.github.stuff_stuffs.turnbasedcombat.client.command.DebugRendererArgument;
import io.github.stuff_stuffs.turnbasedcombat.client.network.Network;
import io.github.stuff_stuffs.turnbasedcombat.client.render.Render;
import io.github.stuff_stuffs.turnbasedcombat.client.render.debug.DebugRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

@Environment(EnvType.CLIENT)
public class TurnBasedCombatExperimentClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Network.init();
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("debugRenderer").then(ClientCommandManager.argument("renderer", DebugRendererArgument.debugRendererArgument()).then(ClientCommandManager.argument("on", BoolArgumentType.bool()).executes(context -> {
            final String renderer = context.getArgument("renderer", String.class);
            final boolean on = BoolArgumentType.getBool(context, "on");
            DebugRenderers.set(renderer, on);
            return 0;
        }))));
        Render.init();
    }
}
