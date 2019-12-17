package de.effnerapp.effner.tools;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import de.effnerapp.effner.MainActivity;
import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.json.Error;
import de.effnerapp.effner.json.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginManager {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private User user;
    private Error error;

    public LoginManager() {

    }

    public boolean register(String id, String password, String sClass, String username) {
        final String[] res = new String[1];
        final boolean[] ok = new boolean[1];
        new Thread(() -> {
            System.out.println("Req!");
            OkHttpClient client = new OkHttpClient();

            String url = "https://api.effnerapp.de/auth/register" + "?id=" + id + "&password=" + password + "&class=" + sClass;

            if(username != null && !username.isEmpty()) {
                url += "&username=" + username;
            }

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    System.out.println("Res");
                    res[0] = response.body().string();
                    System.out.println(res[0]);

                    error = gson.fromJson(res[0], Error.class);
                    if(error.getError() != null && !error.getError().isEmpty()) {
                        ok[0] = false;
                    } else {
                        ok[0] = true;
                        user = gson.fromJson(res[0], User.class);
                    }

                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ok[0] = false;
                }
            });
        }).start();

        while (res[0] == null) {
            System.out.println("Waiting for LoginServer....");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(ok[0]) {
            SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
            editor.putBoolean("APP_REGISTERED", true);
            editor.putString("APP_AUTH_TOKEN", user.getToken());
            editor.putString("APP_USER_CLASS", user.getsClass());
            if(user.getUsername() != null && !user.getUsername().isEmpty()) {
                editor.putString("APP_USER_USERNAME", user.getUsername());
            }
            editor.apply();
        }

        return ok[0];
    }

    public boolean login(String token) {
        final String[] res = new String[1];
        final boolean[] ok = new boolean[1];

        new Thread(() -> {
            System.out.println("Req!");
            OkHttpClient client = new OkHttpClient();

            String url = "https://api.effnerapp.de/auth/login" + "?token=" + token;

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    System.out.println("Res");
                    res[0] = response.body().string();
                    System.out.println(res[0]);

                    error = gson.fromJson(res[0], Error.class);
                    if(error.getError() != null && !error.getError().isEmpty()) {
                        ok[0] = false;
                    } else {
                        ok[0] = true;
                        user = gson.fromJson(res[0], User.class);
                    }

                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ok[0] = false;
                }
            });
        }).start();

        while (res[0] == null) {
            System.out.println("Waiting for LoginServer....");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return ok[0];
    }

    public Error getError() {
        return error;
    }
}
