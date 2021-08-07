package io.github.stuff_stuffs.tbcexcore.common;

import com.mojang.brigadier.CommandDispatcher;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleActionRegistry;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamageType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleBounds;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.ServerBattleWorld;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexcore.common.entity.EntityTypes;
import io.github.stuff_stuffs.tbcexcore.common.network.Network;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class TurnBasedCombatExperiment implements ModInitializer {
    public static final String MOD_ID = "tbcexcore";
    public static final Logger LOGGER = LogManager.getLogger("TBCEx");

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
        BattleEquipmentSlot.init();
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
            final ServerBattleWorld battleWorld = (ServerBattleWorld) ((BattleWorldSupplier) world).tbcex_getBattleWorld();
            final BattleHandle handle = battleWorld.createBattle(bounds);
            final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle);
            if (battle == null) {
                throw new RuntimeException();
            }
            for (final Entity entity : entities) {
                try {
                    if (entity instanceof BattleEntity battleEntity) {
                        battleWorld.join(handle, battleEntity);
                    } else {
                        context.getSource().sendError(new LiteralText("Entity: " + entity.getUuidAsString() + " is not instanceof BattleEntity, excluding it from battle"));
                    }
                } catch (final IllegalArgumentException e) {
                    context.getSource().sendError(new LiteralText("Battle somehow doesn't exist?"));
                }
            }
            return 0;
        })));
    }
}
