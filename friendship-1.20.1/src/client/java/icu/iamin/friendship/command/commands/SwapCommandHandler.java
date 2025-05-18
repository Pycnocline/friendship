package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Echo;
import icu.iamin.friendship.features.Inv;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;

public class SwapCommandHandler implements FriendshipCommand {
    private final Inv inv;
    private final Echo echo;

    public SwapCommandHandler(Inv inv, Echo echo) {
        this.inv = inv;
        this.echo = echo;
    }

    @Override
    public String getName() {
        return "!swap";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            String[] swapList = Arrays.copyOfRange(args, 2, args.length);
            if (inv.swap(swapList, client)) {
                echo.echoChatMessage("成功交换物品位置", client);
            } else {
                echo.echoChatMessage("未能交换物品位置", client);
            }
        });
    }

    @Override
    public String getHelp() {
        return "!swap <slot1-1> <slot1-2> [<slot2-1> <slot2-2>...] 根据槽位编号交换身上的物品位置，可以用来装备物品等。 例如'!swap 0 1 2 3'会交换0号和1号槽，2号和3号槽。";
    }
}