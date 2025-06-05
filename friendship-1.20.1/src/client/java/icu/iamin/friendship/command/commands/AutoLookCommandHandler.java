package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.Action;
import icu.iamin.friendship.features.Echo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AutoLookCommandHandler implements FriendshipCommand {
    private final Action action;
    private final Echo echo;
    private boolean isAutoLookEnabled = false;
    private MutableText autoLookStatus;

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
                autoLookStatus = Text.literal("开启").formatted(Formatting.GREEN);
            } else {
                autoLookStatus = Text.literal("关闭").formatted(Formatting.RED);
            }

            MutableText output = Text.literal("自动看向附近生物设置为：")
                            .append(autoLookStatus);

            echo.echoText(output, client);
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
