package com.ebupt.yuebox.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.adapter.SectionOrderAdapter;
import com.ebupt.yuebox.application.MyApplication;
import com.ebupt.yuebox.database.AppSetupUserDao.Properties;
import com.ebupt.yuebox.database.DbUtil;
import com.ebupt.yuebox.model.AppSetupUser;
import com.ebupt.yuebox.model.SetupTask;
import com.ebupt.yuebox.net.NetEngine;
import com.ebupt.yuebox.service.MyService;
import com.ebupt.yuebox.view.SectionOrderListView;
import com.loopj.android.http.JsonHttpResponseHandler;

import de.greenrobot.dao.query.QueryBuilder;

public class MyFragment extends Fragment {

	private MyApplication app;
	private AppSetupUser user;
	private SectionOrderListView list_my_order;
	private SectionOrderAdapter myOrderAdapter;
	private List<SetupTask> setupTasks;
	private List<SetupTask> orderNoFinishList;
	private List<SetupTask> orderFinishList;
	private ArrayList<Pair<String, List<SetupTask>>> setupTaskLists;
	private TextView tv_my_name;
	private TextView tv_my_department;
	private TextView tv_my_credits;
	private TextView tv_no_my_order;
	private Button button_online;
	private Intent intent;
	private QueryBuilder<AppSetupUser> qb;
	private final static String TAG = "MyFragment"; 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contextView = inflater.inflate(R.layout.fragment_my, container,
				false);
		return contextView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initResource();
		updateListData();
	}

	public void initResource() {
		app = MyApplication.getInstance();
		tv_my_name = (TextView) getActivity().findViewById(R.id.tv_my_name);
		tv_my_department = (TextView) getActivity().findViewById(R.id.tv_my_department);
		tv_my_credits = (TextView) getActivity().findViewById(R.id.tv_my_credits);
		tv_no_my_order = (TextView) getActivity().findViewById(R.id.tv_no_my_order);
		qb = DbUtil.getAppSetupUserDao().queryBuilder();
		try{
			user = qb.where(qb.or(Properties.User_name_spell.eq(app.userName),
						 		  Properties.User_tel.eq(app.userName),
						 	      Properties.User_name.eq(app.userName))).list().get(0);
			tv_my_name.setText(user.getUser_name());
			if(user.getUser_department().equals("null") || 
			   user.getUser_department() == null ||
			   user.getUser_department().equals(""))
				tv_my_department.setText("未知");
			else
				tv_my_department.setText(user.getUser_department());
			tv_my_credits.setText("积分:" + user.getUser_total_credits().toString());
			}
		catch(Exception e){			
		}
		list_my_order = (SectionOrderListView) getActivity().findViewById(
				R.id.list_my_order);
		button_online = (Button) getActivity().findViewById(R.id.button_online);
		if(app.isOnline == true)
		{
			button_online.setText("在线");
			button_online.setBackgroundResource(R.color.btn_orange);
		}
		orderNoFinishList = new ArrayList<SetupTask>();
		orderFinishList = new ArrayList<SetupTask>();
		setupTaskLists = new ArrayList<Pair<String, List<SetupTask>>>();
		list_my_order.setPinnedHeaderView(LayoutInflater.from(getActivity())
				.inflate(R.layout.item_my_order_header, list_my_order, false));
		list_my_order.setAdapter(myOrderAdapter = new SectionOrderAdapter(
				getActivity(), setupTaskLists));
		button_online.setOnClickListener(new OnClickListener() {
			// 点击在线/离线按钮更在状态
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (app.isOnline == false) {
					NetEngine.modifyUserStatus(modifyUserStatusOnHandler,
							app.userName, app.password, "010");
				} else if (app.isOnline == true) {
					NetEngine.modifyUserStatus(modifyUserStatusOffHandler,
							app.userName, app.password, "011");				
				}
			}
		});
		intent = new Intent(getActivity(), MyService.class);
		IntentFilter intentFilter = new IntentFilter(
				"com.ebupt.orderreceiver.myorder");
		getActivity().registerReceiver(myReceiver, intentFilter);
	}

	public void updateListData() {
		orderNoFinishList.clear();
		orderFinishList.clear();
		setupTaskLists.clear();
		setupTasks = app.getSetupTasks();
		for (int i = 0; i < setupTasks.size(); i++) {
			if (setupTasks.get(i).getTask_status().equals("11")) {
				orderFinishList.add(setupTasks.get(i));
			} else if (setupTasks.get(i).getTask_status().equals("01")) {
				orderNoFinishList.add(setupTasks.get(i));
			}
		}
		Pair<String, List<SetupTask>> list = new Pair<String, List<SetupTask>>(
				"未完成工单", orderNoFinishList);
		setupTaskLists.add(list);
		list = new Pair<String, List<SetupTask>>("已完成工单", orderFinishList);
		setupTaskLists.add(list);
		myOrderAdapter.notifyDataSetChanged();
		if(orderFinishList.size() == 0 && orderNoFinishList.size() == 0)
			tv_no_my_order.setVisibility(View.VISIBLE);
		else
			tv_no_my_order.setVisibility(View.GONE);
	}

	JsonHttpResponseHandler modifyUserStatusOnHandler = new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(JSONObject result) {
			try {
				if (result.getBoolean("success") == true) {
					button_online.setText("在线");
					button_online.setBackgroundResource(R.color.btn_orange);
					app.startService(intent);
					JPushInterface.setDebugMode(true);
					JPushInterface.init(getActivity());
					app.isOnline = true;
				} else {
					Log.e(TAG, result.getString("info"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void onFailure(Throwable error) {
			Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
		}
	};

	JsonHttpResponseHandler modifyUserStatusOffHandler = new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(JSONObject result) {
			try {
				if (result.getBoolean("success") == true) {
					button_online.setText("离线");
					button_online.setBackgroundResource(R.color.btn_gray);
					app.stopService(intent);
					JPushInterface.stopPush(getActivity());
					app.isOnline = false;
				} else {
					Log.e(TAG, result.getString("info"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void onFailure(Throwable error) {
			Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
		}
	};
	
	BroadcastReceiver myReceiver = new BroadcastReceiver() {
		// 由service告诉OrderFragment需要更新数据了
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("com.ebupt.orderreceiver.myorder")) {
				user = qb.where(qb.or(Properties.User_name_spell.eq(app.userName),
				 		  Properties.User_tel.eq(app.userName),
				 	      Properties.User_name.eq(app.userName))).list().get(0);
				tv_my_name.setText(user.getUser_name());
				if(user.getUser_department().equals("null") || 
				   user.getUser_department() == null ||
				   user.getUser_department().equals(""))
					tv_my_department.setText("未知");
				else
					tv_my_department.setText(user.getUser_department());
				tv_my_credits.setText("积分:" + user.getUser_total_credits().toString());
				updateListData();
			}
		}
	};

	@Override
	public void onDestroy() {
//		getActivity().stopService(intent);
		getActivity().unregisterReceiver(myReceiver);
//		NetEngine.modifyUserStatus(modifyUserStatusOffHandler, app.userName,
//				app.password, "011");
		super.onDestroy();
	}
}