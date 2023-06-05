import lombok.extern.slf4j.Slf4j;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class TaskReporter {
    void reportTask(String token, String answer) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        JsonObject ans = Json.createObjectBuilder().add("answer", answer).build();

        HttpRequest answerRequest = HttpRequest.newBuilder()
                .uri(new URI("https://zadania.aidevs.pl/answer/" + token))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ans.toString()))
                .build();

        HttpResponse<String> answerResponse = client.send(answerRequest, HttpResponse.BodyHandlers.ofString());
        log.info("Answer response code: {}", answerResponse.statusCode());
    }
}

