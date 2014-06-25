package com.example.mobilecalendar5;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class EventProvider extends ContentProvider {
	
	private EventDatabaseHelper mEventDatabaseHelper = null;
	private static final int CURRENT_DATABASE_VERSION = 1;

	public boolean onCreate() {
		Log.v("ContentProvider", "onCreate");
		mEventDatabaseHelper = new EventDatabaseHelper(getContext());
		return false;
	}
	
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mEventDatabaseHelper.getWritableDatabase();
		int numDeleted = db.delete(EventInfo.DB_NAME, selection, selectionArgs);
		
		return numDeleted;
	}

	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mEventDatabaseHelper.getWritableDatabase();
		//�����̃p�����[�^�[�ɏ]����insert�����s����
		long newId = db.insert(EventInfo.DB_NAME, null, values);
		//�V�K�ɒǉ����ꂽ���R�[�h��ID���Ԃ��Ă���̂ł��������Uri���쐬����
		Uri newUri = Uri.parse(uri+"/"+newId);
		
		//�V�K���R�[�h���w��Uri��Ԃ�
		return newUri;
	}

	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = mEventDatabaseHelper.getReadableDatabase();
	    
		Cursor c = db.query(EventInfo.DB_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		
		return c;
	}

	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mEventDatabaseHelper.getWritableDatabase();
		//�����̃p�����[�^�[�ɏ]����update�����s����
		int numUpdated = db.update(EventInfo.DB_NAME, values, selection, selectionArgs);
		
		return numUpdated; //�X�V���ꂽ���R�[�h����Ԃ�
	}

	

	/* *********************SQLiteOpenHelper*********************** */
	public class EventDatabaseHelper extends SQLiteOpenHelper {
		public EventDatabaseHelper(Context context){
			super(context, EventInfo.DB_NAME+".db", null, CURRENT_DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE "+EventInfo.DB_NAME+"("
					+ EventInfo.ID + " INTEGER PRIMARY KEY,"
					+ EventInfo.TITLE + " TEXT,"
					+ EventInfo.CONTENT + " TEXT,"
					+ EventInfo.WHERE + " TEXT,"
					+ EventInfo.END_TIME + " TEXT,"
					+ EventInfo.START_TIME + " TEXT"
					+ ");";
			Log.v("SQlite", "CreateDatabase");
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + EventInfo.DB_NAME);
			onCreate(db);
		}
		
	}

	
}
