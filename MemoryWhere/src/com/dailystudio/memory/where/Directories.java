package com.dailystudio.memory.where;

import java.io.File;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.os.Environment;

public class Directories {
	
	private static final String MAP_CACHES_DIR = "/map_caches/";
	private static final String MAP_FILE_EXT = ".png";
	
	private static final String PHOTOS_DIR = "/NOWherePhotos/";
	private static final String PHOTO_FILE_EXT = ".jpg";

	public static final String getPhotosDirectory(Context context) {
		final File extDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		if (extDir == null) {
			return null;
		}
		
		final File photosDir = new File(extDir, PHOTOS_DIR);
		
		return photosDir.getAbsolutePath();
	}
	
	public static final String getMapCachesDirectory(Context context) {
		final File extDir = context.getExternalFilesDir(null);
		if (extDir == null) {
			return null;
		}
		
		final File cachesDir = new File(extDir, MAP_CACHES_DIR);
		
		return cachesDir.getAbsolutePath();
	}
	
	public static final String generatePhotoPath(Context context) {
		String photosDir = Directories.getPhotosDirectory(context);
		if (photosDir == null) {
			return null;
		}
		
		String timeStamp = 
				new SimpleDateFormat("yyyy_MMdd_HHmmss").format(
						System.currentTimeMillis());
		
		StringBuilder builder = new StringBuilder(photosDir);
		
		builder.append('/');
		builder.append(timeStamp);
		builder.append(PHOTO_FILE_EXT);
		
		return builder.toString();
	}

	public static final String composeMapCachePath(
			Context context, double lat, double lon) {
		return composeMapCachePath(context, 
				(int) (lat * 1E6),
    			(int) (lon * 1E6));
	}
	
	public static final String composeMapCachePath(
			Context context, long lat, long lon) {
		if (context == null) {
			return null;
		}
		
		String cachesDir = Directories.getMapCachesDirectory(context);
		if (cachesDir == null) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder(cachesDir);
		
		builder.append("/");
		builder.append(lat);
		builder.append("_");
		builder.append(lon);
		builder.append(MAP_FILE_EXT);
		
		return builder.toString();
	}


}
