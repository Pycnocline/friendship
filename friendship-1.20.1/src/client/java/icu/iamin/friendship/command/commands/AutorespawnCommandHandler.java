package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Echo;
import icu.iamin.friendship.features.Status;
import net.minecraft.client.MinecraftClient;

public class AutorespawnCommandHandler implements FriendshipCommand {
    private final Status status;
    private final Echo echo;

    private boolean isAutoResurrectEnabled = false;


    public AutorespawnCommandHandler(Status status, Echo echo) {
        this.status = status;
        this.echo = echo;
    }

    @Override
    public String getName() {
        return "!autorespawn";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            if (args.length == 3) {
                if (args[2].toLowerCase().equals("true")) {
                    isAutoResurrectEnabled = true;
                } else if (args[2].toLowerCase().equals("false")) {
                    isAutoResurrectEnabled = false;
                } else {
                    echo.echoChatMessage("未知的参数", client);
                }
            } else {
                isAutoResurrectEnabled = !isAutoResurrectEnabled;
            }

            echo.echoChatMessage("自动复活：" + isAutoResurrectEnabled, client);
        });
    }

    @Override
    public String getHelp() {
        return "开/关死亡后自动复活功能。";
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        client.execute(() -> {
            if (status.respawn(client)) {
                echo.echoChatMessage("<自动复活>", client);
            }
        });

    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isAutoResurrectEnabled;
    }
}
