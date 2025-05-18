package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Echo;
import icu.iamin.friendship.features.Hello;
import net.minecraft.client.MinecraftClient;

public class HelloCommandHandler implements FriendshipCommand {
    private final Hello hello;
    private final Echo echo;

    public HelloCommandHandler(Hello hello, Echo echo) {
        this.hello = hello;
        this.echo = echo;
    }

    @Override
    public String getName() {
        return "!hello";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            String message = hello.helloWorld();
            echo.echoChatMessage(message,client);
        });
    }

    @Override
    public String getHelp() {
        return "只是一条测试命令。向你问好！";
    }
}
