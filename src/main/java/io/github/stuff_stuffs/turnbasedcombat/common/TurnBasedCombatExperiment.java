package io.github.stuff_stuffs.turnbasedcombat.common;

import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleEntityComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattlePlayerComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleWorldComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.EntityTypes;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ServerBattleImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.BattlePlayerComponentImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.ServerBattleWorldComponentImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.network.AddBattleS2C;
import io.github.stuff_stuffs.turnbasedcombat.common.network.RemoveBattleS2C;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Set;

public class TurnBasedCombatExperiment implements ModInitializer {
    public static final String MOD_ID = "turn_based_combat";

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        EntityTypes.init();
        EntityTrackingEvents.START_TRACKING.register((trackedEntity, player) -> {
            if (trackedEntity instanceof ServerPlayerEntity) {
                final BattlePlayerComponent battlePlayer = Components.BATTLE_PLAYER_COMPONENT_KEY.get(trackedEntity);
                final BattleWorldComponent battleWorld = Components.BATTLE_WORLD_COMPONENT_KEY.get(player.world);
                if (battlePlayer.isInBattle()) {
                    final Battle battle = battleWorld.fromHandle(battlePlayer.getBattleHandle());
                    assert battle != null;
                    AddBattleS2C.sendBattleToPlayer((ServerBattleImpl) battle, (ServerPlayerEntity) trackedEntity);
                }
            }
        });
        EntityTrackingEvents.STOP_TRACKING.register((trackedEntity, player) -> {
            if (trackedEntity instanceof ServerPlayerEntity) {
                final BattlePlayerComponent battlePlayer = Components.BATTLE_PLAYER_COMPONENT_KEY.get(trackedEntity);
                if (battlePlayer.isInBattle()) {
                    final BattleHandle handle = battlePlayer.getBattleHandle();
                    assert handle != null;
                    RemoveBattleS2C.removeBattleFromPlayerWorld(handle, player);
                }
            } else if (trackedEntity instanceof BattleEntity) {
                final BattleEntityComponent battleEntity = Components.BATTLE_ENTITY_COMPONENT_KEY.get(trackedEntity);
                if (battleEntity.isInBattle()) {
                    final BattleWorldComponent world = Components.BATTLE_WORLD_COMPONENT_KEY.get(trackedEntity.world);
                    final Battle battle = world.fromHandle(battleEntity.getBattleHandle());
                    assert battle != null;
                    battle.remove((BattleEntity) trackedEntity);
                }
            }
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
                    dispatcher.register(
                            CommandManager.literal("battle").then(
                                    CommandManager.argument("player", EntityArgumentType.player()).then(
                                            CommandManager.argument("entities", EntityArgumentType.entities()).executes(context -> {
                                                final ServerPlayerEntity playerEntity = EntityArgumentType.getPlayer(context, "player");
                                                final Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
                                                final Set<BattleEntity> battleEntities = new ReferenceOpenHashSet<>(entities.size());
                                                for (final Entity entity : entities) {
                                                    if (entity instanceof BattleEntity) {
                                                        battleEntities.add((BattleEntity) entity);
                                                    }
                                                }
                                                entities.remove(playerEntity);
                                                final ServerBattleWorldComponentImpl battleWorld = (ServerBattleWorldComponentImpl) Components.BATTLE_WORLD_COMPONENT_KEY.get(playerEntity.world);
                                                final BattleHandle battle = battleWorld.createBattle(playerEntity, battleEntities);
                                                return 0;
                                            })
                                    )
                            )
                    );
                    dispatcher.register(
                            CommandManager.literal("endBattle").then(
                                    CommandManager.argument("player", EntityArgumentType.player()).executes(context -> {
                                                final ServerPlayerEntity entity = EntityArgumentType.getPlayer(context, "player");
                                                final BattlePlayerComponentImpl battlePlayer = (BattlePlayerComponentImpl) Components.BATTLE_PLAYER_COMPONENT_KEY.get(entity);
                                                battlePlayer.setBattleHandle(null);
                                                return 0;
                                            }
                                    )
                            )
                    );
                }
        );
    }
}
