package com.ebupt.yuebox.util;

public class Const {
	// 扫描页面
	public static final String SCAN_KEY = "scan_mode";
	public static final int SCAN_VALUE_BARCODE = 0;
	public static final int SCAN_VALUE_BOOKCOVER = 1;

	// 排行榜类别
	public static final int RANK_MONTH = 0;
	public static final int RANK_YEAR = 1;
	public static final int RANK_TOTAL = 2;

	// Status
	public static final String TASK_FREE = "00";
	public static final String TASK_NOT_FREE = "01";
	public static final String TASK_UNDONE = "10";
	public static final String TASK_DONE = "11";

	// Scan
	public static final int GET_BOX_ID_OK = 0;
	public static final int UPLOAD_PIC_OK = 2;
	// Edit
	public static final int EDIT_TASK_OK = 1;
	// Just back
	public static final int BACK = -1;
	
	//Pic file dir
	public static final String PIC_DIR = "/sdcard/TaskPicture/";

}
