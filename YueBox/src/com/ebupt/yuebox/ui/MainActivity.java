package com.ebupt.yuebox.ui;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.application.MyApplication;
import com.ebupt.yuebox.database.DbUtil;
import com.ebupt.yuebox.fragment.MyFragment;
import com.ebupt.yuebox.fragment.OrderFragment;
import com.ebupt.yuebox.fragment.RankFragment;
import com.ebupt.yuebox.net.NetEngine;
import com.ebupt.yuebox.util.Const;
import com.ebupt.yuebox.util.MyFileFilter;
import com.ebupt.yuebox.util.SharedPrefUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * @ClassName MainActivity
 * @Description 主界面,底部有3个按钮控制页面
 * @author ZhouZheChen
 * @date 2014-03-12
 * @RevisionHistory
 */

public class MainActivity extends FragmentActivity {
	public static String currentTab;// 当前选中的tab栏
	private MyApplication app;
	private FragmentManager fm;
	private long mExitTime;
	private RadioGroup tab_group;// 底部tab栏
	private TextView textview_title; // 顶部标题栏文字
	private ImageView image_title_right;
	private ImageView image_title_left;
	private int orderState; // 列表模式或地图模式，保证tab切换后保留之前的显示状态
	private boolean isExit = false;
	private final static boolean isLite = true;
	private final static int ORDER_LIST = 1;
	private final static int ORDER_MAP = 2;
	private final static String TAB_ORDER = "0";
	private final static String TAB_MY = "1";
	private final static String TAB_RANK = "2";

