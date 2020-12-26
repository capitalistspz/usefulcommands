package com.github.capitlistspz;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ObjectiveArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.text.DecimalFormat;
import static net.minecraft.server.command.CommandManager.*;

public class Commands {
    public static final int SINGLE_FAIL = 0;
    public static DecimalFormat df = new DecimalFormat("0.00");
    private static final Logger log = LogManager.getLogger("PZ");

    static public void init() {
        df.setMaximumFractionDigits(2);
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
                {
                    // Damage command
                    LiteralArgumentBuilder<ServerCommandSource> damageArgBuilder = literal("damage")
                            .requires(executor -> executor.hasPermissionLevel(2));

                    // Damage command definition scope
                    {
                        damageArgBuilder.then(literal("entity")
                                .then(argument("target", EntityArgumentType.entity()) // Selector arg
                                        .then(literal("value")
                                                .then(argument("damageAmount",FloatArgumentType.floatArg(0.0f)) // Take in damage parameter
                                                        .executes(cmd ->
                                                                damageEnt(cmd, EntityArgumentType.getEntity(cmd, "target"),FloatArgumentType.getFloat(cmd,"damageAmount"))
                                                        )
                                                )
                                        )
                                        .then(literal("score")
                                                .then(argument("source",ScoreHolderArgumentType.scoreHolder())
                                                        .then(argument("sourceObjective", ObjectiveArgumentType.objective())
                                                                .then(argument("scale", FloatArgumentType.floatArg())
                                                                        .executes(cmd->{
                                                                            World world = cmd.getSource().getWorld();
                                                                            ScoreboardPlayerScore score = world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"source"),ObjectiveArgumentType.getObjective(cmd,"sourceObjective"));
                                                                            float val = score.getScore() * FloatArgumentType.getFloat(cmd,"scale");
                                                                            damageEnt(cmd,EntityArgumentType.getEntity(cmd, "target"),val);
                                                                            return Command.SINGLE_SUCCESS;
                                                                        })
                                                                )
                                                        )
                                                )

                                        )
                                )
                        );
                        damageArgBuilder.then(literal("help").executes(cmd -> {
                            ServerCommandSource source = cmd.getSource();
                            source.sendFeedback(new LiteralText("§a========== Damage Help ==========\n" +
                                    "§6/damage entity <entity> value <damageAmount> \n" +
                                    "/damage entity <entity> score <source> <sourceObjective> <scale>\n" +
                                    "§fe.g.\n" +
                                    "§7/damage entity §b@e[type=!player,limit=1,sort=nearest] §7value §e5.3"),false);
                            return 1;
                        }));
                    }
                    dispatcher.register(damageArgBuilder);
                    log.info("Registered /damage");

                    // Heal command
                    LiteralArgumentBuilder<ServerCommandSource> healArgBuilder = literal("heal")
                            .requires(executor -> executor.hasPermissionLevel(2));

