package net.monoamin.rivergen.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.monoamin.rivergen.gen.WorldStateHandler;
import net.monoamin.rivergen.terrain.TerrainUtils;

public class DebugToolItem extends Item {

    public DebugToolItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            // Perform your action here. For example, printing a message or modifying blocks/entities.
            //DebugMessage.Send("Debug stick used!", player.getServer().overworld());
            // Example: Perform any custom action you need here
            performCustomAction(level, player, itemStack, player.getOnPos());
        }
        return InteractionResultHolder.success(itemStack);
    }

    private void performCustomAction(Level level, Player player, ItemStack itemStack, BlockPos blockPos) {
        // Define your custom action here
        // For example, changing block states, interacting with entities, etc.
        //DebugMessage.Send("Triggering Calculation!", player.getServer().overworld());
        //RiverGenerationHandler.doErosionCalculation(blockPos);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemStack = player.getItemInHand(player.getUsedItemHand());
        BlockPos blockPos = context.getClickedPos();
        if (!level.isClientSide && player != null) {
            // Example of a right-click on a block doing something
            //DebugMessage.Send("Debug stick usedOn!", player.getServer().overworld());
            performCustomActionOnBlock(level, context, player, blockPos, itemStack);
        }
        return InteractionResult.SUCCESS;
    }

    private void performCustomActionOnBlock(Level level, UseOnContext context, Player player, BlockPos blockPos, ItemStack itemStack) {
        // Define your custom action here
        // For example, changing block states, interacting with entities, etc.
        DebugMessage.Send(itemStack.getDescriptionId(), player.getServer().overworld());

        switch (itemStack.getDescriptionId()){
            case "item.rivergen.get_height":
                int height = TerrainUtils.getYValueAt(blockPos.getX(), blockPos.getZ());
                DebugMessage.Send("GetHeight used! Height is " + height, player.getServer().overworld());
                break;
            /*case "item.rivergen.get_normal":
                Vec3 normal = TerrainUtils.getSmoothedNormalCorrect(blockPos, 3);
                DebugMessage.Send("GetNormal used! Normal is " + normal, player.getServer().overworld());
                RenderHandler.AddLineIfAbsent(
                        "n"+ TerrainUtils.idFromXZ(blockPos),
                        TerrainUtils.BlockPosToVec3(blockPos),
                        TerrainUtils.BlockPosToVec3(blockPos).add(normal),
                        0,255,0,255
                );
                break;
            case "item.rivergen.get_lowest":
                BlockPos pos = TerrainUtils.getLowestCircular(TerrainUtils.BlockPosToVec3(blockPos),3, 8,  player.getServer().overworld());
                DebugMessage.Send("GetLowest used! Lowest is " + pos.toString(), player.getServer().overworld());
                break;
            case "item.rivergen.get_flow":
                Vec3 flow = TerrainUtils.getSmoothedNormalCorrect(blockPos, 5).multiply(1,0,1).normalize();
                DebugMessage.Send("GetFlow used! Flow is " + flow, player.getServer().overworld());
                RenderHandler.AddLineIfAbsent(
                        "f"+ TerrainUtils.idFromXZ(blockPos),
                        TerrainUtils.BlockPosToVec3(blockPos),
                        TerrainUtils.BlockPosToVec3(blockPos).add(flow),
                        0,0,255,255
                );
                break;
            case "item.rivergen.get_accumulation":
                Vec3 accumulation = RiverGenerationHandler.erosionDataHolder.accumulations.get(blockPos);
                DebugMessage.Send("GetAccumulation used! Accumulation is " + accumulation.toString(), player.getServer().overworld());
                RenderHandler.AddLineIfAbsent(
                        "f"+ TerrainUtils.idFromXZ(blockPos),
                        TerrainUtils.BlockPosToVec3(blockPos),
                        TerrainUtils.BlockPosToVec3(blockPos).add(accumulation),
                        20,255,255,255
                );
                break;*/
            case "item.rivergen.get_density":
                Double density = TerrainUtils.getFinalDensityAt(blockPos);
                DebugMessage.Send("GetDensity used! Normal is " + density, player.getServer().overworld());
                /*RenderHandler.AddLineIfAbsent(
                        "n"+TerrainUtils.idFromXZ(blockPos),
                        TerrainUtils.BlockPosToVec3(blockPos),
                        TerrainUtils.BlockPosToVec3(blockPos).add(normal),
                        0,255,0,255
                );*/
                break;
            case "item.rivergen.start_gen":
                DebugMessage.Send("StartGen used!", player.getServer().overworld());
                WorldStateHandler.traceRiver(TerrainUtils.BlockPosToVec3(blockPos));
                break;
            default: break;
        }

        //DebugMessage.Send("Triggering Calculation for BlockPos(" + blockPos + ") xyId("+ TerrainUtils.idFromXZ(blockPos) + ")!", player.getServer().overworld());
    }
}
