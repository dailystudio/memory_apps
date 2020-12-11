package com.dailystudio.memory.boot.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.dataobject.MonthObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MonthsAdatper extends ArrayAdapter<MonthObject> {

	public MonthsAdatper(Context context) {
		super(context, 0, new ArrayList<MonthObject>());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Context context = getContext();
		
		if (convertView == null)  {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.layout_month, null);
		}
		
		MonthObject month = getItem(position);

		if (convertView != null) {
			final long time = month.getTime();
			
			TextView numView = (TextView) convertView.findViewById(R.id.month);
			if (numView != null) {
		    	SimpleDateFormat formater = new SimpleDateFormat("MMM yyyy");

				numView.setText(formater.format(time));
			}
		}
		
		return convertView;
	}

}

