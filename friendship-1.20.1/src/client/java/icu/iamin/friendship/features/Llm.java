package icu.iamin.friendship.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Llm {
    private static final Logger LOGGER = LoggerFactory.getLogger(Llm.class);

    public String callLLMAPI(String LLM_API_URL, String LLM_API_KEY, String LLM_MODEL, String LLM_PROMPT, String userInput) {
        try {
            URL url = new URL(LLM_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + LLM_API_KEY);
            connection.setDoOutput(true);

            String jsonInput = "{"
                    + "\"model\": \"" + LLM_MODEL +"\","
                    + "\"messages\": ["
                    + "  {"
                    + "    \"role\": \"system\","
                    + "    \"content\": \"" + escapeJson(LLM_PROMPT) + "\""
                    + "  },"
                    + "  {"
                    + "    \"role\": \"user\","
                    + "    \"content\": \"" + escapeJson(userInput) + "\""
                    + "  }"
                    + "]"
                    + "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    return parseResponse(response.toString());
                }
            } else {
                return "API 错误，状态码：" + responseCode;
            }
        } catch (IOException e) {
            return "请求失败：" + e.getMessage();
        }
    }

    private String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    private String parseResponse(String jsonResponse) {
        try {
            JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray choices = root.getAsJsonArray("choices");
            if (choices.size() == 0) return "未收到有效响应";

            JsonObject message = choices.get(0).getAsJsonObject()
                    .getAsJsonObject("message");
            return message.get("content").getAsString();
        } catch (Exception e) {
            return "解析响应失败";
        }
    }
}
