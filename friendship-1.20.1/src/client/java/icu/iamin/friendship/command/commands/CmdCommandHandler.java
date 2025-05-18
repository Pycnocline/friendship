package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Echo;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;

public class CmdCommandHandler implements FriendshipCommand {
    private final Echo echo;

    public CmdCommandHandler(Echo echo) {
        this.echo = echo;
    }

    @Override
    public String getName() {
        return "!cmd";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            String[] usedArgs = Arrays.copyOfRange(args, 2, args.length);
            String cmdCommand = String.join(" ", usedArgs);

            echo.echoCommand(cmdCommand, client);
        });
    }

    @Override
    public String getHelp() {
        return "!cmd <command> 执行游戏内命令。例如'!cmd kill'";
    }
}
