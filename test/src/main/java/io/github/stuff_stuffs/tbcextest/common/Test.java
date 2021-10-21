package io.github.stuff_stuffs.tbcextest.common;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.EndTurnBattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentType;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleBounds;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.ServerBattleWorld;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcextest.common.battle.equipment.TestWeaponEquipment;
import io.github.stuff_stuffs.tbcextest.common.battle.item.TestBattleParticipantItem;
import io.github.stuff_stuffs.tbcextest.common.battle.item.TestWeaponBattleParticipantItem;
import io.github.stuff_stuffs.tbcextest.common.entity.EntityTypes;
import io.github.stuff_stuffs.tbcextest.common.item.TestItem;
import io.github.stuff_stuffs.tbcextest.common.item.TestWeaponItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.List;

public class Test implements ModInitializer {
    public static final Item TEST_ITEM = new TestItem();
    public static final Item TEST_WEAPON_ITEM = new TestWeaponItem();
    public static final BattleEquipmentType TEST_WEAPON_EQUIPMENT_TYPE = new BattleEquipmentType(new LiteralText("test_weapon"), TestWeaponEquipment.CODEC);
    public static final BattleParticipantItemType TEST_ITEM_TYPE = new BattleParticipantItemType(TestBattleParticipantItem.CODEC, TestBattleParticipantItem.CAN_MERGE, TestBattleParticipantItem.MERGER, TestBattleParticipantItem.TO_ITEM_STACK);
    public static final BattleParticipantItemType TEST_WEAPON_ITEM_TYPE = new BattleParticipantItemType(TestWeaponBattleParticipantItem.CODEC, (i, j) -> false, (i, j) -> null, i -> List.of(new ItemStack(TEST_ITEM, i.getCount())));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("tbcextest", "test_item"), TEST_ITEM);
        Registry.register(BattleParticipantItemType.REGISTRY, new Identifier("tbcextest", "test_item"), TEST_ITEM_TYPE);
        Registry.register(Registry.ITEM, new Identifier("tbcextest", "test_weapon"), TEST_WEAPON_ITEM);
        Registry.register(BattleEquipmentType.REGISTRY, new Identifier("tbcextest", "test_weapon"), TEST_WEAPON_EQUIPMENT_TYPE);
        Registry.register(BattleParticipantItemType.REGISTRY, new Identifier("tbcextest", "test_weapon"), TEST_WEAPON_ITEM_TYPE);
        EntityTypes.init();
        CommandRegistrationCallback.EVENT.register(Test::register);
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
        dispatcher.register(CommandManager.literal("battleAdvance").executes(new Command<ServerCommandSource>() {
            @Override
            public int run(final CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
                final Entity entity = context.getSource().getEntityOrThrow();
                if (entity instanceof BattleAwareEntity battleAware) {
                    final BattleHandle handle = battleAware.tbcex_getCurrentBattle();
                    if (handle == null) {
                        throw new RuntimeException("Entity not in battle tried to advance battle");
                    }
                    final Battle battle = ((BattleWorldSupplier) entity.world).tbcex_getBattleWorld().getBattle(handle);
                    if (battle == null) {
                        throw new RuntimeException("Unknown battle");
                    }
                    battle.push(new EndTurnBattleAction(BattleParticipantHandle.UNIVERSAL.apply(handle)));
                } else {
                    throw new RuntimeException("Non battle aware entity tried to advance battle");
                }
                return 0;
            }
        }));
    }
}
