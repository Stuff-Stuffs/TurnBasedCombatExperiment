package io.github.stuff_stuffs.tbcexequipment.common.part.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexequipment.common.creation.PartDataCreationContext;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;
import io.github.stuff_stuffs.tbcexequipment.common.part.Parts;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;

public class SimplePommelPartData extends AbstractPartData {
    public static final Codec<SimplePommelPartData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Materials.REGISTRY.getCodec().fieldOf("material").forGetter(AbstractPartData::getMaterial),
            Codec.INT.fieldOf("level").forGetter(AbstractPartData::getLevel)
    ).apply(instance, SimplePommelPartData::new));

    private SimplePommelPartData(final Material material, final int level) {
        super(Parts.SIMPLE_POMMEL, material, level);
    }

    public SimplePommelPartData(final PartDataCreationContext ctx) {
        super(Parts.SIMPLE_POMMEL, ctx.getMaterial(), ctx.getLevel());
    }

    @Override
    public Text getName() {
        return material.getName().copy().append(new LiteralText(" Simple Pommel")).setStyle(Style.EMPTY.withColor(getRarity().rarity().getColour()));
    }

    @Override
    public List<Text> getDescription() {
        return List.of();
    }
}
