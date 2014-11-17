package com.ebupt.yuebox.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class CustomDialog extends Dialog {
	public CustomDialog(Context context, int layout, int style) {
		super(context, style);
		setContentView(layout);
	}

	public CustomDialog(Context context, double width, double height, int layout, int style) {
		super(context, style);

		setContentView(layout);

		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();

		params.width = (int) width;
		params.height = (int) height;
		params.gravity = Gravity.BOTTOM;

		window.setAttributes(params);
	}

}