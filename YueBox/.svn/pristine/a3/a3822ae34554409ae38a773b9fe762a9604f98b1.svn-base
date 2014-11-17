package com.ebupt.yuebox.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.adapter.RankListAdapter;
import com.ebupt.yuebox.database.AppSetupUserDao;
import com.ebupt.yuebox.database.AppSetupUserDao.Properties;
import com.ebupt.yuebox.database.DbUtil;
import com.ebupt.yuebox.model.AppSetupUser;
import com.ebupt.yuebox.ui.UserDetailActivity;
import com.ebupt.yuebox.util.Const;

//实现监听点击接口，点击跳转到具体联系人详情页面
public class RankSecFragment extends Fragment implements OnItemClickListener{

	List<AppSetupUser> dataList;
	int mIndex;
	RankListAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View contextView = inflater.inflate(R.layout.layout_page_rank, container,
				false);
		Bundle bundle = getArguments();
		mIndex = bundle.getInt("index");
		Log.d("TAG", mIndex + "");
		ListView lv = (ListView) contextView.findViewById(R.id.rank_list);
		adapter = new RankListAdapter(getActivity());

		// 获取排行榜列表
		AppSetupUserDao userDao = DbUtil.getAppSetupUserDao();
		switch (mIndex) {
		case Const.RANK_MONTH:
			dataList = userDao.queryBuilder()
					.orderDesc(Properties.User_month_credits).list();
			break;
		case Const.RANK_YEAR:
			dataList = userDao.queryBuilder()
					.orderDesc(Properties.User_year_credits).list();
			break;
		case Const.RANK_TOTAL:
			dataList = userDao.queryBuilder()
					.orderDesc(Properties.User_total_credits).list();
			break;
		}

		adapter.setDataList(dataList);
		adapter.setRankType(mIndex);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		
		return contextView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	//
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//arg2参数是点击ListView中item的序号；
		//从adapter里取出对应序号的AppSetupUser对象user_item,取出主键user_id,传递给UserDetail活动
		 AppSetupUser user_item = adapter.getItem(arg2);
		 String user_id = user_item.getUser_id(); 
		Log.v("联系人",user_item+"" );
		Log.v("联系人",user_id+"" );
		//传递user_id给UserDetail活动
		Intent intent = new Intent();
		intent.putExtra("user_id", user_id+"");
	    intent.setClass(getActivity(), UserDetailActivity.class);
	    startActivity(intent);

	}

}