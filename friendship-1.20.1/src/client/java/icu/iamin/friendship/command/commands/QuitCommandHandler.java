package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Echo;
import icu.iamin.friendship.features.Status;
import net.minecraft.client.MinecraftClient;

public class QuitCommandHandler implements FriendshipCommand {
    private final Status status;
    private final Echo echo;

    public QuitCommandHandler(Status status, Echo echo) {
        this.status = status;
        this.echo = echo;
    }

    @Override
    public String getName() {
        return "!quit";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            echo.echoChatMessage("Goodbye!",client);
            status.quit(client);
        });
    }

    @Override
    public String getHelp() {
        return "退出游戏。";
    }
}
