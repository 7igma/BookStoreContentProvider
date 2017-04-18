package edu.stevens.cs522.bookstore.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Author implements Parcelable {

	// TODO Modify this to implement the Parcelable interface.

	// NOTE: middleInitial may be NULL!

	public String firstName;

	public String middleInitial;

	public String lastName;

	public Author()
	{

	}

	public Author(String name) {
		if ((name != null)&&(!name.equals("")))
		{
			String[] parts = name.split(" ");
			if (parts.length >= 1)
				this.firstName = parts[0];
			if (parts.length == 3)
				this.lastName = parts[2];
			if (parts.length >= 5) {
				this.middleInitial = parts[2];
				this.lastName = parts[4];
			}
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (firstName != null && !"".equals(firstName)) {
			sb.append(firstName);
			sb.append(' ');
		}
		if (middleInitial != null && !"".equals(middleInitial)) {
			sb.append(middleInitial);
			sb.append(' ');
		}
		if (lastName != null && !"".equals(lastName)) {
			sb.append(lastName);
		}
		return sb.toString();
	}

	protected Author(Parcel in) {
		firstName = in.readString();
		middleInitial = in.readString();
		lastName = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(firstName);
		dest.writeString(middleInitial);
		dest.writeString(lastName);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Author> CREATOR = new Parcelable.Creator<Author>() {
		@Override
		public Author createFromParcel(Parcel in) {
			return new Author(in);
		}

		@Override
		public Author[] newArray(int size) {
			return new Author[size];
		}
	};

}
