package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;

public class BaritoneCommandHandler implements FriendshipCommand {
    @Override
    public String getName() {
        return "!baritone";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            String[] usedArgs = Arrays.copyOfRange(args, 2, args.length);
            String baritoneCommand = "#" + String.join(" ", usedArgs);

            client.player.networkHandler.sendChatMessage(baritoneCommand);
        });
    }

    @Override
    public String getHelp() {
        return "!baritone <baritone command> 执行Baritone的指令。例如'!baritone thisway 100'";
    }
}
