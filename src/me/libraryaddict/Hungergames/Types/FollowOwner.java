package me.libraryaddict.Hungergames.Types;

import net.minecraft.server.v1_5_R2.EntityLiving;
import net.minecraft.server.v1_5_R2.MathHelper;
import net.minecraft.server.v1_5_R2.Navigation;
import net.minecraft.server.v1_5_R2.PathfinderGoal;
import net.minecraft.server.v1_5_R2.World;

public class FollowOwner extends PathfinderGoal {
    private EntityLiving d;
    private EntityLiving e;
    World a;
    private float f;
    private Navigation g;
    private int h;
    float b;
    float c;

    public FollowOwner(EntityLiving paramEntityTameableAnimal, float paramFloat1, float paramFloat2, float paramFloat3,
            EntityLiving owner) {
        this.d = paramEntityTameableAnimal;
        this.a = paramEntityTameableAnimal.world;
        this.f = paramFloat1;
        this.g = paramEntityTameableAnimal.getNavigation();
        this.c = paramFloat2;
        this.b = paramFloat3;
        this.e = owner;
        a(3);
    }

    public boolean a() {
        if (this.d.e(e) < this.c * this.c)
            return false;
        return true;
    }

    public boolean b() {
        return (!this.g.f()) && (this.d.e(this.e) > this.b * this.b);
    }

    public void c() {
        this.h = 0;
        this.d.getNavigation().a(false);
    }

    public void d() {
        return;
        /*
         * this.e = null; this.g.g(); this.d.getNavigation().a(this.i);
         */
    }

    public void e() {
        this.d.getControllerLook().a(this.e, 10.0F, this.d.bs());

        if (--this.h > 0)
            return;
        this.h = 10;

        if (this.g.a(this.e, this.f))
            return;
        if (this.d.e(this.e) < 144.0D)
            return;

        int j = MathHelper.floor(this.e.locX) - 2;
        int k = MathHelper.floor(this.e.locZ) - 2;
        int m = MathHelper.floor(this.e.boundingBox.b);
        for (int n = 0; n <= 4; n++)
            for (int i1 = 0; i1 <= 4; i1++)
                if ((n < 1) || (i1 < 1) || (n > 3) || (i1 > 3)) {
                    if ((this.a.w(j + n, m - 1, k + i1)) && (!this.a.u(j + n, m, k + i1)) && (!this.a.u(j + n, m + 1, k + i1))) {
                        this.d.setPositionRotation(j + n + 0.5F, m, k + i1 + 0.5F, this.d.yaw, this.d.pitch);
                        this.g.g();
                        return;
                    }
                }
    }
}