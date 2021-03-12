package io.github.stuff_stuffs.turnbasedcombat.common.component;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.BattleEntityComponentImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.BattlePlayerComponentImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.ClientBattleWorldComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.ServerBattleWorldComponentImpl;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

public final class ComponentRegistry implements EntityComponentInitializer, WorldComponentInitializer {

    @Override
    public void registerEntityComponentFactories(final EntityComponentFactoryRegistry registry) {
        //TODO, replace this next line
        registry.registerFor(Entity.class, Components.BATTLE_ENTITY_COMPONENT_KEY, BattleEntityComponentImpl::new);
        registry.registerForPlayers(Components.BATTLE_PLAYER_COMPONENT_KEY, BattlePlayerComponentImpl::new, RespawnCopyStrategy.NEVER_COPY);
    }

    @Override
    public void registerWorldComponentFactories(final WorldComponentFactoryRegistry registry) {
        registry.register(Components.BATTLE_WORLD_COMPONENT_KEY, world -> {
            if (world.isClient()) {
                return new ClientBattleWorldComponent((ClientWorld) world);
            } else {
                return new ServerBattleWorldComponentImpl((ServerWorld) world);
            }
        });
    }
}
