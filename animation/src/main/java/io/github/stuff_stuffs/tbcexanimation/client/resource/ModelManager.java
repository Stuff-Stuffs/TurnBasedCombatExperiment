package io.github.stuff_stuffs.tbcexanimation.client.resource;

import io.github.stuff_stuffs.tbcexanimation.common.TBCExAnimation;
import io.github.stuff_stuffs.tbcexanimation.client.model.SkeletonData;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ModelManager implements SimpleResourceReloadListener<ModelManager.PreparationData> {
    private static final Identifier IDENTIFIER = new Identifier(TBCExAnimation.MOD_ID, "model_manager");
    private final Map<Identifier, SkeletonData> skeletonDatas;
    private boolean initialized;

    public ModelManager() {
        skeletonDatas = new Object2ReferenceOpenHashMap<>();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public @Nullable SkeletonData getSkeletonData(final Identifier identifier) {
        return skeletonDatas.get(identifier);
    }

    @Override
    public Identifier getFabricId() {
        return IDENTIFIER;
    }

    @Override
    public CompletableFuture<PreparationData> load(final ResourceManager manager, final Profiler profiler, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            final Collection<Identifier> skeletonDataResources = manager.findResources("tbcex_models/skeleton/data", filename -> filename.endsWith(".json"));
            final Map<Identifier, Resource> skeletonDataResourceMap = new Object2ReferenceOpenHashMap<>();
            for (final Identifier resourceId : skeletonDataResources) {
                final Identifier canonical = new Identifier(resourceId.getNamespace(), resourceId.getPath().substring(26));
                if (!skeletonDataResourceMap.containsKey(canonical)) {
                    try {
                        final Resource resource = manager.getResource(resourceId);
                        skeletonDataResourceMap.put(canonical, resource);
                    } catch (final Exception e) {
                        LoggerUtil.LOGGER.error("Error while loading skeleton resource", e);
                    }
                }
            }
            initialized = true;
            return new PreparationData(skeletonDataResourceMap);
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(final PreparationData data, final ResourceManager manager, final Profiler profiler, final Executor executor) {
        return CompletableFuture.runAsync(() -> {
            skeletonDatas.clear();
            for (final Map.Entry<Identifier, Resource> entry : data.skeletonDataResources.entrySet()) {
                final SkeletonData skeletonData = SkeletonDataLoader.fromResource(entry.getValue());
                if (skeletonData != null) {
                    skeletonDatas.put(entry.getKey(), skeletonData);
                }
            }
        }, executor);
    }

    protected static final class PreparationData {
        private final Map<Identifier, Resource> skeletonDataResources;

        private PreparationData(final Map<Identifier, Resource> skeletonDataResources) {
            this.skeletonDataResources = skeletonDataResources;
        }
    }
}
