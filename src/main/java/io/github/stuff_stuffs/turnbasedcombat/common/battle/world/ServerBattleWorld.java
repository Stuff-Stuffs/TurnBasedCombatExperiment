package io.github.stuff_stuffs.turnbasedcombat.common.battle.world;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.ParticipantJoinBattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ServerBattleWorld implements BattleWorld {
    private static final Logger LOGGER = TurnBasedCombatExperiment.LOGGER;
    //TODO config option
    //5 minutes
    private static final long TIME_OUT = 20 * 60 * 5;
    private static final String VERSION = "0.0";
    private final Path directory;
    private final Map<BattleHandle, Battle> activeBattles;
    private final Object2LongMap<BattleHandle> lastAccess;
    private int nextId = 0;
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

    public BattleHandle createBattle(final Collection<BattleEntity> entities, final BattleBounds bounds) {
        final BattleHandle handle = createBattle(bounds);
        final Battle battle = activeBattles.get(handle);
        for (final BattleEntity entity : entities) {
            final BattleAction<?> action = new ParticipantJoinBattleAction(BattleParticipantHandle.UNIVERSAL.apply(handle), new BattleParticipantState(new BattleParticipantHandle(handle, ((Entity) entity).getUuid()), entity));
            battle.push(action);
        }
        return handle;
    }

    public BattleHandle createBattle(final BattleBounds bounds) {
        final BattleHandle handle = new BattleHandle(nextId++);
        final Battle battle = new Battle(handle, bounds);
        activeBattles.put(handle, battle);
        lastAccess.put(handle, tickCount);
        return handle;
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

    private static String handleToFile(final BattleHandle handle) {
        return "Battle" + Integer.toString(handle.id(), 16) + "tbcex_battle";
    }

    private @Nullable Battle tryLoad(final BattleHandle handle) {
        final Path path = directory.resolve(handleToFile(handle));
        if (Files.exists(path) && !Files.isDirectory(path)) {
            try (final InputStream stream = Files.newInputStream(path, StandardOpenOption.READ)) {
                final int versionHeaderLength = stream.read();
                final String version = new String(stream.readNBytes(versionHeaderLength));
                if (!version.equals(VERSION)) {
                    LOGGER.error("Error loading battle: " + handleToFile(handle) + ", version mismatch");
                    return null;
                }
                final Optional<Battle> result = Battle.CODEC.parse(NbtOps.INSTANCE, NbtIo.read(new DataInputStream(stream))).result();
                if (result.isPresent()) {
                    final Battle battle = result.get();
                    activeBattles.put(handle, battle);
                    lastAccess.put(handle, tickCount);
                    return battle;
                } else {
                    LOGGER.error("Error loading battle: " + handleToFile(handle) + ", decoding error");
                    return null;
                }
            } catch (final IOException e) {
                LOGGER.error("Error loading battle: " + handleToFile(handle) + ", IOException {}", e);
            }
        }
        LOGGER.error("Error loading battle: " + handleToFile(handle) + ", non-existent battle");
        return null;
    }

    private void save(final BattleHandle handle) {
        final Battle battle = activeBattles.get(handle);
        if (battle != null) {
            try (final OutputStream stream = Files.newOutputStream(directory.resolve(handleToFile(handle)), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                stream.write(VERSION.length());
                stream.write(VERSION.getBytes(StandardCharsets.UTF_8));
                final DataOutput output = new DataOutputStream(stream);
                Battle.CODEC.encodeStart(NbtOps.INSTANCE, battle).result().ifPresentOrElse(element -> {
                    try {
                        element.write(output);
                    } catch (final IOException e) {
                        throw new RuntimeException("Cannot write battle file");
                    }
                }, () -> LOGGER.error("Error saving battle: " + handleToFile(handle)));
                stream.flush();
            } catch (final IOException e) {
                LOGGER.error("Error saving battle: " + handleToFile(handle));
            }
        } else {
            LOGGER.error("Error saving battle: " + handleToFile(handle));
        }
    }
}
