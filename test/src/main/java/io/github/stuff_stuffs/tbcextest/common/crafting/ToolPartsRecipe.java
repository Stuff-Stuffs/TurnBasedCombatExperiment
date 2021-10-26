package io.github.stuff_stuffs.tbcextest.common.crafting;

import io.github.stuff_stuffs.tbcexequipment.common.item.Items;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import io.github.stuff_stuffs.tbcextest.common.Test;
import io.github.stuff_stuffs.tbcextest.common.item.ToolPartsTestItem;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ToolPartsRecipe extends SpecialCraftingRecipe {
    public ToolPartsRecipe(final Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(final CraftingInventory inventory, final World world) {
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            final ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                count++;
                if (stack.getItem() != Items.PART_INSTANCE || stack.getSubNbt("partInstance") == null) {
                    return false;
                }
            }
        }
        return count != 0;
    }

    @Override
    public ItemStack craft(final CraftingInventory inventory) {
        final List<PartInstance> partInstances = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            final ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == Items.PART_INSTANCE && stack.getSubNbt("partInstance") != null) {
                partInstances.add(PartInstance.CODEC.parse(NbtOps.INSTANCE, stack.getSubNbt("partInstance")).getOrThrow(false, s -> {
                    throw new TBCExException(s);
                }));
            }
        }
        final ItemStack stack = new ItemStack(Test.TEST_PARTS_ITEM, 1);
        stack.setSubNbt("parts", ToolPartsTestItem.CODEC.encodeStart(NbtOps.INSTANCE, partInstances).getOrThrow(false, s -> {
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
