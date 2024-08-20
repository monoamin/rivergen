package net.monoamin.rivergen;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class ChatMessageHandler {

    public static void Send(String message, ServerLevel level)
    {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            PlayerChatMessage chatMessage = PlayerChatMessage.unsigned(player.getUUID(), message);
            player.createCommandSourceStack().sendChatMessage(new OutgoingChatMessage.Player(chatMessage), false, ChatType.bind(ChatType.CHAT, player));
        }
    }
}
