package com.zhuinden.realmbookexample.data.entity;

import io.realm.RealmObject;

/**
 * Created by Zhuinden on 2016.08.13..
 */
public class RealmString extends RealmObject {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
