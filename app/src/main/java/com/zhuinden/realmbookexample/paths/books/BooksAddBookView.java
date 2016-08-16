package com.zhuinden.realmbookexample.paths.books;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhuinden.realmbookexample.R;
import com.zhuinden.realmbookexample.data.entity.Book;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

/**
 * Created by Zhuinden on 2016.08.16..
 */
public class BooksAddBookView extends LinearLayout implements BooksActivity.DialogContract {
    public BooksAddBookView(Context context) {
        super(context);
    }

    public BooksAddBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BooksAddBookView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public BooksAddBookView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    String title;
    String author;
    String thumbnail;

    @BindView(R.id.title)
    EditText textTitle;

    @BindView(R.id.author)
    EditText textAuthor;

    @BindView(R.id.thumbnail)
    EditText textThumbnail;

    @OnTextChanged(R.id.title)
    public void titleChanged(CharSequence _title) {
        title = _title.toString();
    }

    @OnTextChanged(R.id.author)
    public void authorChanged(CharSequence _author) {
        author = _author.toString();
    }

    @OnTextChanged(R.id.thumbnail)
    public void thumbnailChanged(CharSequence _thumbnail) {
        thumbnail = _thumbnail.toString();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public void bind(Book book) {
        String _title = book.getTitle();
        String _author = book.getAuthor();
        String _thumbnail = book.getImageUrl();
        textTitle.setText(_title);
        textAuthor.setText(_author);
        textThumbnail.setText(_thumbnail);
        title = _title;
        author = _author;
        thumbnail = _thumbnail;
    }
}
