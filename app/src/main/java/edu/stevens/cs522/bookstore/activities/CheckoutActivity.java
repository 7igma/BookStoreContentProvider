package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import edu.stevens.cs522.bookstore.R;


public class CheckoutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO display ORDER and CANCEL options.
		menu.add(0, 1, 0, "Order");
		menu.add(0, 2, 0, "Cancel");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO

		switch (item.getItemId()) {

			// ORDER: display a toast message of how many books have been ordered and return
			case 1:
				Toast toast = Toast.makeText(getApplicationContext(), "Ordered", Toast.LENGTH_SHORT);
				toast.show();
				setResult(RESULT_OK);
				finish();
				// CANCEL: just return with REQUEST_CANCELED as the result code
			case 2:
				setResult(RESULT_CANCELED);
				finish();

		}
		return false;
	}
	
}