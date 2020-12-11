package com.dailystudio.memory.where.databaseobject;

import android.content.Context;

import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;

public class MemoryLocationModal {
	
	public static MemoryLocation queryLastLocation(Context context) {
		if (context == null) {
			return null;
		}
		
		TimeCapsuleDatabaseReader<MemoryLocation> reader =
				new TimeCapsuleDatabaseReader<MemoryLocation>(context,
						MemoryLocation.class);

		return reader.queryLastOne();
	}
	

}
