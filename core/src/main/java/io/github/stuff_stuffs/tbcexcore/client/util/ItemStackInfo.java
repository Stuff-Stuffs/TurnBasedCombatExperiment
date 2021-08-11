package io.github.stuff_stuffs.tbcexcore.client.util;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;

public final class ItemStackInfo {
    public final Either<BattleParticipantInventoryHandle, Pair<BattleParticipantHandle, BattleEquipmentSlot>> location;
    public final BattleParticipantItemStack stack;

    public ItemStackInfo(final BattleParticipantInventoryHandle handle, final BattleParticipantItemStack stack) {
        location = Either.left(handle);
        this.stack = stack;
    }

    public ItemStackInfo(final BattleParticipantHandle handle, final BattleEquipmentSlot slot, final BattleParticipantItemStack stack) {
        location = Either.right(Pair.of(handle, slot));
        this.stack = stack;
    }
}
