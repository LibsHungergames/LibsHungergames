package me.libraryaddict.Hungergames.Types;

public class Damage {
    private long time;
    private Gamer damager;

    public Damage(long time, Gamer damager) {
        this.damager = damager;
        this.time = time;
    }

    public Gamer getDamager() {
        return damager;
    }

    public long getTime() {
        return time;
    }

}
