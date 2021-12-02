package io.github.stuff_stuffs.tbcexcharacter.mixin.impl;

import io.github.stuff_stuffs.tbcexcharacter.common.entity.CharacterInfo;
import io.github.stuff_stuffs.tbcexcharacter.mixin.api.PlayerCharacterInfoSupplier;
import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements BattleEntity, PlayerCharacterInfoSupplier {
    @Unique
    private CharacterInfo characterInfo = new CharacterInfo();

    protected MixinPlayerEntity(final EntityType<? extends LivingEntity> entityType, final World world) {
        super(entityType, world);
    }

    @Override
    public double tbcex_getStat(final BattleParticipantStat stat) {
        return characterInfo.getStat(stat);
    }

    @Override
    public double tbcex_getCurrentHealth() {
        return getHealth();
    }

    @Override
    public CharacterInfo tbcex_getCharacterInfo() {
        return characterInfo;
    }

    @Override
    public int tbcex_getLevel() {
        return characterInfo.getLevel();
    }

    @Inject(at = @At("RETURN"), method = "writeCustomDataToNbt")
    private void saveCharacterInfo(final NbtCompound nbt, final CallbackInfo ci) {
        final Optional<NbtElement> containerResult = CharacterInfo.CODEC.encodeStart(NbtOps.INSTANCE, characterInfo).result();
        containerResult.ifPresent(element -> nbt.put("tbcex_character_info", element));
    }

    @Inject(at = @At("RETURN"), method = "readCustomDataFromNbt")
    private void loadCharacterInfo(final NbtCompound nbt, final CallbackInfo ci) {
        final NbtElement statNbt = nbt.get("tbcex_character_info");
        if (statNbt != null) {
            final Optional<CharacterInfo> result = CharacterInfo.CODEC.parse(NbtOps.INSTANCE, statNbt).result();
            result.ifPresent(container -> characterInfo = container);
        } else {
            characterInfo = new CharacterInfo();
        }
    }

    @Shadow
    @Final
    private PlayerInventory inventory;

    @Unique
    private static final Team TEAM = new Team("test_player");

    @Override
    public Team tbcex_getTeam() {
        return TEAM;
    }

    @Override
    public Iterable<ItemStack> tbcex_getInventory() {
        final List<ItemStack> stacks = new ArrayList<>(inventory.main);
        stacks.set(inventory.selectedSlot, ItemStack.EMPTY);
        return stacks;
    }

    @Override
    public BattleParticipantBounds tbcex_getBounds() {
        return BattleParticipantBounds.builder().add(TBCExCore.createId("body"), new Box(0, 0, 0, 1, 1.5, 1)).add(TBCExCore.createId("head"), new Box(0.25, 1.5, 0.25, 0.75, 2, 0.75)).build(new Vec3d(0.5, 0, 0.5));
    }


    @Override
    public @Nullable ItemStack tbcex_getEquipped(final BattleEquipmentSlot slot) {
        final Function<PlayerEntity, @Nullable ItemStack> extractor = TBCExCore.getPlayerExtractor(slot);
        if (extractor == null) {
            return null;
        }
        return extractor.apply((PlayerEntity) (Object) this);
    }

    @Override
    public void tbcex_onBattleJoin(final BattleHandle handle) {
    }

    @Override
    public boolean tbcex_shouldSaveToTag() {
        return false;
    }
}
