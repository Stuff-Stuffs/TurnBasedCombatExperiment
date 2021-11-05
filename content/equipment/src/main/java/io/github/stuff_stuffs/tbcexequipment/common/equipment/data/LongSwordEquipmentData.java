package io.github.stuff_stuffs.tbcexequipment.common.equipment.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItem;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemCategory;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.battle.equipment.BattleEquipmentSlots;
import io.github.stuff_stuffs.tbcexequipment.common.creation.EquipmentDataCreationContext;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentTypes;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import io.github.stuff_stuffs.tbcexequipment.common.part.data.PartData;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class LongSwordEquipmentData extends AbstractEquipmentData {
    public static final Identifier POMMEL_PART = TBCExEquipment.createId("long_sword_pommel");
    public static final Identifier HANDLE_PART = TBCExEquipment.createId("long_sword_handle");
    public static final Identifier GUARD_PART = TBCExEquipment.createId("long_sword_guard");
    public static final Identifier BLADE_PART = TBCExEquipment.createId("long_sword_blade");
    public static final Codec<LongSwordEquipmentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.unboundedMap(Identifier.CODEC, PartInstance.CODEC).fieldOf("parts").forGetter(data -> data.parts)).apply(instance, LongSwordEquipmentData::new));
    public static final Function<EquipmentDataCreationContext, LongSwordEquipmentData> INITIALIZER = ctx -> new LongSwordEquipmentData(ctx.getParts());

    private final BattleParticipantItem.RarityInstance rarity;

    private LongSwordEquipmentData(final Map<Identifier, PartInstance> parts) {
        super(parts, EquipmentTypes.LONG_SWORD_EQUIPMENT);
        double sum = 0;
        for (final PartInstance part : parts.values()) {
            final PartData data = part.getData();
            sum += data.getRarity().getRarity().getStart() * (1 + data.getRarity().getProgress());
        }
        rarity = BattleParticipantItem.Rarity.find(sum / parts.size());
    }

    public @Nullable PartInstance getPommel() {
        return parts.get(POMMEL_PART);
    }

    public PartInstance getHandle() {
        return parts.get(HANDLE_PART);
    }

    public @Nullable PartInstance getGuard() {
        return parts.get(GUARD_PART);
    }

    public PartInstance getBlade() {
        return parts.get(BLADE_PART);
    }

    @Override
    public boolean validSlot(final BattleEquipmentSlot slot) {
        return slot == BattleEquipmentSlots.MAIN_HAND_SLOT;
    }

    @Override
    public Set<BattleEquipmentSlot> getBlockedSlots() {
        return Set.of(BattleEquipmentSlots.OFF_HAND_SLOT);
    }

    @Override
    public boolean isInCategory(final BattleParticipantItemCategory category) {
        return category == BattleParticipantItemCategory.BATTLE_EQUIPMENT_CATEGORY.apply(BattleEquipmentSlots.MAIN_HAND_SLOT);
    }

    @Override
    public Text getName() {
        //TODO name generation
        return new LiteralText("Long sword");
    }

    @Override
    public List<Text> getTooltip() {
        return List.of();
    }

    @Override
    public BattleParticipantItem.RarityInstance getRarity() {
        return rarity;
    }
}
