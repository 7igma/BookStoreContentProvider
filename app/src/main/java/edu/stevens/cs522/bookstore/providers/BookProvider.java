package edu.stevens.cs522.bookstore.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;

import static edu.stevens.cs522.bookstore.contracts.BookContract.CONTENT_PATH;
import static edu.stevens.cs522.bookstore.contracts.BookContract.CONTENT_PATH_ITEM;
import static edu.stevens.cs522.bookstore.contracts.BookContract.contentItemType;

public class BookProvider extends ContentProvider {
    public BookProvider() {
    }

    private static final String AUTHORITY = BookContract.AUTHORITY;

    private static final String CONTENT_PATH = BookContract.CONTENT_PATH;

    private static final String CONTENT_PATH_ITEM = BookContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "books.db";

    private static final int DATABASE_VERSION = 1;

    private static final String BOOKS_TABLE = "books";

    private static final String AUTHORS_TABLE = "authors";

    private SQLiteDatabase db;

    private static final String DATABASE_CREATE =
            "create table "+ BOOKS_TABLE +" ("+BookContract.ID+" integer primary key autoincrement, "+BookContract.TITLE+" text, "+BookContract.AUTHORS+" text, "+BookContract.ISBN+" text, "+BookContract.PRICE+" text);";

    private static final String DATABASE_CREATE2 =
            "create table "+ AUTHORS_TABLE +" ("+ AuthorContract.ID+" integer primary key autoincrement, "+AuthorContract.FIRST_NAME+" text, "+AuthorContract.MIDDLE_INITIAL+" text, "+AuthorContract.LAST_NAME+" text);";

    // Create the constants used to differentiate between the different URI requests.
    private static final int ALL_ROWS = 1;
    private static final int SINGLE_ROW = 2;

    public static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO initialize database tables
            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO upgrade database if necessary
            db.execSQL("DROP TABLE IF EXISTS "+BOOKS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+AUTHORS_TABLE);
            onCreate(db);
        }
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH_ITEM, SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                return BookContract.contentType("book");
            case SINGLE_ROW:
                return BookContract.contentItemType("book");
            default:
                throw new IllegalArgumentException("Unsupported URI: "+uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new row.
                long id = db.insert(BOOKS_TABLE, null, values);
                if (id > 0)
                {
                    Uri insertid = ContentUris.withAppendedId(BookContract.CONTENT_URI, id);
                    getContext().getContentResolver().notifyChange(insertid, null);
                    return insertid;
                }
                else
                {
                    return null;
                }
            case SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // TODO: Implement this to handle query of all books.
                //return db.query(BOOKS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = db.query(BOOKS_TABLE, new String[] {BookContract.ID, BookContract.TITLE, BookContract.AUTHORS,
                    BookContract.ISBN, BookContract.PRICE}, null, null, null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;


            case SINGLE_ROW:
                // TODO: Implement this to handle query of a specific book.
                String rowId = uri.getLastPathSegment();
                cursor = db.query(BOOKS_TABLE, new String[] {BookContract.ID, BookContract.TITLE, BookContract.AUTHORS, BookContract.ISBN, BookContract.PRICE}, BookContract.ID + "=?", selectionArgs,
                        null, null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                if (cursor != null)
                {
                    cursor.moveToFirst();
                }
                return cursor;
                //return db.query(true, BOOKS_TABLE, projection, selection, selectionArgs, null, null, sortOrder, null);
            default:
                throw new IllegalStateException("query: bad case");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new IllegalStateException("Update of books not supported");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Implement this to handle requests to delete one or more rows.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                db.delete(BOOKS_TABLE, null, null);
                getContext().getContentResolver().notifyChange(uri, null);
                return 0;
            case SINGLE_ROW:
                String rowId = uri.getLastPathSegment();
                String[] args = {rowId};
                db.delete(BOOKS_TABLE, BookContract.ID+"=?",args);
                getContext().getContentResolver().notifyChange(uri, null);
                return Integer.parseInt(rowId);
            default:
                throw new IllegalStateException("delete: bad case");
        }
    }

}
