package io.github.stuff_stuffs.tbcexutil.common.path;

import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.WorldShapeCache;
import net.minecraft.world.World;

import java.util.Iterator;

public interface PathProcessor {
    double getCost(Iterator<Movement> reverseIterator, BattleParticipantBounds bounds, World world, WorldShapeCache cache);

    static PathProcessor fallDamageProcessor(final double minDamageHeight, final double fallCostPerBlock) {
        return (reverseIterator, bounds, world, cache) -> {
            double sum = 0;
            if (reverseIterator.hasNext()) {
                Movement movement = reverseIterator.next();
                while (reverseIterator.hasNext()) {
                    int fallHeight = 0;
                    boolean takeDamage = true;
                    while ((movement.getFlags().contains(MovementFlag.FALL) || movement.getFlags().contains(MovementFlag.FALL_RESET) || movement.getFlags().contains(MovementFlag.FALL_RESET_TAKE_DAMAGE)) && reverseIterator.hasNext()) {
                        if (movement.getFlags().contains(MovementFlag.FALL)) {
                            fallHeight++;
                        }
                        if (movement.getFlags().contains(MovementFlag.FALL_RESET) || movement.getFlags().contains(MovementFlag.FALL_RESET_TAKE_DAMAGE)) {
                            if (movement.getFlags().contains(MovementFlag.FALL_RESET)) {
                                takeDamage = false;
                            }
                            break;
                        }
                        movement = reverseIterator.next();
                    }
                    if (!reverseIterator.hasNext()) {
                        if (movement.getFlags().contains(MovementFlag.FALL)) {
                            fallHeight++;
                        }
                        if (movement.getFlags().contains(MovementFlag.FALL_RESET)) {
                            takeDamage = false;
                        }
                    } else {
                        movement = reverseIterator.next();
                    }
                    final double damageHeight = fallHeight - minDamageHeight;
                    if (takeDamage && damageHeight > 0) {
                        sum += fallCostPerBlock * damageHeight;
                    }
                }
            }
            return sum;
        };
    }
}
