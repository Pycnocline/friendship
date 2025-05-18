package icu.iamin.friendship.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;

public class Status {
    private static final Logger LOGGER = LoggerFactory.getLogger(Status.class);

    public void quit(MinecraftClient client) {
        LOGGER.info("Feature activated: Status-quit");
        MinecraftClient.getInstance().scheduleStop();
    }

    public ArrayList<Object> getStatus(MinecraftClient client) {
        LOGGER.info("Feature activated: Status-getStatus");

        ArrayList<Object> statusList = new ArrayList<>();

        statusList.add(String.format("%.1f", (float)client.player.getHealth()));
        statusList.add(String.format("%.1f", (float)client.player.getMaxHealth()));

        HungerManager playerHunger = client.player.getHungerManager();
        statusList.add(String.format("%.1f", (float)playerHunger.getFoodLevel()));
        statusList.add(String.format("%.1f", (float)playerHunger.getPrevFoodLevel()));

        Vec3d position = client.player.getPos();
        Vec3d roundedPosition = new Vec3d(
                Math.round(position.x * 10.0) / 10.0,
                Math.round(position.y * 10.0) / 10.0,
                Math.round(position.z * 10.0) / 10.0
        );
        statusList.add(roundedPosition);

        if (!client.player.getActiveStatusEffects().isEmpty()) {
            for (StatusEffectInstance effectInstance : client.player.getActiveStatusEffects().values()) {
                StatusEffect effect = effectInstance.getEffectType();
                String effectName = Objects.requireNonNull(Registries.STATUS_EFFECT.getId(effect)).toString();
                int amplifier = effectInstance.getAmplifier();
                int duration = effectInstance.getDuration();

                Object[] effectDetail = new Object[3];
                effectDetail[0] = effectName;
                effectDetail[1] = amplifier + 1;
                effectDetail[2] = duration / 20;
                statusList.add(effectDetail);
            }
        }

        return statusList;
    }

    public boolean respawn(MinecraftClient client) {
        boolean success = false;

        if (client != null && client.player != null && client.currentScreen instanceof DeathScreen) {
            LOGGER.info("Feature activated: Status-resurrect");
            client.player.requestRespawn();
            success = true;
        }

        return success;
    }
}
