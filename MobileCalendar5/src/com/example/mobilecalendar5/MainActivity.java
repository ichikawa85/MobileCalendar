package com.example.mobilecalendar5;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.example.mobilecalendar5.*;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodSession.EventCallback;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{
	private static final int DAYS_OF_WEEK = 7; //１週間の日数
	private GridView mGridView = null; //GridViewのインスタンス
	private DateCellAdapter mDateCellAdapter = null; //DateCellAdapter
	private GregorianCalendar mCalendar = null;
	private TextView mYearMonthTextView;
	private ContentResolver mContentResolver = null;
	private Button mPrevMonthButton = null,
				   mNextMonthButton = null;
	public static final Uri mResolverUri = Uri.parse("content://com.example.mobilecalendar5.eventprovider");
	protected static final int EVENT_DETAIL = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* **************カレンダーの表示********************* */
		setContentView(R.layout.activity_main);
		mGridView = (GridView)findViewById(R.id.gridView1); //Gridカラム数を設定する
		mGridView.setNumColumns(DAYS_OF_WEEK);
		mDateCellAdapter = new DateCellAdapter(this);
		mGridView.setAdapter(mDateCellAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View v, int position, long id){
				Calendar cal = (Calendar)mCalendar.clone(); //カレンダーをコピー
				//positionから日付を計算
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.DAY_OF_MONTH, position-cal.get(Calendar.DAY_OF_WEEK)+1);
				//日付文字列を生成
				String dateString = EventInfo.dateFormat.format(cal.getTime());
				//Intent を作成
				Intent intent = new Intent(MainActivity.this, EventDetailActivity.class);
				//日付をExtraにセット
				intent.putExtra("date", dateString);
				//Activityを実行
				startActivityForResult(intent, EVENT_DETAIL);
			}
		});
		
		mYearMonthTextView = (TextView)findViewById(R.id.yearMonth);
		mCalendar = new GregorianCalendar();
		//年月の取得
		int year = mCalendar.get(Calendar.YEAR);
		int month = mCalendar.get(Calendar.MONTH)+1;
		mYearMonthTextView.setText(year+"/"+month); 
		mContentResolver = getContentResolver();
		
		mPrevMonthButton = (Button)findViewById(R.id.preveMonth);
		mPrevMonthButton.setOnClickListener(this);
		mNextMonthButton = (Button)findViewById(R.id.nextMonth);
		mNextMonthButton.setOnClickListener(this);
		
		Cursor c = mContentResolver.query(
				Uri.parse("content://com.example.mobilecalendar5.eventprovider"), null, null, null, null);
		Log.d("CALENDAR", "Num of recordes:"+c.getCount());
	}
	
	public class DateCellAdapter extends BaseAdapter{
		private static final int NUM_ROWS = 6;
		private static final int NUM_OF_CELLS = DAYS_OF_WEEK * NUM_ROWS;
		private LayoutInflater mLayoutInflater = null;
		
		DateCellAdapter(Context context){
			mLayoutInflater =
					(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public int getCount(){
			return NUM_OF_CELLS;
		}
		
		public Object getItem(int position){
			return null;
		}
		
		public long getItemId(int position){
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = mLayoutInflater.inflate(R.layout.datecell, null);
			} 
			
			convertView.setMinimumHeight(parent.getHeight()/NUM_ROWS - 1); //Viewの最小の高さを設定する
			TextView dayOfMonthView = (TextView)convertView.findViewById(R.id.dayOfMonth);
			Calendar cal = (Calendar)mCalendar.clone();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, position-cal.get(Calendar.DAY_OF_WEEK)+1);
			dayOfMonthView.setText(""+cal.get(Calendar.DAY_OF_MONTH));
			
			if(position%7 == 0){
				dayOfMonthView.setBackgroundResource(R.color.red);
			}else if(position%7 == 6){
				dayOfMonthView.setBackgroundResource(R.color.blue);
			}else{
				dayOfMonthView.setBackgroundResource(R.color.gray);
			}
			TextView scheduleView = (TextView)convertView.findViewById(R.id.schedule);

			scheduleView.setText("Schedule"+position);

			//Queryパラメータの設定
			String[] projection = {EventInfo.TITLE};
			String selection = EventInfo.START_TIME+" LIKE ?";
			String[] selectionArgs = {EventInfo.dateFormat.format(cal.getTime())+"%"};
			String sortOrder = EventInfo.START_TIME;
			//Queryの実行
			Cursor c = mContentResolver.query(mResolverUri, projection, selection, selectionArgs,
					sortOrder);
			
			StringBuilder sb = new StringBuilder();
			while(c.moveToNext()){
				sb.append(c.getString(c.getColumnIndex(EventInfo.TITLE)));
				sb.append("\n");
			}
			c.close();
			
			//scheduleView.setText(sb.toString());
		return convertView;
		}
		
	}

	@Override
	public void onClick(View v) {
		mCalendar.set(Calendar.DAY_OF_MONTH,1);
		if(v == mPrevMonthButton){
			mCalendar.add(Calendar.MONTH, -1);
		}else if(v == mNextMonthButton){
			mCalendar.add(Calendar.MONTH, 1);
		}

		mYearMonthTextView.setText(
				mCalendar.get(Calendar.YEAR)+"/"+(mCalendar.get(Calendar.MONTH)+1));
		mDateCellAdapter.notifyDataSetChanged();
		
	}

}
