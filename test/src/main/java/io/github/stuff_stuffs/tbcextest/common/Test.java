package io.github.stuff_stuffs.tbcextest.common;

import com.mojang.brigadier.CommandDispatcher;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.BattleParticipantItemRenderer;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.BattleRendererRegistry;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemType;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleBounds;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.ServerBattleWorld;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcextest.common.entity.EntityTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;

public class Test implements ModInitializer {
    public static final Item TEST_ITEM = new TestItem();
    public static final BattleParticipantItemType TEST_ITEM_TYPE = new BattleParticipantItemType(TestBattleParticipantItem.CODEC, TestBattleParticipantItem.CAN_MERGE, TestBattleParticipantItem.MERGER, TestBattleParticipantItem.TO_ITEM_STACK);

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("tbcextest", "test_item"), TEST_ITEM);
        Registry.register(BattleParticipantItemType.REGISTRY, new Identifier("tbcextest", "test_item"), TEST_ITEM_TYPE);
        EntityTypes.init();
        CommandRegistrationCallback.EVENT.register(Test::register);
        BattleRendererRegistry.addItemRenderer(TEST_ITEM_TYPE, new BattleParticipantItemRenderer.DefaultRenderer());
    }

    private static void register(final CommandDispatcher<ServerCommandSource> dispatcher, final boolean dedicated) {
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
