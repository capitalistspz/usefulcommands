package com.github.capitalistspz.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMxn extends LivingEntity {
    protected PlayerEntityMxn(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    @Inject(method="readCustomDataFromNbt", locals = LocalCapture.CAPTURE_FAILSOFT, at=@At("TAIL"))
    void markVelocityChangedFromTag(NbtCompound nbt, CallbackInfo ci){
        NbtList motion = nbt.getList("Motion", 6);
        Vec3d mot = new Vec3d(motion.getDouble(0), motion.getDouble(1), motion.getDouble(2));
        if (mot.equals(this.getVelocity()))
            velocityModified = true;
    }
}
