/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 22.06.21, 19:43.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.api;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.api.json.data.DataResponse;
import de.effnerapp.effner.tools.misc.Promise;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClient {
    private static ApiClient instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String token;
    private final String BASE_URL;
    private PackageInfo info;

    private DataResponse data;

    public ApiClient(Context context, String token) {
        instance = this;
        this.BASE_URL = context.getString(R.string.uri_api_get_data);
        this.token = token;

        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadData(Promise<DataResponse, String> promise) {
        String url = BASE_URL + "?token=" + token + "&app_version=" + info.versionName;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                promise.reject(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = Objects.requireNonNull(response.body()).string();
                try {
                    data = gson.fromJson(res, DataResponse.class);
                    if (data.getStatus().isLogin()) {
                        promise.accept(data);
                    } else {
                        promise.reject(data.getStatus().getMsg());
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    promise.reject(response.code() + " " + response.message());
                }
            }
        });
    }

    public DataResponse getData() {
        return data;
    }

    public static ApiClient getInstance() {
        return instance;
    }
}
