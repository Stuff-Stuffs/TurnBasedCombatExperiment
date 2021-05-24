package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity;

public final class EntityState {
    private final SkillInfo info;
    private int health;

    public EntityState(final SkillInfo info) {
        this.info = info;
        health = info.health;
    }

    public void heal(final int amount) {
        health = Math.min(health + amount, info.maxHealth);
    }

    public void damage(final int amount) {
        health = Math.max(health - amount, 0);
    }

    public int getHealth() {
        return health;
    }

    public int getLevel() {
        return info.level;
    }

    public int getMaxHealth() {
        return info.maxHealth;
    }
}
