package io.github.stuff_stuffs.tbcexequipment.common.equipment.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BasicAttackBattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamageComposition;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamagePacket;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamageSource;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamageType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantActionInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.SingleTargetParticipantActionInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.ParticipantTargetType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetStreams;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItem;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemCategory;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.battle.equipment.BattleEquipmentSlots;
import io.github.stuff_stuffs.tbcexequipment.common.creation.EquipmentDataCreationContext;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentTypes;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import io.github.stuff_stuffs.tbcexequipment.common.part.data.PartData;
import io.github.stuff_stuffs.tbcexequipment.common.part.data.SwordBladePartData;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class LongSwordEquipmentData extends AbstractEquipmentData {
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

    @Override
    public List<ParticipantAction> getActions(final BattleStateView stateView, final BattleParticipantStateView participantView, final BattleEquipmentSlot slot) {
        return List.of(new ParticipantAction() {
            @Override
            public Text getName() {
                return new LiteralText("Melee Attack");
            }

            @Override
            public List<TooltipComponent> getTooltip() {
                return List.of(TooltipComponent.of(new LiteralText("Basic melee attack with a base damage of " + ((SwordBladePartData) parts.get(TBCExEquipment.createId("long_sword_blade")).getData()).getBaseDamage()).asOrderedText()));
            }

            @Override
            public ParticipantActionInstance createInstance(final BattleStateView battleState, final BattleParticipantHandle handle, final Consumer<BattleAction<?>> sender) {
                return new ParticipantActionInstance(
                        new SingleTargetParticipantActionInfo<>(
                                new ParticipantTargetType(
                                        (battleState1, handle1) -> {
                                            final TargetStreams.Context context = new TargetStreams.Context(battleState1, handle1);
                                            return () -> TargetStreams.getParticipantStream(context, false).filter(TargetStreams.team(context, false)).filter(TargetStreams.withinRange(context, 1)).iterator();
                                        }
                                ),
                                (battleState12, user, target) ->
                                        new BasicAttackBattleAction(
                                                user,
                                                target.getHandle(),
                                                new BattleDamagePacket(
                                                        BattleDamageComposition.builder().addWeight(BattleDamageType.PHYSICAL, 1).build(),
                                                        new BattleDamageSource(Optional.of(user)),
                                                        ((SwordBladePartData) parts.get(TBCExEquipment.createId("long_sword_blade")).getData()).getBaseDamage()
                                                ),
                                                1
                                        ),
                                sender,
                                List.of()
                        ), battleState, handle
                );
            }
        }, BattleEquipment.createUnequipAction(participantView, slot));
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
