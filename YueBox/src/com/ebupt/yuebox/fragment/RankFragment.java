package com.ebupt.yuebox.fragment;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.adapter.RankPageAdapter;
import com.ebupt.yuebox.application.MyApplication;
import com.ebupt.yuebox.database.AppSetupUserDao;
import com.ebupt.yuebox.database.DbUtil;
import com.ebupt.yuebox.model.AppSetupUser;
import com.ebupt.yuebox.net.NetEngine;
import com.ebupt.yuebox.util.JsonParser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.viewpagerindicator.TitlePageIndicator;

public class RankFragment extends Fragment {
	final String[] TITLE = new String[] { "周排行", "月排行", "总排行" };
	ViewPager pager;
	FragmentPagerAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// 获取用户信息
		String username = MyApplication.getInstance().userName;
		String pwd = MyApplication.getInstance().password;

		View contextView = inflater.inflate(R.layout.fragment_rank, container,
				false);
		// ViewPager的adapter
		adapter = new RankPageAdapter(getFragmentManager(), TITLE);
		pager = (ViewPager) contextView.findViewById(R.id.pager);
		pager.setAdapter(adapter);

		// 实例化TabPageIndicator然后设置ViewPager与之关联
		TitlePageIndicator indicator = (TitlePageIndicator) contextView
				.findViewById(R.id.indicator);
		indicator.setViewPager(pager);

		// 如果我们要对ViewPager设置监听，用indicator设置就行了
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		NetEngine.getUserData(mHandler, username, pwd);
		return contextView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

	}

	JsonHttpResponseHandler mHandler = new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(JSONObject response) {
			Log.d("TAG", "RESPOND");
			try {

				if (response != null)
					Log.d("TAG", response.toString());
				List<AppSetupUser> users = JsonParser
						.parseAppSetupUser(response.getJSONObject("data")
								.getJSONArray("TaskReceiver").toString());
				if (users.size() > 0) {
					Log.d("TAG", "Get users succefully");
					AppSetupUserDao userDao = DbUtil.getAppSetupUserDao();
					userDao.deleteAll();
					for (int i = 0; i < users.size(); i++) {
						userDao.insert(users.get(i));
					}
					adapter.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.d("TAG", "Parse Json Failed");
			}
		};
	};
	


}