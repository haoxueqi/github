package com.ebupt.yuebox.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefUtil {

	private static String name = "pic_upload_rec";

	public static void AddToPicUploadRec(Context ctx, String key, boolean b) {
		SharedPreferences preferences = ctx.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(key, b);
		editor.commit();
	}

	public static void RemovePicUploadRec(Context ctx, String key) {
		SharedPreferences preferences = ctx.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}

	public static boolean getFromPicUploadRec(Context ctx, String key) {
		SharedPreferences preferences = ctx.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		boolean flag = preferences.getBoolean(key, false);
		return flag;
	}

	public static List<String> getAllUnfinishedTask(Context ctx) {
		SharedPreferences preferences = ctx.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		List<String> array = new ArrayList<String>();
		Set<String> set = preferences.getAll().keySet();
		for (String s : set) {
			if (preferences.getBoolean(s, false)) {
				array.add(s);
			}
		}
		return array;
	}

}
