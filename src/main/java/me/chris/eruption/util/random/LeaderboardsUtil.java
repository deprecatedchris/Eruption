package me.chris.eruption.util.random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class LeaderboardsUtil {

    private static final String KEY = "ce07a742-cccc-4888-b595-a62a7dd3bb19";

    public static JsonArray getTop10(String ladder) {
        OkHttpClient client = new OkHttpClient();
        JsonParser parser = new JsonParser();

        try {
            Request request = new Request
                    .Builder()
                    .url("http://89.163.206.202:8080/api/" + KEY + "/practice/leaderboards/" + ladder)
                    .build();

            Response response = client
                    .newCall(request)
                    .execute();

            if (response.isSuccessful()) {
                JsonElement json = parser.parse(response.body().string());
                JsonObject object = json.getAsJsonObject();
                return object.get("entries").getAsJsonArray();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JsonArray();
    }

}
