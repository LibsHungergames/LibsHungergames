package me.libraryaddict.Hungergames.Types;

import org.bukkit.craftbukkit.v1_5_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_5_R2.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import net.minecraft.server.v1_5_R2.EntityCreature;
import net.minecraft.server.v1_5_R2.EntityHuman;
import net.minecraft.server.v1_5_R2.EntityLiving;
import net.minecraft.server.v1_5_R2.EntityTameableAnimal;
import net.minecraft.server.v1_5_R2.MathHelper;
import net.minecraft.server.v1_5_R2.PathEntity;
import net.minecraft.server.v1_5_R2.PathPoint;
import net.minecraft.server.v1_5_R2.PathfinderGoalTarget;

public class OwnerAttacks extends PathfinderGoalTarget {
    EntityLiving d;
    EntityLiving idk;
    EntityLiving owner;
    protected float e;
    protected boolean f;
    private boolean a;
    private int b;
    private int c;
    private int g;

    public OwnerAttacks(EntityLiving paramEntityTameableAnimal, EntityLiving owner) {
        super(paramEntityTameableAnimal, 32.0F, false);
        this.d = paramEntityTameableAnimal;
        this.owner = owner;
        a(1);
    }

    public boolean a() {
        if (owner == null)
            return false;
        this.idk = owner.aG();
        return a(this.idk, false);
    }

    public void c() {
        this.d.setGoalTarget(this.idk);
        super.c();
    }

    protected boolean a(EntityLiving entityliving, boolean flag) {
        if (entityliving == null)
            return false;
        if (entityliving == this.d)
            return false;
        if (!entityliving.isAlive())
            return false;
        if (!this.d.a(entityliving.getClass())) {
            return false;
        }
        if (((this.d instanceof EntityTameableAnimal)) && (((EntityTameableAnimal) this.d).isTamed())) {
            if (((entityliving instanceof EntityTameableAnimal)) && (((EntityTameableAnimal) entityliving).isTamed())) {
                System.out.print("sd");
                return false;
                // If they share the same owner
            }

            if (entityliving == ((EntityTameableAnimal) this.d).getOwner()) {
                System.out.print("sd");
                return false;
            }
        } else if (((entityliving instanceof EntityHuman)) && (!flag) && (((EntityHuman) entityliving).abilities.isInvulnerable)) {
            return false;
        }

        if (!this.d.d(MathHelper.floor(entityliving.locX), MathHelper.floor(entityliving.locY),
                MathHelper.floor(entityliving.locZ)))
            return false;
        if ((this.f) && (!this.d.aD().canSee(entityliving))) {
            return false;
        }
        if (this.a) {
            if (--this.c <= 0) {
                this.b = 0;
            }

            if (this.b == 0) {
                this.b = (a(entityliving) ? 1 : 2);
            }

            if (this.b == 2) {
                return false;
            }

        }

        EntityTargetEvent.TargetReason reason = EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER;

        EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(this.d, entityliving, reason);
        if ((event.isCancelled()) || (event.getTarget() == null)) {
            this.d.setGoalTarget(null);
            return false;
        }
        if (entityliving.getBukkitEntity() != event.getTarget()) {
            this.d.setGoalTarget((EntityLiving) ((CraftEntity) event.getTarget()).getHandle());
        }
        if ((this.d instanceof EntityCreature)) {
            ((EntityCreature) this.d).target = ((CraftEntity) event.getTarget()).getHandle();
        }

        return true;
    }

    private boolean a(EntityLiving entityliving) {
        this.c = (10 + this.d.aE().nextInt(5));
        PathEntity pathentity = this.d.getNavigation().a(entityliving);

        if (pathentity == null) {
            return false;
        }
        PathPoint pathpoint = pathentity.c();

        if (pathpoint == null) {
            return false;
        }
        int i = pathpoint.a - MathHelper.floor(entityliving.locX);
        int j = pathpoint.c - MathHelper.floor(entityliving.locZ);

        return i * i + j * j <= 2.25D;
    }
}