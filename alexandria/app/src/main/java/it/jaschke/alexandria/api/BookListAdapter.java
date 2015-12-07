package it.jaschke.alexandria.api;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;

/**
 * Created by saj on 11/01/15.
 */
public class BookListAdapter extends CursorAdapter {


    public static class ViewHolder {
        public final ImageView bookCover;
        public final TextView bookTitle;
        //public final TextView bookSubTitle;
        public final ImageButton deleteButton;

        public ViewHolder(View view) {
            bookCover = (ImageView) view.findViewById(R.id.listBookCover);
            bookTitle = (TextView) view.findViewById(R.id.listBookTitle);
            //bookSubTitle = (TextView) view.findViewById(R.id.listBookSubTitle);
            deleteButton = (ImageButton) view.findViewById(R.id.list_delete_button);
        }
    }

    public BookListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String imgUrl = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        Glide.with(mContext)
                .load(imgUrl)
                .error(R.drawable.default_cover)
                .crossFade()
                .into(viewHolder.bookCover);

        String bookTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        viewHolder.bookTitle.setText(bookTitle);

        /*String bookSubTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        viewHolder.bookSubTitle.setText(bookSubTitle);*/

        viewHolder.deleteButton.setTag(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID)));
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ean = (String) view.getTag();
                Intent bookIntent = new Intent(mContext, BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.DELETE_BOOK);
                mContext.startService(bookIntent);
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }
}
