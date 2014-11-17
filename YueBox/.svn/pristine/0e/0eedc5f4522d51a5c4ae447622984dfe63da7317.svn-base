package com.ebupt.yuebox.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyFileFilter implements FileFilter {

	/**
	 * 匹配文件名称
	 */
	private String mTaskId;

	public MyFileFilter(String mTaskId) {
		this.mTaskId = mTaskId;
	}

	public boolean accept(File file) {
		try {
			Pattern pattern = Pattern.compile(mTaskId + "_image\\d+.*");
			Matcher match = pattern.matcher(file.getName());
			return match.matches();
		} catch (Exception e) {
			return true;
		}
	}
}