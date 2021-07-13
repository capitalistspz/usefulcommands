package com.github.capitalistspz.mixin;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.EntityDataObject;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(EntityDataObject.class)
public abstract class EntityDataObjectMxn {
    @Shadow
    @Final
    private static SimpleCommandExceptionType INVALID_ENTITY_EXCEPTION;
    @Final
    @Shadow
    private Entity entity;
    /**
     * @author capitalistspz
     * @reason Allow modification of all player NBT apart from UUID
     *
     */
    @Overwrite
    public void setNbt(NbtCompound nbt){
        UUID uUID = this.entity.getUuid();
        this.entity.readNbt(nbt);
        this.entity.setUuid(uUID);
    }
}
