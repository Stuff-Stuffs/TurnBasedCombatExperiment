package io.github.stuff_stuffs.tbcextest.common.item;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.World;

import java.util.List;

public class ToolPartsTestItem extends Item {
    public static final Codec<List<PartInstance>> CODEC = Codec.list(PartInstance.CODEC);

    public ToolPartsTestItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public void inventoryTick(final ItemStack stack, final World world, final Entity entity, final int slot, final boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        final NbtElement nbt = stack.getOrCreateNbt().get("parts");
        if (nbt == null) {
            stack.setCount(0);
        }
    }
}
