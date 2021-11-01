package io.github.stuff_stuffs.tbcextest.common.crafting;

import io.github.stuff_stuffs.tbcexequipment.common.creation.EquipmentDataCreationContext;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.data.EquipmentData;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentInstance;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentType;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentTypes;
import io.github.stuff_stuffs.tbcexequipment.common.item.EquipmentInstanceItem;
import io.github.stuff_stuffs.tbcexequipment.common.item.Items;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.*;

public class ToolPartsRecipe extends SpecialCraftingRecipe {
    public ToolPartsRecipe(final Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(final CraftingInventory inventory, final World world) {
        final List<PartInstance> partInstances = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            final ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() != Items.PART_INSTANCE) {
                    return false;
                }
                final NbtCompound nbt = stack.getSubNbt("partInstance");
                if (nbt == null) {
                    return false;
                }
                partInstances.add(PartInstance.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow(false, s -> {
                    throw new TBCExException();
                }));
            }
        }
        final Optional<EquipmentType<?>> any = EquipmentTypes.REGISTRY.stream().filter(type -> type.fromList(partInstances)!=null).findAny();
        return any.isPresent();
    }

    @Override
    public ItemStack craft(final CraftingInventory inventory) {
        final List<PartInstance> partInstances = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            final ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                final NbtCompound nbt = stack.getSubNbt("partInstance");
                if (nbt == null) {
                    return ItemStack.EMPTY;
                }
                partInstances.add(PartInstance.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow(false, s -> {
                    throw new TBCExException();
                }));
            }
        }
        final Optional<EquipmentType<?>> any = EquipmentTypes.REGISTRY.stream().filter(type -> type.fromList(partInstances)!=null).findAny();
        if (any.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Map<Identifier, PartInstance> parts = any.get().fromList(partInstances);
        final EquipmentDataCreationContext ctx = EquipmentDataCreationContext.createForEntity(null, parts);
        final EquipmentType<?> equipmentType = any.get();
        final EquipmentData equipmentData = equipmentType.initialize(ctx);
        final EquipmentInstance instance = new EquipmentInstance(equipmentType, equipmentData);
        final ItemStack stack = new ItemStack(Items.EQUIPMENT_INSTANCE, 1);
        stack.setSubNbt(EquipmentInstanceItem.INSTANCE_KEY, EquipmentInstance.CODEC.encodeStart(NbtOps.INSTANCE, instance).getOrThrow(false, s -> {
            throw new TBCExException(s);
        }));
        return stack;
    }

    @Override
    public boolean fits(final int width, final int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}
