package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Echo;
import icu.iamin.friendship.features.Inv;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;
import java.util.Map;

public class InvCommandHandler implements FriendshipCommand {
    private final Inv inv;
    private final Echo echo;

    public InvCommandHandler(Inv inv, Echo echo) {
        this.inv = inv;
        this.echo = echo;
    }

    @Override
    public String getName() {
        return "!inv";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            Map<Integer, Object[]> invList = inv.getInvList(client);
            echo.echoChatMessage("---------- 物品栏 ----------", client);
            invList.forEach((key, valueArray) -> {
                if (key != null) {
                    echo.echoChatMessage("槽位:" + key + " -> " + (valueArray != null ? Arrays.toString(valueArray) : "null"), client);
                }
            });
            echo.echoChatMessage("---------------------------", client);
        });
    }

    @Override
    public String getHelp() {
        return "列出物品栏内的所有内容。";
    }
}
