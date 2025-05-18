package icu.iamin.friendship.command;

import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("CommandManager");
    private final Map<String, FriendshipCommand> commandHandlerMap = new HashMap<>();

    public void register(FriendshipCommand commandHandler){
        if (commandHandler == null || commandHandler.getName() == null || commandHandler.getName().isEmpty()) {
            LOGGER.warn("Attempted to register a null or invalid command handler.");
            return;
        }

        String commandName = commandHandler.getName().toLowerCase();

        if (commandHandlerMap.containsKey(commandName)) {
            LOGGER.warn("Command handler for '{}' is already registered! Overwriting.", commandName);
        }

        commandHandlerMap.put(commandName, commandHandler);
        LOGGER.info("Registered command handler for: {}", commandName);
    }

    public boolean handleCommand(MinecraftClient client, String message){
        if (message == null || message.isEmpty()) {
            return false;
        }

        String[] parts = message.trim().split("\\s+");
        if (parts.length == 0) {
            return false;
        }

        String commandName = parts[1].toLowerCase();
        FriendshipCommand handler = commandHandlerMap.get(commandName);
        if (handler != null) {
            String[] args = Arrays.copyOfRange(parts, 0, parts.length);
            try {
                handler.execute(client, args);
            } catch (Exception e) {
                client.player.networkHandler.sendChatMessage("[Friendship]在尝试执行 " + commandName + " 命令时遇到错误。");
                LOGGER.error("Error executing command handler for '" + commandName + "':", e);
            }
            return true;
        }
        return false;
    }

    public boolean knowsCommand(String commandName) {
        return commandHandlerMap.containsKey(commandName.toLowerCase());
    }

    public void sendHelp(MinecraftClient client) {
        if (commandHandlerMap.isEmpty()) {
            client.player.networkHandler.sendChatMessage("[Friendship]未找到命令。");
            return;
        }
        client.player.networkHandler.sendChatMessage("[Friendship]可用命令：");
        for (FriendshipCommand cmd : commandHandlerMap.values()) {
            client.player.networkHandler.sendChatMessage("[Friendship]" + cmd.getName() + " - " + cmd.getHelp());
        }
    }

    public Collection<FriendshipCommand> getToggleableFeatures() {
        return commandHandlerMap.values().stream()
                .filter(FriendshipCommand::isToggleable) // 方法引用
                .collect(Collectors.toList());
    }
}