                    // Heal command definition scope
                    {
                        healArgBuilder.then(literal("entity")
                                .then(argument("target", EntityArgumentType.entity()) // Selector arg
                                        .then(literal("value")
                                                .then(argument("healAmount",FloatArgumentType.floatArg(0.0f)) // Take in heal parameter
                                                        .executes(cmd ->
                                                                healEnt(cmd, EntityArgumentType.getEntity(cmd, "target"),FloatArgumentType.getFloat(cmd,"healAmount"))
                                                        )
                                                )
                                        )
                                        .then(literal("score")
                                                .then(argument("source",ScoreHolderArgumentType.scoreHolder())
                                                        .then(argument("sourceObjective", ObjectiveArgumentType.objective())
                                                                .then(argument("scale", FloatArgumentType.floatArg())
                                                                        .executes(cmd->{
                                                                            World world = cmd.getSource().getWorld();
                                                                            ScoreboardPlayerScore score = world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"source"),ObjectiveArgumentType.getObjective(cmd,"sourceObjective"));
                                                                            float val = score.getScore() * FloatArgumentType.getFloat(cmd,"scale");
                                                                            healEnt(cmd,EntityArgumentType.getEntity(cmd, "target"),val);
                                                                            return Command.SINGLE_SUCCESS;
                                                                        })
                                                                )
                                                        )
                                                )

                                        )
                                )
                        );
                        healArgBuilder.then(literal("help").executes(cmd -> {
                            ServerCommandSource source = cmd.getSource();
                            source.sendFeedback(new LiteralText("§a========== Heal Help ==========\n" +
                                    "§6/heal entity <entity> value <healAmount> \n" +
                                    "/heal entity <entity> score <source> <sourceObjective> <scale> \n" +
                                    "§fe.g.\n" +
                                    "§7/heal entity §b@e[type=!player,limit=1,sort=nearest] §7value §e5.3"),false);
                            return 1;
                        }));
                    }

                    dispatcher.register(healArgBuilder);
                    log.info("Registered /heal");

                    // Scoreboard Operation command
                    LiteralArgumentBuilder<ServerCommandSource> scoreboardMaths = literal("sb_operation")
                            .requires(executor -> executor.hasPermissionLevel(2));

                    // Scoreboard Operation command definition scope
                    {
                        scoreboardMaths.then(argument("target", ScoreHolderArgumentType.scoreHolder()).then(argument("targetObjective", ObjectiveArgumentType.objective())
                                .then(literal("pow")
                                        .then(argument("source",ScoreHolderArgumentType.scoreHolder())
                                                .then(argument("sourceObjective", ObjectiveArgumentType.objective())
                                                        .executes(cmd->{
                                                            World world = cmd.getSource().getWorld();
                                                            if (world == null){
                                                                cmd.getSource().sendFeedback(new LiteralText("Failure: This command must be executed in a world."),false);
                                                                return SINGLE_FAIL;
                                                            }
                                                            ScoreboardPlayerScore targetScore = world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"target"),ObjectiveArgumentType.getObjective(cmd,"targetObjective"));
                                                            ScoreboardPlayerScore sourceScore =  world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"source"),ObjectiveArgumentType.getObjective(cmd,"sourceObjective"));
                                                            targetScore.setScore(powScore(targetScore,sourceScore));
                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                        )
                                )
                                .then(literal("sqrt")
                                        .executes(cmd->{
                                            World world = cmd.getSource().getWorld();
                                            if (world == null){
                                                cmd.getSource().sendFeedback(new LiteralText("Failure: This command must be executed in a world."),false);
                                                return SINGLE_FAIL;
                                            }
                                            ScoreboardPlayerScore targetScore = world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"target"),ObjectiveArgumentType.getObjective(cmd,"targetObjective"));
                                            targetScore.setScore((int)Math.sqrt(targetScore.getScore()));
                                            return Command.SINGLE_SUCCESS;
                                        }))
                        ));
                    }
                    dispatcher.register(scoreboardMaths);
                    log.info("Registered /sb_operation");

                    // Velocity command
                    LiteralArgumentBuilder<ServerCommandSource> velocityCommand = literal("velocity")
                            .requires(executor -> executor.hasPermissionLevel(2));

                    // Velocity command definition scope
                    {
                        velocityCommand.then(literal("modify")
                                .then(argument("target",EntityArgumentType.entity())
                                        .then(literal("set")
                                                .then(literal("value")
                                                        .then(argument("motion", Vec3ArgumentType.vec3(false))
                                                                .executes(cmd->{
                                                                    Entity target  = EntityArgumentType.getEntity(cmd,"target");
                                                                    Vec3d vec = Vec3ArgumentType.getVec3(cmd,"motion");
                                                                    target.setVelocity(vec);
                                                                    target.velocityModified = true;
                                                                    return Command.SINGLE_SUCCESS;
                                                                })
                                                        )
                                                )
                                                .then(literal("from")
                                                        .then(argument("source",EntityArgumentType.entity())
                                                                .executes(cmd->{
                                                                    Entity target = EntityArgumentType.getEntity(cmd,"target");
                                                                    Entity source = EntityArgumentType.getEntity(cmd,"source");
                                                                    target.setVelocity(source.getVelocity());
                                                                    target.velocityModified = true;
                                                                    return Command.SINGLE_SUCCESS;
                                                                })
                                                        )
                                                )
                                        )
                                        .then(literal("add")
                                                .then(literal("value")
                                                        .then(argument("motion", Vec3ArgumentType.vec3(false))
                                                                .executes(cmd->{
                                                                    Entity target = EntityArgumentType.getEntity(cmd,"target");
                                                                    Vec3d vec = Vec3ArgumentType.getVec3(cmd,"motion");
                                                                    target.addVelocity(vec.x,vec.y,vec.z);
                                                                    target.velocityModified = true;
                                                                    return Command.SINGLE_SUCCESS;
                                                                })
                                                        )
                                                )
                                                .then(literal("from")
                                                        .then(argument("source",EntityArgumentType.entity())
                                                                .executes(cmd->{
                                                                    Entity target = EntityArgumentType.getEntity(cmd,"target");
                                                                    Entity source = EntityArgumentType.getEntity(cmd,"source");

                                                                    target.addVelocity(source.getVelocity().x, source.getVelocity().y, source.getVelocity().z);
                                                                    target.velocityModified = true;
                                                                    return Command.SINGLE_SUCCESS;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        );
                        velocityCommand.then(literal("get")
                                .then(argument("target",EntityArgumentType.entity())
                                        .then(literal("x")
                                                .then(argument("scale", DoubleArgumentType.doubleArg())
                                                        .executes(cmd->{
                                                            Entity target = EntityArgumentType.getEntity(cmd,"target");
                                                            double scale = DoubleArgumentType.getDouble(cmd,"scale");
                                                            double x = target.getVelocity().getX() * scale;

                                                            ServerCommandSource src = cmd.getSource();
                                                            src.sendFeedback(new LiteralText("X Velocity on " + target.getEntityName() + " after a scale factor of " + scale + " is " + x),false);
                                                            return (int)x;
                                                        })
                                                )
                                        )
                                        .then(literal("y")
                                                .then(argument("scale", DoubleArgumentType.doubleArg())
                                                        .executes(cmd->{
                                                            Entity target = EntityArgumentType.getEntity(cmd,"target");
                                                            double scale = DoubleArgumentType.getDouble(cmd,"scale");
                                                            double y = target.getVelocity().getY() * scale;

                                                            ServerCommandSource src = cmd.getSource();
                                                            src.sendFeedback(new LiteralText("Y Velocity on " + target.getEntityName() + " after a scale factor of " + scale + " is " + y),false);
                                                            return (int)y;
                                                        })
                                                )
                                        )
                                        .then(literal("z")
                                                .then(argument("scale", DoubleArgumentType.doubleArg())
                                                        .executes(cmd->{
                                                            Entity target = EntityArgumentType.getEntity(cmd,"target");
                                                            double scale = DoubleArgumentType.getDouble(cmd,"scale");
                                                            double z = target.getVelocity().getZ() * scale;

                                                            ServerCommandSource src = cmd.getSource();
                                                            src.sendFeedback(new LiteralText("Z Velocity on " + target.getEntityName() + " after a scale factor of " + scale + " is " + z),false);
                                                            return (int)z;
                                                        })
                                                )
                                        )
                                        .then(literal("magnitude")
                                                .then(argument("scale",DoubleArgumentType.doubleArg())
                                                        .executes(cmd-> {
                                                            ServerCommandSource src = cmd.getSource();
                                                            Entity entity = EntityArgumentType.getEntity(cmd,"target");
                                                            double scale = DoubleArgumentType.getDouble(cmd,"scale");
                                                            Vec3d vec = entity.getVelocity();
                                                            double magnitude = Math.sqrt(vec.x * vec.x + vec.y * vec.y + vec.z + vec.z) * scale;
                                                            src.sendFeedback(new LiteralText("Magnitude of Velocity on " + entity.getEntityName() + " after a scale factor of " + df.format(scale) + " is " + df.format(magnitude)),false);
                                                            return (int)magnitude;
                                                        })
                                                )
                                        )
                                )
                        );
                        velocityCommand.then(literal("help")
                                .then(literal("get")
                                        .executes(cmd->{
                                            ServerCommandSource source = cmd.getSource();
                                            source.sendFeedback(new LiteralText("This command returns the velocity of the target entity."),false);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                                .then(literal("modify")
                                        .executes(cmd->{
                                            ServerCommandSource source = cmd.getSource();
                                            source.sendFeedback(new LiteralText("This command modifies the value of the target entity"),false);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                                .executes(cmd->{
                                    ServerCommandSource source = cmd.getSource();
                                    source.sendFeedback(new LiteralText("The velocity commands are associated with modifying entity motion."),false);
                                    return Command.SINGLE_SUCCESS;
                                })
                        );
                    }
                    dispatcher.register(velocityCommand);
                    log.info("Registered /velocity");
                }
        );

    }

    public static int damageEnt(final CommandContext<ServerCommandSource> context, final Entity ent, float damage)
    {
        // Entities with no health cause errors if damage function is used
        ServerCommandSource source = context.getSource();
        if (ent == null){
            log.warn("Attempted to damage null entity.");
            source.sendFeedback(new LiteralText("Entity is null or does not exist."),false);
            return 0;
        }
        else if (!(ent instanceof LivingEntity) || (ent instanceof ArmorStandEntity)) {
            source.sendFeedback(new LiteralText("Entity " + ent.getEntityName()  + " does not take damage."),false);
            return 0;
        }
        else {
            ((LivingEntity)ent).damage(DamageSource.GENERIC, damage);
            String out ="Entity " + ent.getEntityName() + " damaged for " + damage;
            source.sendFeedback(new LiteralText(out),false);
            return Command.SINGLE_SUCCESS;
        }

    }

    public static int healEnt(final CommandContext<ServerCommandSource> context, final Entity ent, float heal) // Same as damageEnt but heals.
    {
        ServerCommandSource source = context.getSource();
        if (ent == null){
            log.warn("Attempted to heal null entity.");
            source.sendFeedback(new LiteralText("Entity is null or does not exist."),false);
            return 0;
        }
        else if (!(ent instanceof LivingEntity) || (ent instanceof ArmorStandEntity)) {
            source.sendFeedback(new LiteralText("Entity " + ent.getEntityName()  + " does not heal."),false);
            return 0;
        }
        else {
            ((LivingEntity) ent).heal(heal);
            String out = "Entity " + ent.getEntityName() + " healed for " + heal;
            source.sendFeedback(new LiteralText(out),false);
            return Command.SINGLE_SUCCESS;
        }
    }

    public static int powScore(ScoreboardPlayerScore targetScore,final ScoreboardPlayerScore sourceScore){
        int trg = targetScore.getScore();
        int total = 1;
        for(int i = 0; i < sourceScore.getScore(); ++i)
            total *= trg;
        return total;
    }
}