package com.github.capitalistspz.command;

import com.github.capitalistspz.utils.CommandUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class VelocityCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        // Velocity command
        LiteralArgumentBuilder<ServerCommandSource> velocityCommand = literal("velocity")
                .requires(executor -> executor.hasPermissionLevel(2));

        // Velocity command definition scope
        {
            velocityCommand.then(literal("modify")
                    .then(argument("target", EntityArgumentType.entity())
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
                                                    .then(argument("scale", FloatArgumentType.floatArg())
                                                            .executes(cmd->{
                                                                Entity target = EntityArgumentType.getEntity(cmd,"target");
                                                                Entity source = EntityArgumentType.getEntity(cmd,"source");
                                                                target.setVelocity(source.getVelocity().multiply(FloatArgumentType.getFloat(cmd,"scale")));
                                                                target.velocityModified = true;
                                                                return Command.SINGLE_SUCCESS;
                                                            })
                                                    ).executes(cmd->{
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
                                                        target.setVelocity(target.getVelocity().add(vec));
                                                        target.velocityModified = true;
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )
                                    .then(literal("from")
                                            .then(argument("source",EntityArgumentType.entity())
                                                    .then(argument("scale", FloatArgumentType.floatArg())
                                                            .executes(cmd->{
                                                                Entity target = EntityArgumentType.getEntity(cmd,"target");
                                                                Entity source = EntityArgumentType.getEntity(cmd,"source");

                                                                target.setVelocity(target.getVelocity().add(source.getVelocity().multiply(FloatArgumentType.getFloat(cmd,"scale"))));
                                                                target.velocityModified = true;
                                                                return Command.SINGLE_SUCCESS;
                                                            })
                                                    )
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
                                                src.sendFeedback(new LiteralText("Magnitude of Velocity on " + entity.getEntityName() + " after a scale factor of " + CommandUtils.df.format(scale) + " is " + CommandUtils.df.format(magnitude)),false);
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
    }
}
