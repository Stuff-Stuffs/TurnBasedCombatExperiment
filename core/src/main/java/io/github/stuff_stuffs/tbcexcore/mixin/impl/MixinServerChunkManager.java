package io.github.stuff_stuffs.tbcexcore.mixin.impl;

import com.mojang.datafixers.DataFixer;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleWorld;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.ServerBattleWorld;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerChunkManager.class)
public abstract class MixinServerChunkManager implements BattleWorldSupplier {
    @Unique
    private ServerBattleWorld battleWorld;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(final ServerWorld world, final LevelStorage.Session session, final DataFixer dataFixer, final StructureManager structureManager, final Executor workerExecutor, final ChunkGenerator chunkGenerator, final int viewDistance, final boolean bl, final WorldGenerationProgressListener worldGenerationProgressListener, final ChunkStatusChangeListener chunkStatusChangeListener, final Supplier<PersistentStateManager> supplier, final CallbackInfo ci) {
        final File worldDirectory = session.getWorldDirectory(world.getRegistryKey());
        final File battleWorldDirectory = new File(worldDirectory, "tbcex_battle_world");
        battleWorld = new ServerBattleWorld(battleWorldDirectory.toPath());
    }

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At("HEAD"))
    private void tickInject(final BooleanSupplier booleanSupplier, final CallbackInfo ci) {
        battleWorld.tick();
    }

    @Inject(method = "save", at = @At("HEAD"))
    private void saveInject(final boolean flush, final CallbackInfo ci) {
        battleWorld.save();
    }

    @Override
    public BattleWorld tbcex_getBattleWorld() {
        return battleWorld;
    }
}