	// 上传遗留图片
	private String mTaskId;
	private File[] mPicList;
	private List<String> mTaskList;
	private int mUploadFlag = 0;
	private int mTaskFlag = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		NetEngine.initUserData();
		DbUtil.confirmDB(this);
		app = MyApplication.getInstance();
		app.uploadData();
		app.isOrderFragmentShown = true;
		if (app.currentTab != null)
			currentTab = app.currentTab;
		fm = getSupportFragmentManager();
		initResource();
		// 上传未上传成功的图片
		mTaskList = SharedPrefUtil.getAllUnfinishedTask(this);
		if (mTaskList.size() > 0) {
			uploadRestPic(mTaskList.get(mTaskFlag));
		}
	}

	private void uploadRestPic(String s) {
		mTaskId = s;
		mUploadFlag = 0;
		mPicList = getFileList();
		if (mPicList != null && mPicList.length != 0) {
			uploadPic(mUploadFlag++);
		}
	}

	private void uploadPic(int flag) {
		NetEngine.uploadPicture(mHandlerPic, mTaskId,
				mPicList[flag].getAbsolutePath(), mPicList[flag].getName());
	}

	private File[] getFileList() {
		File destDir = new File(Const.PIC_DIR);
		File[] files = null;
		if (destDir.isDirectory()) {
			files = destDir.listFiles(new MyFileFilter(mTaskId));
		}
		return files;
	}

	JsonHttpResponseHandler mHandlerPic = new JsonHttpResponseHandler() {// 上传图片
		@Override
		public void onSuccess(JSONObject response) {
			try {
				if (response != null) {
					Log.d("TAG", "picture upload no." + (mUploadFlag - 1) + " "
							+ response.toString());
					if (response.getBoolean("success") == true) {
						// 上传成功上传下一张
						if (mUploadFlag < mPicList.length) {
							uploadPic(mUploadFlag);
							mUploadFlag++;
						} else {
							SharedPrefUtil.RemovePicUploadRec(
									MainActivity.this, mTaskId);
							// 上传完成,继续上传下个任务
							if (mTaskFlag < mTaskList.size() - 1) {
								mTaskFlag++;
								uploadRestPic(mTaskList.get(mTaskFlag));
							}
						}
					} else {
						// 上个任务上传失败，继续上传下个任务
						if (mTaskFlag < mTaskList.size() - 1) {
							mTaskFlag++;
							uploadRestPic(mTaskList.get(mTaskFlag));
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.d("TAG", "Parse Json Failed");
			}
		};
	};

	@Override
	public void onPause() {
		if (isExit == false)
			app.currentTab = currentTab;
		super.onPause();
	}

	/**
	 * 初始化各类控件资源
	 */
	public void initResource() {
		FragmentTransaction tr1 = fm.beginTransaction();
		if(isLite)
		{
			tr1.add(R.id.maincontent, new OrderFragment(), TAB_ORDER);
			tr1.commit();
			findViewById(R.id.main_tab_order).setVisibility(View.GONE);
		}
		if(!isLite)
		{
			if (fm.findFragmentByTag(TAB_ORDER) != null) {
				tr1.hide(fm.findFragmentByTag(TAB_ORDER));
			}
		}
		if (fm.findFragmentByTag(TAB_MY) != null) {
			tr1.hide(fm.findFragmentByTag(TAB_MY));
		}
		if (fm.findFragmentByTag(TAB_RANK) != null) {
			tr1.hide(fm.findFragmentByTag(TAB_RANK));
		}
		tab_group = (RadioGroup) findViewById(R.id.radio_tab);
		textview_title = (TextView) findViewById(R.id.textview_title);
		image_title_left = (ImageView) findViewById(R.id.image_title_left);
		image_title_right = (ImageView) findViewById(R.id.image_title_right);
		tab_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				FragmentTransaction tr = fm.beginTransaction();
				if(isLite)
				{
					tr.hide(fm.findFragmentByTag(TAB_ORDER));
				}
				Fragment currentFragment = fm.findFragmentByTag(currentTab);
				if (currentFragment != null)
					tr.hide(currentFragment);
				Fragment toaddFragment = null;
				switch (checkedId) {
				case R.id.main_tab_order:
					app.isOrderFragmentShown = true;
					currentTab = TAB_ORDER;
					toaddFragment = fm.findFragmentByTag(currentTab);
					if (toaddFragment == null)
						toaddFragment = new OrderFragment();
					if (orderState == ORDER_LIST)
						image_title_left.setVisibility(View.VISIBLE);
					else if (orderState == ORDER_MAP)
						image_title_right.setVisibility(View.VISIBLE);
					textview_title.setText("抢单信息");
					break;
				case R.id.main_tab_my:
					app.isOrderFragmentShown = false;
					currentTab = TAB_MY;
					if (image_title_right.isShown())
						orderState = ORDER_MAP;
					else if (image_title_left.isShown())
						orderState = ORDER_LIST;
					toaddFragment = fm.findFragmentByTag(currentTab);
					if (toaddFragment == null)
						toaddFragment = new MyFragment();
					image_title_left.setVisibility(View.GONE);
					image_title_right.setVisibility(View.GONE);
					textview_title.setText("我的工单");
					break;
				case R.id.main_tab_rank:
					app.isOrderFragmentShown = false;
					currentTab = TAB_RANK;
					if (image_title_right.isShown())
						orderState = ORDER_MAP;
					else if (image_title_left.isShown())
						orderState = ORDER_LIST;
					toaddFragment = fm.findFragmentByTag(currentTab);
					if (toaddFragment == null)
						toaddFragment = new RankFragment();
					image_title_left.setVisibility(View.GONE);
					image_title_right.setVisibility(View.GONE);
					textview_title.setText("排行榜");
					break;
				default:
					toaddFragment = currentFragment;
					break;
				}
				if (toaddFragment.isAdded()) {
					tr.show(toaddFragment);
				} else {
					tr.add(R.id.maincontent, toaddFragment, currentTab);
					tr.show(toaddFragment);
				}
				tr.commit();
				fm.executePendingTransactions();
			}
		});
		if (currentTab == null || currentTab.equals("")) {
			if(isLite)
			{
				currentTab = TAB_MY;
				textview_title.setText("我的工单");
			}
			else
			{
				currentTab = TAB_ORDER;
				textview_title.setText("抢单信息");
			}
		}
		showFragment(currentTab);
	}

	/**
	 * 根据fragmentTag来决定显示哪个fragment
	 * 
	 * @param fragmentTag
	 */
	public void showFragment(String fragmentTag) {
		Fragment currentFragment = fm.findFragmentByTag(currentTab);
		FragmentTransaction tr = fm.beginTransaction();
		if (currentFragment != null)
			tr .hide(currentFragment);
		currentTab = fragmentTag;
		Fragment toaddFragment = null;
		toaddFragment = fm.findFragmentByTag(fragmentTag);
		if (toaddFragment == null) {
			if (fragmentTag.equals(TAB_ORDER)) {
				toaddFragment = new OrderFragment();
			}
		}
		if (toaddFragment == null) {
			if (fragmentTag.equals(TAB_MY)) {
				toaddFragment = new MyFragment();
			}
		}
		if (toaddFragment == null) {
			if (fragmentTag.equals(TAB_RANK)) {
				toaddFragment = new RankFragment();
			}
		}
		if (!toaddFragment.isAdded())
			tr.add(R.id.maincontent, toaddFragment, currentTab);
		tr.show(toaddFragment);
		tr.commit();
	}

	// --点击两次back键退出程序---------------
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (currentTab.equals(TAB_ORDER)
					&& ((OrderFragment) fm.findFragmentByTag(currentTab))
							.excuteBack())
				return true;
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				// JPushInterface.stopPush(this);
				FragmentTransaction tr = fm.beginTransaction();
				if (fm.findFragmentByTag(TAB_ORDER) != null) {
					tr.remove(fm.findFragmentByTag(TAB_ORDER));
				}
				if (fm.findFragmentByTag(TAB_MY) != null) {
					tr.remove(fm.findFragmentByTag(TAB_MY));
				}
				if (fm.findFragmentByTag(TAB_RANK) != null) {
					tr.remove(fm.findFragmentByTag(TAB_RANK));
				}
				tr.commit();
				isExit = true;
				if(isLite)
				{
					app.currentTab = TAB_MY;
				}
				else
				{
					app.currentTab = TAB_ORDER;
				}
				app.isOnline = false;
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
