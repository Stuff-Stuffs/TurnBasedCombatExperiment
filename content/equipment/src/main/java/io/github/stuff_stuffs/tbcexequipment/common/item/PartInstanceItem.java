package io.github.stuff_stuffs.tbcexequipment.common.item;

import io.github.stuff_stuffs.tbcexequipment.common.creation.PartDataCreationContext;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;
import io.github.stuff_stuffs.tbcexequipment.common.part.Part;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import io.github.stuff_stuffs.tbcexequipment.common.part.Parts;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Optional;

public class PartInstanceItem extends Item {
    public PartInstanceItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public Text getName(final ItemStack stack) {
        final Optional<PartInstance> result = PartInstance.CODEC.parse(NbtOps.INSTANCE, stack.getSubNbt("partInstance")).result();
        if (result.isPresent()) {
            final PartInstance instance = result.get();
            MutableText text = instance.getPart().getName().copy();
            text = text.append(new LiteralText(": "));
            text = text.append(instance.getMaterial().getName());
            return text;
        }
        return super.getName(stack);
    }

    @Override
    public void inventoryTick(final ItemStack stack, final World world, final Entity entity, final int slot, final boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        final NbtCompound nbt = stack.getSubNbt("partInstance");
        if (nbt != null) {
            return;
        }
        stack.setCount(0);
    }

    @Override
    public void appendStacks(final ItemGroup group, final DefaultedList<ItemStack> stacks) {
        if (group == Items.PART_GROUP) {
            for (final Part<?> part : Parts.REGISTRY) {
                for (final Material material : Materials.REGISTRY) {
                    if (part.isValidMaterial(material)) {
                        final ItemStack stack = new ItemStack(this, 1);
                        final NbtCompound nbt = stack.getOrCreateNbt();
                        final PartDataCreationContext context = PartDataCreationContext.createForEntity(MinecraftClient.getInstance().player);
                        nbt.put("partInstance", PartInstance.CODEC.encodeStart(NbtOps.INSTANCE, new PartInstance(part, part.initialize(context), material)).getOrThrow(false, s -> {
                            throw new TBCExException(s);
                        }));
                        stacks.add(stack);
                    }
                }
            }
        }
    }
}
