package icu.iamin.friendship;

import icu.iamin.friendship.command.CommandManager;
import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.command.commands.*;
import icu.iamin.friendship.features.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class FriendshipClient implements ClientModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("FriendshipClient");
	private CommandManager commandManager;
	private Hello hello;
	private Echo echo;
	private Status status;
	private Inv inv;
	private Action action;
	private Llm llm;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Friendship!");

		this.hello = new Hello();
		this.echo = new Echo();
		this.status = new Status();
		this.inv = new Inv();
		this.action = new Action();
		this.llm = new Llm();

		this.commandManager = new CommandManager();

		registerCommandHandlers();

		ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
			String rawMessage = message.getString().trim();
			MinecraftClient client = MinecraftClient.getInstance();
			LOGGER.info("Get chat message: " + rawMessage);

			boolean commandHandled = false;

			String[] parts = rawMessage.trim().split("\\s+");
			if (parts.length > 1) {
				if (commandManager.knowsCommand(parts[1].toLowerCase())) {
					commandHandled = commandManager.handleCommand(client, rawMessage);
				} else if (rawMessage.contains(":")) {
					rawMessage = parts[0] + " " + rawMessage.substring(rawMessage.indexOf(":") + 1);
					parts = rawMessage.trim().split("\\s+");
					if (parts.length > 1) {
						if (commandManager.knowsCommand(parts[1].toLowerCase())) {
							commandHandled = commandManager.handleCommand(client, rawMessage);
						}
					}
				}
			}

			int index = rawMessage.indexOf("悄");
			if (parts.length > 0 && rawMessage.contains("：") && index != -1) {
				rawMessage = rawMessage.substring(0, index) + " " + rawMessage.substring(rawMessage.indexOf("：") + 1);
				parts = rawMessage.trim().split("\\s+");
				if (parts.length > 1) {
					if (commandManager.knowsCommand(parts[1].toLowerCase())) {
						commandHandled = commandManager.handleCommand(client, rawMessage);
					}
				}
			}

			LOGGER.info("Message handled: " + commandHandled);
		});

		// 注册!help
		commandManager.register(new FriendshipCommand() {
			@Override
			public String getName() {
				return "!help";
			}

			@Override
			public void execute(MinecraftClient client, String[] args) {
				commandManager.sendHelp(client);
			}

			@Override
			public String getHelp() {
				return "获取指令列表.";
			}
		});
		
		// 自动逻辑
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			client.execute(() -> {
				Collection<FriendshipCommand> featuresToTick = commandManager.getToggleableFeatures();

				for (FriendshipCommand feature : featuresToTick) {
					if (feature.isEnabled()) {
						feature.onClientTick(client);
					}
				}
			});
		});

	}

	// 注册命令
	private void registerCommandHandlers() {
		commandManager.register(new HelloCommandHandler(this.hello, this.echo));
		commandManager.register(new BaritoneCommandHandler());
		commandManager.register(new CmdCommandHandler(this.echo));
		commandManager.register(new InvCommandHandler(this.inv, this.echo));
		commandManager.register(new DropCommandHandler(this.inv, this.echo));
		commandManager.register(new DropallCommandHandler(this.inv, this.echo));
		commandManager.register(new SwapCommandHandler(this.inv, this.echo));
		commandManager.register(new StatusCommandHandler(this.status, this.echo));
		commandManager.register(new QuitCommandHandler(this.status, this.echo));
		commandManager.register(new AutorespawnCommandHandler(this.status, this.echo));
		commandManager.register(new AutoeatCommandHandler(this.action, this.echo));
		commandManager.register(new LlmCommandHandler(this.llm, this.inv, this.echo, this.status));

		LOGGER.info("Command handlers registered.");
	}
}
