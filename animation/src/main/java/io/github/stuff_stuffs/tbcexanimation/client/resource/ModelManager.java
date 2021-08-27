package io.github.stuff_stuffs.tbcexanimation.client.resource;

import io.github.stuff_stuffs.tbcexanimation.client.animation.keyframe.KeyframeAnimationData;
import io.github.stuff_stuffs.tbcexanimation.client.animation.keyframe.KeyframeDataLoader;
import io.github.stuff_stuffs.tbcexanimation.client.model.SkeletonData;
import io.github.stuff_stuffs.tbcexanimation.client.model.bundle.ModelPartBundle;
import io.github.stuff_stuffs.tbcexanimation.client.model.bundle.ModelPartBundleLoader;
import io.github.stuff_stuffs.tbcexanimation.client.model.loader.SkeletonDataLoader;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
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
    private final Map<String, ModelPartArgumentApplier> modifiers;
    private final Map<Identifier, SkeletonData> skeletonDatas;
    private final Map<Identifier, SimpleModelPart> simpleModelParts;
    private final Map<Identifier, KeyframeAnimationData> animationDatas;
    private final Map<Identifier, ModelPartBundle> bundledParts;
    private boolean initialized;

    public ModelManager() {
        skeletonDatas = new Object2ReferenceOpenHashMap<>();
        simpleModelParts = new Object2ReferenceOpenHashMap<>();
        animationDatas = new Object2ReferenceOpenHashMap<>();
        modifiers = new Object2ReferenceOpenHashMap<>();
        bundledParts = new Object2ReferenceOpenHashMap<>();
    }

    public void addModifier(final String argumentName, final ModelPartArgumentApplier applier) {
        if (modifiers.put(argumentName, applier) != null) {
            throw new RuntimeException();
        }
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

    public @Nullable ModelPartBundle getModelPartBundle(final Identifier identifier) {
        if (!initialized) {
            throw new RuntimeException();
        }
        return bundledParts.get(identifier);
    }

    public @Nullable ModelPart getModelPart(final Identifier identifier) {
        return getModelPart(ModelPartIdentifier.builder().build(identifier));
    }

    public @Nullable ModelPart getModelPart(final ModelPartIdentifier identifier) {
        if (!initialized) {
            throw new RuntimeException();
        }
        ModelPart tmp = getSimpleModelPart(identifier.getIdentifier());
        if (tmp == null) {
            return null;
        }
        for (final Map.Entry<String, String> entry : identifier.getArguments().entrySet()) {
            final String argumentName = entry.getKey();
            final String argument = entry.getValue();
            final ModelPartArgumentApplier applier = modifiers.get(argumentName);
            if (applier == null) {
                LoggerUtil.LOGGER.warn("Unknown argument: {}, with  value: {}", argumentName, argument);
            } else {
                tmp = applier.apply(tmp, argument);
            }
        }
        return tmp;
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

            final Collection<Identifier> bundledModelPartResources = manager.findResources("tbcex_models/model/bundle", filename -> filename.endsWith(".json"));
            final Set<Identifier> bundledPartResources = new ObjectOpenHashSet<>(animationResources.size());
            bundledPartResources.addAll(bundledModelPartResources);

            initialized = true;
            return new PreparationData(skeletonDataResourceMap, simpleModelPartResourceSet, animationResourceSet, bundledPartResources);
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
                    final Identifier canonical = new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/animation".length() + 1, resourceId.getPath().lastIndexOf('.')));
                    final Map<Identifier, KeyframeAnimationData> animations = KeyframeDataLoader.getAnimations(canonical, manager.getResource(resourceId));
                    if (animations != null) {
                        animationDatas.putAll(animations);
                    }
                } catch (final IOException e) {
                    LoggerUtil.LOGGER.error("Error while deserializing keyframe animation data", e);
                }
            }
            bundledParts.clear();
            for (final Identifier resourceId : data.bundledPartResources) {
                try {
                    bundledParts.put(new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/model/bundle".length() + 1, resourceId.getPath().lastIndexOf('.'))), ModelPartBundleLoader.fromResource(manager.getResource(resourceId)));
                } catch (final IOException e) {
                    LoggerUtil.LOGGER.error("Error while deserializing bundled parts", e);
                }
            }
        }, executor);
    }

    protected static final class PreparationData {
        private final Map<Identifier, Resource> skeletonDataResources;
        private final Set<Identifier> simpleModelPartResources;
        private final Set<Identifier> animationResources;
        private final Set<Identifier> bundledPartResources;

        private PreparationData(final Map<Identifier, Resource> skeletonDataResources, final Set<Identifier> simpleModelPartResources, final Set<Identifier> animationResources, final Set<Identifier> bundledPartResources) {
            this.skeletonDataResources = skeletonDataResources;
            this.simpleModelPartResources = simpleModelPartResources;
            this.animationResources = animationResources;
            this.bundledPartResources = bundledPartResources;
        }
    }
}
