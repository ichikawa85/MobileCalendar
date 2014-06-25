package com.example.mobilecalendar5;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EventDetailActivity extends Activity {

	private String mDateString = null;
	private ArrayList<Long> mEventIds = null;
	protected static final int EVENT_CODE = 3;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventdetail);
		//呼び出しもとから送られたIntentを取得
		Intent intent = getIntent();
		//IntentのExtraから日付文字列を取得
		mDateString = intent.getStringExtra("date");
		//dateViewに日付をセット
		TextView dateView = (TextView)findViewById(R.id.detailDate);
		dateView.setText(mDateString);
		
		//イベントの詳細を格納するArrayを作成
		ArrayList<String> eventDetailArray = new ArrayList<String>();
				

		//コンテントリゾルバから該当する日付のイベント情報を取得
		ContentResolver contentResolver = getContentResolver();
		String selection = EventInfo.START_TIME+" LIKE ?";
		String[] selectionArgs = {mDateString+"%"};
		String sortOrder = EventInfo.START_TIME;
		Cursor c = contentResolver.query(MainActivity.mResolverUri, null, selection, selectionArgs, sortOrder);
		mEventIds = new ArrayList<Long>();
		while(c.moveToNext()){
			//イベントの詳細文字列を改行コードでつないでArrayにセット
			mEventIds.add(c.getLong(c.getColumnIndex(EventInfo.ID)));
			
			eventDetailArray.add(c.getString(c.getColumnIndex(EventInfo.TITLE))+"\n"
					+c.getString(c.getColumnIndex(EventInfo.START_TIME))+"\n"
					+c.getString(c.getColumnIndex(EventInfo.END_TIME))+"\n"
					+c.getString(c.getColumnIndex(EventInfo.WHERE))+"\n"
					+c.getString(c.getColumnIndex(EventInfo.CONTENT))
					);
		}
		c.close();
		//ListViewを取得し，setAdapterでArrayAdapterをセット
		ListView eventListView = (ListView)findViewById(R.id.eventList);
		eventListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, eventDetailArray));
		
		//eventListViewのアイテムをクリックされた時の処理をセット
		eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position,long id){
				Intent intent = new Intent(EventDetailActivity.this, EventEditorActivity.class);
				//IDと日付の文字列をExtraにセット
				intent.putExtra(EventInfo.ID, mEventIds.get(position));
				intent.putExtra("date", mDateString);
				//EventEditorActivityを起動
				startActivityForResult(intent, EVENT_EDITOR);
		}
		}); 
	}
	
}
