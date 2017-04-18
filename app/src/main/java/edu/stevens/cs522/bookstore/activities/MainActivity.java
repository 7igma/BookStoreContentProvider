package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.providers.BookProvider;
import edu.stevens.cs522.bookstore.util.BookAdapter;

public class MainActivity extends Activity implements OnItemClickListener, AdapterView.OnItemLongClickListener, AbsListView.MultiChoiceModeListener, LoaderManager.LoaderCallbacks<Cursor> {
	
	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    static final private int LOADER_ID = 1;

    BookAdapter bookAdapter;
    ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO check if there is saved UI state, and if so, restore it (i.e. the cart contents)

		// TODO Set the layout (use cart.xml layout)
        setContentView(R.layout.cart);

        // Use a custom cursor adapter to display an empty (null) cursor.
        bookAdapter = new BookAdapter(this, null);
        lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(bookAdapter);
        lv.setSelection(0);

        // TODO set listeners for item selection and multi-choice CAB
        lv.setOnItemClickListener(this);
        lv.setMultiChoiceModeListener(this);
        lv.setOnItemLongClickListener(this);

        // TODO use loader manager to initiate a query of the database
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, this);

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO inflate a menu with ADD and CHECKOUT options
        getMenuInflater().inflate(R.menu.bookstore_menu, menu);

        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // TODO ADD provide the UI for adding a book
            case R.id.add:
                Intent addIntent = new Intent(this, AddBookActivity.class);
                startActivityForResult(addIntent, ADD_REQUEST);
                break;

            // TODO CHECKOUT provide the UI for checking out
            case R.id.checkout:
                Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
                startActivityForResult(checkoutIntent, CHECKOUT_REQUEST);
                break;

            default:
        }
        return false;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // TODO Handle results from the Search and Checkout activities.

        // Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
        switch(requestCode) {
            case ADD_REQUEST:
                // ADD: add the book that is returned to the shopping cart.
                if (resultCode == RESULT_OK){
                    Bundle data = intent.getExtras();
                    Book book = (Book) data.getParcelable("book");
                    //TODO add book via content provider
                    ContentValues vals = new ContentValues();
                    book.writeToProvider(vals);
                    getContentResolver().insert(BookContract.CONTENT_URI, vals);
                }
                break;
            case CHECKOUT_REQUEST:
                // CHECKOUT: empty the shopping cart.
                if (resultCode == RESULT_OK)
                {
                    //TODO delete all books via content provider
                    getContentResolver().delete(BookContract.CONTENT_URI, null, null);
                }
                break;
        }
    }
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO save the shopping cart contents (which should be a list of parcelables).
		
	}

    /*
     * Loader callbacks
     */

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO use a CursorLoader to initiate a query on the database
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this, BookContract.CONTENT_URI, null, null, null, null);
        }
		return null;
	}

	@Override
	public void onLoadFinished(Loader loader, Cursor data) {
        // TODO populate the UI with the result of querying the provider
        this.bookAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader loader) {
        // TODO reset the UI when the cursor is empty
        this.bookAdapter.swapCursor(null);
	}


    /*
     * Selection of a book from the list view
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO query for this book's details, and send to ViewBookActivity
        // ok to do on main thread for BookStoreWithContentProvider
        Intent intent = new Intent(MainActivity.this, ViewBookActivity.class);
        //intent.putExtra("book", dba.fetchBook(l));
        String[] args = new String[] {Long.toString(id)};
        Cursor cursor = getContentResolver().query(BookContract.CONTENT_URI(id), null, null, args, null);
        Book book = new Book(cursor);
        cursor.close();
        intent.putExtra("book", book);
        startActivity(intent);
    }

    /*
     * Long click listener
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setItemChecked(i, true);
        return true;
    }

    /*
     * Handle multi-choice action mode for deletion of several books at once
     */

    Set<Long> selected;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // TODO inflate the menu for the CAB
        mode.getMenuInflater().inflate(R.menu.books_cab, menu);
        selected = new HashSet<Long>();
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            selected.add(id);
        } else {
            selected.remove(id);
        }
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.delete:
                // TODO delete the selected books
                long[] itemIds = lv.getCheckedItemIds();
                for (int i = 0; i < itemIds.length; i++)
                {
                    //dba.delete(dba.fetchBook(itemIds[i]));
                    getContentResolver().delete(BookContract.CONTENT_URI(itemIds[i]), null, null);
                }
                Toast toast=Toast.makeText(getApplicationContext(),"deleting", Toast.LENGTH_LONG);
                toast.show();
                selected.clear();
                bookAdapter.notifyDataSetChanged();
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

}