package io.github.stuff_stuffs.tbcexanimation.client.resource;

import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPartFactory;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.SimpleModelPartFactory;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.loader.SimpleModelPartLoader;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public interface ModelPartFactoryProvider {
    @Nullable ModelPartFactory get(ModelPartIdentifier modelPartIdentifier);

    @Nullable Identifier getResourceReloadId();

    class DefaultModelPartFactoryProvider implements ModelPartFactoryProvider, SimpleSynchronousResourceReloadListener {
        private static final Identifier IDENTIFIER = new Identifier("tbcexanimation", "default_model_loader");
        private final Map<Identifier, SimpleModelPartFactory> simpleModelParts = new Object2ReferenceOpenHashMap<>();

        @Override
        public @Nullable ModelPartFactory get(final ModelPartIdentifier modelPartIdentifier) {
            return simpleModelParts.get(modelPartIdentifier.getIdentifier());
        }

        @Override
        public @Nullable Identifier getResourceReloadId() {
            return IDENTIFIER;
        }

        @Override
        public Identifier getFabricId() {
            return IDENTIFIER;
        }

        @Override
        public void reload(final ResourceManager manager) {
            final Collection<Identifier> simpleModelPartResources = manager.findResources("tbcex_models/model/simple", filename -> filename.endsWith(".obj"));
            simpleModelParts.clear();
            for (final Identifier resourceId : simpleModelPartResources) {
                try {
                    final SimpleModelPartFactory part = SimpleModelPartLoader.load(resourceId, manager);
                    simpleModelParts.put(new Identifier(resourceId.getNamespace(), resourceId.getPath().substring("tbcex_models/model".length() + 1, resourceId.getPath().lastIndexOf('.'))), part);
                } catch (final Exception e) {
                    LoggerUtil.LOGGER.error("Error while deserializing SimpleModelPart", e);
                }
            }
        }
    }
}
