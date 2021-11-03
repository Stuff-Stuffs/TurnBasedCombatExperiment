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

public class HandlePartData extends AbstractPartData {
    public static final Codec<HandlePartData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Materials.REGISTRY.fieldOf("material").forGetter(HandlePartData::getMaterial),
            Codec.INT.fieldOf("level").forGetter(HandlePartData::getLevel)
    ).apply(instance, HandlePartData::new));

    private HandlePartData(final Material material, final int level) {
        super(Parts.HANDLE_PART, material, level);
    }

    public HandlePartData(final PartDataCreationContext ctx) {
        super(Parts.HANDLE_PART, ctx.getMaterial(), ctx.getLevel());
    }

    @Override
    public Text getName() {
        return material.getName().copy().append(new LiteralText(" Handle")).setStyle(Style.EMPTY.withColor(getRarity().rarity().getColour()));
    }

    @Override
    public List<Text> getDescription() {
        return List.of();
    }
}
