package com.zhuinden.realmbookexample.paths.books;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

    public BooksAdapter(RealmResults<Book> books) {
        super(books, true);
    }

    // create new views (invoked by the layout manager)
    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        return new BookViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_books, parent, false));
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, final int position) {
        // get the article
        final Book book = getItem(position);
        if(book != null) {
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

        public BookViewHolder(View itemView) {
            // standard view holder pattern with ButterKnife view injection
            super(itemView);
            this.context = itemView.getContext();
            this.booksPresenter = BooksPresenter.getService(context);
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
                    booksPresenter.showEditDialog(book);
                }
            });
        }
    }
}
