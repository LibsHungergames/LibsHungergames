package me.libraryaddict.Hungergames.Types;

public class Damage {
    private Gamer damager;
    private long time;

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
