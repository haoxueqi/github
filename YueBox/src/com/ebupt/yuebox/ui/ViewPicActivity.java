package com.ebupt.yuebox.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.ebupt.yuebox.R;

public class ViewPicActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_pic);
		initView();
	}

	private void initView() {
		String pic_path = getIntent().getStringExtra("pic_path");
		ImageView view = (ImageView) findViewById(R.id.view_pic);
		Bitmap bm = BitmapFactory.decodeFile(pic_path);
		view.setImageBitmap(bm);
	}

}
