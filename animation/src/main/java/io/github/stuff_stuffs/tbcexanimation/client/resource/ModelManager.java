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
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.SimpleModelPart;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.loader.SimpleModelPartLoader;
import io.github.stuff_stuffs.tbcexanimation.common.TBCExAnimation;
import io.github.stuff_stuffs.tbcexutil.common.Easing;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class ModelManager implements SimpleResourceReloadListener<ModelManager.PreparationData> {
    private static final Identifier IDENTIFIER = new Identifier(TBCExAnimation.MOD_ID, "model_manager");
    private final Map<String, ModelPartArgumentApplier> modifiers;
    private final Map<Identifier, SkeletonData> skeletonDatas;
    private final Map<Identifier, SimpleModelPart> simpleModelParts;
    private final Map<Identifier, KeyframeAnimationData> animationDatas;
    private final Map<Identifier, CompoundAnimationData> compoundAnimationDatas;
    private final Map<Identifier, ModelPartBundle> bundledParts;
    private boolean initialized;

    public ModelManager() {
        skeletonDatas = new Object2ReferenceOpenHashMap<>();
        simpleModelParts = new Object2ReferenceOpenHashMap<>();
        animationDatas = new Object2ReferenceOpenHashMap<>();
        modifiers = new Object2ReferenceOpenHashMap<>();
        compoundAnimationDatas = new Object2ReferenceOpenHashMap<>();
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

            final Collection<Identifier> simpleModelPartResources = manager.findResources("tbcex_models/model/simple", filename -> filename.endsWith(".obj"));
            final Map<Identifier, SimpleModelPart> simpleModelParts = new Object2ReferenceOpenHashMap<>();
            for (final Identifier resourceId : simpleModelPartResources) {
                try {
                    final SimpleModelPart part = SimpleModelPartLoader.load(resourceId, manager);
                    simpleModelParts.put(new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/model".length() + 1, resourceId.getPath().lastIndexOf('.'))), part);
                } catch (final Exception e) {
                    LoggerUtil.LOGGER.error("Error while deserializing SimpleModelPart", e);
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
            final Map<Identifier, Supplier<@Nullable ModelPartBundle>> bundledParts = new Object2ReferenceOpenHashMap<>();
            for (final Identifier resourceId : bundledModelPartResources) {
                try {
                    final Identifier canonical = new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/model/bundle".length() + 1, resourceId.getPath().lastIndexOf('.')));
                    bundledParts.put(canonical, () -> {
                        try {
                            return ModelPartBundleLoader.fromResource(manager.getResource(resourceId));
                        } catch (final Exception e) {
                            LoggerUtil.LOGGER.error("Error while deserializing bundled parts", e);
                            return null;
                        }
                    });
                } catch (final Exception e) {
                    LoggerUtil.LOGGER.error("Error while deserializing bundled parts", e);
                }
            }

            return new PreparationData(skeletonDatas, simpleModelParts, animationDatas, compoundAnimationDatas, bundledParts);
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(final PreparationData data, final ResourceManager manager, final Profiler profiler, final Executor executor) {
        return CompletableFuture.runAsync(() -> {
            skeletonDatas.clear();
            skeletonDatas.putAll(data.skeletonDatas);
            simpleModelParts.clear();
            simpleModelParts.putAll(data.simpleModelParts);
            animationDatas.clear();
            animationDatas.putAll(data.animationDatas);
            compoundAnimationDatas.clear();
            compoundAnimationDatas.putAll(data.compoundAnimationDatas);
            bundledParts.clear();
            for (Map.Entry<Identifier, Supplier<@Nullable ModelPartBundle>> entry : data.bundledParts.entrySet()) {
                final Identifier key = entry.getKey();
                final ModelPartBundle bundle = entry.getValue().get();
                if(bundle!=null) {
                    bundledParts.put(key, bundle);
                }
            }
        }, executor);
    }

    protected static final class PreparationData {
        private final Map<Identifier, SkeletonData> skeletonDatas;
        private final Map<Identifier, SimpleModelPart> simpleModelParts;
        private final Map<Identifier, KeyframeAnimationData> animationDatas;
        private final Map<Identifier, CompoundAnimationData> compoundAnimationDatas;
        private final Map<Identifier, Supplier<@Nullable ModelPartBundle>> bundledParts;

        public PreparationData(final Map<Identifier, SkeletonData> skeletonDatas, final Map<Identifier, SimpleModelPart> simpleModelParts, final Map<Identifier, KeyframeAnimationData> animationDatas, final Map<Identifier, CompoundAnimationData> compoundAnimationDatas, final Map<Identifier, Supplier<@Nullable ModelPartBundle>> bundledParts) {
            this.skeletonDatas = skeletonDatas;
            this.simpleModelParts = simpleModelParts;
            this.animationDatas = animationDatas;
            this.compoundAnimationDatas = compoundAnimationDatas;
            this.bundledParts = bundledParts;
        }
    }
}
