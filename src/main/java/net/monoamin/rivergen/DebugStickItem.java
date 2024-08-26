package net.monoamin.rivergen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class DebugStickItem extends Item {

    public DebugStickItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            // Perform your action here. For example, printing a message or modifying blocks/entities.
            //ChatMessageHandler.Send("Debug stick used!", player.getServer().overworld());
            // Example: Perform any custom action you need here
            performCustomAction(level, player, itemStack, player.getOnPos());
        }
        return InteractionResultHolder.success(itemStack);
    }

    private void performCustomAction(Level level, Player player, ItemStack itemStack, BlockPos blockPos) {
        // Define your custom action here
        // For example, changing block states, interacting with entities, etc.
        //ChatMessageHandler.Send("Triggering Calculation!", player.getServer().overworld());
        //RivergenHandler.doErosionCalculation(blockPos);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemStack = player.getItemInHand(player.getUsedItemHand());
        BlockPos blockPos = context.getClickedPos();
        if (!level.isClientSide && player != null) {
            // Example of a right-click on a block doing something
            //ChatMessageHandler.Send("Debug stick usedOn!", player.getServer().overworld());
            performCustomActionOnBlock(level, context, player, blockPos, itemStack);
        }
        return InteractionResult.SUCCESS;
    }

    private void performCustomActionOnBlock(Level level, UseOnContext context, Player player, BlockPos blockPos, ItemStack itemStack) {
        // Define your custom action here
        // For example, changing block states, interacting with entities, etc.
        ChatMessageHandler.Send(itemStack.getDescriptionId(), player.getServer().overworld());

        switch (itemStack.getDescriptionId()){
            case "item.rivergen.get_height":
                int height = Util.getYValueAt(blockPos, player.getServer().overworld());
                ChatMessageHandler.Send("GetHeight used! Height is " + height, player.getServer().overworld());
                break;
            case "item.rivergen.get_normal":
                Vec3 normal = Util.getSmoothedNormalCorrect(blockPos, player.getServer().overworld(), 3);
                ChatMessageHandler.Send("GetNormal used! Normal is " + normal.toString(), player.getServer().overworld());
                RenderHandler.AddLineIfAbsent(
                        "n"+Util.idFromXZ(blockPos),
                        Util.BlockPosToVec3(blockPos),
                        Util.BlockPosToVec3(blockPos).add(normal),
                        0,255,0,255
                );
                break;
            case "item.rivergen.get_flow":
                Vec3 flow = Util.getSmoothedNormalCorrect(blockPos, player.getServer().overworld(), 5).multiply(1,0,1).normalize();
                ChatMessageHandler.Send("GetFlow used! Flow is " + flow.toString(), player.getServer().overworld());
                RenderHandler.AddLineIfAbsent(
                        "f"+Util.idFromXZ(blockPos),
                        Util.BlockPosToVec3(blockPos),
                        Util.BlockPosToVec3(blockPos).add(flow),
                        0,0,255,255
                );
                break;
            case "item.rivergen.get_accumulation":
                Vec3 accumulation = RivergenHandler.fluidGrid.accumulations.get(blockPos);
                ChatMessageHandler.Send("GetAccumulation used! Accumulation is " + accumulation.toString(), player.getServer().overworld());
                RenderHandler.AddLineIfAbsent(
                        "f"+Util.idFromXZ(blockPos),
                        Util.BlockPosToVec3(blockPos),
                        Util.BlockPosToVec3(blockPos).add(accumulation),
                        20,255,255,255
                );
                break;
            case "item.rivergen.start_gen":
                ChatMessageHandler.Send("StartGen used!", player.getServer().overworld());
                //RivergenHandler.doErosionCalculation(blockPos);
                break;
            default: break;
        }

        //ChatMessageHandler.Send("Triggering Calculation for BlockPos(" + blockPos + ") xyId("+ Util.idFromXZ(blockPos) + ")!", player.getServer().overworld());
    }
}
