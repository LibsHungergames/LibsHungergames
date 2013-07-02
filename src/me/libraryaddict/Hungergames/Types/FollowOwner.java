package me.libraryaddict.Hungergames.Types;

import net.minecraft.server.v1_6_R1.EntityInsentient;
import net.minecraft.server.v1_6_R1.EntityLiving;
import net.minecraft.server.v1_6_R1.MathHelper;
import net.minecraft.server.v1_6_R1.Navigation;
import net.minecraft.server.v1_6_R1.PathfinderGoal;
import net.minecraft.server.v1_6_R1.World;

public class FollowOwner extends PathfinderGoal {
    private EntityInsentient d;
    private EntityLiving e;
    World a;
    private double f;
    private Navigation g;
    private int h;
    float b;
    float c;
    private boolean i;

    public FollowOwner(EntityInsentient paramEntityTameableAnimal, double paramDouble, float paramFloat1, float paramFloat2) {
        this.d = paramEntityTameableAnimal;
        this.a = paramEntityTameableAnimal.world;
        this.f = paramDouble;
        this.g = paramEntityTameableAnimal.getNavigation();
        this.c = paramFloat1;
        this.b = paramFloat2;
        a(3);
    }

    public boolean a() {
        if (this.d.e(e) < this.c * this.c)
            return false;
        return true;
    }

    public boolean b() {
        return (!this.g.g()) && (this.d.e(this.e) > this.b * this.b);
    }

    public void c() {
        this.h = 0;
        this.i = g.a();
        g.a(false);
    }

    public void d() {
        this.e = null;
        this.g.h();
        g.a(this.i);
    }

    public void e() {
        this.d.getControllerLook().a(this.e, 10.0F, this.d.bl());
        if (--this.h > 0)
            return;
        this.h = 10;

        if (this.g.a(this.e, this.f))
            return;
        if (this.d.bD())
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
                        this.g.h();
                        return;
                    }
                }
    }
}