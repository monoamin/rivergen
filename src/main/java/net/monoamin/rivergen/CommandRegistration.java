package net.monoamin.rivergen;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.monoamin.rivergen.gen.RiverGenerationHandler;
import net.monoamin.rivergen.terrain.TerrainUtils;

@Mod.EventBusSubscriber(modid = "rivergen")
public class CommandRegistration {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("rgen")
                        .then(Commands.argument("x", StringArgumentType.string())
                        .then(Commands.argument("z", StringArgumentType.string())
                                .executes(CommandRegistration::executeCommand))
                        .executes(context -> {
                            //context.getSource().sendSuccess(() -> Component.literal("rgen..."), false);
                            return 1;
                        }))
        );
    }

    private static int executeCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String xVal = StringArgumentType.getString(context, "x");
        String zVal = StringArgumentType.getString(context, "z");
        int x = Integer.parseInt(xVal);
        int z = Integer.parseInt(zVal);
        int height = TerrainUtils.getYValueAt(x, z);
        BlockPos riverPos = new BlockPos(x, height, z);
        RiverGenerationHandler.traceRiver(riverPos);
        context.getSource().sendSuccess(() -> Component.literal("River generated at: " + xVal + " " +zVal), false);
        return 1;
    }
}
