package io.github.stuff_stuffs.tbcexutil.mixin;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(KeyBindingRegistryImpl.class)
public class MixinKeyBindingRegistryImpl {
    @Shadow
    @Final
    private static List<KeyBinding> moddedKeyBindings;

    /**
     * @author Stuff-Stuffs
     * @reason https://github.com/FabricMC/fabric/issues/1772
     */
    @Overwrite
    public static KeyBinding[] process(final KeyBinding[] keysAll) {
        final List<KeyBinding> newKeysAll = Lists.newArrayList(keysAll);
        newKeysAll.removeAll(new ReferenceArrayList<>(moddedKeyBindings));
        newKeysAll.addAll(moddedKeyBindings);
        return newKeysAll.toArray(new KeyBinding[0]);
    }
}
