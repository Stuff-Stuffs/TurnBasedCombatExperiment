package io.github.stuff_stuffs.tbcexanimation.client.resource;

import io.github.stuff_stuffs.tbcexanimation.client.animation.keyframe.KeyframeAnimationData;
import io.github.stuff_stuffs.tbcexanimation.client.animation.keyframe.KeyframeDataLoader;
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
    private final Map<Identifier, KeyframeAnimationData> animationDatas;
    private boolean initialized;

    public ModelManager() {
        skeletonDatas = new Object2ReferenceOpenHashMap<>();
        simpleModelParts = new Object2ReferenceOpenHashMap<>();
        animationDatas = new Object2ReferenceOpenHashMap<>();
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

    public @Nullable KeyframeAnimationData getAnimationData(final Identifier identifier) {
        if (!initialized) {
            throw new RuntimeException();
        }
        return animationDatas.get(identifier);
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
            final Set<Identifier> simpleModelPartResourceSet = new ObjectOpenHashSet<>(simpleModelPartResources.size());
            simpleModelPartResourceSet.addAll(simpleModelPartResources);
            final Collection<Identifier> animationResources = manager.findResources("tbcex_models/animation", filename -> filename.endsWith(".json"));
            final Set<Identifier> animationResourceSet = new ObjectOpenHashSet<>(animationResources.size());
            animationResourceSet.addAll(animationResources);
            initialized = true;
            return new PreparationData(skeletonDataResourceMap, simpleModelPartResourceSet, animationResourceSet);
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
                    final SimpleModelPart part = SimpleModelPartLoader.load(resourceId, manager);
                    simpleModelParts.put(new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/model".length() + 1, resourceId.getPath().lastIndexOf('.'))), part);
                } catch (final IOException e) {
                    LoggerUtil.LOGGER.error("Error while deserializing SimpleModelPart", e);
                }
            }
            animationDatas.clear();
            for (final Identifier resourceId : data.animationResources) {
                try {
                    Identifier canonical = new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/animation".length() + 1, resourceId.getPath().lastIndexOf('.')));
                    final Map<Identifier, KeyframeAnimationData> animations = KeyframeDataLoader.getAnimations(canonical, manager.getResource(resourceId));
                    if (animations != null) {
                        animationDatas.putAll(animations);
                    }
                } catch (final IOException e) {
                    LoggerUtil.LOGGER.error("Error while deserializing keyframe animation data", e);
                }
            }
        }, executor);
    }

    protected static final class PreparationData {
        private final Map<Identifier, Resource> skeletonDataResources;
        private final Set<Identifier> simpleModelPartResources;
        private final Set<Identifier> animationResources;

        private PreparationData(final Map<Identifier, Resource> skeletonDataResources, final Set<Identifier> simpleModelPartResources, final Set<Identifier> animationResources) {
            this.skeletonDataResources = skeletonDataResources;
            this.simpleModelPartResources = simpleModelPartResources;
            this.animationResources = animationResources;
        }
    }
}
