import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class Main {
    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        try (InputStream input = Main.class.getResourceAsStream("/config.properties")) {
            prop.load(input);
        } catch (IOException e) {
            log.error("Error loading properties file: ", e);
        }
        String myKey = prop.getProperty("api.key");

        TaskObtainer taskObtainer = new TaskObtainer(myKey);
        String task = taskObtainer.getTask();

        JSONObject jsonObject = new JSONObject(task);
        String question = jsonObject.getString("question");
        String inputURL = jsonObject.getString("input");

        String article = WebSourcing.getFromUrlWithRetry(inputURL, 5);

        OpenAICommunicator openAICommunicator = new OpenAICommunicator();

        String answer = openAICommunicator.getAnswer(article, question);

        JSONObject jsonObjectWithAnswer = new JSONObject(answer);

        JSONArray choices = jsonObjectWithAnswer.getJSONArray("choices");

        JSONObject firstChoice = choices.getJSONObject(0);

        JSONObject message = firstChoice.getJSONObject("message");

        String pureAnswer = message.getString("content");

        TaskReporter taskReporter = new TaskReporter();
        taskReporter.reportTask(taskObtainer.getToken(), pureAnswer);

    }
}
