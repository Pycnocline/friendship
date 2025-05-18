package icu.iamin.friendship.command.commands;

import icu.iamin.friendship.command.FriendshipCommand;
import icu.iamin.friendship.features.*;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LlmCommandHandler implements FriendshipCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(LlmCommandHandler.class);
    private final Llm llm;
    private final Inv inv;
    private final Echo echo;
    private final Status status;

    private String API_URL;
    private String API_KEY;
    private String MODEL;
    private String PROMPT = "你是一个接入了mc的智能助理玩家，你可以在回复的结尾通过用[]包裹你想要执行的指令来操纵一个你目前在游戏中的角色。你可以通过这个方法执行所有原版的指令，例如：'/gamemode creative';或是使用Baritone这个插件的指令，例如：'#mine minecraft:dirt';可以使用另外一些已经支持的特殊自定义命令。自定义命令列表如下：1.'!drop <slot> <amount> [<slot> <amount>...]'丢弃指定槽位的物品。<slot>改为槽位编号，<amount>改为物品数量。 例如'!drop 0 1 20 4'; 2.'!dropall'丢弃所有物品（包括装备栏和副手）; 3.'!swap <slot1-1> <slot1-2> [<slot2-1> <slot2-2>...]' 根据槽位编号交换身上的物品位置，可以用来装备物品等。 例如'!swap 0 1 2 3'会交换0号和1号槽，2号和3号槽。";

    public LlmCommandHandler(Llm llm, Inv inv, Echo echo, Status status) {
        this.llm = llm;
        this.inv = inv;
        this.echo = echo;
        this.status = status;
    }

    @Override
    public String getName() {
        return "!llm";
    }

    @Override
    public void execute(MinecraftClient client, String[] args) {
        client.execute(() -> {
            if (args.length >= 5 && Objects.equals(args[2], "set")) {
                if (Objects.equals(args[3], "url")) {
                    API_URL = args[4];
                    echo.echoChatMessage("Llm api url已设置。", client);
                } else if (Objects.equals(args[3], "key")) {
                    API_KEY = args[4];
                    echo.echoChatMessage("Llm api key已设置。", client);
                } else if (Objects.equals(args[3], "model")) {
                    MODEL = args[4];
                    echo.echoChatMessage("Llm 模型已设置。", client);
                } else if (Objects.equals(args[3], "prompt")) {
                    PROMPT = args[4];
                    echo.echoChatMessage("Llm 提示词已设置。", client);
                }
            } else if (args.length > 2) {
                String[] usedArgs = Arrays.copyOfRange(args, 2, args.length);
                String message = String.join(" ", usedArgs);

                PROMPT += "以下是你的物品栏（可能为空）{";
                Map<Integer, Object[]> invList = inv.getInvList(client);
                invList.forEach((key, valueArray) -> {
                    if (key != null) {
                        PROMPT +="槽位:" + key + " -> " + (valueArray != null ? Arrays.toString(valueArray) : "null" + " ");
                    }
                });
                PROMPT += "} 以下是你的状态（可能为空）{";
                ArrayList<Object> statusList = status.getStatus(client);
                PROMPT +="生命：" + statusList.get(0) + " / " + statusList.get(1);
                PROMPT +=" 饱食：" + statusList.get(2) + " / " + statusList.get(3);
                PROMPT +=" 位置：" + statusList.get(4);
                PROMPT +=" 效果：";
                if (statusList.size() < 6 ) {
                    PROMPT += " - 无";
                } else {
                    for (int i = 5; i < statusList.size(); i++) {
                        Object effectDetailList = statusList.get(i);
                        Object[] innerArray = (Object[]) effectDetailList;
                        PROMPT += " - " + innerArray[0] + " lv." + innerArray[1] + " " + innerArray[2] + "s";
                    }
                }
                PROMPT += "} 在和你交谈的玩家ID为：" + args[0] + "。如果上文中提到的ID被<>包裹则意思为公屏聊天。";

                String response = llm.callLLMAPI(API_URL, API_KEY, MODEL, PROMPT, message);
                LOGGER.info("Raw LLM response string: " + response.replace("\n", "\\n").replace("\r", "\\r"));

                List<String> responseCmd = extractContent(response);
                LOGGER.info("Extracted commands: " + responseCmd.toString());

                if (!responseCmd.isEmpty()) {
                    for (int i = 0; i < responseCmd.size(); i++) {
                        String commandToSend = responseCmd.get(i).trim();
                        if (!commandToSend.isEmpty() && !commandToSend.contains("\n") && !commandToSend.contains("\r")) {
                            LOGGER.info("Sending command: " + commandToSend);
                            if (commandToSend.startsWith("/")) {
                                echo.echoCommand(commandToSend, client);
                            }
                            client.player.networkHandler.sendChatMessage(commandToSend);
                        } else if (!commandToSend.isEmpty()) {
                            LOGGER.warn("Command contained newline, not sending: " + commandToSend.replace("\n", "\\n").replace("\r", "\\r"));
                        }
                    }
                }

                String modifiedText = response.replaceAll("\\[(.*?)\\]", "");
                LOGGER.info("Text part from LLM (before cleaning): [" + modifiedText.replace("\n", "\\n").replace("\r", "\\r") + "]");

                String cleanedTextForChat = modifiedText.replace("\n", " ").replace("\r", " ").trim();

                LOGGER.info("Text part from LLM (after cleaning for chat): [" + cleanedTextForChat + "]");

                if (!cleanedTextForChat.isEmpty()) {
                    echo.echoChatMessage(cleanedTextForChat, client);
                }
            }
        });
    }

    public static List<String> extractContent(String text) {
        List<String> results = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(text);

        // 查找所有匹配项
        while (matcher.find()) {
            results.add(matcher.group(1));
        }

        return results;
    }

    @Override
    public String getHelp() {
        return "使用api连接到llm进行对话。用法1：!llm set <url|key|model|prompt> <值> 来设定api相关参数（更改prompt可能导致无法执行游戏内操作）。用法2：!llm <对话内容> 使用此命令来和配置好了的llm交互";
    }
}
