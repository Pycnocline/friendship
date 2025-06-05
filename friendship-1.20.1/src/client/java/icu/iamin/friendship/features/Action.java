package icu.iamin.friendship.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action.class);
    private int eatCooldownTicks = 0;
    private int attackCooldownTicks = 0;
    private static final int EAT_COOLDOWN_DURATION = 70; // ticks
    private static final int ATTACK_COOLDOWN_DURATION = 10;

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

    public LivingEntity lookAtNearestLivingEntity(double distance, boolean isEnemy, MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        double MAX_LOOK_DISTANCE = distance;

        Vec3d playerPos = player.getPos();
        Box searchBox = new Box(
                playerPos.subtract(MAX_LOOK_DISTANCE, MAX_LOOK_DISTANCE, MAX_LOOK_DISTANCE),
                playerPos.add(MAX_LOOK_DISTANCE, MAX_LOOK_DISTANCE, MAX_LOOK_DISTANCE)
        );

        if (!isEnemy) {
            List<LivingEntity> nearbyLivingEntities = client.world.getEntitiesByClass(
                    LivingEntity.class,
                    searchBox,
                    entity -> entity.isAlive() &&
                            entity != player &&
                            player.squaredDistanceTo(entity) <= MAX_LOOK_DISTANCE * MAX_LOOK_DISTANCE &&
                            player.canSee(entity)
            );

            if (nearbyLivingEntities.isEmpty()) {
                return null;
            }

            Optional<LivingEntity> closestEntityOpt = nearbyLivingEntities.stream()
                    .min(Comparator.comparingDouble(player::squaredDistanceTo));

            if (closestEntityOpt.isPresent()) {
                LivingEntity targetEntity = closestEntityOpt.get();
                setPlayerLook(player, targetEntity.getEyePos());
                return targetEntity;
            }
        } else {
            List<LivingEntity> nearbyLivingEntities = client.world.getEntitiesByClass(
                    LivingEntity.class,
                    searchBox,
                    entity -> entity.isAlive() &&
                            entity != player &&
                            player.squaredDistanceTo(entity) <= MAX_LOOK_DISTANCE * MAX_LOOK_DISTANCE &&
                            player.canSee(entity) &&
                            entity instanceof Monster
            );

            if (nearbyLivingEntities.isEmpty()) {
                return null;
            }

            Optional<LivingEntity> closestEntityOpt = nearbyLivingEntities.stream()
                    .min(Comparator.comparingDouble(player::squaredDistanceTo));

            if (closestEntityOpt.isPresent()) {
                LivingEntity targetEntity = closestEntityOpt.get();
                setPlayerLook(player, targetEntity.getEyePos());
                return targetEntity;
            }
        }

        return null;
    }

    public static void setPlayerLook(ClientPlayerEntity player, Vec3d targetLookPos) {
        if (player == null) return;

        Angles angles = calculateAngles(player.getEyePos(), targetLookPos);
        player.setYaw(angles.yaw);
        player.setPitch(angles.pitch);
    }

    private record Angles(float yaw, float pitch) {}

    private static Angles calculateAngles(Vec3d playerEyePos, Vec3d targetPos) {
        Vec3d direction = targetPos.subtract(playerEyePos).normalize(); // 标准化方向向量

        double dx = direction.x;
        double dy = direction.y;
        double dz = direction.z;

        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);

        float pitch = (float) (-Math.toDegrees(Math.asin(dy)));

        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);

        return new Angles(yaw, pitch);
    }

    public boolean attack(LivingEntity entity, MinecraftClient client,boolean tryattack) {
        LOGGER.info(String.valueOf(attackCooldownTicks));
        if (client.currentScreen != null) {
            return false;
        }

        if (attackCooldownTicks > 0) {
            attackCooldownTicks--;
            return false;
        }

        if (tryattack) {
            client.interactionManager.attackEntity(client.player, entity);
            attackCooldownTicks = ATTACK_COOLDOWN_DURATION;
            return true;
        }

        return false;
    }

}
