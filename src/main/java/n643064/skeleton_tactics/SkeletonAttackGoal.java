package n643064.skeleton_tactics;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.BowItem;

public class SkeletonAttackGoal extends Goal
{
    private final RangedBowAttackGoal<AbstractSkeleton> rangedAttackGoal;
    private final MeleeAttackGoal meleeAttackGoal;
    private final AbstractSkeleton skeleton;

    public SkeletonAttackGoal(AbstractSkeleton skeleton, boolean follow, double rangedSpeedMod, int rangedAttackInterval, float rangedRadiusSqrt, double meleeSpeedMod)
    {
        rangedAttackGoal = new RangedBowAttackGoal<>(skeleton, rangedSpeedMod, rangedAttackInterval, rangedRadiusSqrt);
        meleeAttackGoal = new MeleeAttackGoal(skeleton, meleeSpeedMod, follow)
        {
            public void stop()
            {
                super.stop();
                skeleton.setAggressive(false);
            }

            public void start()
            {
                super.start();
                skeleton.setAggressive(true);
            }
        };
        this.skeleton = skeleton;
    }

    boolean isBow()
    {
        return skeleton.getMainHandItem().getItem() instanceof BowItem;
    }

    @Override
    public boolean canUse()
    {
        return isBow() ? rangedAttackGoal.canUse() : meleeAttackGoal.canUse();
    }

    @Override
    public boolean canContinueToUse()
    {
        return isBow() ? rangedAttackGoal.canContinueToUse() : meleeAttackGoal.canContinueToUse();
    }

    @Override
    public void start()
    {
        if (isBow())
            rangedAttackGoal.start();
        else
            meleeAttackGoal.start();
    }

    @Override
    public void tick()
    {
        if (isBow())
            rangedAttackGoal.tick();
        else
            meleeAttackGoal.tick();

    }

    @Override
    public void stop()
    {
        if (isBow())
            rangedAttackGoal.stop();
        else
            meleeAttackGoal.stop();
    }
}
