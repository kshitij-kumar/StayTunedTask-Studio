package com.kshitij.android.staytunedtask.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.kshitij.android.staytunedtask.model.User;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final String TAG = DatabaseHandler.class.getSimpleName();

	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "locatordb";

	public static final String TABLE_USER = "user_table";

	public static final String COL_USER_EMAIL = "email";
	public static final String COL_USER_PASSWORD = "password";
	public static final String COL_USER_NAME = "name";


	private static DatabaseHandler instance;

	public static synchronized DatabaseHandler getInstance(Context context) {

		if (instance == null) {
			instance = new DatabaseHandler(context);
		}

		return instance;
	}

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public DatabaseHandler(Context context, String databaseName,
			CursorFactory object, int databaseVersion) {
		super(context, databaseName, object, databaseVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createUserTable = "Create table " + TABLE_USER + " ( "
				+ COL_USER_EMAIL + " TEXT PRIMARY KEY," + COL_USER_PASSWORD
				+ " TEXT," + COL_USER_NAME + " TEXT" + ")";

		db.execSQL(createUserTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String queryDropUserTable = "Drop table if exists " + TABLE_USER;
		db.execSQL(queryDropUserTable);

		onCreate(db);
	}

	public synchronized long addUser(User user) {
		long addResult = -1;
		if (!isUserPrsent(user)) {
			SQLiteDatabase db = getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(COL_USER_EMAIL, user.getEmail());
			values.put(COL_USER_PASSWORD, user.getPassword());
			values.put(COL_USER_NAME, user.getName());
			addResult = db.insert(TABLE_USER, null, values);
		}
		return addResult;
	}

	public boolean isUserPrsent(User user) {
		String query = "Select * from " + TABLE_USER + " Where email = '"
				+ user.getEmail() + "'";
		Cursor cursor = getReadableDatabase().rawQuery(query, new String[] {});
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}

	public User getUserFromDb(String email) {
		String query = "Select * from " + TABLE_USER + " Where email = '"
				+ email + "'";
		Cursor cursor = getReadableDatabase().rawQuery(query, new String[] {});
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			User user = readUserObjectFromCursor(cursor);
			cursor.close();
			return user;
		}
		cursor.close();
		return null;
	}

	private User readUserObjectFromCursor(Cursor cursor) {
		User user = new User();
		user.setEmail(cursor.getString(cursor.getColumnIndex(COL_USER_EMAIL)));
		user.setPassword(cursor.getString(cursor
				.getColumnIndex(COL_USER_PASSWORD)));
		user.setName(cursor.getString(cursor.getColumnIndex(COL_USER_NAME)));
		return user;
	}

}
