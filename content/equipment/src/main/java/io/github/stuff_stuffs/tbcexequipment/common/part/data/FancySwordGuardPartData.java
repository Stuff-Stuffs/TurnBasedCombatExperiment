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

public class FancySwordGuardPartData extends AbstractPartData {
    public static final Codec<FancySwordGuardPartData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Materials.REGISTRY.fieldOf("material").forGetter(AbstractPartData::getMaterial),
            Codec.INT.fieldOf("level").forGetter(AbstractPartData::getLevel)
    ).apply(instance, FancySwordGuardPartData::new));

    private FancySwordGuardPartData(final Material material, final int level) {
        super(Parts.FANCY_SWORD_GUARD, material, level);
    }

    public FancySwordGuardPartData(final PartDataCreationContext ctx) {
        super(Parts.FANCY_SWORD_GUARD, ctx.getMaterial(), ctx.getLevel());
    }

    @Override
    public Text getName() {
        return material.getName().copy().append(new LiteralText(" Fancy Sword Guard")).setStyle(Style.EMPTY.withColor(getRarity().rarity().getColour()));
    }

    @Override
    public List<Text> getDescription() {
        return List.of();
    }
}
