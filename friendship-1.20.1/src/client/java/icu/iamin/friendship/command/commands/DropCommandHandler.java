package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Echo;
import icu.iamin.friendship.features.Inv;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;

public class DropCommandHandler implements FriendshipCommand {
    private final Inv inv;
    private final Echo echo;

    public DropCommandHandler(Inv inv, Echo echo) {
        this.inv = inv;
        this.echo = echo;
    }

    @Override
    public String getName() {
        return "!drop";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            String[] dropList = Arrays.copyOfRange(args, 2, args.length);
            if (inv.drop(dropList, client)) {
                echo.echoChatMessage("成功丢弃物品", client);
            } else {
                echo.echoChatMessage("未能丢弃物品", client);
            }
        });
    }

    @Override
    public String getHelp() {
        return "!drop <slot> <number> [<slot> <number> ...] 丢弃物品栏内指定槽位的指定数量物品。";
    }
}
