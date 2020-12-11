package com.dailystudio.memory.application.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;

import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.ui.AlertDialogFragment;
import com.dailystudio.memory.application.R;

public class UninstallSystemAppDialogFragment extends AlertDialogFragment {

	private String mAppLabel = null;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		if (args != null) {
			mAppLabel = args.getString(Constants.EXTRA_APP_LABEL); 
		}

		Dialog dialog = super.onCreateDialog(savedInstanceState);
		
		if (dialog instanceof AlertDialog) {
			((AlertDialog)dialog).setIcon(
					R.drawable.ic_app_uninstall_system_normal);
		}
		
		return dialog;
	}
	
	@Override
	protected CharSequence getConfirmText() {
		return getActivity().getString(R.string.alert_dialog_button_confirm);
	}

	@Override
	protected CharSequence getCancelText() {
		return null;
	}
	
	@Override
	protected CharSequence getTitle() {
		return getActivity().getString(R.string.alert_dialog_uninst_app_title);
	}

	@Override
	protected CharSequence getMessage() {
		String templ = getString(
				R.string.alert_dialog_uninst_system_app_message_templ);
		if (templ == null || mAppLabel == null) {
			return null;
		}
		
		return Html.fromHtml(String.format(templ, mAppLabel));
	}
	
	@Override
	protected void onDialogConfirmed(DialogInterface dialog, int which) {
	}

	@Override
	protected void onDialogCancelled(DialogInterface dialog, int which) {
	}

	
}
