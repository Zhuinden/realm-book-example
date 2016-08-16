package com.zhuinden.realmbookexample.paths.books;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zhuinden.realmbookexample.R;
import com.zhuinden.realmbookexample.data.entity.Book;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by Zhuinden on 2016.08.13..
 */
public class BooksAdapter extends RealmRecyclerViewAdapter<Book, BooksAdapter.BookViewHolder> {
    final BooksPresenter booksPresenter;

    public BooksAdapter(Context context, RealmResults<Book> books, BooksPresenter booksPresenter) {
        super(context, books, true);
        this.booksPresenter = booksPresenter;
    }

    // create new views (invoked by the layout manager)
    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        return new BookViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_books, parent, false), booksPresenter);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, final int position) {
        // get the article
        final Book book = getItem(position);
        if(book == null) {
            return;
        } else {
            holder.bind(book);
        }
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_books)
        CardView card;

        @BindView(R.id.text_books_title)
        TextView textTitle;

        @BindView(R.id.text_books_author)
        TextView textAuthor;

        @BindView(R.id.text_books_description)
        TextView textDescription;

        @BindView(R.id.image_background)
        ImageView imageBackground;

        final Context context;
        final BooksPresenter booksPresenter;

        public BookViewHolder(View itemView, BooksPresenter booksPresenter) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);
            this.context = itemView.getContext();
            this.booksPresenter = booksPresenter;
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Book book) {
            // cast the generic view holder to our specific one
            // set the title and the snippet
            final long id = book.getId();

            textTitle.setText(book.getTitle());
            textAuthor.setText(book.getAuthor());
            textDescription.setText(book.getDescription());

            // load the background image
            if (book.getImageUrl() != null) {
                Glide.with(context)
                        .load(book.getImageUrl())
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageBackground);
            }

            //remove single match from realm
            card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    booksPresenter.deleteBookById(id);
                    return false;
                }
            });

            //update single match from realm
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View content = LayoutInflater.from(context).inflate(R.layout.edit_item, card, false);
                    final BooksPresenter.ViewContract.DialogContract dialogContract = (BooksPresenter.ViewContract.DialogContract)content;
                    dialogContract.bind(book);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(content)
                            .setTitle(context.getString(R.string.edit_book))
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
            });
        }
    }
}
