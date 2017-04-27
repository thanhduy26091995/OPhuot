package com.thanhduy.ophuot.push_notification;

import android.os.AsyncTask;
import android.util.Log;

import com.thanhduy.ophuot.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by buivu on 05/12/2016.
 */
public class PushMessage {
    private static final String TAG = "PushMessage";
    private static OkHttpClient mClient = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static void sendMessage(final JSONArray recipients, final String title, final String body, final String icon, final String uid, final String deviceToken, final String avatar) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);
                    notification.put("icon", "ic_launcher");
                    notification.put("sound", "/res/raw/notification");
                    notification.put("color", "#8BC34A");

                    JSONObject data = new JSONObject();
                    data.put("message", body);
                    data.put("title", title);
                    data.put("uid", uid);
                    data.put("deviceToken", deviceToken);
                    data.put("avatar", avatar);
                    //root.put("notification", notification);
                    root.put("data", data);
                    root.put("registration_ids", recipients);
                    Log.d(TAG, root.toString());

                    String result = postToFCM(root.toString());
                    Log.d(TAG, result);
                    return result;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                //  super.onPostExecute(result);
                // showToast(result);
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
                    Log.d(TAG, "" + success + "/" + failure);
                    // Toast.makeText(ChatActivity.this, "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    e.getMessage();
                    // Toast.makeText(ChatActivity.this, "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private static String postToFCM(String bodyString) throws IOException {
        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(Constants.FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + Constants.SERVER_KEY)
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
}
