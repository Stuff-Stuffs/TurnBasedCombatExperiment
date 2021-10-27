package io.github.stuff_stuffs.tbcexequipment.mixin;

import io.github.stuff_stuffs.tbcexequipment.common.material.MaterialTags;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartTags;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(RequiredTagListRegistry.class)
public class MixinRequiredTagListRegistry {
    @Inject(at = @At("RETURN"), method = "getBuiltinTags", cancellable = true)
    private static void addTags(final CallbackInfoReturnable<Set<RequiredTagList<?>>> cir) {
        final Set<RequiredTagList<?>> requiredTagLists = new ObjectOpenHashSet<>();
        requiredTagLists.addAll(cir.getReturnValue());
        requiredTagLists.add(PartTags.REQUIRED_TAG_LIST);
        requiredTagLists.add(MaterialTags.REQUIRED_TAG_LIST);
        cir.setReturnValue(requiredTagLists);
    }
}
