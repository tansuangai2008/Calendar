package com.example.young.calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.young.calendar.biz.ShowIsHide;
import com.example.young.calendar.log.Log;
import com.example.young.calendar.view.NumericWheelAdapter;
import com.example.young.calendar.view.OnWheelChangedListener;
import com.example.young.calendar.view.OnWheelScrollListener;
import com.example.young.calendar.view.WheelView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class SelectBirthdayDialog extends DialogFragment implements OnClickListener {

	private TextView btn_submit, btn_cancel;
	private DateNumericAdapter monthAdapter, dayAdapter, yearAdapter;
	private WheelView year, month, day;
	private int mCurYear = 80, mCurMonth = 5, mCurDay = 14;

	private Context context;

	/***显示的时间段需要显示多少段位的年份***/
	public int range=50;

	/****起始时间 begin Date***/
	public int beginRange =0;

    public String date;
	private int curYearIndex;
	private String[] dates;

	@Override
	public void onAttach(Activity activity) {
		this.context=activity;
		super.onAttach(activity);
		try {
			showIsHide = (ShowIsHide) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement ShowIsHide");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
	}

	public ShowIsHide showIsHide;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity(),R.style.dialog);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View mMenuView = inflater.inflate(R.layout.birthday, null);
		year =  mMenuView.findViewById(R.id.year);
		month =  mMenuView.findViewById(R.id.month);
		day = mMenuView.findViewById(R.id.day);
		btn_submit =  mMenuView.findViewById(R.id.submit);
		btn_cancel =  mMenuView.findViewById(R.id.cancel);
		btn_submit.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		dialog.setContentView(mMenuView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		setAdapter();
		return dialog;
	}

	int curMonthIndex;
	boolean tempKey=false;
	public boolean previousMonth=false;

	private void setAdapter() {
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - beginRange);
		mCurYear=calendar.get(Calendar.YEAR);
		Log.e("当前年份",""+mCurYear);
		mCurMonth=calendar.get(Calendar.MONTH)+1;

		if(previousMonth){
			mCurMonth=mCurMonth-1;
		}
		mCurDay=calendar.get(Calendar.DAY_OF_MONTH);

		if(TextUtils.isEmpty(date)){
			Date dt = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
			this.date = sdf.format(dt);
			dates =  date.split("-");
			curYearIndex =  Integer.parseInt(dates[0]);
			curYearIndex=curYearIndex-mCurYear-beginRange+range;
			tempKey=false;
		}else{
			dates = date.split("-");
			curYearIndex =  Integer.parseInt(dates[0]);
			curYearIndex=curYearIndex-mCurYear+range;
			tempKey=true;
		}
		if(String.valueOf(dates[1].charAt(0)).equals("0")){
			dates[1]=dates[1].replace("0","");
		}
		curMonthIndex = Integer.parseInt(dates[1]);
		if(previousMonth&&!tempKey){
			curMonthIndex=curMonthIndex-1;
		}
		if(String.valueOf(dates[2].charAt(0)).equals("0")){
			dates[2]=dates[2].replace("0","");
		}
		int curDayIndex = Integer.parseInt(dates[2]);

		yearAdapter = new DateNumericAdapter(context, mCurYear-range, mCurYear, range-beginRange);
		yearAdapter.setTextType("年");
		yearAdapter.setItemResource(R.layout.item_birth);
		yearAdapter.setItemTextResource(R.id.item_birth);
		Log.e("【curYearIndex】="+String.valueOf(curYearIndex));
		yearAdapter.setCurrentIndex(curYearIndex);
		year.setViewAdapter(yearAdapter);
		year.setCurrentItem(curYearIndex);

		year.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateMonths(year, month, day);
				updateDays(year, month, day);
				Log.e("year changeListener changeListener");
			}

		});
		year.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}
			@Override
			public void onScrollingFinished(WheelView wheel) {
				yearAdapter.setCurrentIndex(wheel.getCurrentItem());
				String currentText = (String) yearAdapter.getItemText(wheel.getCurrentItem());
				setTextViewSize(currentText, yearAdapter);
			}
		});
		if(mCurYear==Integer.parseInt(dates[0])-beginRange){
			monthAdapter = new DateNumericAdapter(context, 1, mCurMonth, mCurMonth - 1);
			monthAdapter.setItemResource(R.layout.item_birth);
			monthAdapter.setItemTextResource(R.id.item_birth);
			monthAdapter.setTextType("月");
			monthAdapter.setCurrentIndex(curMonthIndex - 1);
			month.setViewAdapter(monthAdapter);
			month.setCurrentItem(curMonthIndex - 1);
		}else if(mCurYear==Integer.parseInt(dates[0])&&tempKey){
			monthAdapter = new DateNumericAdapter(context, 1, mCurMonth, mCurMonth - 1);
			monthAdapter.setItemResource(R.layout.item_birth);
			monthAdapter.setItemTextResource(R.id.item_birth);
			monthAdapter.setTextType("月");
			monthAdapter.setCurrentIndex(curMonthIndex - 1);
			month.setViewAdapter(monthAdapter);
			month.setCurrentItem(curMonthIndex - 1);
		}
		else if((mCurYear!=Integer.parseInt(dates[0])-beginRange)&&curYearIndex==0){
			int tempMonth;
			if(previousMonth){
				tempMonth=mCurMonth+1;
			}else{
				tempMonth=mCurMonth;
			}
			monthAdapter = new DateNumericAdapter(context, tempMonth, 12, 0);
			monthAdapter.setItemResource(R.layout.item_birth);
			monthAdapter.setItemTextResource(R.id.item_birth);
			monthAdapter.setTextType("月");
			monthAdapter.setCurrentIndex(curMonthIndex-tempMonth);
			month.setViewAdapter(monthAdapter);
			month.setCurrentItem(curMonthIndex-tempMonth);
		}else{
			monthAdapter = new DateNumericAdapter(context, 1, 12, mCurMonth - 1);
			monthAdapter.setItemResource(R.layout.item_birth);
			monthAdapter.setItemTextResource(R.id.item_birth);
			monthAdapter.setTextType("月");
			monthAdapter.setCurrentIndex(curMonthIndex - 1);
			month.setViewAdapter(monthAdapter);
			month.setCurrentItem(curMonthIndex - 1);
		}

		month.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				monthAdapter.setCurrentIndex(wheel.getCurrentItem());
				Log.e("month onChanged onChanged");
				updateMonths(year, month, day);
				updateDays(year, month, day);
			}

		});
		month.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {

			}
			@Override
			public void onScrollingFinished(WheelView wheel) {
				Log.e("month onScrollingFinished onScrollingFinished");
				monthAdapter.setCurrentIndex(wheel.getCurrentItem());
				String currentText = (String) monthAdapter.getItemText(wheel.getCurrentItem());
				setTextViewSize(currentText, monthAdapter);
				updateDays(year, month, day);
			}
		});

		if(mCurYear==Integer.parseInt(dates[0])-beginRange&&mCurMonth==curMonthIndex){
			dayAdapter = new DateNumericAdapter(context, 1, mCurDay, mCurDay - 1);
			dayAdapter.setItemResource(R.layout.item_birth);
			dayAdapter.setItemTextResource(R.id.item_birth);
			dayAdapter.setTextType("日");
			dayAdapter.setCurrentIndex(curDayIndex-1);
			day.setViewAdapter(dayAdapter);
			day.setCurrentItem(curDayIndex - 1);
		}else if(mCurYear==Integer.parseInt(dates[0])&&mCurMonth==curMonthIndex&&tempKey){
			dayAdapter = new DateNumericAdapter(context, 1, mCurDay, mCurDay - 1);
			dayAdapter.setItemResource(R.layout.item_birth);
			dayAdapter.setItemTextResource(R.id.item_birth);
			dayAdapter.setTextType("日");
			dayAdapter.setCurrentIndex(curDayIndex-1);
			day.setViewAdapter(dayAdapter);
			day.setCurrentItem(curDayIndex - 1);
		}else if((mCurYear!=Integer.parseInt(dates[0])-beginRange)&&curYearIndex==0){
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - range + year.getCurrentItem());
			int tempMonth;
			if(previousMonth){
				tempMonth=mCurMonth+1;
			}else{
				tempMonth=mCurMonth;
			}
			calendar.set(Calendar.MONTH, (tempMonth-1)+month.getCurrentItem());
			calendar.set(Calendar.DATE, 1);
			calendar.roll(Calendar.DATE, -1);
			int maxDays = calendar.get(Calendar.DATE);
			if(month.getCurrentItem()==0){
				dayAdapter = new DateNumericAdapter(context, mCurDay, maxDays, mCurDay - 1);
				dayAdapter.setItemResource(R.layout.item_birth);
				dayAdapter.setItemTextResource(R.id.item_birth);
				dayAdapter.setTextType("日");
				dayAdapter.setCurrentIndex(curDayIndex-mCurDay);
				day.setViewAdapter(dayAdapter);
				day.setCurrentItem(curDayIndex - mCurDay);
			}else{
				dayAdapter = new DateNumericAdapter(context, 1, maxDays, mCurDay - 1);
				dayAdapter.setItemResource(R.layout.item_birth);
				dayAdapter.setItemTextResource(R.id.item_birth);
				dayAdapter.setTextType("日");
				dayAdapter.setCurrentIndex(curDayIndex-1);
				day.setViewAdapter(dayAdapter);
				day.setCurrentItem(curDayIndex - 1);
			}

		}else{
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - range + year.getCurrentItem());
			calendar.set(Calendar.MONTH, month.getCurrentItem());
			calendar.set(Calendar.DATE, 1);
			calendar.roll(Calendar.DATE, -1);
			int maxDays = calendar.get(Calendar.DATE);
			dayAdapter = new DateNumericAdapter(context, 1, maxDays, mCurDay - 1);
			dayAdapter.setTextType("日");
			dayAdapter.setCurrentIndex(curDayIndex-1);
			dayAdapter.setItemResource(R.layout.item_birth);
			dayAdapter.setItemTextResource(R.id.item_birth);
			day.setViewAdapter(dayAdapter);
			day.setCurrentItem(curDayIndex - 1);
		}

		day.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				dayAdapter.setCurrentIndex(wheel.getCurrentItem());
			}

		});
		day.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}
			@Override
			public void onScrollingFinished(WheelView wheel) {
				Log.e("【当前天数选择Item下标】="+String.valueOf(wheel.getCurrentItem()));
				dayAdapter.setCurrentIndex(wheel.getCurrentItem());
				String currentText = (String) dayAdapter.getItemText(wheel.getCurrentItem());
				setTextViewSize(currentText, dayAdapter);
			}
		});
	}

	private void updateDays(WheelView year, WheelView month, WheelView day) {
      if(year.getCurrentItem()==0&&monthAdapter.getItemsCount()!=12){
		  Calendar calendar = Calendar.getInstance();
		  calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - range-beginRange + year.getCurrentItem());
		  Log.e(""+calendar.get(Calendar.YEAR));
		  Log.e("【当前年，但是月份不足12个月时】 updateDays当前月份="+mCurMonth+"="+month.getCurrentItem());
		  if(previousMonth){
			  calendar.set(Calendar.MONTH, month.getCurrentItem()+mCurMonth);
		  }else{
			  calendar.set(Calendar.MONTH, month.getCurrentItem()+(mCurMonth-1));
		  }
		  calendar.set(Calendar.DATE, 1);
		  calendar.roll(Calendar.DATE, -1);
		  int maxDays = calendar.get(Calendar.DATE);
		  Log.e("【当前月份】"+calendar.get(Calendar.MONTH)+"updateDays当前月份最大天数="+maxDays);

		  if(month.getCurrentItem()==0){
			  int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
			  setAdapterAndIndex(day,dayAdapter,mCurDay,maxDays,curDay - 1);
		  }else{
			  int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
			  setAdapterAndIndex(day,dayAdapter,1,maxDays,curDay - 1);
		  }

	  }else if(year.getCurrentItem()!=0&&year.getCurrentItem()!=range&&dayAdapter.getItemsCount()<26){

		  Calendar calendar = Calendar.getInstance();
		  calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - range-beginRange + year.getCurrentItem());
		  calendar.set(Calendar.MONTH, month.getCurrentItem()+(mCurMonth-1));
		  calendar.set(Calendar.DATE, 1);
		  calendar.roll(Calendar.DATE, -1);
		  int maxDays = calendar.get(Calendar.DATE);
		  Log.e("【当前月份】"+mCurMonth+"="+month.getCurrentItem()+"updateDays当前月份最大天数="+maxDays);
			  int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
			  setAdapterAndIndex(day,dayAdapter,1,maxDays,curDay - 1);

	  }else{
			  Calendar calendar = Calendar.getInstance();
			  calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - range-beginRange + year.getCurrentItem());
			  calendar.set(Calendar.MONTH, month.getCurrentItem());
		      calendar.set(Calendar.DATE, 1);
		      calendar.roll(Calendar.DATE, -1);
			  int maxDays = calendar.get(Calendar.DATE);
			  int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
              Log.e("【current year】"+calendar.get(Calendar.YEAR)+"【current month】"+calendar.get(Calendar.MONTH)+"【maxDays maxDays】="+String.valueOf(maxDays));
		      Calendar calendarTemp = Calendar.getInstance();
		      calendarTemp.set(Calendar.YEAR,mCurYear);
		  	  calendarTemp.set(Calendar.MONTH,mCurMonth-1);
		  	if(calendar.get(Calendar.YEAR)==calendarTemp.get(Calendar.YEAR)&&calendarTemp.get(Calendar.MONTH)==calendar.get(Calendar.MONTH)){
				Log.e("需要优化点!!!");
				setAdapterAndIndex(day,dayAdapter,1,mCurDay,curDay - 1);
			}else{
				setAdapterAndIndex(day,dayAdapter,maxDays,curDay - 1);
			}
	  }
	}

	private void updateMonths(WheelView year, WheelView month, WheelView day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - range + year.getCurrentItem());
		calendar.set(Calendar.MONTH, month.getCurrentItem());
		calendar.set(Calendar.DATE, 1);
		calendar.roll(Calendar.DATE, -1);
		int maxDays = calendar.get(Calendar.DATE);
		//年份是今年而且月份数不是不是12个月
		if (year.getCurrentItem() == range) {
			Log.e("=====bu bu bu"+month.getCurrentItem());
			if (month.getCurrentItem() >= mCurMonth - 1) {
				setAdapterAndIndex(month,monthAdapter,mCurMonth,mCurMonth - 1);
			} else {
				setAdapterAndIndex(month,monthAdapter,mCurMonth,month.getCurrentItem());
			}
			if (month.getCurrentItem() == mCurMonth - 1) {
				setAdapterAndIndex(day,dayAdapter,mCurDay,mCurDay - 1);
			} else {
				setAdapterAndIndex(day,dayAdapter,maxDays,day.getCurrentItem());
			}
		}else if(year.getCurrentItem()==0){
			Log.e("monthAdapter中月份的ItemCount数"+monthAdapter.getItemsCount());
			int tempMonth;
			if(previousMonth){
				tempMonth=mCurMonth+1;
			}else{
				tempMonth=mCurMonth;
			}
			if( monthAdapter.getItemsCount()==12){
				Log.e("到最后一年，月份重新初始化！！！"+monthAdapter.hashCode());
				setAdapterAndIndex(month,monthAdapter,tempMonth,12,0);

			}else{
				Log.e("最后一年时，根据下标更改月份="+month.getCurrentItem());
				setAdapterAndIndex(month,monthAdapter,tempMonth,12,month.getCurrentItem());
			}

			if (month.getCurrentItem() == 0) {
				Log.e("到最后一年，第一个月份的天数重新初始化！！！"+dayAdapter+"mayDays=="+maxDays);
				setAdapterAndIndex(day,dayAdapter,mCurDay,maxDays,0);
				updateDays(year, month, day);
			} else {
				Log.e("到最后一年，选择下标不是为0的月份的天数执行化！！！"+day.getCurrentItem());
				setAdapterAndIndex(day,dayAdapter,1,maxDays,day.getCurrentItem());
			}
			//年份不是今年而且月份不是12个月的时候刷新，让月份恢复12个月，天数恢复满月
		} else if (year.getCurrentItem() != range) {
			Log.e("check check check!!!"+monthAdapter.getItemText(month.getCurrentItem()));
			Log.e(""+Integer.valueOf(""+dayAdapter.getItemText(day.getCurrentItem())));
			if(monthAdapter.getItemsCount()==12){
				setAdapterAndIndex(month,monthAdapter,1,12,month.getCurrentItem());
				setAdapterAndIndex(day,dayAdapter,maxDays,day.getCurrentItem());
			}else{
				setAdapterAndIndex(month,monthAdapter,1,12,month.getCurrentItem());
				setAdapterAndIndex(day,dayAdapter,1,maxDays,day.getCurrentItem());
			}
		}


	}

	private void setAdapterAndIndex(WheelView wheelView, NumericWheelAdapter adapter,int min,int max,int index) {
		wheelView.setCurrentItem(index);
		adapter.setCurrentIndex(index);
		adapter.setMinValue(min);
		adapter.setMaxValue(max);
		wheelView.setViewAdapter(adapter);
	}

	private void setAdapterAndIndex(WheelView wheelView, NumericWheelAdapter adapter,int max,int index) {
		wheelView.setCurrentItem(index);
		adapter.setCurrentIndex(index);
		adapter.setMaxValue(max);
		wheelView.setViewAdapter(adapter);
	}


	/**
	 * Set Selected Item font color
	 * @param currentItemText
	 * @param adapter
	 */
	public void setTextViewSize(String currentItemText, DateNumericAdapter adapter) {
		ArrayList<View> arrayList = adapter.getArrayList();
		int size = arrayList.size();
		String currentText;
		for (int i = 0; i < size; i++) {
			TextView textView = (TextView) arrayList.get(i);
			currentText = textView.getText().toString();
			currentText = currentText.substring(0, currentText.length() - 1);
			if (currentItemText.equals(currentText)) {
				textView.setTextSize(14);
				textView.setTextColor(Color.parseColor("#ff961a"));
			} else {
				textView.setTextColor(Color.parseColor("#909090"));
				textView.setTextSize(14);
			}
		}
	}

	private class DateNumericAdapter extends NumericWheelAdapter {
		private DateNumericAdapter(Context context, int minValue, int maxValue,
								  int current) {
			super(context, minValue, maxValue);
			setCurrentIndex(current);
			setMaxValue(maxValue);
		}
		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.submit:
				String years =  yearAdapter.getItemText(year.getCurrentItem()).toString().substring(0,4);
				String months = monthAdapter.getItemText(month.getCurrentItem()).toString();
				months=months.substring(0,months.length());
				if(months.length()==1){
					months="0"+months;
				}
				String days = dayAdapter.getItemText(day.getCurrentItem()).toString();
				days=days.substring(0,days.length());
				if(days.length()==1){
					days="0"+days;
				}
				String age = years + "-" + months + "-" + days;
				showIsHide.checkCurrentTime(age);
				this.dismiss();
				break;
			case R.id.cancel:
				this.dismiss();
				break;
			default:
				break;
		}
	}

}
