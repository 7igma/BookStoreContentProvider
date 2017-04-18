package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.BookContract;

public class Book implements Parcelable {

	// TODO Modify this to implement the Parcelable interface.

	public int id;

	public String title;

	public Author[] authors;

	public String isbn;

	public String price;

	public Book(int id, String title, Author[] author, String isbn, String price) {
		this.id = id;
		this.title = title;
		this.authors = author;
		this.isbn = isbn;
		this.price = price;
	}

	public String getFirstAuthor() {
		if (authors != null && authors.length > 0) {
			return authors[0].toString();
		} else {
			return "";
		}
	}

	protected Book(Parcel in)
	{
		id = in.readInt();
		title = in.readString();
		authors = in.createTypedArray(Author.CREATOR);
		isbn = in.readString();
		price = in.readString();
	}

	public Book(Cursor cursor) {
		// TODO init from cursor
		this.title = BookContract.getTitle(cursor);
		this.authors = stringsToAuthors(BookContract.getAuthors(cursor));
		this.isbn = BookContract.getISBN(cursor);
		this.price = BookContract.getPrice(cursor);
	}

	public void writeToProvider(ContentValues out) {
		// TODO write to ContentValues
		BookContract.putTitle(out, title);
		BookContract.putISBN(out, isbn);
		BookContract.putPrice(out, price);
		BookContract.putAuthors(out, this.getAuthorNames());
	}

	public static Author[] stringsToAuthors(String[] names)
	{
		Author[] authors = new Author[10];
		for (int i = 0; i < names.length; i++)
		{
			authors[i] = new Author(names[i]);
		}
		return authors;
	}

	public String[] getAuthorNames()
	{
		if (authors != null) {
			String[] names = new String[authors.length];
			for (int i = 0; i < authors.length; i++) {
				if (authors[i] != null) {
					names[i] = authors[i].toString();
				}
				else
				{
					names[i] = "";
				}
			}
			return names;
		}
		return null;
	}

	public String getTitle()
	{
		return title;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(title);
		dest.writeTypedArray(authors, 0);
		dest.writeString(isbn);
		dest.writeString(price);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
		@Override
		public Book createFromParcel(Parcel in) {
			return new Book(in);
		}

		@Override
		public Book[] newArray(int size) {
			return new Book[size];
		}
	};
}