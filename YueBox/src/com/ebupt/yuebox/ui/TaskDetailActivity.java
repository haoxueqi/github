package com.ebupt.yuebox.ui;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.R.color;
import com.ebupt.yuebox.application.MyApplication;
import com.ebupt.yuebox.database.AppSetupUserDao.Properties;
import com.ebupt.yuebox.database.DbUtil;
import com.ebupt.yuebox.model.AppSetupUser;
import com.ebupt.yuebox.model.SetupTask;
import com.ebupt.yuebox.net.NetEngine;
import com.loopj.android.http.JsonHttpResponseHandler;

import de.greenrobot.dao.query.QueryBuilder;

public class TaskDetailActivity extends Activity {

	private MyApplication app;
	private RelativeLayout layout_mobile;
	private TextView tv_detail_name;
	private TextView tv_detail_box_num;
	private TextView tv_detail_publish_time;
	private TextView tv_detail_end_time;
	private TextView tv_detail_grab_credits;
	private TextView tv_detail_finish_credits;
	private TextView tv_detail_address;
	private TextView tv_detail_mobile;
	private ImageView button_back;
	private Button button_grab;
	private SetupTask task;
	private SimpleDateFormat mDateFormat;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail);

		initResource();
		updateData();
	}

	public void initResource() {
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.CHINA);
		app = MyApplication.getInstance();
		tv_detail_name = (TextView) findViewById(R.id.tv_detail_name);
		tv_detail_box_num = (TextView) findViewById(R.id.tv_detail_box_num);
		tv_detail_publish_time = (TextView) findViewById(R.id.tv_detail_publish_time);
		tv_detail_end_time = (TextView) findViewById(R.id.tv_detail_end_time);
		tv_detail_address = (TextView) findViewById(R.id.tv_detail_address);
		tv_detail_mobile = (TextView) findViewById(R.id.tv_detail_mobile);
		tv_detail_grab_credits = (TextView) findViewById(R.id.tv_detail_grab_credits);
		tv_detail_finish_credits = (TextView) findViewById(R.id.tv_detail_finish_credits);
		button_back = (ImageView) findViewById(R.id.btn_back);
		button_grab = (Button) findViewById(R.id.button_grab);
		layout_mobile = (RelativeLayout) findViewById(R.id.layout_detail_mobile);
		layout_mobile.setOnClickListener(new OnClickListener() {
			// 工单详情中，点击电话栏进入拨打电话界面
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (task.getTask_client_tel() == null
						|| task.getTask_client_tel().equals("")
						|| task.getTask_client_tel().equals("null")) {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel:" + task.getTask_client_mobile()));
					startActivity(intent);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(TaskDetailActivity.this);
					builder.setTitle("请选择要拨打的电话")
						   .setSingleChoiceItems(
									new String[] {
											"手机："
													+ task.getTask_client_mobile(),
											"座机：" + task.getTask_client_tel() },
									0, new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (which == 0) {
												Intent intent = new Intent(
														Intent.ACTION_CALL,
														Uri.parse("tel:"
																+ task.getTask_client_mobile()));
												startActivity(intent);
											} else {
												Intent intent = new Intent(
														Intent.ACTION_CALL,
														Uri.parse("tel:"
																+ task.getTask_client_tel()));
												startActivity(intent);
											}
											dialog.dismiss();
										}
									}).setNegativeButton("取消", null).show();
				}
			}
		});
		button_grab.setOnClickListener(new OnClickListener() {
			// 点击抢单按钮
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetEngine.grabWorkOrder(grabWorkOrderHandler, app.userName,
						app.password, task.getTask_id());
			}
		});
		button_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	public void updateData() {
		String task_id = getIntent().getStringExtra("task_id");
		task = DbUtil
				.getSetupTaskDao()
				.queryBuilder()
				.where(com.ebupt.yuebox.database.SetupTaskDao.Properties.Task_id
						.eq(task_id)).list().get(0);
		tv_detail_name.setText(task.getTask_client_name());
		tv_detail_address.setText(task.getTask_client_address());
		StringBuilder sb = new StringBuilder();
		sb.append(task.getTask_client_mobile());
		if ((task.getTask_client_tel() == null
				|| task.getTask_client_tel().equals("") || task
				.getTask_client_tel().equals("null")) == false)
			sb.append("、" + task.getTask_client_tel());
		tv_detail_mobile.setText(sb.toString());
		if (task.getTask_box_num() != null)
			tv_detail_box_num.setText("盒子数："
					+ task.getTask_box_num().intValue());
		else
			tv_detail_box_num.setText("盒子数：未知");
		tv_detail_publish_time.setText("开始时间："
				+ mDateFormat.format(new Date(task.getTask_publish_time()*1000)));
		tv_detail_end_time.setText("截止时间："
				+ mDateFormat.format(new Date(task.getTask_end_time()*1000)));
		tv_detail_grab_credits.setText("抢单 " + task.getTask_grab_credits());
		tv_detail_finish_credits.setText("完成 " + task.getTask_finish_credits());
	}

	JsonHttpResponseHandler grabWorkOrderHandler = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(JSONObject result) {
			try {
				if (result.getBoolean("success") == true) {
					QueryBuilder<AppSetupUser> qb = DbUtil.getAppSetupUserDao().queryBuilder();
					AppSetupUser user = qb.where(qb.or(Properties.User_name_spell.eq(app.userName),
								 		  Properties.User_tel.eq(app.userName),
								 	      Properties.User_name.eq(app.userName))).list().get(0);
					String user_id = user.getUser_id();
					task.setTask_status("01");
					task.setTask_get_userid(user_id);
					user.setUser_total_credits(user.getUser_total_credits()
							+ task.getTask_grab_credits());
					user.setUser_year_credits(user.getUser_year_credits()
							+ task.getTask_grab_credits());
					user.setUser_month_credits(user.getUser_month_credits()
							+ task.getTask_grab_credits());
					user.setUser_week_credits(user.getUser_week_credits()
							+ task.getTask_grab_credits());
					DbUtil.getSetupTaskDao().update(task);
					DbUtil.getAppSetupUserDao().update(user);
					Intent intent = new Intent("com.ebupt.orderreceiver.order");
					app.sendBroadcast(intent);
					intent = new Intent("com.ebupt.orderreceiver.myorder");
					app.sendBroadcast(intent);
					button_grab.setText("已抢单");
					button_grab.setClickable(false);
					button_grab.setBackgroundColor(color.btn_gray);
					Toast.makeText(TaskDetailActivity.this, "抢单成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(TaskDetailActivity.this,
							result.getString("info"), Toast.LENGTH_SHORT)
							.show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}
