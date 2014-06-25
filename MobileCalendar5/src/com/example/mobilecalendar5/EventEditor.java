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
	//Intent�ł�������f�[�^�x�[�XID
	private long mId = 0;
	//���t�̕�����
	private String mDateString = null;
	
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.eventeditor);
		//TextEdit�Ȃǂ̃r���[���擾����
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
		
		//�uOnClickListener�v�ɁuEventEditorActivity�v���Z�b�g����
		mDiscardButton.setOnClickListener(this);
		mSaveButton.setOnClickListener(this);
		Intent intent = getIntent();
		//�C���e���g��Extra����f�[�^��ID���擾����
		mId = intent.getLongExtra(EventInfo.ID, 0);
		//�C���e���g��Extra������t���擾����
		mDateString = intent.getStringExtra("date");
		if(mId==0){
			//�^�b�v�������t�ŃC�}�̎�������̃X�P�W���[���Ƃ��ăf�[�^���쐬����
			//�����ł���������t���J�����_�[�ɕϊ�
			Calendar targetCal = EventInfo.toCalendar(mDateString);
			//���̎������擾
			Calendar nowCal = new GregorianCalendar();
			//�J�n���̓^�b�v�������t
			mStartDateTextView.setText(EventInfo.dateFormat.format(targetCal.getTime()));
			mStartTimeTextView.setText(EventInfo.timeFormat.format(nowCal.getTime()));
			nowCal.add(Calendar.HOUR, 1);
			mEndDateTextView.setText(EventInfo.dateFormat.format(targetCal.getTime()));
			mEndTimeTextView.setText(EventInfo.timeFormat.format(nowCal.getTime()));
		}else{
			//�f�[�^�x�[�X����f�[�^���擾���C�f�[�^�̓��e��ҏW�G���A�ɐݒ肷��
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
			//Discard�{�^�����^�b�v���ꂽ�牽�������A�N�e�B�r�e�B���I������
			Log.d("CALENDAR", "Discard");
			finish();
		}else{
			//Save�{�^�����^�b�v���ꂽ��ҏW���̃f�[�^���f�[�^�x�[�X�ɕۑ�����
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
				//ID��0�Ȃ�V�K�Ȃ̂�Insert
				contentResolver.insert(MainActivity.mResolverUri, values);
				Log.d("CALENDAR", "Insert:"+mId);
			}else{
				//ID���P�ȏ�Ȃ�X�V
				String where = EventInfo.ID+" = "+mId;
				contentResolver.update(MainActivity.mResolverUri, values, where, null);
				Log.d("CALENDAR", "Update: "+mId);
			}
			finish();
		}
	}
}
