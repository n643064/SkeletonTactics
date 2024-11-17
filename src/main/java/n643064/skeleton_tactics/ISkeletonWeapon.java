package n643064.skeleton_tactics;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ISkeletonWeaponGetter
{
    @NotNull ItemStack getRanged();
    @NotNull ItemStack getMelee();
}
