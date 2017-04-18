package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;


public class AddBookActivity extends Activity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_RESULT_KEY = "book_result";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_book);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO provide ADD and CANCEL options
		menu.add(0, 1, 0, "Add");
		menu.add(0, 2, 0, "Cancel");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
		switch (item.getItemId()) {

			// ADD: return the book details to the BookStore activity
			case 1:
				Intent data = new Intent();
				data.putExtra("book", addBook());
				setResult(RESULT_OK, data);
				finish();

				// CANCEL: cancel the request
			case 2:
				setResult(RESULT_CANCELED);
				finish();
		}
		return false;
	}
	
	public Book addBook(){
		// TODO Just build a Book object with the search criteria and return that.
		EditText titleEditText = (EditText) findViewById(R.id.search_title);
		EditText authorEditText = (EditText) findViewById(R.id.search_author);
		EditText isbnEditText = (EditText) findViewById(R.id.search_isbn);
		String title = titleEditText.getText().toString();
		String authorname = authorEditText.getText().toString();
		Author author = new Author(authorname);
		Author[] authors = new Author[1];
		authors[0] = author;
		String isbn = isbnEditText.getText().toString();
		Book book = new Book(0, title, authors, isbn, "5.00");
		return book;
	}

}