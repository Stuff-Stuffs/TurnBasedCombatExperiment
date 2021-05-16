package io.github.stuff_stuffs.turnbasedcombat.client.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.stuff_stuffs.turnbasedcombat.client.render.debug.DebugRenderers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.command.CommandSource;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class DebugRendererArgument implements ArgumentType<String> {
    private static final SimpleCommandExceptionType EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("turn_base_combat.debugRenderer.invalid"));

    private DebugRendererArgument() {
    }

    public static DebugRendererArgument debugRendererArgument() {
        return new DebugRendererArgument();
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        final int i = reader.getCursor();
        final String s = reader.readStringUntil(' ');
        //cursed
        reader.setCursor(reader.getCursor()-1);
        if (DebugRenderers.contains(s)) {
            return s;
        }
        reader.setCursor(i);
        throw EXCEPTION.createWithContext(reader);
    }

    @Override
    public Collection<String> getExamples() {
        return new ObjectArrayList<>(DebugRenderers.getKeys());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(DebugRenderers.getKeys(), builder);
    }
}
