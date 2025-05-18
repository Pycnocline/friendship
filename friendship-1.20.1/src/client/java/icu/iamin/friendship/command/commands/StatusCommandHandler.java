package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Echo;
import icu.iamin.friendship.features.Status;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;

public class StatusCommandHandler implements FriendshipCommand {
    private final Status status;
    private final Echo echo;

    public StatusCommandHandler(Status status, Echo echo) {
        this.status = status;
        this.echo = echo;
    }

    @Override
    public String getName() {
        return "!status";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            ArrayList<Object> statusList = status.getStatus(client);

            echo.echoChatMessage("---------- 人物状态 ----------", client);
            echo.echoChatMessage("生命：" + statusList.get(0) + " / " + statusList.get(1), client);
            echo.echoChatMessage("饱食：" + statusList.get(2) + " / " + statusList.get(3), client);
            echo.echoChatMessage("位置：" + statusList.get(4), client);
            echo.echoChatMessage("效果：", client);

            if (statusList.size() < 6 ) {
                echo.echoChatMessage(" - 无", client);
            } else {
                for (int i = 5; i < statusList.size(); i++) {
                    Object effectDetailList = statusList.get(i);
                    Object[] innerArray = (Object[]) effectDetailList;
                    echo.echoChatMessage(" - " + innerArray[0] + " lv." + innerArray[1] + " " + innerArray[2] + "s",client);
                }
            }

            echo.echoChatMessage("-----------------------------", client);
        });
    }

    @Override
    public String getHelp() {
        return "列出当前的人物状态。";
    }
}
