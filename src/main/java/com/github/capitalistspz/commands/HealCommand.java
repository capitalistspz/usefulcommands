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
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HealCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        // Heal command
        LiteralArgumentBuilder<ServerCommandSource> healArgBuilder = literal("heal")
                .requires(executor -> executor.hasPermissionLevel(2));

        // Heal command definition scope
        {
            healArgBuilder.then(literal("entity")
                    .then(argument("target", EntityArgumentType.entity()) // Selector arg
                            .then(literal("value")
                                    .then(argument("healAmount", FloatArgumentType.floatArg(0.0f)) // Take in heal parameter
                                            .executes(cmd ->
                                                    executeHeal(cmd, EntityArgumentType.getEntity(cmd, "target"),FloatArgumentType.getFloat(cmd,"healAmount")))))
                            .then(literal("score")
                                    .then(argument("source", ScoreHolderArgumentType.scoreHolder())
                                            .then(argument("sourceObjective", ObjectiveArgumentType.objective())
                                                    .then(argument("scale", FloatArgumentType.floatArg())
                                                            .executes(cmd->{
                                                                World world = cmd.getSource().getWorld();
                                                                ScoreboardPlayerScore score = world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"source"),ObjectiveArgumentType.getObjective(cmd,"sourceObjective"));
                                                                float val = score.getScore() * FloatArgumentType.getFloat(cmd,"scale");
                                                                executeHeal(cmd,EntityArgumentType.getEntity(cmd, "target"),val);
                                                                return Command.SINGLE_SUCCESS; })))))));
        }
        dispatcher.register(healArgBuilder);
    }
    public static int executeHeal(final CommandContext<ServerCommandSource> context, final Entity ent, float heal) // Same as damageEnt but heals.
    {
        ServerCommandSource source = context.getSource();
        if (!(ent instanceof LivingEntity)){
            source.sendFeedback(new LiteralText("Invalid entity"),false);
            return 0;
        }
        else {
            ((LivingEntity) ent).heal(heal);
            String out = "Entity " + ent.getEntityName() + " healed for " + heal;
            source.sendFeedback(new LiteralText(out),false);
            return Command.SINGLE_SUCCESS;
        }
    }
}
