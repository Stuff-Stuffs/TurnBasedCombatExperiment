package io.github.stuff_stuffs.tbcexcore.common.battle.world;

import io.github.stuff_stuffs.tbcexcore.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.ParticipantJoinBattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexcore.common.network.PlayerJoinBattleSender;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

//TODO remember valid battle ids
public final class ServerBattleWorld implements BattleWorld {
    private static final Logger LOGGER = TurnBasedCombatExperiment.LOGGER;
    //TODO config option
    //5 minutes
    private static final long TIME_OUT = 20 * 60 * 5;
    private static final String VERSION = "0.0";
    private final Path directory;
    private final Path metaFile;
    private final Map<BattleHandle, Battle> activeBattles;
    private final Object2LongMap<BattleHandle> lastAccess;
    private int nextId;
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
        metaFile = directory.resolve("central.tbcex_battle_meta");
        if (Files.isRegularFile(metaFile)) {
            try (final InputStream metaStream = Files.newInputStream(metaFile, StandardOpenOption.READ)) {
                final ByteBuffer buffer = ByteBuffer.allocate(256);
                buffer.put(metaStream.readAllBytes());
                buffer.position(0);
                nextId = buffer.getInt();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        } else {
            nextId = 0;
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

    public void join(final BattleHandle handle, final BattleEntity entity) {
        final Battle battle = getBattle(handle);
        if (battle != null) {
            battle.push(new ParticipantJoinBattleAction(BattleParticipantHandle.UNIVERSAL.apply(handle), new BattleParticipantState(new BattleParticipantHandle(handle, ((Entity) entity).getUuid()), entity)));
            if (entity instanceof BattleAwareEntity battleAware) {
                battleAware.tbcex_setCurrentBattle(handle);
                if (entity instanceof ServerPlayerEntity player) {
                    PlayerJoinBattleSender.send(player, handle);
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public BattleHandle createBattle(final BattleBounds bounds) {
        final BattleHandle handle = new BattleHandle(nextId++);
        final Battle battle = new Battle(handle, bounds, 20 * 30, 20 * 30);
        activeBattles.put(handle, battle);
        lastAccess.put(handle, tickCount);
        return handle;
    }

    public void tick() {
        final Set<BattleHandle> toRemove = new ObjectOpenHashSet<>();
        for (final BattleHandle entry : activeBattles.keySet()) {
            if (tickCount - lastAccess.getOrDefault(entry, tickCount) >= TIME_OUT) {
                toRemove.add(entry);
            } else {
                activeBattles.get(entry).tick();
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
        try (final OutputStream metaStream = Files.newOutputStream(metaFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            final ByteBuffer buffer = ByteBuffer.allocate(256);
            buffer.putInt(nextId);
            metaStream.write(buffer.array());
        } catch (final IOException e) {
            //TODO
            throw new RuntimeException();
        }
    }

    private static String handleToFile(final BattleHandle handle) {
        return "Battle" + Integer.toString(handle.id(), 16) + ".tbcex_battle";
    }

    private static String handleToFileMeta(final BattleHandle handle) {
        return "Battle" + Integer.toString(handle.id(), 16) + ".tbcex_battle_meta";
    }

    private @Nullable Battle tryLoad(final BattleHandle handle) {
        final Path battlePath = directory.resolve(handleToFile(handle));
        final Path metaPath = directory.resolve(handleToFileMeta(handle));
        if (Files.exists(battlePath) && !Files.isDirectory(battlePath) && Files.exists(metaPath) && !Files.isDirectory(metaPath)) {
            try (final InputStream battleStream = Files.newInputStream(battlePath, StandardOpenOption.READ); final InputStream metaStream = Files.newInputStream(metaPath, StandardOpenOption.READ)) {
                final int versionHeaderLength = metaStream.read();
                final String version = new String(metaStream.readNBytes(versionHeaderLength));
                if (!version.equals(VERSION)) {
                    LOGGER.error("Error loading battle: " + handleToFile(handle) + ", version mismatch");
                    return null;
                }
                final Optional<Battle> result = Battle.CODEC.parse(NbtOps.INSTANCE, NbtIo.readCompressed(new DataInputStream(battleStream)).get("data")).result();
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
            try (
                    final OutputStream battleStream = Files.newOutputStream(directory.resolve(handleToFile(handle)), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                    final OutputStream metaStream = Files.newOutputStream(directory.resolve(handleToFileMeta(handle)), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
            ) {
                metaStream.write(VERSION.length());
                metaStream.write(VERSION.getBytes(StandardCharsets.UTF_8));
                Battle.CODEC.encodeStart(NbtOps.INSTANCE, battle).result().ifPresentOrElse(element -> {
                    try {
                        final NbtCompound nbtCompound = new NbtCompound();
                        nbtCompound.put("data", element);
                        NbtIo.writeCompressed(nbtCompound, battleStream);
                    } catch (final IOException e) {
                        throw new RuntimeException("Cannot write battle file");
                    }
                }, () -> LOGGER.error("Error saving battle: " + handleToFile(handle)));
                battleStream.flush();
            } catch (final IOException e) {
                LOGGER.error("Error saving battle: " + handleToFile(handle));
            }
        } else {
            LOGGER.error("Error saving battle: " + handleToFile(handle));
        }
    }
}
