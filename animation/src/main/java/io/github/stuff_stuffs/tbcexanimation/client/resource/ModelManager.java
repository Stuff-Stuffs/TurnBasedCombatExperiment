package io.github.stuff_stuffs.tbcexanimation.client.resource;

import io.github.stuff_stuffs.tbcexanimation.client.animation.Animation;
import io.github.stuff_stuffs.tbcexanimation.client.animation.CompoundAnimation;
import io.github.stuff_stuffs.tbcexanimation.client.animation.CompoundAnimationData;
import io.github.stuff_stuffs.tbcexanimation.client.animation.CompoundAnimationLoader;
import io.github.stuff_stuffs.tbcexanimation.client.animation.builtin.ResetAnimation;
import io.github.stuff_stuffs.tbcexanimation.client.animation.builtin.ResetAutomaticAnimation;
import io.github.stuff_stuffs.tbcexanimation.client.animation.keyframe.KeyframeAnimationData;
import io.github.stuff_stuffs.tbcexanimation.client.animation.keyframe.KeyframeDataLoader;
import io.github.stuff_stuffs.tbcexanimation.client.animation.keyframe.SimpleKeyframeAnimation;
import io.github.stuff_stuffs.tbcexanimation.client.model.SkeletonData;
import io.github.stuff_stuffs.tbcexanimation.client.model.bundle.ModelPartBundle;
import io.github.stuff_stuffs.tbcexanimation.client.model.bundle.ModelPartBundleLoader;
import io.github.stuff_stuffs.tbcexanimation.client.model.loader.SkeletonDataLoader;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPartFactory;
import io.github.stuff_stuffs.tbcexanimation.common.TBCExAnimation;
import io.github.stuff_stuffs.tbcexutil.common.Easing;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public final class ModelManager implements SimpleResourceReloadListener<ModelManager.PreparationData> {
    private static final Identifier IDENTIFIER = new Identifier(TBCExAnimation.MOD_ID, "model_manager");
    private final Map<String, ModelPartArgumentApplier> modifiers;
    private final Map<Identifier, SkeletonData> skeletonDatas;
    private final Map<Identifier, KeyframeAnimationData> animationDatas;
    private final Map<Identifier, CompoundAnimationData> compoundAnimationDatas;
    private final Map<Identifier, ModelPartBundle> bundledParts;
    private final List<ModelPartFactoryProvider> providers;
    private boolean initialized;

    public ModelManager() {
        skeletonDatas = new Object2ReferenceOpenHashMap<>();
        animationDatas = new Object2ReferenceOpenHashMap<>();
        modifiers = new Object2ReferenceOpenHashMap<>();
        compoundAnimationDatas = new Object2ReferenceOpenHashMap<>();
        bundledParts = new Object2ReferenceOpenHashMap<>();
        providers = new ArrayList<>();
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return providers.stream().map(ModelPartFactoryProvider::getResourceReloadId).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void addModifier(final String argumentName, final ModelPartArgumentApplier applier) {
        if (modifiers.put(argumentName, applier) != null) {
            throw new RuntimeException();
        }
    }

    public void registerModelProvider(final ModelPartFactoryProvider factoryProvider) {
        providers.add(factoryProvider);
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

    private @Nullable ModelPartFactory loadModelPart(final ModelPartIdentifier identifier) {
        if (!initialized) {
            throw new RuntimeException();
        }
        ModelPartFactory part = null;
        final Iterator<ModelPartFactoryProvider> iterator = providers.iterator();
        while (part == null && iterator.hasNext()) {
            part = iterator.next().get(identifier);
        }
        return part;
    }

    public @Nullable ModelPartBundle getModelPartBundle(final Identifier identifier) {
        if (!initialized) {
            throw new RuntimeException();
        }
        return bundledParts.get(identifier);
    }

    public @Nullable ModelPartFactory getModelFactoryPart(final Identifier identifier) {
        return getModelFactoryPart(ModelPartIdentifier.builder().build(identifier));
    }

    public @Nullable ModelPartFactory getModelFactoryPart(final ModelPartIdentifier identifier) {
        if (!initialized) {
            throw new RuntimeException();
        }
        ModelPartFactory tmp = loadModelPart(identifier);
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

    public @Nullable Animation getAnimation(final Identifier identifier) {
        if (identifier.getNamespace().equals("tbcexanimation")) {
            final String path = identifier.getPath();
            if (path.startsWith("builtin")) {
                try {
                    final String builtin = path.substring("builtin".length() + 1);
                    if (builtin.startsWith("reset/")) {
                        final String arg = builtin.substring("reset/".length());
                        final String[] args = arg.split("/");
                        if (args.length != 2) {
                            throw new RuntimeException();
                        }
                        final double time = Double.parseDouble(args[0]);
                        final Easing easing = Easing.getEasing(args[1]);
                        return new ResetAnimation(time, easing);
                    } else if (builtin.startsWith("automatic_reset/")) {
                        final String arg = builtin.substring("automatic_reset/".length());
                        final String[] args = arg.split("/");
                        if (args.length != 2) {
                            throw new RuntimeException();
                        }
                        final double scale = Double.parseDouble(args[0]);
                        final Easing easing = Easing.getEasing(args[1]);
                        return new ResetAutomaticAnimation(scale, easing);
                    }
                } catch (final Exception e) {
                    LoggerUtil.LOGGER.error("Something went wrong while trying to access builtin animation", e);
                }
                return null;
            }
        }
        final CompoundAnimationData compoundData = compoundAnimationDatas.get(identifier);
        if (compoundData != null) {
            return new CompoundAnimation(compoundData);
        }
        final KeyframeAnimationData data = animationDatas.get(identifier);
        if (data != null) {
            return new SimpleKeyframeAnimation(data);
        }
        return null;
    }

    @Override
    public Identifier getFabricId() {
        return IDENTIFIER;
    }

    @Override
    public CompletableFuture<PreparationData> load(final ResourceManager manager, final Profiler profiler, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            initialized = true;
            final Collection<Identifier> skeletonDataResources = manager.findResources("tbcex_models/skeleton/data", filename -> filename.endsWith(".json"));
            final Map<Identifier, SkeletonData> skeletonDatas = new Object2ReferenceOpenHashMap<>();
            for (final Identifier resourceId : skeletonDataResources) {
                try {
                    final SkeletonData skeletonData = SkeletonDataLoader.fromResource(manager.getResource(resourceId));
                    if (skeletonData != null) {
                        final Identifier canonical = new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/skeleton/data".length() + 1, resourceId.getPath().lastIndexOf('.')));
                        skeletonDatas.put(canonical, skeletonData);
                    }
                } catch (final Exception e) {
                    LoggerUtil.LOGGER.error("Error while deserializing SkeletonData", e);
                }
            }

            final Collection<Identifier> animationResources = manager.findResources("tbcex_models/animation/keyframe", filename -> filename.endsWith(".json"));
            final Map<Identifier, KeyframeAnimationData> animationDatas = new Object2ReferenceOpenHashMap<>();
            for (final Identifier resourceId : animationResources) {
                try {
                    final Identifier canonical = new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/animation/keyframe".length() + 1, resourceId.getPath().lastIndexOf('.')));
                    final Map<Identifier, KeyframeAnimationData> animations = KeyframeDataLoader.getAnimations(canonical, manager.getResource(resourceId));
                    if (animations != null) {
                        animationDatas.putAll(animations);
                    }
                } catch (final Exception e) {
                    LoggerUtil.LOGGER.error("Error while deserializing keyframe animation data", e);
                }
            }

            final Collection<Identifier> compoundAnimationResources = manager.findResources("tbcex_models/animation/compound", filename -> filename.endsWith(".json"));
            final Map<Identifier, CompoundAnimationData> compoundAnimationDatas = new Object2ReferenceOpenHashMap<>();
            for (final Identifier resourceId : compoundAnimationResources) {
                try {
                    final CompoundAnimationData data = CompoundAnimationLoader.fromResource(manager.getResource(resourceId));
                    if (data != null) {
                        final Identifier canonical = new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/animation/compound".length() + 1, resourceId.getPath().lastIndexOf('.')));
                        compoundAnimationDatas.put(canonical, data);
                    }
                } catch (final Exception e) {
                    LoggerUtil.LOGGER.error("Error while deserializing bundled parts", e);
                }
            }

            final Collection<Identifier> bundledModelPartResources = manager.findResources("tbcex_models/model/bundle", filename -> filename.endsWith(".json"));
            final Map<Identifier, ModelPartBundle> bundledParts = new Object2ReferenceOpenHashMap<>();
            for (final Identifier resourceId : bundledModelPartResources) {
                final Identifier canonical = new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/model/bundle".length() + 1, resourceId.getPath().lastIndexOf('.')));
                try {
                    final ModelPartBundle bundle = ModelPartBundleLoader.fromResource(manager.getResource(resourceId));
                    if (bundle != null) {
                        bundledParts.put(canonical, bundle);
                    }
                } catch (final Exception e) {
                    LoggerUtil.LOGGER.error("Error while deserializing bundled parts", e);
                }
            }

            return new PreparationData(skeletonDatas, animationDatas, compoundAnimationDatas, bundledParts);
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(final PreparationData data, final ResourceManager manager, final Profiler profiler, final Executor executor) {
        return CompletableFuture.runAsync(() -> {
            skeletonDatas.clear();
            skeletonDatas.putAll(data.skeletonDatas);
            animationDatas.clear();
            animationDatas.putAll(data.animationDatas);
            compoundAnimationDatas.clear();
            compoundAnimationDatas.putAll(data.compoundAnimationDatas);
            bundledParts.clear();
            bundledParts.putAll(data.bundledParts);
        }, executor);
    }

    protected static final class PreparationData {
        private final Map<Identifier, SkeletonData> skeletonDatas;
        private final Map<Identifier, KeyframeAnimationData> animationDatas;
        private final Map<Identifier, CompoundAnimationData> compoundAnimationDatas;
        private final Map<Identifier, ModelPartBundle> bundledParts;

        public PreparationData(final Map<Identifier, SkeletonData> skeletonDatas, final Map<Identifier, KeyframeAnimationData> animationDatas, final Map<Identifier, CompoundAnimationData> compoundAnimationDatas, final Map<Identifier, ModelPartBundle> bundledParts) {
            this.skeletonDatas = skeletonDatas;
            this.animationDatas = animationDatas;
            this.compoundAnimationDatas = compoundAnimationDatas;
            this.bundledParts = bundledParts;
        }
    }
}
