package com.zhuinden.realmbookexample.application;

import com.zhuinden.realmbookexample.data.entity.Book;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Zhuinden on 2016.08.16..
 */
public class RealmInitialData implements Realm.Transaction {
    @Override
    public void execute(Realm realm) {
        Book book = new Book();
        
        book.setId(1);
        book.setAuthor("Reto Meier");
        book.setTitle("Android 4 Application Development");
        book.setImageUrl("http://api.androidhive.info/images/realm/1.png");
        realm.insertOrUpdate(book);

        book.setId(2);
        book.setAuthor("Itzik Ben-Gan");
        book.setTitle("Microsoft SQL Server 2012 T-SQL Fundamentals");
        book.setImageUrl("http://api.androidhive.info/images/realm/2.png");
        realm.insertOrUpdate(book);

        book.setId(3);
        book.setAuthor("Magnus Lie Hetland");
        book.setTitle("Beginning Python: From Novice To Professional Paperback");
        book.setImageUrl("http://api.androidhive.info/images/realm/3.png");
        realm.insertOrUpdate(book);

        book.setId(4);
        book.setAuthor("Chad Fowler");
        book.setTitle("The Passionate Programmer: Creating a Remarkable Career in Software Development");
        book.setImageUrl("http://api.androidhive.info/images/realm/4.png");
        realm.insertOrUpdate(book);

        book.setId(5);
        book.setAuthor("Yashavant Kanetkar");
        book.setTitle("Written Test Questions In C Programming");
        book.setImageUrl("http://api.androidhive.info/images/realm/5.png");
        realm.insertOrUpdate(book);
    }
    
    @Override
    public int hashCode() {
        return RealmInitialData.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof RealmInitialData;
    }
}
