package io.github.stuff_stuffs.turnbasedcombat.common.api;

import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ClientBattleImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.ClientBattleWorldComponent;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Function;

public abstract class BattleAction {
    private final static Map<Class<? extends BattleAction>, Function<PacketByteBuf, BattleAction>> CACHE = new Reference2ReferenceOpenHashMap<>();
    private final static Class<?>[] SUBCLASSES = BattleAction.class.getDeclaredClasses();

    private BattleAction() {
    }

    public abstract void apply(ClientBattleImpl battle, World world);

    public void toBuf(final PacketByteBuf buf) {
        buf.writeVarInt(getIndexOfInnerClass(getClass()));
        encode(buf);
    }

    protected abstract void encode(PacketByteBuf buf);

    public IntSet getAddedEntities() {
        return IntSets.EMPTY_SET;
    }

    public IntSet getRemovedEntities() {
        return IntSets.EMPTY_SET;
    }

    public static BattleAction createEntityJoinAction(final Entity entity) {
        return new EntityJoinAction(entity.getId());
    }

    private static class EntityJoinAction extends BattleAction {
        private final int id;

        private EntityJoinAction(final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public void apply(final ClientBattleImpl battle, final World world) {
            final BattleEntity entity = (BattleEntity) world.getEntityById(id);
            if (entity != null) {
                battle.addParticipant(entity);
            } else {
                throw new RuntimeException("Can not add unloaded entity to a battle");
            }
        }

        @Override
        protected void encode(final PacketByteBuf buf) {
            buf.writeVarInt(id);
        }

        @Override
        public IntSet getAddedEntities() {
            return IntSets.singleton(id);
        }

        public static EntityJoinAction fromBuf(final PacketByteBuf buf) {
            return new EntityJoinAction(buf.readVarInt());
        }
    }

    public static BattleAction createEntityLeaveAction(final Entity entity, final EntityLeaveAction.Reason reason) {
        return new EntityLeaveAction(entity.getId(), reason);
    }

    private static class EntityLeaveAction extends BattleAction {
        private final int id;
        private final Reason reason;

        public EntityLeaveAction(final int id, final Reason reason) {
            this.id = id;
            this.reason = reason;
        }

        @Override
        public void apply(final ClientBattleImpl battle, final World world) {
            if (!battle.remove(id)) {
                throw new RuntimeException("Cant removed already removed entity");
            }
        }

        @Override
        protected void encode(final PacketByteBuf buf) {
            buf.writeVarInt(id);
            buf.writeEnumConstant(reason);
        }

        @Override
        public IntSet getRemovedEntities() {
            return IntSets.singleton(id);
        }

        public Reason getReason() {
            return reason;
        }

        public static BattleAction fromBuf(final PacketByteBuf buf) {
            return new EntityLeaveAction(buf.readVarInt(), buf.readEnumConstant(Reason.class));
        }

        public enum Reason {
            DIED,
            LEFT
        }
    }

    public static BattleAction createEndBattleAction() {
        return EndBattleAction.INSTANCE;
    }

    private static class EndBattleAction extends BattleAction {
        private static final EndBattleAction INSTANCE = new EndBattleAction();

        private EndBattleAction() {
        }

        @Override
        public void apply(final ClientBattleImpl battle, final World world) {
            final ClientBattleWorldComponent battleWorld = (ClientBattleWorldComponent) Components.BATTLE_WORLD_COMPONENT_KEY.get(world);
            battleWorld.removeBattle(battle.getHandle());
        }

        @Override
        protected void encode(final PacketByteBuf buf) {

        }

        public static BattleAction fromBuf(final PacketByteBuf buf) {
            return INSTANCE;
        }
    }

    public static BattleAction createNextTurnAction(final int prevId, final int currId) {
        return new NextTurnAction(prevId, currId);
    }

    private static class NextTurnAction extends BattleAction {
        private final int prevId;
        private final int currId;

        private NextTurnAction(final int prevId, final int currId) {
            this.prevId = prevId;
            this.currId = currId;
        }

        @Override
        public void apply(final ClientBattleImpl battle, final World world) {
            final BattleEntity prev = battle.getCurrentTurnEntity();
            if (((Entity) prev).getId() != prevId) {
                throw new RuntimeException("de-sync");
            }
            battle.setCurrentTurn(currId);
        }

        @Override
        protected void encode(final PacketByteBuf buf) {
            buf.writeVarInt(prevId);
            buf.writeVarInt(currId);
        }

        public static BattleAction fromBuf(final PacketByteBuf buf) {
            return new NextTurnAction(buf.readVarInt(), buf.readVarInt());
        }
    }

    private static int getIndexOfInnerClass(final Class<? extends BattleAction> clazz) {
        for (int i = 0; i < SUBCLASSES.length; i++) {
            if (SUBCLASSES[i] == clazz) {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }

    public static BattleAction fromBuf(final PacketByteBuf buf) {
        final int index = buf.readVarInt();
        final Class<?> clazz = BattleAction.class.getDeclaredClasses()[index];
        if (clazz.getSuperclass() == BattleAction.class) {
            final Function<PacketByteBuf, BattleAction> function = CACHE.computeIfAbsent((Class<? extends BattleAction>) clazz, c -> {
                final Method fromBuf;
                try {
                    fromBuf = c.getDeclaredMethod("fromBuf", PacketByteBuf.class);
                } catch (final NoSuchMethodException e) {
                    throw new RuntimeException("BattleAction does not implement fromBuf");
                }
                if (!Modifier.isStatic(fromBuf.getModifiers())) {
                    throw new RuntimeException("fromBuf not static");
                }
                if (!fromBuf.isAccessible()) {
                    fromBuf.setAccessible(true);
                }
                return b -> {
                    try {
                        return (BattleAction) fromBuf.invoke(null, b);
                    } catch (final IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("BattleAction does not implement fromBuf");
                    }
                };
            });
            return function.apply(buf);
        } else {
            throw new RuntimeException("BattleAction inner class that does not extend BattleAction");
        }
    }
}
