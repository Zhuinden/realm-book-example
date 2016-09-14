package com.zhuinden.realmbookexample.application;

import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zhuinden.realmbookexample.data.entity.Book;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataLoader {
    private static final String TAG = "DataLoader";

    private static DataLoader instance;
    private static Gson gsonInstance;

    private final OkHttpClient okHttpClient;

    private DataLoader() {
        this.okHttpClient = new OkHttpClient.Builder().build();
    }

    public synchronized static DataLoader getInstance() {
        if (instance == null) {
            instance = new DataLoader();
        }

        return instance;
    }

    public synchronized static Gson getGsonInstance() {
        if (gsonInstance == null) {
            gsonInstance = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
        }

        return gsonInstance;
    }

    public void loadData() {
        Request request = new Request.Builder()
            .url("https://raw.githubusercontent.com/kokeroulis/realm-book-example/real_data/json_book.json")
            .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "failed to get the books: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Unexpected code " + response);
                    return;
                }

                final String responseBody = response.body().string();
                final Type bookType = new TypeToken<List<Book>>(){}.getType();
                final List<Book> bookList = getGsonInstance().fromJson(responseBody, bookType);
                Realm realm = null;
                try {
                    realm = Realm.getInstance(RealmManager.getRealmConfiguration());
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.insertOrUpdate(bookList);
                        }
                    });
                } finally {
                    if (realm != null) {
                        realm.close();
                    }
                }
            }
        });
    }
}
