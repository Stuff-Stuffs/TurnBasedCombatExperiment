package io.github.stuff_stuffs.turnbasedcombat.common;

import io.github.stuff_stuffs.turnbasedcombat.common.entity.EntityTypes;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.ServerBattleCameraEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TurnBasedCombatExperiment implements ModInitializer {
    public static final String MOD_ID = "turn_based_combat";

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        EntityTypes.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("camera").requires(s -> s.getEntity() instanceof ServerPlayerEntity).executes(context -> {
            final ServerBattleCameraEntity serverBattleCameraEntity = new ServerBattleCameraEntity(EntityTypes.BATTLE_CAMERA_ENTITY_TYPE, context.getSource().getWorld(), context.getSource().getPlayer().getUuid());
            context.getSource().getWorld().spawnEntity(serverBattleCameraEntity);
            return 0;
        })));
    }
}
