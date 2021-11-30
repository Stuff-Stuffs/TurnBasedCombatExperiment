package io.github.stuff_stuffs.tbcexequipment.common.part.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.creation.PartDataCreationContext;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;
import io.github.stuff_stuffs.tbcexequipment.common.part.Parts;
import io.github.stuff_stuffs.tbcexequipment.common.part.stats.PartStatContext;
import io.github.stuff_stuffs.tbcexequipment.common.part.stats.PartStats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SwordBladePartData extends AbstractPartData {
    public static final Codec<SwordBladePartData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Materials.REGISTRY.getCodec().fieldOf("material").forGetter(AbstractPartData::getMaterial),
            Codec.INT.fieldOf("level").forGetter(AbstractPartData::getLevel)
    ).apply(instance, SwordBladePartData::new));

    private SwordBladePartData(final Material material, final int level) {
        super(Parts.SWORD_BLADE_PART, material, level);
    }

    public SwordBladePartData(final PartDataCreationContext ctx) {
        super(Parts.SWORD_BLADE_PART, ctx.getMaterial(), ctx.getLevel());
    }

    @Override
    public Text getName() {
        return material.getName().copy().append(new LiteralText(" ")).append(new LiteralText("Sword Blade")).setStyle(Style.EMPTY.withColor(getRarity().rarity().getColour()));
    }

    @Override
    public List<Text> getDescription() {
        final List<Text> texts = new ArrayList<>();
        texts.add(new LiteralText("Base damage: " + TBCExEquipment.PART_STAT_MANAGER.get(this, PartStats.LONG_SWORD_BLADE_DAMAGE, PartStatContext.empty())));
        return texts;
    }
}
