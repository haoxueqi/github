package com.ebupt.yuebox.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.database.DbUtil;
import com.ebupt.yuebox.database.SetupTaskDao;
import com.ebupt.yuebox.model.SetupTask;
import com.ebupt.yuebox.util.Const;

public class EditInfoActivity extends Activity implements OnClickListener {

	EditText mMall;
	EditText mPerson;
	EditText mMobile;
	EditText mTel;

	SetupTaskDao mTaskDao;
	SetupTask mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_info);
		mTaskDao = DbUtil.getSetupTaskDao();
		mTask = mTaskDao.load(getIntent().getStringExtra("task_id"));
		initView();
	}

	private void initView() {
		mMall = (EditText) findViewById(R.id.ed_mall);
		mPerson = (EditText) findViewById(R.id.ed_person);
		mMobile = (EditText) findViewById(R.id.ed_mobile);
		mTel = (EditText) findViewById(R.id.ed_tel);

		mMall.setText(mTask.getTask_client_name());
		mPerson.setText(mTask.getTask_client_person());
		mMobile.setText(mTask.getTask_client_mobile());
		String tel = mTask.getTask_client_tel();
		if (!tel.equals("null"))
			mTel.setText(mTask.getTask_client_tel());

		findViewById(R.id.submit_btn).setOnClickListener(this);
		findViewById(R.id.back_btn).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit_btn:
			saveToDataBase();
			setResult(Const.EDIT_TASK_OK);
			finish();
			break;
		case R.id.back_btn:
			setResult(Const.BACK);
			finish();
			break;
		}
	}

	private void saveToDataBase() {
		mTask.setTask_client_name(mMall.getText().toString());
		mTask.setTask_client_person(mPerson.getText().toString());
		mTask.setTask_client_mobile(mMobile.getText().toString());
		mTask.setTask_client_tel(mTel.getText().toString());
		mTaskDao.update(mTask);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(Const.BACK);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

}
