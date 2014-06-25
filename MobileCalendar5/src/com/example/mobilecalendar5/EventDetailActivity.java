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
		//�Ăяo�����Ƃ��瑗��ꂽIntent���擾
		Intent intent = getIntent();
		//Intent��Extra������t��������擾
		mDateString = intent.getStringExtra("date");
		//dateView�ɓ��t���Z�b�g
		TextView dateView = (TextView)findViewById(R.id.detailDate);
		dateView.setText(mDateString);
		
		//�C�x���g�̏ڍׂ��i�[����Array���쐬
		ArrayList<String> eventDetailArray = new ArrayList<String>();
				

		//�R���e���g���]���o����Y��������t�̃C�x���g�����擾
		ContentResolver contentResolver = getContentResolver();
		String selection = EventInfo.START_TIME+" LIKE ?";
		String[] selectionArgs = {mDateString+"%"};
		String sortOrder = EventInfo.START_TIME;
		Cursor c = contentResolver.query(MainActivity.mResolverUri, null, selection, selectionArgs, sortOrder);
		mEventIds = new ArrayList<Long>();
		while(c.moveToNext()){
			//�C�x���g�̏ڍו���������s�R�[�h�łȂ���Array�ɃZ�b�g
			mEventIds.add(c.getLong(c.getColumnIndex(EventInfo.ID)));
			
			eventDetailArray.add(c.getString(c.getColumnIndex(EventInfo.TITLE))+"\n"
					+c.getString(c.getColumnIndex(EventInfo.START_TIME))+"\n"
					+c.getString(c.getColumnIndex(EventInfo.END_TIME))+"\n"
					+c.getString(c.getColumnIndex(EventInfo.WHERE))+"\n"
					+c.getString(c.getColumnIndex(EventInfo.CONTENT))
					);
		}
		c.close();
		//ListView���擾���CsetAdapter��ArrayAdapter���Z�b�g
		ListView eventListView = (ListView)findViewById(R.id.eventList);
		eventListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, eventDetailArray));
		
		//eventListView�̃A�C�e�����N���b�N���ꂽ���̏������Z�b�g
		eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position,long id){
				Intent intent = new Intent(EventDetailActivity.this, EventEditorActivity.class);
				//ID�Ɠ��t�̕������Extra�ɃZ�b�g
				intent.putExtra(EventInfo.ID, mEventIds.get(position));
				intent.putExtra("date", mDateString);
				//EventEditorActivity���N��
				startActivityForResult(intent, EVENT_EDITOR);
		}
		}); 
	}
	
}
