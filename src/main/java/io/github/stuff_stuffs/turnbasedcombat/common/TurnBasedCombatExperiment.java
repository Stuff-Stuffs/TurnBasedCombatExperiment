package io.github.stuff_stuffs.turnbasedcombat.common;

import io.github.stuff_stuffs.turnbasedcombat.common.api.*;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleEntityComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleWorldComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.EntityTypes;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ServerBattleImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ServerBattleLog;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.BattlePlayerComponentImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.ServerBattleWorldComponentImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.network.sender.TryAddBattle;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class TurnBasedCombatExperiment implements ModInitializer {
    public static final String MOD_ID = "turn_based_combat";

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        TurnBasedCombatAbilities.init();
        EntityTypes.init();
        EntityTrackingEvents.START_TRACKING.register((trackedEntity, player) -> {
            final BattleEntityComponent battleEntity = Components.BATTLE_ENTITY_COMPONENT_KEY.getNullable(trackedEntity);
            if (battleEntity != null && battleEntity.isInBattle()) {
                final ServerBattleImpl battle = (ServerBattleImpl) Components.BATTLE_WORLD_COMPONENT_KEY.get(player.world).fromHandle(battleEntity.getBattleHandle());
                if (battle != null) {
                    TryAddBattle.send(player, battle, (ServerBattleLog) battle.getLog());
                } else {
                    throw new RuntimeException();
                }
            }
        });
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            final BattleEntityComponent battleEntity = Components.BATTLE_ENTITY_COMPONENT_KEY.getNullable(entity);
            if (battleEntity != null) {
                final BattleHandle handle = battleEntity.getBattleHandle();
                if (handle != null && battleEntity.isInBattle()) {
                    final Battle battle = Components.BATTLE_WORLD_COMPONENT_KEY.get(entity.world).fromHandle(handle);
                    if (battle != null) {
                        battle.remove((BattleEntity) entity);
                    }
                }
            }
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
                    dispatcher.register(
                            CommandManager.literal("battle").then(
                                    CommandManager.argument("entities", EntityArgumentType.entities()).executes(context -> {
                                        final Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
                                        final Set<BattleEntity> battleEntities = new ReferenceOpenHashSet<>(entities.size());
                                        for (final Entity entity : entities) {
                                            if (entity instanceof BattleEntity) {
                                                battleEntities.add((BattleEntity) entity);
                                            }
                                        }
                                        final ServerBattleWorldComponentImpl battleWorld = (ServerBattleWorldComponentImpl) Components.BATTLE_WORLD_COMPONENT_KEY.get(context.getSource().getWorld());
                                        final BattleHandle battle = battleWorld.createBattle(battleEntities, BattleBounds.fromEntities(() -> (Iterator<Entity>) (Object) battleEntities.iterator(), 5, 5));
                                        return 0;
                                    })
                            )
                    );
                    //fixme
                    dispatcher.register(
                            CommandManager.literal("endBattle").then(
                                    CommandManager.argument("player", EntityArgumentType.player()).executes(context -> {
                                                final ServerPlayerEntity entity = EntityArgumentType.getPlayer(context, "player");
                                                final BattlePlayerComponentImpl battlePlayer = (BattlePlayerComponentImpl) Components.BATTLE_PLAYER_COMPONENT_KEY.get(entity);
                                                final BattleWorldComponent battleWorld = Components.BATTLE_WORLD_COMPONENT_KEY.get(entity.world);
                                                final Battle battle = battleWorld.fromHandle(battlePlayer.getBattleHandle());
                                                if (battle != null && battle.isActive()) {
                                                    battle.end(Battle.EndingReason.COMPLETED);
                                                }
                                                return 0;
                                            }
                                    )
                            )
                    );
                }
        );
    }
}
