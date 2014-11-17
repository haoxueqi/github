package com.ebupt.yuebox.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.model.SetupTask;
import com.ebupt.yuebox.ui.MyTaskDetailActivity;

/**
 * 我的工单ListView的自定义Adapter
 * 
 * @author Zhouzhechen
 * @data 2014-3-29
 * 
 */
public class SectionOrderAdapter extends SectionUniversalAdapter {
	private List<Integer> isHide;
	private List<String> hideSection;
	private List<Pair<String, List<SetupTask>>> data;
	private Context context;
	
	public SectionOrderAdapter(Context context, List<Pair<String, List<SetupTask>>> data)
	{
		this.context = context;
		this.data = data;
		isHide = new ArrayList<Integer>();
		hideSection = new ArrayList<String>();
	}
	
	public int getCount() {
		int res = 0;
		for (int i = 0; i < data.size(); i++) {
			res += data.get(i).second.size();
		}
		return res;
	}

	
	public SetupTask getItem(int position) {
		int c = 0;
		for (int i = 0; i < data.size(); i++) {
			if (position >= c && position < c + data.get(i).second.size()) {
				return data.get(i).second.get(position - c);
			}
			c += data.get(i).second.size();
		}
		return null;
	}

	
	public long getItemId(int position) {
		return position;
	}

	
	protected void onNextPageRequested(int page) {
	}

	
	protected void bindSectionHeader(View view, final int position, boolean displaySectionHeader) {
		if (displaySectionHeader) {
			view.findViewById(R.id.layout_header).setVisibility(View.VISIBLE);
			TextView lSectionTitle = (TextView) view.findViewById(R.id.tv_header);
			lSectionTitle.setText(getSections()[getSectionForPosition(position)]);
			view.findViewById(R.id.layout_header).setOnClickListener(new OnClickListener() {
				
				private String sectionTitle;

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int index = 0;
					for(int i=0;i<data.size();i++)
					{
						for(int j=0;j<data.get(i).second.size();j++)
						{
							if(index + j == position)
							{
								sectionTitle = data.get(i).first;
							}
						}
					}
					hideOrShow(sectionTitle);
				}
			});
		} else {
			view.findViewById(R.id.layout_header).setVisibility(View.GONE);
		}
	}

	public void hideOrShow(String sectionTitle)
	{
		int i;
		int index = 0;
		for(i=0;i<hideSection.size();i++)
		{
		//已经被隐藏了
			if(hideSection.get(i).equals(sectionTitle))
			{
				hideSection.remove(i);
				for(int j=0;j<data.size();j++)
				{
					if(data.get(j).first.equals(sectionTitle))
					{
						for(int k=0;k<data.get(j).second.size();k++)
						{
							for(int l=0;l<isHide.size();l++)
							{
								if(isHide.get(l) == index+k)
								{
									isHide.remove(l);
									break;
								}
							}
						}
					}
					index += data.get(j).second.size();
				}
			}
		}
		//未被隐藏
		index = 0;
		if(i == hideSection.size())
		{
			hideSection.add(sectionTitle);
			for(int j=0;j<data.size();j++)
			{
				if(data.get(j).first.equals(sectionTitle))
				{
					for(int k=0;k<data.get(j).second.size();k++)
					{
						isHide.add(index + k);
					}
				}
				index += data.get(j).second.size();
			}
		}
		notifyDataSetChanged();
	}
	
	public View getAmazingView(int position, View convertView, ViewGroup parent) {
		View res = convertView;
		if (res == null) 
			res = LayoutInflater.from(context).inflate(R.layout.item_my_order, null);
		RelativeLayout layout_item = (RelativeLayout) res.findViewById(R.id.layout_item);
		int i;
		for(i=0;i<isHide.size();i++)
		{
			if(isHide.get(i).intValue() == position)
			{
				layout_item.setVisibility(View.GONE);
				break;
			}
		}
		//不需要隐藏
		if(i == isHide.size())
		{
			layout_item.setVisibility(View.VISIBLE);
			TextView tv_client = (TextView) res.findViewById(R.id.tv_client);
			TextView tv_address = (TextView) res.findViewById(R.id.tv_address);
			TextView tv_box_credits = (TextView) res.findViewById(R.id.tv_box_credits);
			TextView tv_end_time = (TextView) res.findViewById(R.id.tv_end_time);	
			
			final SetupTask setupTask = getItem(position);
			tv_client.setText(setupTask.getTask_client_name());
			tv_address.setText(setupTask.getTask_client_address());
			tv_box_credits.setText("盒子("+setupTask.getTask_box_num()+")  " +
					       "积分("+(setupTask.getTask_grab_credits()+setupTask.getTask_finish_credits())+")");
			if(setupTask.getTask_status().equals("01"))
			{
				Date time = new Date(setupTask.getTask_end_time()*1000);
				tv_end_time.setText("截止:" + time.getMonth() + "月" + time.getDay() + "日");
				tv_client.setTextColor(context.getResources().getColor(R.color.base_blue));
			}
			else if(setupTask.getTask_status().equals("11"))
			{
				tv_end_time.setText("");
				tv_client.setTextColor(context.getResources().getColor(R.color.text_lightgray));
			}
			layout_item.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,
					MyTaskDetailActivity.class);
					intent.putExtra("task_id", setupTask.getTask_id());
					context.startActivity(intent);
				}
			});
		}
		return res;
	}

	
	public void configurePinnedHeader(View header, final int position, int alpha) {
		RelativeLayout layout_header = (RelativeLayout) header;
		TextView tv_header = (TextView)header.findViewById(R.id.tv_header);
		layout_header.setBackgroundColor(alpha << 24 | (context.getResources().getColor(R.color.background_yellow)));
		tv_header.setText(getSections()[getSectionForPosition(position)]);
		tv_header.setTextColor(alpha << 24 | (0x999999));
	}

	
	public int getPositionForSection(int section) {
		if (section < 0) section = 0;
		if (section >= data.size()) section = data.size() - 1;
		int c = 0;
		for (int i = 0; i < data.size(); i++) {
			if (section == i) { 
				return c;
			}
			c += data.get(i).second.size();
		}
		return 0;
	}

	public int getSectionForPosition(int position) {
		int c = 0;
		for (int i = 0; i < data.size(); i++) {
			if (position >= c && position < c + data.get(i).second.size()) {
				return i;
			}
			c += data.get(i).second.size();
		}
		return -1;
	}

	public String[] getSections() {
		String[] res = new String[data.size()];
		for (int i = 0; i < data.size(); i++) {
			res[i] = data.get(i).first;
		}
		return res;
	}
}