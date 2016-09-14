package com.zhuinden.realmbookexample.application;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Zhuinden on 2016.08.16..
 */
public class RealmManager {
    private static final String TAG = "RealmManager";

    static Realm realm;

    static RealmConfiguration realmConfiguration;

    public static void initializeRealmConfig(Context appContext) {
        if(realmConfiguration == null) {
            Log.d(TAG, "Initializing Realm configuration.");
            setRealmConfiguration(new RealmConfiguration.Builder(appContext)
                    .deleteRealmIfMigrationNeeded()
                    .build());
        }
    }

    public static void setRealmConfiguration(RealmConfiguration realmConfiguration) {
        RealmManager.realmConfiguration = realmConfiguration;
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static RealmConfiguration getRealmConfiguration() {
        if (realmConfiguration == null) {
            throw new RuntimeException("RealmConfiguration is null");
        }

        return realmConfiguration;
    }

    private static int activityCount = 0;

    public static Realm getRealm() {
        return realm;
    }

    public static void incrementCount() {
        if(activityCount == 0) {
            if(realm != null) {
                if(!realm.isClosed()) {
                    Log.w(TAG, "Unexpected open Realm found.");
                    realm.close();
                }
            }
            Log.d(TAG, "Incrementing Activity Count [0]: opening Realm.");
            realm = Realm.getDefaultInstance();
        }
        activityCount++;
        Log.d(TAG, "Increment: Count [" + activityCount + "]");
    }

    public static void decrementCount() {
        activityCount--;
        Log.d(TAG, "Decrement: Count [" + activityCount + "]");
        if(activityCount <= 0) {
            Log.d(TAG, "Decrementing Activity Count: closing Realm.");
            activityCount = 0;
            realm.close();
            if(Realm.compactRealm(realmConfiguration)) {
                Log.d(TAG, "Realm compacted successfully.");
            }
            realm = null;
        }
    }
}
