package icu.iamin.friendship;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;


public class FriendshipClient implements ClientModInitializer {

	private boolean isAutoResurrectEnabled = false;
	private boolean isAutoEatEnabled = false;

	private static final String CMD_OPEN_INVENTORY = "!openinventory";
	private static final String CMD_HELLO = "!hello";
	private static final String CMD_BARITONE = "!baritone #";
	private static final String CMD_CMD = "!cmd /";
	private static final String CMD_LIST = "!list";
	private static final String CMD_DROP = "!drop";
	private static final String CMD_DROPALL = "!dropall";
	private static final String CMD_SWAP = "!swap";

	private static final String OPTION_AUTORESURRECT = "!autoresurrect";
	private static final String OPTION_AUTOEAT = "!autoeat";
	
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
			} else if (messageContent.contains(CMD_DROPALL)) {
				handleDropAllCommand(client);
			} else if (messageContent.contains(CMD_DROP)) {
				handleDropCommand(client, messageContent);
			} else if (messageContent.contains(CMD_SWAP)) {
				handleSwapCommand(client, messageContent);
			} else if (messageContent.contains(OPTION_AUTORESURRECT)) {
				handleAutoResurrectOption(client);
			} else if (messageContent.contains(OPTION_AUTOEAT)) {
				handleAutoEatOption(client);
			}


		});


		// 自动逻辑
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			handleAutoResurrect(client);
			handleAutoEat(client);
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
				System.out.println("[friendship]Function success: !openinventory");
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
				client.player.networkHandler.sendChatMessage("[friendship]Hello from friendship!");
				System.out.println("[friendship]Function success: !hello");
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
				System.out.println("[friendship]Function success: !cmd");
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

				System.out.println("[friendship]Function success: !list");
			} else {
				System.out.println("[friendship]Error: Player is null when trying to use list command.");
			}
		});
	}

	// 丢下物品
	private void handleDropCommand(MinecraftClient client, String inputString) {
		System.out.println("Executing command '" + CMD_DROP + "'.");
		client.execute(() -> {
			if (client.player != null) {
				// 去前缀，分割
				String argsString;
				int cmdIndex = inputString.indexOf(CMD_DROP);
				if (cmdIndex == -1) {
					client.player.sendMessage(Text.literal("[friendship]Error: Command not found."), false);
					return;
				}
				argsString = inputString.substring(cmdIndex + CMD_DROP.length()).trim();
				String[] parts = argsString.split(" ");

				// 检查参数数量是否为偶数且大于0
				if (parts.length == 0 || parts.length % 2 != 0) {
					client.player.networkHandler.sendChatMessage("[friendship]Error: Syntax error.");
					return;
				}

				// 获取当前屏幕处理器和玩家物品栏
				ScreenHandler currentScreenHandler = client.player.currentScreenHandler;
				int syncId = currentScreenHandler.syncId;
				PlayerInventory inventory = client.player.getInventory();

				boolean success = false;
				for (int i = 0; i < parts.length; i += 2) {
					int inventorySlot;
					int amount;

					inventorySlot = Integer.parseInt(parts[i]);
					amount = Integer.parseInt(parts[i + 1]);

					// 检查槽位有效性 (0-40 包括副手)
					if (inventorySlot < 0 || inventorySlot > 40) {
						continue;
					}

					if (amount <= 0) {
						continue;
					}

					ItemStack stack = inventory.getStack(inventorySlot);
					if (stack.isEmpty()) {
						continue;
					}

					// 转换物品栏槽位到当前屏幕处理器的槽位编号
					int screenHandlerSlot = convertInventorySlotToScreenHandler(inventory, inventorySlot, currentScreenHandler);
					if (screenHandlerSlot == -1) {
						continue;
					}

					int amountToDrop = Math.min(amount, stack.getCount());
					if (amountToDrop <= 0) continue;

					// 执行丢弃操作
					for (int j = 0; j < amountToDrop; j++) {
                        if (client.interactionManager != null) {
                            client.interactionManager.clickSlot(
                                    syncId,
                                    screenHandlerSlot,
                                    0,
                                    SlotActionType.THROW,
                                    client.player
                            );
                        }
                    }
					success = true;
				}

				if (success) {
					System.out.println("[friendship]Function success: !drop");
				}
			} else {
				System.out.println("[friendship]Error: Player is null.");
			}
		});
	}

	// 转换物品栏槽位到屏幕处理器中的实际槽位编号
	private int convertInventorySlotToScreenHandler(PlayerInventory inventory, int slot, ScreenHandler handler) {
		for (Slot handlerSlot : handler.slots) {
			if (handlerSlot.inventory == inventory && handlerSlot.getIndex() == slot) {
				return handlerSlot.id;
			}
		}
		return -1;
	}

	// 丢下所有物品
	private void handleDropAllCommand(MinecraftClient client) {
		System.out.println("Executing command '" + CMD_DROPALL + "'.");
        if (client.player != null) {
			PlayerInventory inventory = client.player.getInventory();

			ScreenHandler handler = client.player.currentScreenHandler;
			int syncId = handler.syncId;

			boolean success = false;
			// 遍历所有玩家槽位（0-40）
			for (int slot = 0; slot <= 40; slot++) {
				ItemStack stack = inventory.getStack(slot);
				if (stack.isEmpty()) continue;

				// 转换为当前屏幕处理器的槽位编号
				int screenSlot = convertInventorySlotToScreenHandler(inventory, slot, handler);
				if (screenSlot == -1) continue;

				// 执行丢弃操作
                if (client.interactionManager != null) {
                    client.interactionManager.clickSlot(
                            syncId,
                            screenSlot,
                            1,
                            SlotActionType.THROW,
                            client.player
                    );
                }
                success = true;
			}
			if (success) {
				System.out.println("[friendship]Dropped all items.");
			}
		}
	}

	// 交换物品位置
	private void handleSwapCommand(MinecraftClient client, String inputString) {
		client.execute(() -> {
			if (client.player == null) return;

			// 提取命令参数
			int cmdIndex = inputString.indexOf(CMD_SWAP);
			if (cmdIndex == -1) return;

			String argsString = inputString.substring(cmdIndex + CMD_SWAP.length()).trim();
			String[] parts = argsString.split("\\s+");

			// 参数合法性检查
			if (parts.length % 2 != 0) {
				client.player.sendMessage(Text.literal("[friendship]Error: Syntax error."), false);
				return;
			}

			ScreenHandler handler = client.player.currentScreenHandler;
			PlayerInventory inventory = client.player.getInventory();
			boolean success = false;

			// 遍历所有槽位对
			for (int i = 0; i < parts.length; i += 2) {
				try {
					int slotA = Integer.parseInt(parts[i]);
					int slotB = Integer.parseInt(parts[i + 1]);

					// 槽位有效性检查（0-40）
					if (slotA < 0 || slotA > 40 || slotB < 0 || slotB > 40) continue;

					// 转换为屏幕处理器槽位
					int screenSlotA = convertInventorySlotToScreenHandler(inventory, slotA, handler);
					int screenSlotB = convertInventorySlotToScreenHandler(inventory, slotB, handler);
					if (screenSlotA == -1 || screenSlotB == -1) continue;

					// 执行三次点击完成交换
                    if (client.interactionManager != null) {
                        client.interactionManager.clickSlot(
                                handler.syncId, screenSlotA, 0,
                                SlotActionType.PICKUP, client.player
                        );
						client.interactionManager.clickSlot(
								handler.syncId, screenSlotB, 0,
								SlotActionType.PICKUP, client.player
						);
						client.interactionManager.clickSlot(
								handler.syncId, screenSlotA, 0,
								SlotActionType.PICKUP, client.player
						);
                    }

					success = true;
				} catch (NumberFormatException ignored) {}
			}

			if (success) {
				System.out.println("[friendship]Swap complete.");
			}
		});
	}

	// 自动复活开关
	private void handleAutoResurrectOption(MinecraftClient client) {
		System.out.println("Executing command '" + OPTION_AUTORESURRECT + "'.");
		client.execute(() -> {
			if (client.player != null) {
				isAutoResurrectEnabled = !isAutoResurrectEnabled;
				client.player.networkHandler.sendChatMessage("[friendship]Auto resurrect: " + isAutoResurrectEnabled);
				System.out.println("[friendship]Function success: !autoresurrect");
			} else {
				System.out.println("[friendship]Error: Player is null when trying to use command.");
			}
		});
	}

	// 自动复活
	private void handleAutoResurrect(MinecraftClient client) {
		if (isAutoResurrectEnabled && client != null && client.player != null && client.currentScreen instanceof DeathScreen) {
			client.player.networkHandler.sendChatMessage("[friendship]Auto resurrect triggered.");
			client.player.requestRespawn();
		}
	}

	// 自动进食开关
	private void handleAutoEatOption(MinecraftClient client) {
		System.out.println("Executing command '" + OPTION_AUTOEAT + "'.");
		client.execute(() -> {
			if (client.player != null) {
				isAutoEatEnabled = !isAutoEatEnabled;
				client.player.networkHandler.sendChatMessage("[friendship]Auto eat: " + isAutoEatEnabled);
				System.out.println("[friendship]function success: !autoeat");
			} else {
				System.out.println("[friendship]Error: Player is null when trying to use command.");
			}
		});
	}

	// 自动进食
	private void handleAutoEat(MinecraftClient client) {
		// 确保 Mod 功能开启，并且客户端和玩家对象存在
		if (!isAutoEatEnabled || client.player == null || client.interactionManager == null) return;

		// 仅在生存/冒险模式且可操作时运行
		if (client.player.getAbilities().creativeMode ||
				client.player.isUsingItem() ||
				client.player.isHolding(Items.BOW)) {
			return;
		}

		// 获取饥饿管理组件
		HungerManager hunger = client.player.getHungerManager();

		if (hunger.getFoodLevel() >= 20) {
			return;
		}

		// 触发进食条件：饥饿值低于15 或 同时需要生命恢复
		if (hunger.getFoodLevel() >= 15 &&
				client.player.getHealth() >= client.player.getMaxHealth()) {
			return;
		}

		// 寻找最佳食物
		FoodSlot bestFood = findBestFood(client.player);
		if (bestFood == null) return;

		// 切换到食物槽位
		if (client.player.getInventory().selectedSlot != bestFood.slot) {
			client.player.getInventory().selectedSlot = bestFood.slot;
			client.player.networkHandler.sendPacket(
					new UpdateSelectedSlotC2SPacket(bestFood.slot)
			);
		}

		// 执行进食动作（右键长按）
		if (!client.player.isUsingItem()) {
			client.interactionManager.interactItem(client.player,
					Hand.MAIN_HAND);
		}

	}

	// 食物槽位信息容器
	private record FoodSlot(int slot, ItemStack stack, float priority) {}

	// 查找最佳食物算法
	private FoodSlot findBestFood(PlayerEntity player) {
		PlayerInventory inv = player.getInventory();
		FoodSlot best = null;

		// 优先检测已手持的食物
		ItemStack mainHand = inv.getMainHandStack();
		if (isEdible(mainHand)) {
			best = new FoodSlot(inv.selectedSlot, mainHand,
					calculateFoodPriority(mainHand));
		}

		// 遍历物品栏
		for (int slot = 0; slot < 8; slot++) {
			if (slot == inv.selectedSlot) continue; // 跳过已检查的手持物品

			ItemStack stack = inv.getStack(slot);
			if (!isEdible(stack)) continue;

			float priority = calculateFoodPriority(stack);
			if (best == null || priority > best.priority()) {
				best = new FoodSlot(slot, stack, priority);
			}
		}
		return best;
	}

	// 食物有效性检查
	private boolean isEdible(ItemStack stack) {
		return stack.getItem().isFood() &&
				!stack.getItem().hasRecipeRemainder();
	}

	// 食物优先级计算（考虑当前需求）
	private float calculateFoodPriority(ItemStack stack) {
		PlayerEntity player = MinecraftClient.getInstance().player;

		
		HungerManager hunger = player.getHungerManager();

		FoodComponent food = stack.getItem().getFoodComponent();
		float baseScore = food.getHunger() * (1 + food.getSaturationModifier());

		// 生命恢复需求权重
		if (player.getHealth() < player.getMaxHealth()) {
			baseScore *= (food.getHunger() >= 4) ? 1.5f : 0.8f;
		}

		// 低饥饿时优先快速恢复
		if (hunger.getFoodLevel() < 10) {
			baseScore *= (food.getSaturationModifier() > 0.6f) ? 1.2f : 0.9f;
		}

		return baseScore;
	}
}
