package com.github.capitalistspz.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ObjectiveArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DamageCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        // Damage command
        LiteralArgumentBuilder<ServerCommandSource> damageArgBuilder = literal("damage")
                .requires(executor -> executor.hasPermissionLevel(2));

        // Damage command definition scope
        {
            damageArgBuilder.then(literal("entity")
                    .then(argument("target", EntityArgumentType.entity()) // Selector arg
                            .then(literal("value")
                                    .then(argument("damageAmount", FloatArgumentType.floatArg(0.0f)) // Take in damage parameter
                                            .executes(cmd ->
                                                    executeDamage(cmd, EntityArgumentType.getEntity(cmd, "target"),FloatArgumentType.getFloat(cmd,"damageAmount")))))
                            .then(literal("score")
                                    .then(argument("source", ScoreHolderArgumentType.scoreHolder())
                                            .then(argument("sourceObjective", ObjectiveArgumentType.objective())
                                                    .then(argument("scale", FloatArgumentType.floatArg())
                                                            .executes(cmd->{
                                                                World world = cmd.getSource().getWorld();
                                                                ScoreboardPlayerScore score = world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"source"),ObjectiveArgumentType.getObjective(cmd,"sourceObjective"));
                                                                float val = score.getScore() * FloatArgumentType.getFloat(cmd,"scale");
                                                                executeDamage(cmd,EntityArgumentType.getEntity(cmd, "target"),val);
                                                                return Command.SINGLE_SUCCESS; })))))));
        }
        dispatcher.register(damageArgBuilder);

    }
    public static int executeDamage(final CommandContext<ServerCommandSource> context, final Entity ent, float damage)
    {
        // Entities with no health cause errors if damage function is used
        ServerCommandSource source = context.getSource();
        if (!(ent instanceof LivingEntity)){
            source.sendFeedback(new LiteralText("Invalid entity."),false);
            return 0;
        }
        else {
            ent.damage(DamageSource.GENERIC, damage);
            String out ="Entity " + ent.getEntityName() + " damaged for " + damage;
            source.sendFeedback(new LiteralText(out),false);
            return Command.SINGLE_SUCCESS;
        }

    }
}
