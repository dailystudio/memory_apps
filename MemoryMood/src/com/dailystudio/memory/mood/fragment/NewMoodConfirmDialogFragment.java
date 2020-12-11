package com.dailystudio.memory.mood.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.dailystudio.memory.mood.Constants;
import com.dailystudio.memory.mood.Mood;
import com.dailystudio.memory.mood.Moods;
import com.dailystudio.memory.ui.AlertDialogFragment;
import com.dailystudio.memory.mood.R;

public class NewMoodConfirmDialogFragment extends AlertDialogFragment {

	private EditText mCommentsView = null;
	private String mComments = null;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
	
		int moodId = 0;
		
		Bundle args = getArguments();
		if (args != null) {
			moodId = args.getInt(Constants.EXTRA_MOOD_ID, 0);
		}
		
		Mood mood = Moods.getMood(moodId);
		if (mood != null && dialog instanceof AlertDialog) {
			((AlertDialog)dialog).setIcon(mood.iconResId);
		}
		
		return dialog;
	}
	
	@Override
	protected CharSequence getConfirmText() {
		return getActivity().getString(R.string.alert_dialog_button_confirm);
	}

	@Override
	protected CharSequence getCancelText() {
		return getActivity().getString(R.string.alert_dialog_button_cancel);
	}

	@Override
	protected CharSequence getTitle() {
		return getActivity().getString(R.string.alert_dialog_add_mood_title);
	}

	@Override
	protected CharSequence getMessage() {
		return null;
	}
	
	@Override
	protected void onDialogConfirmed(DialogInterface dialog, int which) {
		mComments = null;
		
		if (mCommentsView == null) {
			return;
		}
		
		Editable editable = mCommentsView.getText();
		if (editable == null) {
			return;
		}
		
		mComments = editable.toString();
	}

	@Override
	protected void onDialogCancelled(DialogInterface dialog, int which) {
	}
	
	@Override
	protected boolean hasCustomView() {
		return true;
	}

	@Override
	protected View createCustomView() {
		View customView = LayoutInflater.from(getActivity()).inflate(
				R.layout.alert_dialog_add_new_mood, null);
		
		if (customView != null) {
			mCommentsView = (EditText) customView.findViewById(
					R.id.alert_anm_commnets);
		}
		
		return customView;
	}
	
	public String getComments() {
		return mComments;
	}

}
