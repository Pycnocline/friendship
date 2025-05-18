package icu.iamin.friendship.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action.class);
    private int eatCooldownTicks = 0;
    private static final int EAT_COOLDOWN_DURATION = 70; // ticks

    public boolean eatOffHand(MinecraftClient client) {
        KeyBinding useKey = MinecraftClient.getInstance().options.useKey;
        if (eatCooldownTicks == 30) {
            useKey.setPressed(false);
        }

        if (client.currentScreen != null) {
            return false;
        }

        if (eatCooldownTicks > 0) {
            eatCooldownTicks--;
            return false;
        }

        if (client.player.getAbilities().creativeMode || client.player.isSpectator()) {
            return false;
        }

        if (!isOffhandItemFood(client)) {
            return false;
        }


        useKey.setPressed(true);
        eatCooldownTicks = EAT_COOLDOWN_DURATION;

        return true;
    }

    private static boolean isOffhandItemFood(MinecraftClient client) {
        if (client == null || client.player == null) {
            return false;
        }
        PlayerEntity player = client.player;
        ItemStack offHandStack = player.getOffHandStack();

        return !offHandStack.isEmpty() && offHandStack.isFood();
    }

}
