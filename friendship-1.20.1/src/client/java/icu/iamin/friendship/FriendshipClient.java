package icu.iamin.friendship;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;


public class FriendshipClient implements ClientModInitializer {

	private static final String CMD_OPEN_INVENTORY = "!openinventory";
	private static final String CMD_HELLO = "!hello";
	private static final String CMD_BARITONE = "!baritone #";
	private static final String CMD_CMD = "!cmd /";
	private static final String CMD_LIST = "!list";
	private static final String CMD_DROP = "!drop";


	@Override
	public void onInitializeClient() {

		System.out.println("[friendship]Friendship loaded");

		ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
			String messageContent = message.getString().trim();
			MinecraftClient client = MinecraftClient.getInstance();
			System.out.println("[friendship]get chat message: " + messageContent);

			if (messageContent.contains(CMD_OPEN_INVENTORY)) {
				handleOpenInventoryCommand(client);
			} else if (messageContent.contains(CMD_HELLO)) {
				handleSayHelloCommand(client);
			} else if (messageContent.contains(CMD_BARITONE)) {
				handleBaritoneCommand(client, messageContent);
			} else if (messageContent.contains(CMD_CMD)) {
				handleUseCommand(client, messageContent);
			} else if (messageContent.contains(CMD_LIST)) {
				handleListCommand(client);
			} else if (messageContent.contains(CMD_DROP)) {
				handleDropCommand(client);
			}


		});

	}

	// 打开背包
	private void handleOpenInventoryCommand(MinecraftClient client) {
		System.out.println("Executing command '" + CMD_OPEN_INVENTORY + "'.");
		client.execute(() -> {
			if (client.player != null) {
				// 如果当前在聊天界面，先关闭聊天界面再打开背包
				if (client.currentScreen instanceof ChatScreen) {
					client.setScreen(null);
				}
				client.setScreen(new InventoryScreen(client.player)); // 打开背包界面
				System.out.println("[friendship]function success: !openinventory");
			} else {
				System.out.println("[friendship]Error: Player is null when trying to open inventory in execute block.");
			}
		});
	}

	// hello
	private void handleSayHelloCommand(MinecraftClient client) {
		System.out.println("Executing command '" + CMD_HELLO + "'.");
		client.execute(() -> {
			if (client.player != null) {
				client.player.networkHandler.sendChatMessage("Hello from friendship!");
				System.out.println("[friendship]function success: !hello");
			} else {
				System.out.println("[friendship]Error: Player is null when trying to send message in execute block.");
			}
		});
	}

	// baritone命令
	private void handleBaritoneCommand(MinecraftClient client, String inputString) {
		System.out.println("Executing command '" + CMD_BARITONE + "'.");
		client.execute(() -> {
			if (client.player != null) {

				// 查找 '#' 字符在字符串中第一次出现的位置
				int hashIndex = inputString.indexOf("#");
				var baritoneCommand = "";
				if (hashIndex != -1) {
					baritoneCommand = inputString.substring(hashIndex);
				}

				client.player.networkHandler.sendChatMessage(baritoneCommand);
				System.out.println("[friendship]function success: !baritone");
			} else {
				System.out.println("[friendship]Error: Player is null when trying to use baritone command.");
			}
		});
	}

	// 使用指令
	private void handleUseCommand(MinecraftClient client, String inputString) {
		System.out.println("Executing command '" + CMD_CMD + "'.");
		client.execute(() -> {
			if (client.player != null) {

				// 查找 '/' 字符在字符串中第一次出现的位置
				int hashIndex = inputString.indexOf("/");
				var command = "";
				if (hashIndex != -1) {
					command = inputString.substring(hashIndex + 1);
				}

				client.player.networkHandler.sendChatCommand(command);
				System.out.println("[friendship]function success: !cmd");
			} else {
				System.out.println("[friendship]Error: Player is null when trying to use cmd command.");
			}
		});
	}

	// 列出物品栏中的所有物品
	private void handleListCommand(MinecraftClient client) {
		System.out.println("Executing command '" + CMD_LIST + "'.");
		client.execute(() -> {
			if (client.player != null) {
				client.player.networkHandler.sendChatMessage("--- Inventory Contents ---");
				// 获取玩家背包实例
				PlayerInventory inventory = client.player.getInventory();
				// 获取玩家背包的总槽位数
				int totalSlots = inventory.size();

				// 遍历背包的所有槽位 (从索引 0 到 totalSlots - 1)
				for (int i = 0; i < totalSlots; i++) {
					// 获取当前槽位的物品堆栈
					ItemStack stack = inventory.getStack(i);

					// 检查物品堆栈是否不为空 (即这个槽位有物品)
					if (!stack.isEmpty()) {
						// 获取物品名称 (作为 Text 对象，然后转为 String)
						String itemName = stack.getItem().getName().getString();
						// 获取物品堆栈的数量
						int itemCount = stack.getCount();

						// 格式化输出信息，包含槽位索引、数量和物品名称
						String slotInfo = "Slot " + i + ": " + itemCount + " x " + itemName;

						// 发送这条信息到聊天框
						client.player.networkHandler.sendChatMessage(slotInfo);
					}
				}
				client.player.networkHandler.sendChatMessage("------------------------");

				System.out.println("[friendship]function success: !list");
			} else {
				System.out.println("[friendship]Error: Player is null when trying to use list command.");
			}
		});
	}


	// 丢下物品
	private void handleDropCommand(MinecraftClient client) {
		System.out.println("Executing command '" + CMD_DROP + "'.");
		client.execute(() -> {
			if (client.player != null) {

				System.out.println("[friendship]function success: !drop");
			} else {
				System.out.println("[friendship]Error: Player is null when trying to use drop command.");
			}
		});
	}
}