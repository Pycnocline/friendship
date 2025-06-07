package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Action;
import icu.iamin.friendship.features.Echo;
import net.minecraft.client.MinecraftClient;

public class AutoLookCommandHandler implements FriendshipCommand {
    private final Action action;
    private final Echo echo;
    private boolean isAutoLookEnabled = false;
    private String autoLookStatus;

    public AutoLookCommandHandler(Action action, Echo echo) {
        this.action = action;
        this.echo = echo;
    }

    @Override
    public String getName() {
        return "!autolook";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            if (args.length == 3) {
                if (args[2].toLowerCase().equals("true")) {
                    isAutoLookEnabled = true;
                } else if (args[2].toLowerCase().equals("false")) {
                    isAutoLookEnabled = false;
                } else {
                    echo.echoChatMessage("未知的参数", client);
                }
            } else {
                isAutoLookEnabled = !isAutoLookEnabled;
            }

            if (isAutoLookEnabled) {
                autoLookStatus = "开启";
            } else {
                autoLookStatus = "关闭";
            }

            echo.echoChatMessage("自动看向周围的生物：" + autoLookStatus, client);
        });
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        client.execute(() -> {
            action.lookAtNearestLivingEntity(6, false,client);
        });

    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isAutoLookEnabled;
    }

    @Override
    public String getHelp() {
        return "开/关 尝试看向附近的生物。";
    }
}
