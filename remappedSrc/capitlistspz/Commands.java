package capitlistspz;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.sun.org.apache.xpath.internal.operations.Lt;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ObjectiveArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.jmx.Server;

import java.text.DecimalFormat;

import static capitlistspz.useful_commands.MOD_ID;
import static net.minecraft.server.command.CommandManager.*;

public class Commands {
    public static final int SINGLE_FAIL = 0;
    public static DecimalFormat df = new DecimalFormat("0.00");
    private static final Logger log = LogManager.getLogger("PZ");
    static public void init() {
        df.setMaximumFractionDigits(2);
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
                {
                    // damage command registration
                    LiteralArgumentBuilder<ServerCommandSource> damageArgBuilder = literal("damage")
                            .requires(executor -> executor.hasPermissionLevel(2));

                    damageArgBuilder.then(literal("entity").then(argument("target", EntityArgumentType.entity()) // Use selector to find entity
                            .then(argument("damage_amount",FloatArgumentType.floatArg(0.0f)) // Take in damage parameter
                                    .executes(cmd -> damageEnt(cmd, EntityArgumentType.getEntity(cmd, "target"),FloatArgumentType.getFloat(cmd,"damage_amount"))))));
                    damageArgBuilder.then(literal("help").executes(cmd -> {
                        ServerCommandSource source = cmd.getSource();
                        source.sendFeedback(new LiteralText("§a========== Damage Help ==========\n§6/damage <entity> <damage> \n§re.g.\n§7/damage §b@e[type=!player,limit=1,sort=nearest] §e5.3"),false);
                        return 1;
                    }));
                    dispatcher.register(damageArgBuilder);
                    log.info("Registered /damage");
                    LiteralArgumentBuilder<ServerCommandSource> scoreboardMaths = literal("sb_operation")
                            .requires(executor -> executor.hasPermissionLevel(2));
                    scoreboardMaths.then(argument("target", ScoreHolderArgumentType.scoreHolder()).then(argument("targetObjective", ObjectiveArgumentType.objective())
                            .then(literal("pow")
                                    .then(argument("source",ScoreHolderArgumentType.scoreHolder()).then(argument("sourceObjective", ObjectiveArgumentType.objective())
                                            .executes(cmd->{
                                        World world = cmd.getSource().getWorld();
                                        if (world == null){
                                            cmd.getSource().sendFeedback(new LiteralText("This command must be executed in a world."),false);
                                            return SINGLE_FAIL;
                                        }
                                        ScoreboardPlayerScore targetScore = world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"target"),ObjectiveArgumentType.getObjective(cmd,"targetObjective"));
                                        ScoreboardPlayerScore sourceScore =  world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"source"),ObjectiveArgumentType.getObjective(cmd,"sourceObjective"));
                                        targetScore.setScore(powScore(targetScore,sourceScore));
                                        return Command.SINGLE_SUCCESS;
                                    })))

                            )
                            .then(literal("sqrt")
                                    .executes(cmd->{
                                        World world = cmd.getSource().getWorld();
                                        if (world == null){
                                            cmd.getSource().sendFeedback(new LiteralText("This command must be executed in a world."),false);
                                            return SINGLE_FAIL;
                                        }
                                        ScoreboardPlayerScore targetScore = world.getScoreboard().getPlayerScore(ScoreHolderArgumentType.getScoreHolder(cmd,"target"),ObjectiveArgumentType.getObjective(cmd,"targetObjective"));
                                        targetScore.setScore((int)Math.sqrt(targetScore.getScore()));
                                        return Command.SINGLE_SUCCESS;
                                    }))
                    ));
                    dispatcher.register(scoreboardMaths);
                    log.info("Registered /sb_operation");
                    LiteralArgumentBuilder<ServerCommandSource> argb = literal("velocity")
                            .requires(executor -> executor.hasPermissionLevel(2));
                    argb.then(literal("modify")
                            .then(argument("target",EntityArgumentType.entity())
                                    .then(literal("set")
                                            .then(literal("value")
                                                    .then(argument("motion", Vec3ArgumentType.vec3(false))
                                                            .executes(cmd->{
                                                                Entity target  = EntityArgumentType.getEntity(cmd,"target");
                                                                Vec3d vec = Vec3ArgumentType.getVec3(cmd,"motion");
                                                                log.info(vec.toString());
                                                                log.info(vec.x + "\n"+ vec.y + "\n"+vec.z + "\n");
                                                                target.setVelocity(vec);
                                                                target.velocityModified = true;

                                                                return Command.SINGLE_SUCCESS;
                                                            })
                                                    )
                                            )
                                            .then(literal("from")
                                                    .then(argument("source",EntityArgumentType.entity())
                                                            .executes(cmd->{
                                                                ServerCommandSource src = cmd.getSource();
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
                    argb.then(literal("get")
                            .then(argument("target",EntityArgumentType.entity())
                                    .then(literal("x")
                                            .then(argument("scale", DoubleArgumentType.doubleArg())
                                                    .executes(cmd->{
                                                        Entity target = EntityArgumentType.getEntity(cmd,"target");
                                                        double scale = DoubleArgumentType.getDouble(cmd,"scale");
                                                        double x = target.getVelocity().getX() * scale;

                                                        ServerCommandSource src = cmd.getSource();
                                                        src.sendFeedback(new LiteralText("X Velocity on " + target.getEntityName() + " after a scale factor of " + df.format(scale) + " is " + df.format(x)),false);
                                                        return (int)x;
                                                    })
                                            )
                                    )
                                    .then(literal("y")
                                            .then(argument("scale", DoubleArgumentType.doubleArg())
                                                    .executes(cmd->{
                                                        Entity entity = EntityArgumentType.getEntity(cmd,"target");
                                                        double scale = DoubleArgumentType.getDouble(cmd,"scale");
                                                        double y = entity.getVelocity().getY() * scale;

                                                        ServerCommandSource src = cmd.getSource();
                                                        src.sendFeedback(new LiteralText("Y Velocity on " + entity.getEntityName() + " after a scale factor of " + df.format(scale) + " is " + df.format(y)),false);
                                                        return (int)y;
                                                    })
                                            )
                                    )
                                    .then(literal("z")
                                            .then(argument("scale", DoubleArgumentType.doubleArg())
                                                    .executes(cmd->{
                                                        Entity entity = EntityArgumentType.getEntity(cmd,"target");
                                                        double scale = DoubleArgumentType.getDouble(cmd,"scale");
                                                        double z = entity.getVelocity().getZ() * scale;

                                                        ServerCommandSource src = cmd.getSource();
                                                        src.sendFeedback(new LiteralText("Z Velocity on " + entity.getEntityName() + " after a scale factor of " + df.format(scale) + " is " + df.format(z)),false);
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
                    argb.then(literal("help")
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
                    dispatcher.register(argb);
                    log.info("Registered /motion");
                }
        );

    }

    public static int damageEnt(final CommandContext<ServerCommandSource> context, final Entity ent, float damage){
        // Entities with no health cause errors if damage function is used
        // So such entities are killed instead.
        ServerCommandSource source = context.getSource();
        if (!(ent instanceof LivingEntity) || (ent instanceof ArmorStandEntity)) {
            source.sendFeedback(new LiteralText("Entity " + ent.getEntityName()  + " does not take damage."),false);
            return 0;
        }


        LivingEntity e = (LivingEntity) ent;
        if (e == null){
            log.warn("Attempted to damage null entity.");
            source.sendFeedback(new LiteralText("Entity is null or does not exist."),false);
            return 0;
        }
        else {
            e.damage(DamageSource.GENERIC, damage);
            String out ="Entity " + e.getEntityName() + " damaged for " + damage;
            source.sendFeedback(new LiteralText(out),false);
            return (int)(damage * 100); // Value for 'execute store result' - scoreboards don't support non-int values.
        }
    }
    public static int powScore(ScoreboardPlayerScore targetScore,final ScoreboardPlayerScore sourceScore){
        int trg = targetScore.getScore();
        int total = 1;
        for(int i = 0; i < sourceScore.getScore(); ++i)
            total *= trg;
        return total;
    }
    public static Vec3d addVec3d(Vec3d a, Vec3d b){
        return new Vec3d(a.x + b.x, a.y + b.y, a.z + b.z);
    }
}