package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Action;
import icu.iamin.friendship.features.Echo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;

public class AutoDefendCommandHandler implements FriendshipCommand {
    private final Action action;
    private final Echo echo;
    private boolean isAutoDefendEnabled = false;
    private String autoDefendStatus;

    public AutoDefendCommandHandler(Action action, Echo echo) {
        this.action = action;
        this.echo = echo;
    }

    @Override
    public String getName() {
        return "!autodefend";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            if (args.length == 3) {
                if (args[2].toLowerCase().equals("true")) {
                    isAutoDefendEnabled = true;
                } else if (args[2].toLowerCase().equals("false")) {
                    isAutoDefendEnabled = false;
                } else {
                    echo.echoChatMessage("未知的参数", client);
                }
            } else {
                isAutoDefendEnabled = !isAutoDefendEnabled;
            }

            if (isAutoDefendEnabled) {
                autoDefendStatus = "开启";
            } else {
                autoDefendStatus = "关闭";
            }

            echo.echoChatMessage("自动防御周围的敌对生物：" + autoDefendStatus, client);
        });
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        client.execute(() -> {
            LivingEntity target = action.lookAtNearestLivingEntity(4, true, client);
            if (target != null) {
                action.attack(target, client, true);
            } else {
                action.attack(target, client, false);
            }
        });

    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isAutoDefendEnabled;
    }

    @Override
    public String getHelp() {
        return "开/关 尝试自动防御周围的敌对生物。";
    }
}
