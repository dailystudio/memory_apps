package com.dailystudio.memory.person.databaseobject;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseWriter;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.person.PersonFeatureBundle;

public class PersonFeatureDatabaseModal {

	public static void setFeature(Context context, String person, String feature, String value) {
		if (context == null || !isValidFeature(person, feature)) {
			return;
		}
		
		TimeCapsuleDatabaseWriter<PersonFeatureObject> writer = 
				new TimeCapsuleDatabaseWriter<PersonFeatureObject>(context,
						PersonFeatureObject.class);
		
		PersonFeatureObject f = getFeatureObject(context, person, feature);
		if (f == null) {
			f = new  PersonFeatureObject(context);
		}
		
		f.setTime(System.currentTimeMillis());
		f.setPersonId(person);
		f.setFeatureId(feature);
		f.setFeatureValue(value);
		
		if (f.getId() > 0) {
			writer.update(f);
		} else {
			writer.insert(f);
		}
	}

	public static void setFeatures(Context context, String person,
			List<PersonFeatureBundle> bundles) {
		if (context == null
				|| bundles == null
				|| bundles.size() <= 0) {
			return;
		}
		
		List<PersonFeatureBundle> existedFeatures = new ArrayList<PersonFeatureBundle>();
		for (PersonFeatureBundle b: bundles) {
			if (getFeatureObject(context, person, b.getFeatureId()) != null) {
				existedFeatures.add(b);
			}
		}
		
		deleteFeatures(context, person, existedFeatures);
		
		addFeatures(context, person, bundles);
	}
	
	public static void addFeatures(Context context, String person,
			List<PersonFeatureBundle> bundles) {
		if (context == null
				|| bundles == null
				|| bundles.size() <= 0) {
			return;
		}
		
		TimeCapsuleDatabaseWriter<PersonFeatureObject> writer = 
				new TimeCapsuleDatabaseWriter<PersonFeatureObject>(context, 
						PersonFeatureObject.class);
		
		final PersonFeatureObject[] objects = 
				convertFeatureObjects(context, person, bundles);
		
		writer.insert(objects);
	}
	
	
	public static void deleteFeatures(Context context, String person,
			List<PersonFeatureBundle> bundles) {
		if (context == null
				|| bundles == null
				|| bundles.size() <= 0) {
			return;
		}
		
		
		final String[] featureIds = dumpFeatureIds(bundles);
		if (featureIds == null || featureIds.length <= 0) {
			return;
		}
		
		TimeCapsuleDatabaseWriter<PersonFeatureObject> writer = 
				new TimeCapsuleDatabaseWriter<PersonFeatureObject>(context, 
						PersonFeatureObject.class);
		
		Query query = new Query(PersonFeatureObject.class);
		
		ExpressionToken selToken = 
				PersonFeatureObject.COLUMN_PERSON_ID.eq(person)
					.and(PersonFeatureObject.COLUMN_FEATURE_ID.inValues(featureIds));
		if (selToken == null) {
			return;
		}
		
		query.setSelection(selToken);
		
		writer.delete(query);
	}
	
	private static String[] dumpFeatureIds(List<PersonFeatureBundle> bundles) {
		if (bundles == null
				|| bundles.size() <= 0) {
			return null;
		}
		
		List<String> ids = new ArrayList<String>();
		for (PersonFeatureBundle b: bundles) {
			ids.add(b.getFeatureId());
		}
		
		return ids.toArray(new String[0]);
	}
	
	private static PersonFeatureObject[] convertFeatureObjects(Context context,
			String pid,
			List<PersonFeatureBundle> bundles) {
		if (context == null
				|| TextUtils.isEmpty(pid)
				|| bundles == null
				|| bundles.size() <= 0) {
			return null;
		}
		
		List<PersonFeatureObject> objects = new ArrayList<PersonFeatureObject>();
		PersonFeatureObject object;
		for (PersonFeatureBundle b: bundles) {
			object = new PersonFeatureObject(context);
			
			object.setTime(System.currentTimeMillis());
			object.setFeatureId(b.getFeatureId());
			object.setFeatureValue(b.getFeatureValue());
			object.setPersonId(pid);

			objects.add(object);
		}
		
		return objects.toArray(new PersonFeatureObject[0]);
	}

	public static String getFeature(Context context, String person, String feature) {
		final PersonFeatureObject object = getFeatureObject(
				context, person, feature);
		if (object == null) {
			return null;
		}
		
		return object.getFeatureValue();
	}
	
	public static PersonFeatureObject getFeatureObject(Context context, String person, String feature) {
		if (context == null || !isValidFeature(person, feature)) {
			return null;
		}
		
		TimeCapsuleDatabaseReader<PersonFeatureObject> reader =
				new TimeCapsuleDatabaseReader<PersonFeatureObject>(context,
						PersonFeatureObject.class);
		
		Query query = new Query(PersonFeatureObject.class);
		
		ExpressionToken selToken = 
				PersonFeatureObject.COLUMN_PERSON_ID.eq(person)
					.and(PersonFeatureObject.COLUMN_FEATURE_ID.eq(feature));
		if (selToken == null) {
			return null;
		}
		
		query.setSelection(selToken);
		
		return reader.queryLastOne(query);
	}
	
	private static boolean isValidFeature(String person, String feature) {
		if (TextUtils.isEmpty(person)
				|| TextUtils.isEmpty(feature)) {
			Logger.debug("invalid person feature: pid(%s), feature(%s)",
					person, feature);
			return false;
		}

		return true;
	}
	
}
