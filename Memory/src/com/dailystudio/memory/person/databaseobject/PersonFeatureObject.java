package com.dailystudio.memory.person.databaseobject;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.datetime.dataobject.TimeCapsule;

public class PersonFeatureObject extends TimeCapsule {

	public static final Column COLUMN_PERSON_ID = new TextColumn("person_id", false);
	public static final Column COLUMN_FEATURE_ID = new TextColumn("feature_id", false);
	public static final Column COLUMN_FEATURE_VALUE = new TextColumn("feature_value", false);
	
	private final static Column[] sCloumns = {
		COLUMN_PERSON_ID,
		COLUMN_FEATURE_ID,
		COLUMN_FEATURE_VALUE,
	};
	
	public PersonFeatureObject(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}
	
	public String getPersonId() {
		return getTextValue(COLUMN_PERSON_ID);
	}

	public void setPersonId(String pid) {
		setValue(COLUMN_PERSON_ID, pid);
	}

	public String getFeatureId() {
		return getTextValue(COLUMN_FEATURE_ID);
	}

	public void setFeatureId(String fid) {
		setValue(COLUMN_FEATURE_ID, fid);
	}

	public String getFeatureValue() {
		return getTextValue(COLUMN_FEATURE_VALUE);
	}

	public void setFeatureValue(String value) {
		setValue(COLUMN_FEATURE_VALUE, value);
	}

	@Override
	public String toString() {
		return String.format("%s, person(%s), feature [id: %s, value: %s]",
				super.toString(),
				getPersonId(),
				getFeatureId(),
				getFeatureValue());
	}

}
