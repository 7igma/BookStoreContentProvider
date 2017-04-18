package edu.stevens.cs522.bookstore.util;


import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.providers.BookProvider;

/**
 * Created by dduggan.
 */

public class BookAdapter extends ResourceCursorAdapter {

    protected final static int ROW_LAYOUT = android.R.layout.simple_list_item_2;

    public BookAdapter(Context context, Cursor cursor) {
        super(context, ROW_LAYOUT, cursor, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO
        TextView titleLine = (TextView) view.findViewById(android.R.id.text1);
        TextView authorLine = (TextView) view.findViewById(android.R.id.text2);

        String title = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.TITLE));
        String authors = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.AUTHORS));
        titleLine.setText(title);
        authorLine.setText(authors);
    }
}