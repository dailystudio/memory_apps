package com.dailystudio.memory.ui;

import java.util.ArrayList;

import com.dailystudio.memory.R;
import com.dailystudio.memory.querypiece.MemoryPieceCard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CardsAdapter extends ArrayAdapter<MemoryPieceCard> {

	public CardsAdapter(Context context) {
		super(context, 0, new ArrayList<MemoryPieceCard>());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView instanceof ViewGroup == false) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.card_item, null);
		}
		
		MemoryPieceCard card = getItem(position);

		if (card != null && convertView instanceof CardLayout) {
			CardLayout cardLayout = (CardLayout) convertView;
			
			cardLayout.bindCard(card);
		}

		return convertView;
	}

}
