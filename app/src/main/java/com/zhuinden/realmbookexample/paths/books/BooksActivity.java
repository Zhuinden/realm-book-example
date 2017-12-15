package com.zhuinden.realmbookexample.paths.books;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zhuinden.realmbookexample.R;
import com.zhuinden.realmbookexample.application.RealmManager;
import com.zhuinden.realmbookexample.data.entity.Book;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class BooksActivity
        extends AppCompatActivity
        implements BooksPresenter.ViewContract {

    @BindView(R.id.main_root)
    ViewGroup root;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    BooksPresenter booksPresenter;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The #onCreate of custom Application would be a better location for initializing Realm
        Realm.init(getApplicationContext());
        RealmManager.initializeRealmConfig();
        super.onCreate(savedInstanceState);
        BooksScopeListener fragment = (BooksScopeListener) getSupportFragmentManager().findFragmentByTag("SCOPE_LISTENER");
        if(fragment == null) {
            fragment = new BooksScopeListener();
            getSupportFragmentManager().beginTransaction().add(fragment, "SCOPE_LISTENER").commit();
        }
        //get realm instance
        Realm realm = RealmManager.getRealm();

        //get presenter instance
        booksPresenter = fragment.getPresenter();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //set toolbar
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        //setup recycler
        recycler.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);

        // get all persisted objects
        // changes will be reflected automatically
        recycler.setAdapter(new BooksAdapter(realm.where(Book.class).findAllAsync()));

        if(savedInstanceState == null) {
            Toast.makeText(this, R.string.press_to_edit_long_press_remove, Toast.LENGTH_LONG).show();
        }

        // bind to presenter
        booksPresenter.bindView(this);
    }

    @Override
    protected void onDestroy() {
        if(booksPresenter != null) {
            booksPresenter.unbindView();
        }
        if(dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

    @OnClick(R.id.fab)
    void onFabClicked() {
        booksPresenter.showAddDialog();
    }

    @Override
    public void showAddBookDialog() {
        final View content = getLayoutInflater().inflate(R.layout.edit_item, root, false);
        final DialogContract dialogContract = (DialogContract) content;

        AlertDialog.Builder builder = new AlertDialog.Builder(BooksActivity.this);
        builder.setView(content)
                .setTitle(getString(R.string.add_book))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        booksPresenter.saveBook(dialogContract);
                        booksPresenter.dismissAddDialog();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        booksPresenter.dismissAddDialog();
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showMissingTitle() {
        Toast.makeText(BooksActivity.this, getString(R.string.entry_not_saved), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEditBookDialog(Book book) {
        if(!book.isValid()) {
            return;
        }
        final View content = getLayoutInflater().inflate(R.layout.edit_item, root, false);
        final BooksPresenter.ViewContract.DialogContract dialogContract = (BooksPresenter.ViewContract.DialogContract) content;
        dialogContract.bind(book);

        final long id = book.getId();

        AlertDialog.Builder builder = new AlertDialog.Builder(BooksActivity.this);
        builder.setView(content)
                .setTitle(getString(R.string.edit_book))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        booksPresenter.editBook(dialogContract, id);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public Object getSystemService(String name) {
        if(name.equals(BooksPresenter.TAG)) {
            return booksPresenter;
        }
        return super.getSystemService(name);
    }
}
