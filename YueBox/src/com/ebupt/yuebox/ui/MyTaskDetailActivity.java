package com.ebupt.yuebox.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.application.MyApplication;
import com.ebupt.yuebox.database.DbUtil;
import com.ebupt.yuebox.database.SetupTaskDao;
import com.ebupt.yuebox.model.SetupTask;
import com.ebupt.yuebox.net.NetEngine;
import com.ebupt.yuebox.util.Const;
import com.ebupt.yuebox.util.SharedPrefUtil;
import com.ebupt.yuebox.util.MyFileFilter;
import com.ebupt.yuebox.view.CustomDialog;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * 
 * @author threeStar
 * 
 */

public class MyTaskDetailActivity extends Activity implements OnClickListener {

	private ImageView mTakePicBtn;
	private ImageView mReviewPicBtn;
	private ImageView mScanBarcodeBtn;
	private TextView mTaskName;
	private TextView mTaskAddress;
	private TextView mTaskMobile;
	private Button mSubmitBtn;
	private ImageView mEditInfoBtn;
	private View mBtnLayout;
	private CustomDialog mUploadPicDialog;
	private TextView mUploadHint;

	private String mTaskId;
	private SetupTask mTask;
	File[] mPicList;
	int mUploadFlag;

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd",
			Locale.CHINA);
	private SetupTaskDao mTaskDao = DbUtil.getSetupTaskDao();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_task_detail);

		mTaskId = getIntent().getStringExtra("task_id");
		mTask = mTaskDao.load(mTaskId);
		initView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Const.GET_BOX_ID_OK) {
			String box_id = data.getStringExtra("box_id");
			if (mTask.getTask_boxids() == null
					|| mTask.getTask_boxids().equals("")
					|| mTask.getTask_boxids().equals("null")) {
				mTask.setTask_boxids(box_id);
			} else {
				box_id = mTask.getTask_boxids() + "," + box_id;
				mTask.setTask_boxids(box_id);
			}
			mTaskDao.update(mTask);
		} else if (resultCode == Const.EDIT_TASK_OK) {
			mTask = mTaskDao.load(mTaskId);
			updateView();
		}
	};

	private void updateView() {
		if (mTask.getTask_client_name() != null)
			mTaskName.setText(mTask.getTask_client_name());
		mTaskAddress.setText(mTask.getTask_client_address());
		String tel = mTask.getTask_client_mobile();
		if (!mTask.getTask_client_tel().equals("null")
				&& !mTask.getTask_client_tel().equals("")
				&& mTask.getTask_client_tel() != null) {
			tel += "、" + mTask.getTask_client_tel();
		}
		mTaskMobile.setText(tel);

		((TextView) findViewById(R.id.tv_grab_credits)).setText("抢单 "
				+ mTask.getTask_grab_credits());
		((TextView) findViewById(R.id.tv_finish_credits)).setText("完成 "
				+ mTask.getTask_finish_credits());

		String taskPublishTimeStr = mDateFormat.format(new Date(mTask
				.getTask_publish_time() * 1000));
		String taskEndTimeStr = mDateFormat.format(new Date(mTask
				.getTask_end_time() * 1000));
		String taskAmountStr = mTask.getTask_box_num() + "";

		String taskPublishTimeFormat = getResources().getString(
				R.string.task_time);
		String taskEndTimeFormat = getResources().getString(
				R.string.task_end_time);
		String taskAmountFormat = getResources()
				.getString(R.string.task_amount);
		((TextView) findViewById(R.id.tv_publish_time)).setText(String.format(
				taskPublishTimeFormat, taskPublishTimeStr));
		((TextView) findViewById(R.id.tv_end_time)).setText(String.format(
				taskEndTimeFormat, taskEndTimeStr));
		((TextView) findViewById(R.id.tv_box_num)).setText(String.format(
				taskAmountFormat, taskAmountStr));

	}

	private void initView() {
		mTakePicBtn = (ImageView) findViewById(R.id.take_pic_btn);
		mScanBarcodeBtn = (ImageView) findViewById(R.id.scan_barcode_btn);
		mTaskName = (TextView) findViewById(R.id.tv_detail_name);
		mTaskAddress = (TextView) findViewById(R.id.tv_detail_address);
		mTaskMobile = (TextView) findViewById(R.id.tv_detail_mobile);
		mSubmitBtn = (Button) findViewById(R.id.submit_btn);
		mBtnLayout = findViewById(R.id.layout_btn);
		mEditInfoBtn = (ImageView) findViewById(R.id.edit_btn);
		mReviewPicBtn = (ImageView) findViewById(R.id.review_pic_btn);

		if (mTask.getTask_status().equals(Const.TASK_DONE)) {
			mSubmitBtn.setEnabled(false);
			mSubmitBtn.setText("已完成");
			mSubmitBtn.setBackgroundColor(getResources().getColor(
					R.color.btn_disable));
			mEditInfoBtn.setVisibility(View.INVISIBLE);
			mBtnLayout.setVisibility(View.INVISIBLE);
			mReviewPicBtn.setVisibility(View.VISIBLE);
		} else {
			mSubmitBtn.setEnabled(true);
			mBtnLayout.setVisibility(View.VISIBLE);
			mSubmitBtn.setText("完成提交");
			mSubmitBtn.setBackgroundColor(getResources().getColor(
					R.color.btn_orange));
		}
		updateView();
		mTakePicBtn.setOnClickListener(this);
		mScanBarcodeBtn.setOnClickListener(this);
		mSubmitBtn.setOnClickListener(this);
		mEditInfoBtn.setOnClickListener(this);
		mReviewPicBtn.setOnClickListener(this);
		findViewById(R.id.layout_address).setOnClickListener(this);
		findViewById(R.id.layout_mobile).setOnClickListener(this);
		findViewById(R.id.image_back).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_back:
			finish();
			break;
		case R.id.take_pic_btn:
			Intent intentPic = new Intent(MyTaskDetailActivity.this,
					TaskPicListActivity.class);
			intentPic.putExtra("task_id", mTaskId);
			intentPic.putExtra("isOnlyShow", false);
			intentPic.putExtra("client_name", mTask.getTask_client_name());
			startActivity(intentPic);
			break;
		case R.id.scan_barcode_btn:
			goToScanBarcode();
			break;
		case R.id.submit_btn:
			submitTask();
			break;
		case R.id.edit_btn:
			Intent intent = new Intent(MyTaskDetailActivity.this,
					EditInfoActivity.class);
			intent.putExtra("task_id", mTaskId);
			startActivityForResult(intent, 0);
			break;
		case R.id.layout_address:
			Intent intentToAddress = new Intent(MyTaskDetailActivity.this,
					ShowWayActivity.class);
			intentToAddress.putExtra("task_id", mTaskId);
			startActivity(intentToAddress);
			break;
		case R.id.layout_mobile:
			makeCall();
			break;
		case R.id.review_pic_btn:
			Intent intentShowPic = new Intent(MyTaskDetailActivity.this,
					TaskPicListActivity.class);
			intentShowPic.putExtra("task_id", mTaskId);
			intentShowPic.putExtra("isOnlyShow", true);
			intentShowPic.putExtra("client_name", mTask.getTask_client_name());
			startActivity(intentShowPic);
			break;
		}
	}

	private void makeCall() {
		if (mTask.getTask_client_tel() == null
				|| mTask.getTask_client_tel().equals("")
				|| mTask.getTask_client_tel().equals("null")) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ mTask.getTask_client_mobile()));
			startActivity(intent);
		} else {
			new AlertDialog.Builder(MyTaskDetailActivity.this)
					.setTitle("请选择要拨打的电话")
					.setSingleChoiceItems(
							new String[] {
									"手机：" + mTask.getTask_client_mobile(),
									"座机：" + mTask.getTask_client_tel() }, 0,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										Intent intent = new Intent(
												Intent.ACTION_CALL,
												Uri.parse("tel:"
														+ mTask.getTask_client_mobile()));
										startActivity(intent);
									} else {
										Intent intent = new Intent(
												Intent.ACTION_CALL,
												Uri.parse("tel:"
														+ mTask.getTask_client_tel()));
										startActivity(intent);
									}
									dialog.dismiss();
								}
							}).setNegativeButton("取消", null).show();
		}
	}

	private void submitTask() {
		NetEngine.modifyOrderStatus(mHandler, mTaskId, mTask.getTask_boxids(),
				Const.TASK_DONE, mTask.getTask_client_name(),
				mTask.getTask_client_name(), mTask.getTask_client_tel(),
				mTask.getTask_client_mobile());
		mPicList = getFileList();
		mUploadFlag = 0;
		if (mPicList != null && mPicList.length != 0) {
			mUploadPicDialog = new CustomDialog(this, R.layout.dialog_upload,
					R.style.MyDialog);
			mUploadHint = (TextView) mUploadPicDialog
					.findViewById(R.id.upload_hint);
			mUploadPicDialog.show();
			uploadPic(mUploadFlag++);
			SharedPrefUtil.AddToPicUploadRec(MyTaskDetailActivity.this,
					mTaskId, true);// 添加上传标记，如果全部上传成功则删除
		}
	}

	private void uploadPic(int flag) {
		NetEngine.uploadPicture(mHandlerPic, mTaskId,
				mPicList[flag].getAbsolutePath(), mPicList[flag].getName());
		mUploadHint.setText("正在上传第" + (flag + 1) + "张");
	}

	private File[] getFileList() {
		File destDir = new File(Const.PIC_DIR);
		File[] files = null;
		if (destDir.isDirectory()) {
			files = destDir.listFiles(new MyFileFilter(mTaskId));
		}
		return files;
	}

	private void goToScanBarcode() {
		Intent intent = new Intent(MyTaskDetailActivity.this,
				ScanActivity.class);
		intent.putExtra(Const.SCAN_KEY, Const.SCAN_VALUE_BARCODE);
		String result = mTask.getTask_boxids();
		intent.putExtra("resutl", result);
		startActivityForResult(intent, 0);
	}

	JsonHttpResponseHandler mHandler = new JsonHttpResponseHandler() {// 提交工单
		@Override
		public void onSuccess(JSONObject response) {
			try {
				if (response != null) {
					Log.d("TAG", "submit task : " + response.toString());
					if (response.getBoolean("success") == true) {
						// 修改成功
						mTask.setTask_status(Const.TASK_DONE);
						mTask.setTask_edittime(System.currentTimeMillis() / 1000);
						mTaskDao.update(mTask);
						mSubmitBtn.setEnabled(false);
						mSubmitBtn.setText("已完成");
						mSubmitBtn.setBackgroundColor(getResources().getColor(
								R.color.btn_disable));
						mEditInfoBtn.setVisibility(View.INVISIBLE);
						mBtnLayout.setVisibility(View.INVISIBLE);
						mReviewPicBtn.setVisibility(View.VISIBLE);
						Intent intent = new Intent(
								"com.ebupt.orderreceiver.myorder");
						MyApplication.getInstance().sendBroadcast(intent);

					} else {
						Toast.makeText(MyTaskDetailActivity.this, "提交失败",
								Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.d("TAG", "Parse Json Failed");
			}
		};
	};

	JsonHttpResponseHandler mHandlerPic = new JsonHttpResponseHandler() {// 上传图片
		@Override
		public void onSuccess(JSONObject response) {
			try {
				if (response != null) {
					Log.d("TAG", "picture upload no." + mUploadFlag + " "
							+ response.toString());
					if (response.getBoolean("success") == true) {
						// 上传成功上传下一张
						if (mUploadFlag < mPicList.length) {
							uploadPic(mUploadFlag);
							mUploadFlag++;
						} else {
							mUploadPicDialog.dismiss();
							SharedPrefUtil.RemovePicUploadRec(
									MyTaskDetailActivity.this, mTaskId);// 上传完成
							Toast.makeText(MyTaskDetailActivity.this, "图片上传完成",
									Toast.LENGTH_SHORT).show();
						}

					} else {
						mUploadPicDialog.dismiss();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.d("TAG", "Parse Json Failed");
			}
		};
	};
}
