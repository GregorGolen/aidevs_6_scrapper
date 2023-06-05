import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OpenAICommunicator {
    String getAnswer(String article, String question) throws IOException {

        Properties prop = new Properties();
        try (InputStream input = Main.class.getResourceAsStream("/config.properties")) {
            prop.load(input);
        } catch (IOException e) {
            log.error("Error loading properties file: ", e);
        }
        String openAIKey = prop.getProperty("openai.api.key");

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/json");

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant who always answer in Polish language");

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", " Maximum length for your answer is 200 characters. Basing on the article below, please answer the following question: "+ question + "article: " + article);

        JSONArray messages = new JSONArray();
        messages.put(systemMessage);
        messages.put(userMessage);

        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo");
        json.put("messages", messages);
        json.put("max_tokens", 120);

        RequestBody body = RequestBody.create(json.toString(), mediaType);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + openAIKey)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
