package io.github.stuff_stuffs.turnbasedcombat.mixin.impl;

import com.mojang.authlib.GameProfile;
import io.github.stuff_stuffs.turnbasedcombat.client.battle.data.ClientBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.ClientPlayerExt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements ClientPlayerExt {
    @Shadow
    @Final
    protected MinecraftClient client;

    @Override
    @Shadow
    public abstract void sendMessage(Text message, boolean actionBar);

    @Unique
    private UUID lastTurn = null;

    @Unique
    private boolean currentTurn = false;

    public MixinClientPlayerEntity(final ClientWorld world, final GameProfile profile) {
        super(world, profile);
    }

    @Override
    public boolean tbcex_isCurrentTurn() {
        final Battle battle = ClientBattleWorld.get(client.world).getBattle((BattleEntity) this);
        if (battle == null) {
            return false;
        }
        return currentTurn;
    }

    @Override
    public void tbcex_setCurrentTurn(final boolean currentTurn) {
        this.currentTurn = currentTurn;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickInject(final CallbackInfo ci) {
        final Battle battle = ClientBattleWorld.get(client.world).getBattle((BattleEntity) this);
        if (battle != null) {
            if (!battle.getStateView().isBattleEnded()) {
                final UUID uuid = battle.getStateView().getCurrentTurn().getId();
                if (!uuid.equals(lastTurn)) {
                    lastTurn = uuid;
                    if (lastTurn.equals(getUuid())) {
                        sendMessage(new LiteralText("Your turn"), false);
                    } else {
                        sendMessage(new LiteralText("" + lastTurn + "'s turn"), false);
                    }
                }
            }
        } else {
            lastTurn = null;
        }
    }
}
