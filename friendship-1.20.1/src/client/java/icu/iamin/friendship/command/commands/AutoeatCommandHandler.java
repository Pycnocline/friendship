package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Action;
import icu.iamin.friendship.features.Echo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.HungerManager;

public class AutoeatCommandHandler implements FriendshipCommand {
    private final Action action;
    private final Echo echo;
    private boolean isAutoEatEnabled = false;

    public AutoeatCommandHandler(Action action, Echo echo) {
        this.action = action;
        this.echo = echo;
    }
    @Override
    public String getName() {
        return "!autoeat";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            if (args.length == 3) {
                if (args[2].toLowerCase().equals("true")) {
                    isAutoEatEnabled = true;
                } else if (args[2].toLowerCase().equals("false")) {
                    isAutoEatEnabled = false;
                } else {
                    echo.echoChatMessage("未知的参数", client);
                }
            } else {
                isAutoEatEnabled = !isAutoEatEnabled;
            }

            echo.echoChatMessage("自动进食：" + isAutoEatEnabled, client);
        });
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        client.execute(() -> {
            HungerManager hungerManager = client.player.getHungerManager();

            if (hungerManager.getFoodLevel() < 15) {
                if (action.eatOffHand(client)) {
                    echo.echoChatMessage("<自动进食>", client);
                }
            } else if (hungerManager.getFoodLevel() <= 19 && client.player.getHealth() < client.player.getMaxHealth()) {
                if (action.eatOffHand(client)) {
                    echo.echoChatMessage("<自动进食>", client);
                }
            }
        });

    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isAutoEatEnabled;
    }

    @Override
    public String getHelp() {
        return "开/关 尝试自动进食副手食物。";
    }
}
