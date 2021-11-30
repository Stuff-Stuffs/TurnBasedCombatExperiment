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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerChunkManager.class)
public abstract class MixinServerChunkManager implements BattleWorldSupplier {
    @Shadow @Final
    ServerWorld world;
    @Unique
    private ServerBattleWorld battleWorld;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(ServerWorld world, LevelStorage.Session session, DataFixer dataFixer, StructureManager structureManager, Executor workerExecutor, ChunkGenerator chunkGenerator, int viewDistance, int simulationDistance, boolean dsync, WorldGenerationProgressListener worldGenerationProgressListener, ChunkStatusChangeListener chunkStatusChangeListener, Supplier persistentStateManagerFactory, CallbackInfo ci) {
        final Path worldDirectory = session.getWorldDirectory(world.getRegistryKey());
        final Path battleWorldDirectory = worldDirectory.resolve("tbcex_battle_world");
        battleWorld = new ServerBattleWorld(battleWorldDirectory, this.world);
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
