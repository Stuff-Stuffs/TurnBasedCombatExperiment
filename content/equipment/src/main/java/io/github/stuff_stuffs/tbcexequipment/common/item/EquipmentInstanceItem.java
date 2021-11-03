package io.github.stuff_stuffs.tbcexequipment.common.item;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.item.BattleItem;
import io.github.stuff_stuffs.tbcexequipment.common.battle.item.ParticipantEquipmentInstanceItem;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentInstance;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EquipmentInstanceItem extends Item implements BattleItem {
    public static final String INSTANCE_KEY = "tbcex_equipment_instance";

    public EquipmentInstanceItem(final Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(final ItemStack stack, final World world, final Entity entity, final int slot, final boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        final NbtCompound nbt = stack.getSubNbt(INSTANCE_KEY);
        if (nbt != null) {
            return;
        }
        stack.setCount(0);
    }

    @Override
    public BattleParticipantItemStack toBattleItem(final ItemStack stack) {
        final NbtCompound nbt = stack.getSubNbt(INSTANCE_KEY);
        if (nbt == null) {
            return null;
        }
        final Optional<EquipmentInstance> optional = EquipmentInstance.CODEC.parse(NbtOps.INSTANCE, nbt).result();
        return optional.map(ParticipantEquipmentInstanceItem::new).map(item -> new BattleParticipantItemStack(item, stack.getCount())).orElse(null);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtElement element = stack.getSubNbt(INSTANCE_KEY);
        if(element==null) {
            return;
        }
        final Optional<EquipmentInstance> result = EquipmentInstance.CODEC.parse(NbtOps.INSTANCE, element).result();
        if(result.isPresent()) {
            if(context.isAdvanced()) {
                List<Text> partTooltips = new ArrayList<>();
                final Map<Identifier, PartInstance> parts = result.get().getData().getParts();
                final Set<Identifier> keys = result.get().getType().getParts();
                for (Identifier key : keys) {
                    final PartInstance partInstance = parts.get(key);
                    if(partInstance!=null) {
                        partTooltips.add(partInstance.getData().getName());
                        final List<Text> description = partInstance.getData().getDescription();
                        for (int i = 0; i < description.size(); i++) {
                            description.set(i, new LiteralText("-").append(description.get(i)));
                        }
                        partTooltips.addAll(description);
                    }
                }
                for (int i = 0; i < partTooltips.size(); i++) {
                    partTooltips.set(i, new LiteralText("-").append(partTooltips.get(i)));
                }
                tooltip.addAll(partTooltips);
            } else {
                tooltip.addAll(result.get().getData().getTooltip());
            }
        }
    }
}
