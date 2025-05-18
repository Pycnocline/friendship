package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Echo;
import icu.iamin.friendship.features.Inv;
import net.minecraft.client.MinecraftClient;

public class DropallCommandHandler implements FriendshipCommand {
    private final Inv inv;
    private final Echo echo;

    public DropallCommandHandler(Inv inv, Echo echo) {
        this.inv = inv;
        this.echo = echo;
    }

    @Override
    public String getName() {
        return "!dropall";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            if (inv.dropall(client)) {
                echo.echoChatMessage("成功丢弃所有物品", client);
            } else {
                echo.echoChatMessage("未能丢弃所有物品", client);
            }
        });
    }

    @Override
    public String getHelp() {
        return "丢弃物品栏内的所有物品。";
    }
}
