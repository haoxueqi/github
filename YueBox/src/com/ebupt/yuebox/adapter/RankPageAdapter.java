package com.ebupt.yuebox.adapter;

import com.ebupt.yuebox.fragment.RankSecFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * ViewPager适配器
 * 
 * @author len
 * 
 */
public class RankPageAdapter extends FragmentPagerAdapter {
	final String[] TITLE;

	public RankPageAdapter(FragmentManager fm, String[] title) {
		super(fm);
		TITLE = title;
	}


	@Override
	public Fragment getItem(int position) {
		// 新建一个Fragment来展示ViewPager item的内容，并传递参数
		Fragment fragment = new RankSecFragment();
		Bundle args = new Bundle();
		args.putString("arg", TITLE[position]);
		args.putInt("index", position);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLE[position % TITLE.length];
	}

	@Override
	public int getCount() {
		return TITLE.length;
	}
}