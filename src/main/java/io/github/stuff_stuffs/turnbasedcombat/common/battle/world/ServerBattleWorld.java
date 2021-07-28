package io.github.stuff_stuffs.turnbasedcombat.common.battle.world;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ServerBattleWorld implements BattleWorld {
    private static final Logger LOGGER = LogManager.getLogger();
    //TODO config option
    //5 minutes
    private static final long TIME_OUT = 20 * 60 * 5;
    private static final String VERSION = "0.0";
    private final Path directory;
    private final Map<BattleHandle, Battle> activeBattles;
    private final Object2LongMap<BattleHandle> lastAccess;
    private long tickCount = 0;

    public ServerBattleWorld(final Path directory) {
        this.directory = directory;
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (final IOException e) {
                throw new RuntimeException("Cannot create tbcex_battle_world directory");
            }
        }
        if (!Files.isDirectory(directory)) {
            throw new RuntimeException("Cannot create tbcex_battle_world directory or already exists as file");
        }
        activeBattles = new Object2ReferenceOpenHashMap<>();
        lastAccess = new Object2LongOpenHashMap<>();
    }

    @Override
    public @Nullable Battle getBattle(final BattleHandle handle) {
        final Battle battle = activeBattles.get(handle);
        if (battle == null) {
            return tryLoad(handle);
        }
        lastAccess.put(handle, tickCount);
        return battle;
    }

    public void tick() {
        final Set<BattleHandle> toRemove = new ObjectOpenHashSet<>();
        for (final BattleHandle entry : activeBattles.keySet()) {
            if (tickCount - lastAccess.getOrDefault(entry, tickCount) >= TIME_OUT) {
                toRemove.add(entry);
            }
        }
        for (final BattleHandle battleHandle : toRemove) {
            save(battleHandle);
            activeBattles.remove(battleHandle);
            lastAccess.removeLong(battleHandle);
        }
        tickCount++;
    }

    public void save() {
        for (final BattleHandle handle : activeBattles.keySet()) {
            save(handle);
        }
    }

    private @Nullable Battle tryLoad(final BattleHandle handle) {
        final Path path = directory.resolve("Battle" + Integer.toString(handle.id(), 16));
        if (Files.exists(path) && !Files.isDirectory(path)) {
            try (final InputStream stream = Files.newInputStream(path, StandardOpenOption.READ)) {
                final int versionHeaderLength = stream.read();
                final String version = new String(stream.readNBytes(versionHeaderLength));
                if (!version.equals(VERSION)) {
                    LOGGER.error("Error loading battle: " + Integer.toString(handle.id(), 16) + ", version mismatch");
                    return null;
                }
                final Optional<Battle> result = Battle.CODEC.parse(NbtOps.INSTANCE, NbtIo.read(new DataInputStream(stream))).result();
                if (result.isPresent()) {
                    final Battle battle = result.get();
                    activeBattles.put(handle, battle);
                    lastAccess.put(handle, tickCount);
                    return battle;
                } else {
                    LOGGER.error("Error loading battle: " + Integer.toString(handle.id(), 16) + ", decoding error");
                    return null;
                }
            } catch (final IOException e) {
                LOGGER.error("Error loading battle: " + Integer.toString(handle.id(), 16) + ", IOException {}", e);
            }
        }
        LOGGER.error("Error loading battle: " + Integer.toString(handle.id(), 16) + ", non-existent battle");
        return null;
    }

    private void save(final BattleHandle handle) {
        final Battle battle = activeBattles.get(handle);
        if (battle != null) {
            try (final OutputStream stream = Files.newOutputStream(directory.resolve("Battle" + Integer.toString(handle.id(), 16)), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                stream.write(VERSION.length());
                stream.write(VERSION.getBytes(StandardCharsets.UTF_8));
                final DataOutput output = new DataOutputStream(stream);
                Battle.CODEC.encodeStart(NbtOps.INSTANCE, battle).result().ifPresentOrElse(element -> {
                    try {
                        element.write(output);
                    } catch (final IOException e) {
                        throw new RuntimeException("Cannot write battle file");
                    }
                }, () -> LOGGER.error("Error saving battle: " + Integer.toString(handle.id(), 16)));
                stream.flush();
            } catch (final IOException e) {
                LOGGER.error("Error saving battle: " + Integer.toString(handle.id(), 16));
            }
        } else {
            LOGGER.error("Error saving battle: " + Integer.toString(handle.id(), 16) + ", battle doesn't exist");
        }
    }
}
