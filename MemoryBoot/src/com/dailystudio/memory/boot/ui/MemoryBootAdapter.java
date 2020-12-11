package com.dailystudio.memory.boot.ui;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.memory.boot.MemoryBoot;
import com.dailystudio.memory.boot.MemoryBootDatabaseModal;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.ui.ExpandableItemDatabaseObjectCursorAdatper;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;

public class MemoryBootAdapter extends ExpandableItemDatabaseObjectCursorAdatper<MemoryBoot> {

	public MemoryBootAdapter(Context context) {
		super(context, R.layout.boot_item, MemoryBoot.class);
	}

	@Override
	protected View getExpandView(View itemView) {
		if (itemView == null) {
			return null;
		}
		
		return itemView.findViewById(R.id.boot_expand_layout);
	}

	@Override
	protected void bindExpandedView(View expandView, Context context,
			Cursor c) {
		if (expandView == null || context == null || c == null) {
			return;
		}
		
		final MemoryBoot boot = dumpItem(c);
		if (boot == null) {
			return;
		}
		
		String str = null;
		final long shutdown = boot.getShutDownTime();
		final long poweron = boot.getTime();
		final long uptime = boot.getBootUpTime();
		final long lifetime = shutdown - poweron;
		
/*		Logger.debug("shutdown = %d, poweron = %d, uptime = %d, lifetime = %d",
				shutdown,
				poweron,
				uptime,
				lifetime);
*/		
		boolean isCurrentBoot = MemoryBootDatabaseModal.isCurrentBoot(
				mContext, boot);
		
		TextView shutdownView = (TextView) expandView.findViewById(R.id.boot_shutdown);
		if (shutdownView != null) {
			if (shutdown <= 0 || isCurrentBoot) {
				str = mContext.getString(R.string.error_unknow);
			} else {
				str = DateTimePrintUtils.printTimeString(context, shutdown);
			}
			
			shutdownView.setText(str);
		}
		
		TextView lifetimeView = (TextView) expandView.findViewById(R.id.boot_lifetime);
		if (lifetimeView != null) {
			if (lifetime <= 0) {
				str = mContext.getString(R.string.error_unknow);
			} else {
				str = DateTimePrintUtils.printDurationString(mContext, lifetime);
			}
			
			lifetimeView.setText(str);
		}
		
		TextView uptimeView = (TextView) expandView.findViewById(R.id.boot_uptime);
		if (uptimeView != null) {
			if (uptime <= 0) {
				str = mContext.getString(R.string.error_unknow);
			} else {
				str = DateTimePrintUtils.printDurationString(mContext, uptime, true);
			}
			
			uptimeView.setText(str);
		}
	}

	@Override
	protected void bindCollapsedView(View view, Context context, Cursor c) {
		if (view == null || context == null || c == null) {
			return;
		}
		
		final MemoryBoot boot = dumpItem(c);
		if (boot == null) {
			return;
		}
		
		boolean isCurrentBoot = MemoryBootDatabaseModal.isCurrentBoot(
				mContext, boot);

		ImageView iconView = (ImageView) view.findViewById(R.id.memory_icon);
		if (iconView != null) {
			iconView.setImageResource((isCurrentBoot ? 
					R.drawable.ic_boot_active : R.drawable.ic_boot));			
		}
		
		View separator = view.findViewById(R.id.boot_expand_separator);
		if (separator != null) {
			int bgColorId = (isCurrentBoot ? R.color.boot_expand_separator_active_color
					: R.color.boot_expand_separator_color);
			
			Resources res = mContext.getResources();
			if (res != null) {
				separator.setBackgroundColor(res.getColor(bgColorId));
			}
		}
		
		TextView indexview = (TextView) view.findViewById(R.id.memory_index);
		if (indexview != null) {
			indexview.setText(String.valueOf(c.getPosition()));
		}
		
		TextView seqview = (TextView) view.findViewById(R.id.boot_seq);
		if (seqview != null) {
			seqview.setText(String.format("Boot %04d", boot.getBootSequence()));
		}
		
		TextView timeview = (TextView) view.findViewById(R.id.boot_time);
		if (timeview != null) {
			timeview.setText(DateTimePrintUtils.printTimeString(context, boot.getTime()));
		}
	}
	
}
