package com.example.mobilecalendar5;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.provider.BaseColumns;

public class EventInfo {
	//�f�[�^�x�[�X��
	public final static String DB_NAME = "events";
	//�t�B�[���h��
	public final static String ID = BaseColumns._ID;
	public final static String TITLE = "title";
	public final static String CONTENT = "content";
	public final static String WHERE = "gd_where";
	public final static String END_TIME = "gd_when_endTime";	
	public final static String START_TIME = "gd_when_startTime";
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
			Locale.JAPAN);
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	
	public static SimpleDateFormat RFC822MilliDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	public final static int HOUR_BY_MINUTES = 60;
	public final static int MINUTE_BY_SECONDS = 60;
	public final static int SECOND_BY_MILLI = 1000;
	public final static int MINUTE_BY_MILLI = MINUTE_BY_SECONDS*SECOND_BY_MILLI;
	//�f�[�^�x�[�X�̃t�B�[���h�̒l���i�[���邽�߂̃����o�ϐ�
	private long mId;
	private String mTitle;
	private String mWhere;
	private GregorianCalendar mStart;
	private GregorianCalendar mEnd;
	private String mContent;
	
	
	public static String toDBDateString(String date,String time){
		
		StringBuilder sb = new StringBuilder();
		sb.append(date);
		sb.append("T");
		sb.append(time);
		sb.append(":00.000");
		//TimeZone��������쐬���ǉ�
		sb.append(timeZoneToString(TimeZone.getDefault()));
		return sb.toString();
	}
	
	public static String timeZoneToString(TimeZone tz){
		//�J�����_�[�N���X�̃C���X�^���X���쐬
		Calendar cal = Calendar.getInstance();
		String dir=null;
		//TimeZone����~���b�P�ʂ̃ETC����̂�����擾
		int offset = tz.getRawOffset();
		//�����ƒl�̕���
		if(offset<0){
		//offset���}�C�i�X�Ȃ畄����-
		//����͐��ɂ��Ă���
		offset = -offset;
		dir = "-";
		}else if(offset > 0){
		//UTC�Ɉ�v����ꍇ��Z��Ԃ�
		return "Z";
		}
		//���C�����v�Z��Calendar�ɃZ�b�g
		int offsetMin = offset/MINUTE_BY_MILLI;
		int offsetHour = offsetMin/HOUR_BY_MINUTES;
		offsetMin=offsetMin%60;
		cal.set(Calendar.HOUR_OF_DAY, offsetHour);
		cal.set(Calendar.MINUTE, offsetMin);
		//�����̕�����ǉ������������Ԃ�
		return dir+timeFormat.format(cal.getTime());
		}
		
		
		public static String toDBDateString(Calendar cal){
		//RFC 822�`���ŕ�����𐶐�
		String dateStr = RFC822MilliDateFormat.format(cal.getTime());
		//�^�C���]�[������������
		if(dateStr.matches(".+[+-][0-9]{4}$")){
			dateStr = dateStr.replaceAll("([+-][0-9]{2})([0-9]{2})", "$1:$2");
		}
		return dateStr;
	}
	
	public static GregorianCalendar toCalendar(String startTime){
		GregorianCalendar calendar = new GregorianCalendar();
		if(startTime == null){
			return calendar;
		}
		//������𐔒l�ȊO�̕����ŕ������Đ؂蕪����
		String[] strs = startTime.split("[^0-9]");
		TimeZone timeZone = TimeZone.getDefault();
		if(startTime.matches("^[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]$")){
			//���t�݂̂̕�����@������00:00�ɐݒ�
			calendar.set(Calendar.YEAR, Integer.valueOf(strs[0]));
			calendar.set(Calendar.MONTH, Integer.valueOf(strs[1])-1);
			calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(strs[2]));
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.setTimeZone(timeZone);
		}else{
		//����������@���l������𐔒l�ɕϊ����Đݒ�

			calendar.set(Calendar.YEAR, Integer.valueOf(strs[0]));
			calendar.set(Calendar.MONTH, Integer.valueOf(strs[1])-1);
			calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(strs[2]));
			calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(strs[3]));
			calendar.set(Calendar.MINUTE, Integer.valueOf(strs[4]));
			calendar.set(Calendar.SECOND, Integer.valueOf(strs[5]));
			calendar.set(Calendar.MILLISECOND, Integer.valueOf(strs[6]));
			calendar.setTimeZone(timeZone);
			
			//TimeZone�̃p�^�[���ɂ�鏈��
			if(startTime.matches(".+Z$")){
				//UTC
				timeZone.setRawOffset(0);
			}else if(startTime.matches(".+\\+[0-9][0-9]:[0-9][0-9]$")){
				//�I�t�Z�b�g���}�C�i�X
				timeZone.setRawOffset((Integer.valueOf(strs[7])*HOUR_BY_MINUTES
						+Integer.valueOf(strs[8]))*MINUTE_BY_MILLI);
			}else if(startTime.matches(".+-[0-9][0-9]:[0-9][0-9]$")){
				//�I�t�Z�b�g���v���X
				timeZone.setRawOffset(-(Integer.valueOf(strs[7])*HOUR_BY_MINUTES
						+ Integer.valueOf(strs[8]))*MINUTE_BY_MILLI);
			}
			//TimeZone��ݒ�
			calendar.setTimeZone(timeZone);
		}
		return calendar;
	}
	
	
	public String toString(){
		return getTitle()+"\n"
				+getStartDateString()+" "+getStartTimeString()+"\n"
				+getEndDateString()+" "+getEndTimeString()+"\n"
				+getWhere()+"\n"
				+getContent();
	}
	
	//��������@setter/getter
	public void setId(long mId){
		this.mId = mId;
	}
	
	public long getId(){
		return mId;
	}
	
	public void setTitle(String mTitle){
		this.mTitle = mTitle;
	}
	
	public String getTitle(){
		return mTitle;
	}
	
	public void setWhere(String mWhere){
		this.mWhere = mWhere;
	}
	
	public String getWhere(){
		return mWhere;
	}
	
	public void setStart(GregorianCalendar mStart){
		this.mStart = mStart;
	}
	
	public void setStart(String dateString){
		this.mStart = toCalendar(dateString);
	}
	
	public Calendar getStart(){
		return mStart;
	}
	public String getStartString(){
		return toDBDateString(mStart);
	}
	public String getStartDateString(){
		return dateFormat.format(mStart.getTime());
	}
	public String getStartTimeString(){
		return timeFormat.format(mStart.getTime());
	}
	
	public void setEnd(GregorianCalendar mEnd){
		this.mEnd = mEnd;
	}
	
	public String getEndString(){
		return toDBDateString(mEnd);
	}
	public void setEnd(String dateString){
		this.mEnd = toCalendar(dateString);
	}
	
	public Calendar getEnd(){
		return mEnd;
	}
	
	public String getEndDateString(){
		return dateFormat.format(mEnd.getTime());
	}
	
	public String getEndTimeString(){
		return timeFormat.format(mEnd.getTime());
	}
	
	public void setContent(String mContent){
		this.mContent = mContent;
	}
	
	public String getContent(){
		return mContent;
	}
	

}
