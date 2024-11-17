package n643064.skeleton_tactics;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.BowItem;

public class UpdateWeaponGoal<T extends AbstractSkeleton & ISkeletonWeapon> extends Goal
{
    private final T skeleton;
    private final double distance;
    private final boolean dontShootShields;
    public UpdateWeaponGoal(T skeleton, double distance, boolean dontShootShields)
    {
        this.skeleton = skeleton;
        this.distance = distance;
        this.dontShootShields = dontShootShields;
    }

    @Override
    public boolean canUse()
    {
        final LivingEntity target = skeleton.getTarget();
        if (target == null) return false;
        double d = skeleton.distanceTo(target);
        return skeleton.getMainHandItem().getItem() instanceof BowItem ? ((d <= distance) || (dontShootShields && target.isBlocking())) : (d > distance) && !(dontShootShields && target.isBlocking());
    }


    @Override
    public boolean canContinueToUse()
    {
        return false;
    }

    @Override
    public void start()
    {
        skeleton.skeletonTactics$swap();
        skeleton.getNavigation().stop();
    }
}
