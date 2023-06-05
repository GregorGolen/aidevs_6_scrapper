import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WebSourcing {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public static String getFromUrlWithRetry(String url, int maxRetries) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                .header("Referer", "https://www.google.com")
                .build();

        int attempts = 0;
        while (attempts < maxRetries) {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    if (attempts >= maxRetries) {
                        throw new IOException("Unexpected code " + response);
                    }
                } else {
                    return response.body().string();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            attempts++;
        }
        throw new Exception("Failed to fetch content after " + maxRetries + " attempts.");
    }
}
