package com.zhuinden.realmbookexample.paths.books;

import android.content.Context;

import com.zhuinden.realmbookexample.application.RealmManager;
import com.zhuinden.realmbookexample.data.entity.Book;
import com.zhuinden.realmbookexample.data.entity.BookFields;

import io.realm.Realm;

/**
 * Created by Zhuinden on 2016.08.16..
 */
public class BooksPresenter {
    public static BooksPresenter getService(Context context) {
        //noinspection ResourceType
        return (BooksPresenter) context.getSystemService(TAG);
    }

    public static final String TAG = "BooksPresenter";

    public interface ViewContract {
        void showAddBookDialog();

        void showMissingTitle();

        void showEditBookDialog(Book book);

        interface DialogContract {
            String getTitle();
            String getAuthor();
            String getThumbnail();

            void bind(Book book);
        }
    }

    ViewContract viewContract;

    boolean isDialogShowing;

    boolean hasView() {
        return viewContract != null;
    }

    public void bindView(ViewContract viewContract) {
        this.viewContract = viewContract;
        if(isDialogShowing) {
            showAddDialog();
        }
    }

    public void unbindView() {
        this.viewContract = null;
    }

    public void showAddDialog() {
        if(hasView()) {
            isDialogShowing = true;
            viewContract.showAddBookDialog();
        }
    }

    public void dismissAddDialog() {
        isDialogShowing = false;
    }

    public void showEditDialog(Book book) {
        if(hasView()) {
            viewContract.showEditBookDialog(book);
        }
    }

    public void saveBook(ViewContract.DialogContract dialogContract) {
        if(hasView()) {
            final String author = dialogContract.getAuthor();
            final String title = dialogContract.getTitle();
            final String thumbnail = dialogContract.getThumbnail();

            if(title == null || "".equals(title.trim())) {
                viewContract.showMissingTitle();
            } else {
                Realm realm = RealmManager.getRealm();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Book book = new Book();
                        long id = 1;
                        if(realm.where(Book.class).count() > 0) {
                            id = realm.where(Book.class).max(BookFields.ID).longValue() + 1; // auto-increment id
                        }
                        book.setId(id);
                        book.setAuthor(author);
                        book.setDescription("");
                        book.setImageUrl(thumbnail);
                        book.setTitle(title);
                        realm.insertOrUpdate(book);
                    }
                });
            }
        }
    }

    public void deleteBookById(final long id) {
        Realm realm = RealmManager.getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Book book = realm.where(Book.class).equalTo(BookFields.ID, id).findFirst();
                if(book != null) {
                    book.deleteFromRealm();
                }
            }
        });
    }

    public void editBook(final ViewContract.DialogContract dialogContract, final long id) {
        Realm realm = RealmManager.getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Book book = realm.where(Book.class).equalTo(BookFields.ID, id).findFirst();
                if(book != null) {
                    book.setTitle(dialogContract.getTitle());
                    book.setImageUrl(dialogContract.getThumbnail());
                    book.setAuthor(dialogContract.getAuthor());
                }
            }
        });
    }
}
