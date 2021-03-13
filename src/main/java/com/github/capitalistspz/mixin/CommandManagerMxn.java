package com.github.capitalistspz.mixin;

import com.github.capitalistspz.commands.DamageCommand;
import com.github.capitalistspz.commands.ExScoreboardCommand;
import com.github.capitalistspz.commands.HealCommand;
import com.github.capitalistspz.commands.VelocityCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public abstract class CommandManagerMxn {
    @Shadow @Final
    private CommandDispatcher<ServerCommandSource> dispatcher;
    @Inject(method = "<init>",at = @At("TAIL"))
    void addCommands(CallbackInfo ci){
        DamageCommand.register(this.dispatcher);
        ExScoreboardCommand.register(this.dispatcher);
        HealCommand.register(this.dispatcher);
        VelocityCommand.register(this.dispatcher);
    }
}
