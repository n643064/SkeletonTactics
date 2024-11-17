package n643064.skeleton_tactics.mixin;

import n643064.skeleton_tactics.Config;
import n643064.skeleton_tactics.ISkeletonWeapon;
import n643064.skeleton_tactics.SkeletonAttackGoal;
import n643064.skeleton_tactics.UpdateWeaponGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(AbstractSkeleton.class)
public abstract class SkeletonMixin extends Monster implements ISkeletonWeapon
{
    @Shadow @ParametersAreNonnullByDefault
    public abstract void setItemSlot(EquipmentSlot slot, ItemStack stack);
    @Unique private ItemStack skeletonTactics$backup = new ItemStack(Config.getForEntity(getType()).ranged());

    protected SkeletonMixin(EntityType<? extends Monster> entityType, Level level)
    {
        super(entityType, level);
    }

    @SuppressWarnings("all")
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci)
    {
        final Config.CachedEntry e = Config.getForEntity(getType());
        this.goalSelector.addGoal(3, new UpdateWeaponGoal<>((AbstractSkeleton & ISkeletonWeapon) (Object) this, e.distance(), e.dontShootShields()));
        this.goalSelector.addGoal(4, new SkeletonAttackGoal((AbstractSkeleton) (Object) this,
                e.meleeFollowAfterLosingLineOfSight(),
                e.rangedSpeedMod(),
                e.rangedAttackInterval(),
                e.rangedRadiusSqrt(),
                e.meleeSpeedMod()
        ));
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir)
    {
        if (getMainHandItem().isEmpty())
        {
            setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Config.getForEntity(getType()).melee()));
            skeletonTactics$backup = new ItemStack(Config.getForEntity(getType()).ranged());
        } else if ((getMainHandItem().getItem() instanceof BowItem))
            skeletonTactics$backup = new ItemStack(Config.getForEntity(getType()).melee());
        else
            skeletonTactics$backup = new ItemStack(Config.getForEntity(getType()).ranged());

    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(CompoundTag compound, CallbackInfo ci)
    {
        Tag t = compound.get("skeleton_backup_stack");
        if (t != null)
            skeletonTactics$backup = ItemStack.parse(this.registryAccess(), t).orElseGet(() ->
            {
                if (getMainHandItem().getItem() instanceof BowItem)
                    return new ItemStack(Config.getForEntity(getType()).melee());
                else
                    return new ItemStack(Config.getForEntity(getType()).ranged());
            });
    }

    /**
     * @author me
     * @reason yes
     */
    @Overwrite
    public void reassessWeaponGoal()
    {

    }

    @Override
    @NotNull
    public ItemStack skeletonTactics$getCurrent()
    {
        return getMainHandItem();
    }

    @Override
    @NotNull
    public ItemStack skeletonTactics$getBackup()
    {
        return skeletonTactics$backup;
    }

    @Override
    public void skeletonTactics$swap()
    {
        final ItemStack a = getMainHandItem();
        setItemSlot(EquipmentSlot.MAINHAND, skeletonTactics$backup);
        skeletonTactics$backup = a;
    }
}
