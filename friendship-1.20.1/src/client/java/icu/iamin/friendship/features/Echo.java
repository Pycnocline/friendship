package icu.iamin.friendship.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Echo {
    private static final Logger LOGGER = LoggerFactory.getLogger(Echo.class);

    public void echoChatMessage(String echoString, MinecraftClient client) {
        LOGGER.info("Feature activated: Echo-echoChatMessage");
        String prefix = "[Friendship] ";

        client.player.networkHandler.sendChatMessage(prefix + echoString);
    }

    public void echoCommand(String echoString, MinecraftClient client) {
        LOGGER.info("Feature activated: Echo-echoCommand");

        client.player.networkHandler.sendCommand(echoString);
    }

    public void echoText(MutableText text, MinecraftClient client) {
        LOGGER.info("Feature activated: Echo-echoText");
        MutableText prefix = Text.literal("[Friendship] ").formatted(Formatting.DARK_AQUA, Formatting.ITALIC);
        text = prefix.append(text);

        client.player.sendMessage(text);
    }
}
