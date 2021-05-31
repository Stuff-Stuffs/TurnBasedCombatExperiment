package io.github.stuff_stuffs.turnbasedcombat.mixin.impl;

import com.mojang.authlib.GameProfile;
import io.github.stuff_stuffs.turnbasedcombat.client.battle.data.ClientBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
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
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    @Shadow @Final protected MinecraftClient client;

    @Shadow public abstract void sendMessage(Text message, boolean actionBar);

    @Unique
    private UUID lastTurn = null;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }


    @Inject(method = "tick", at = @At("HEAD"))
    private void tickInject(CallbackInfo ci) {
        Battle battle = ClientBattleWorld.get(this.client.world).getBattle((BattleEntity) this);
        if(battle!=null) {
            if (!battle.getStateView().isBattleEnded()) {
                final UUID uuid = battle.getStateView().getCurrentTurn().getId();
                if(!uuid.equals(lastTurn)) {
                    lastTurn = uuid;
                    if(lastTurn.equals(this.getUuid())) {
                        this.sendMessage(new LiteralText("Your turn"), false);
                    } else {
                        this.sendMessage(new LiteralText("" + lastTurn +"'s turn"), false);
                    }
                }
            }
        } else {
            lastTurn = null;
        }
    }
}
