package io.github.stuff_stuffs.tbcexanimation.client.resource;

import io.github.stuff_stuffs.tbcexanimation.client.model.SkeletonData;
import io.github.stuff_stuffs.tbcexanimation.client.model.loader.SkeletonDataLoader;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.SimpleModelPart;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.loader.SimpleModelPartLoader;
import io.github.stuff_stuffs.tbcexanimation.common.TBCExAnimation;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ModelManager implements SimpleResourceReloadListener<ModelManager.PreparationData> {
    private static final Identifier IDENTIFIER = new Identifier(TBCExAnimation.MOD_ID, "model_manager");
    private final Map<Identifier, SkeletonData> skeletonDatas;
    private final Map<Identifier, SimpleModelPart> simpleModelParts;
    private boolean initialized;

    public ModelManager() {
        skeletonDatas = new Object2ReferenceOpenHashMap<>();
        simpleModelParts = new Object2ReferenceOpenHashMap<>();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public @Nullable SkeletonData getSkeletonData(final Identifier identifier) {
        if (!initialized) {
            throw new RuntimeException();
        }
        return skeletonDatas.get(identifier);
    }

    public @Nullable SimpleModelPart getSimpleModelPart(final Identifier identifier) {
        if (!initialized) {
            throw new RuntimeException();
        }
        return simpleModelParts.get(identifier);
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
                final Identifier canonical = new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/skeleton/data".length() + 1, resourceId.getPath().lastIndexOf('.')));
                if (!skeletonDataResourceMap.containsKey(canonical)) {
                    try {
                        final Resource resource = manager.getResource(resourceId);
                        skeletonDataResourceMap.put(canonical, resource);
                    } catch (final Exception e) {
                        LoggerUtil.LOGGER.error("Error while loading skeleton resource", e);
                    }
                }
            }
            final Collection<Identifier> simpleModelPartResources = manager.findResources("tbcex_models/model/simple", filename -> filename.endsWith(".obj"));
            final Set<Identifier> simpleModelPartResourceSet = new ObjectOpenHashSet<>();
            simpleModelPartResourceSet.addAll(simpleModelPartResources);
            initialized = true;
            return new PreparationData(skeletonDataResourceMap, simpleModelPartResourceSet);
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
            simpleModelParts.clear();
            for (final Identifier resourceId : data.simpleModelPartResources) {
                try {
                    SimpleModelPart part = SimpleModelPartLoader.load(resourceId, manager);
                    simpleModelParts.put(new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/model".length() + 1, resourceId.getPath().lastIndexOf('.'))), part);
                } catch (final IOException e) {
                    LoggerUtil.LOGGER.error("Error while deserializing SimpleModelPart", e);
                }
            }
        }, executor);
    }

    protected static final class PreparationData {
        private final Map<Identifier, Resource> skeletonDataResources;
        private final Set<Identifier> simpleModelPartResources;

        private PreparationData(final Map<Identifier, Resource> skeletonDataResources, final Set<Identifier> simpleModelPartResources) {
            this.skeletonDataResources = skeletonDataResources;
            this.simpleModelPartResources = simpleModelPartResources;
        }
    }
}
