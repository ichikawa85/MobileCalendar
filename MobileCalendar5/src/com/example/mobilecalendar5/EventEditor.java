package com.example.mobilecalendar5;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodSession.EventCallback;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class EventEditor extends Activity implements OnClickListener{

	private EditText mTitleEditText = null;
	private EditText mWhereEditText = null;
	private EditText mContentEditText = null;
	private EditText mStartDateTextView = null;
	private EditText mStartTimeTextView = null;
	private EditText mEndDateTextView = null;
	private EditText mEndTimeTextView = null;
	private Button mDiscardButton = null;
	private Button mSaveButton = null;
	private CheckBox mAllDayCheckBox = null;
	//IntentでもらったデータベースID
	private long mId = 0;
	//日付の文字列
	private String mDateString = null;
	
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.eventeditor);
		//TextEditなどのビューを取得する
		mTitleEditText = (EditText)findViewById(R.id.title);
		mWhereEditText = (EditText)findViewById(R.id.where);
		mContentEditText = (EditText)findViewById(R.id.content);
		mStartDateTextView = (EditText)findViewById(R.id.startDate);
		mStartTimeTextView = (EditText)findViewById(R.id.startTime);
		mEndDateTextView = (EditText)findViewById(R.id.endDate);
		mEndTimeTextView = (EditText)findViewById(R.id.endTime);
		mDiscardButton = (Button)findViewById(R.id.discard);
		mSaveButton = (Button)findViewById(R.id.save);
		mAllDayCheckBox = (CheckBox)findViewById(R.id.allDay);
		
		//「OnClickListener」に「EventEditorActivity」をセットする
		mDiscardButton.setOnClickListener(this);
		mSaveButton.setOnClickListener(this);
		Intent intent = getIntent();
		//インテントのExtraからデータのIDを取得する
		mId = intent.getLongExtra(EventInfo.ID, 0);
		//インテントのExtraから日付を取得する
		mDateString = intent.getStringExtra("date");
		if(mId==0){
			//タップした日付でイマの時刻からのスケジュールとしてデータを作成する
			//引数でもらった日付をカレンダーに変換
			Calendar targetCal = EventInfo.toCalendar(mDateString);
			//今の時刻を取得
			Calendar nowCal = new GregorianCalendar();
			//開始日はタップした日付
			mStartDateTextView.setText(EventInfo.dateFormat.format(targetCal.getTime()));
			mStartTimeTextView.setText(EventInfo.timeFormat.format(nowCal.getTime()));
			nowCal.add(Calendar.HOUR, 1);
			mEndDateTextView.setText(EventInfo.dateFormat.format(targetCal.getTime()));
			mEndTimeTextView.setText(EventInfo.timeFormat.format(nowCal.getTime()));
		}else{
			//データベースからデータを取得し，データの内容を編集エリアに設定する
			ContentResolver contentResolver = getContentResolver();
			String selection = EventInfo.ID+" = "+mId;
			Cursor c = contentResolver.query(MainActivity.mResolverUri, null, selection, null, null);
			
			if(c.moveToNext()){
				mTitleEditText.setText(c.getString(c.getColumnIndex(EventInfo.TITLE)));
				mWhereEditText.setText(c.getString(c.getColumnIndex(EventInfo.WHERE)));
				mContentEditText.setText(c.getString(c.getColumnIndex(EventInfo.CONTENT)));
				
				String startTime = c.getString(c.getColumnIndex(EventInfo.START_TIME));
				Calendar startCal = EventInfo.toCalendar(startTime);
				mStartDateTextView.setText(EventInfo.dateFormat.format(startCal.getTime()));
				mStartTimeTextView.setText(EventInfo.timeFormat.format(startCal.getTime()));
				String endTime = c.getString(c.getColumnIndex(EventInfo.END_TIME));
				Calendar emdCal = EventInfo.toCalendar(endTime);
				mEndDateTextView.setText(EventInfo.dateFormat.format(endCal.getTime()));
				mEndTimeTextView.setText(EventInfo.timeFormat.format(endCal.getTime()));
			}
			c.close();
		}
	}
	
	@Override
	public void onClick(View v) {
		if(v == mDiscardButton){
			//Discardボタンがタップされたら何もせずアクティビティを終了する
			Log.d("CALENDAR", "Discard");
			finish();
		}else{
			//Saveボタンがタップされたら編集中のデータをデータベースに保存する
			ContentResolver contentResolver = getContentResolver();
			ContentValues values = new ContentValues();
			values.put(EventInfo.TITLE, mTitleEditText.getText().toString());
			values.put(EventInfo.CONTENT, mTitleEditText.getText().toString());
			values.put(EventInfo.START_TIME, 
					EventInfo.toDBDateString(mStartDateTextView.getText().toString(),
					mStartTimeTextView.getText().toString()));
			values.put(EventInfo.END_TIME, 
					EventInfo.toDBDateString(mEndDateTextView.getText().toString(),
					mEndTimeTextView.getText().toString()));
			if(mId == 0){
				//IDが0なら新規なのでInsert
				contentResolver.insert(MainActivity.mResolverUri, values);
				Log.d("CALENDAR", "Insert:"+mId);
			}else{
				//IDが１以上なら更新
				String where = EventInfo.ID+" = "+mId;
				contentResolver.update(MainActivity.mResolverUri, values, where, null);
				Log.d("CALENDAR", "Update: "+mId);
			}
			finish();
		}
	}
}
