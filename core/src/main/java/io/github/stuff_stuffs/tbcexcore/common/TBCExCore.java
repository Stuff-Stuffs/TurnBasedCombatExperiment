package io.github.stuff_stuffs.tbcexcore.common;

import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleActionRegistry;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamageType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.component.BattleComponents;
import io.github.stuff_stuffs.tbcexcore.common.network.Network;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class TBCExCore implements ModInitializer {
    public static final String MOD_ID = "tbcexcore";
    public static final Logger LOGGER = LoggerUtil.LOGGER;
    private static final Map<BattleEquipmentSlot, Function<PlayerEntity, @Nullable ItemStack>> PLAYER_EXTRACTORS = new Reference2ObjectOpenHashMap<>();

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        Network.init();
        BattleDamageType.init();
        BattleActionRegistry.init();
        BattleParticipantStat.init();
        ParticipantComponents.init();
        BattleComponents.init();
    }

    public static void registerPlayerExtractor(final BattleEquipmentSlot slot, final Function<PlayerEntity, @Nullable ItemStack> extractor) {
        if (PLAYER_EXTRACTORS.put(slot, extractor) != null) {
            throw new TBCExException();
        }
    }

    public static @Nullable Function<PlayerEntity, @Nullable ItemStack> getPlayerExtractor(final BattleEquipmentSlot slot) {
        return PLAYER_EXTRACTORS.get(slot);
    }
}
