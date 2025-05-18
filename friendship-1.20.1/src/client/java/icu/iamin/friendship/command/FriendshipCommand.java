package icu.iamin.friendship.command;

import net.minecraft.client.MinecraftClient;

public interface FriendshipCommand {

    String getName();

    void execute(MinecraftClient client, String[] args);

    default String getHelp() {
        return "没有针对此命令的帮助。";
    }

    default boolean isToggleable() {
        return false;
    }

    default boolean isEnabled() {
        return false;
    }

    default void onClientTick(MinecraftClient client) {}
}
