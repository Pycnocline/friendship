package icu.iamin.friendship.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Inv {
    private static final Logger LOGGER = LoggerFactory.getLogger(Inv.class);

    public Map<Integer, Object[]> getInvList(MinecraftClient client) {
        LOGGER.info("Feature activated: Status-getInvList");

        Map<Integer, Object[]> invMap = new HashMap<>();;
        PlayerInventory inventory = client.player.getInventory();
        int totalSlots = inventory.size();

        for (int i = 0; i < totalSlots; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                String itemName = stack.getItem().getName().getString();
                int itemCount = stack.getCount();

                invMap.put(i, new Object[]{itemName, itemCount});
            }
        }

        return invMap;
    }

    public boolean drop(String[] parts, MinecraftClient client){
        LOGGER.info("Feature activated: Inv-drop");

        boolean success = false;

        if (parts.length == 0 || parts.length % 2 != 0) {
            return false;
        }

        ScreenHandler currentScreenHandler = client.player.currentScreenHandler;
        int syncId = currentScreenHandler.syncId;
        PlayerInventory inventory = client.player.getInventory();

        for (int i = 0; i < parts.length; i += 2) {
            int inventorySlot;
            int amount;

            inventorySlot = Integer.parseInt(parts[i]);
            amount = Integer.parseInt(parts[i + 1]);

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

        return success;
    }

    private int convertInventorySlotToScreenHandler(PlayerInventory inventory, int slot, ScreenHandler handler) {
        for (Slot handlerSlot : handler.slots) {
            if (handlerSlot.inventory == inventory && handlerSlot.getIndex() == slot) {
                return handlerSlot.id;
            }
        }
        return -1;
    }

    public boolean dropall(MinecraftClient client) {
        LOGGER.info("Feature activated: Inv-dropall");

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
        return success;
    }

    public boolean swap(String[] parts, MinecraftClient client) {
        if (parts.length % 2 != 0) {
            return false;
        }

        ScreenHandler handler = client.player.currentScreenHandler;
        PlayerInventory inventory = client.player.getInventory();
        boolean success = false;

        for (int i = 0; i < parts.length; i += 2) {
            int slotA = Integer.parseInt(parts[i]);
            int slotB = Integer.parseInt(parts[i + 1]);

            if (slotA < 0 || slotA > 40 || slotB < 0 || slotB > 40) continue;

            int screenSlotA = convertInventorySlotToScreenHandler(inventory, slotA, handler);
            int screenSlotB = convertInventorySlotToScreenHandler(inventory, slotB, handler);
            if (screenSlotA == -1 || screenSlotB == -1) continue;

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
        }

        return success;
    }
}
