package n643064.skeleton_tactics;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ISkeletonWeapon
{
    @NotNull ItemStack skeletonTactics$getCurrent();
    @NotNull ItemStack skeletonTactics$getBackup();
    void skeletonTactics$swap();
}
