package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.BiFunction;

public interface BattleParticipantItem {
    List<ParticipantAction> getActions(BattleStateView battleState, BattleParticipantStateView participantState, BattleParticipantInventoryHandle handle);

    BattleParticipantItemType getType();

    BattleParticipantItemCategory getCategory();

    Text getName();

    List<TooltipComponent> getTooltip();

    RarityInstance getRarity();

    enum Rarity {
        COMMON(0.0, 0xFFFFFFFF),
        UNCOMMON(1_00.0, 0xFF02F262),
        RARE(10_000.0, 0xFF43EEFD),
        EPIC(1_000_000.0, 0xFFFD43EE),
        LEGENDARY(100_000_000.0, 0xFFFFD700);
        public static final Codec<Rarity> CODEC = new Codec<>() {
            @Override
            public <T> DataResult<Pair<Rarity, T>> decode(DynamicOps<T> ops, T input) {
                return DataResult.success(Pair.of(Rarity.valueOf(ops.getStringValue(input).getOrThrow(false, s -> {
                    throw new RuntimeException(s);
                })), ops.empty()));
            }

            @Override
            public <T> DataResult<T> encode(Rarity input, DynamicOps<T> ops, T prefix) {
                return DataResult.success(ops.createString(input.name()));
            }
        };
        private static final Rarity[] values = values();
        private final double start;
        private final int colour;

        Rarity(final double start, final int colour) {
            this.start = start;
            this.colour = colour;
        }

        public int getColour() {
            return colour;
        }

        public static RarityInstance find(final double val) {
            if (val < 0) {
                return new RarityInstance(COMMON, 0);
            } else {
                Rarity min = COMMON;
                for (final Rarity rarity : values) {
                    if (rarity.start > val) {
                        break;
                    } else {
                        min = rarity;
                    }
                }
                if (min == LEGENDARY) {
                    return new RarityInstance(LEGENDARY, (val - LEGENDARY.start) / LEGENDARY.start);
                }
                final Rarity max = values[min.ordinal() + 1];
                final double progress = (val - min.start) / (max.start) - min.start;
                return new RarityInstance(min, progress);
            }
        }
    }

    record RarityInstance(Rarity rarity, double progress) {
        private static final DecimalFormat RARITY_FORMAT = new DecimalFormat("0.00");
        public static final Codec<RarityInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(Rarity.CODEC.fieldOf("rarity").forGetter(RarityInstance::getRarity), Codec.DOUBLE.fieldOf("progress").forGetter(RarityInstance::getProgress)).apply(instance, RarityInstance::new));
        public Rarity getRarity() {
            return rarity;
        }

        public double getProgress() {
            return progress;
        }

        public Text getAsText() {
            return new LiteralText(rarity.name() + "(" + RARITY_FORMAT.format(progress) + ")");
        }
    }
}
