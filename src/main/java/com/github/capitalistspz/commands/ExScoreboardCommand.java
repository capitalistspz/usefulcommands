package com.github.capitalistspz.commands;

import com.github.capitalistspz.utils.CommandUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ExScoreboardCommand {
    public static void register (CommandDispatcher<ServerCommandSource> dispatcher) {
        // Scoreboard Operation command
        LiteralArgumentBuilder<ServerCommandSource> scoreboardMaths = literal("sb_operation")
                .requires(executor -> executor.hasPermissionLevel(2));

        // Scoreboard Operation command definition scope
        {
            scoreboardMaths.then(argument("target", ScoreHolderArgumentType.scoreHolder()).then(argument("targetObjective", ScoreboardObjectiveArgumentType.scoreboardObjective())
                    .then(literal("pow")
                            .then(argument("source",ScoreHolderArgumentType.scoreHolder())
                                    .then(argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective())
                                            .executes(cmd->{
                                                World world = cmd.getSource().getWorld();
                                                if (world == null){
                                                    cmd.getSource().sendFeedback(new LiteralText("Failure: This command must be executed in a world."),false);
                                                    return CommandUtils.SINGLE_FAIL;
                                                }
                                                ScoreboardPlayerScore targetScore = world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"target"),ScoreboardObjectiveArgumentType.getObjective(cmd,"targetObjective"));
                                                ScoreboardPlayerScore sourceScore =  world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"source"),ScoreboardObjectiveArgumentType.getObjective(cmd,"sourceObjective"));
                                                targetScore.setScore(powScore(targetScore,sourceScore));
                                                return Command.SINGLE_SUCCESS; }))))
                    .then(literal("sqrt")
                            .executes(cmd->{
                                World world = cmd.getSource().getWorld();
                                if (world == null){
                                    cmd.getSource().sendFeedback(new LiteralText("Failure: This command must be executed in a world."),false);
                                    return CommandUtils.SINGLE_FAIL;
                                }
                                ScoreboardPlayerScore targetScore = world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"target"),ScoreboardObjectiveArgumentType.getObjective(cmd,"targetObjective"));
                                targetScore.setScore((int)Math.sqrt(targetScore.getScore()));
                                return Command.SINGLE_SUCCESS;
                            }))));
        }
        dispatcher.register(scoreboardMaths);
    }
    public static int powScore(ScoreboardPlayerScore targetScore,final ScoreboardPlayerScore sourceScore){
        int trg = targetScore.getScore();
        int total = 1;
        for(int i = 0; i < sourceScore.getScore(); ++i)
            total *= trg;
        return total;
    }
}
