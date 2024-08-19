package net.monoamin.erosion;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class DebugStickItem extends Item {

    public DebugStickItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            // Perform your action here. For example, printing a message or modifying blocks/entities.
            ChatMessageHandler.Send("Debug stick used!", player.getServer().overworld());
            // Example: Perform any custom action you need here
            performCustomAction(level, player, itemStack, player.getOnPos());
        }
        return InteractionResultHolder.success(itemStack);
    }

    private void performCustomAction(Level level, Player player, ItemStack itemStack, BlockPos blockPos) {
        // Define your custom action here
        // For example, changing block states, interacting with entities, etc.
        ChatMessageHandler.Send("Triggering Calculation!", player.getServer().overworld());
        ErosionHandler.doErosionCalculation(blockPos);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos blockPos = context.getClickedPos();
        if (!level.isClientSide && player != null) {
            // Example of a right-click on a block doing something
            ChatMessageHandler.Send("Debug stick usedOn!", player.getServer().overworld());
            performCustomActionOnBlock(level, context, player, blockPos);
        }
        return InteractionResult.SUCCESS;
    }

    private void performCustomActionOnBlock(Level level, UseOnContext context, Player player, BlockPos blockPos) {
        // Define your custom action here
        // For example, changing block states, interacting with entities, etc.
        ChatMessageHandler.Send("Triggering Calculation for BlockPos(" + blockPos + ") xyId("+ Util.idFromXZ(blockPos) + ")!", player.getServer().overworld());
        ErosionHandler.doErosionCalculation(blockPos);
    }
}
