package com.ebupt.yuebox.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ebupt.yuebox.R;

/**
 * 图片列表ListView的自定义Adapter
 * 
 * @author xuchang
 * 
 */
public class TaskPicListAdapter extends BaseAdapter{

	List<HashMap<String, String>> mDataList;
	Context mContext;
	SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd",
			Locale.CHINA);
	int mRankType;
	String clientName;

	public TaskPicListAdapter(Context ctx) {
		mContext = ctx;
	}

	public TaskPicListAdapter(Context ctx,
			List<HashMap<String, String>> dataList) {
		mDataList = dataList;
		mContext = ctx;
	}

	public void setDataList(List<HashMap<String, String>> dataList) {
		mDataList = dataList;
	}

	public void setClientName(String s) {
		clientName = s;
	}

	public void setRankType(int rankType) {
		mRankType = rankType;
	}

	@Override
	public int getCount() {
		if (mDataList == null)
			return 0;
		return mDataList.size();
	}

	@Override
	public HashMap<String, String> getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_task_pic, null);
			holder = new ViewHolder();
			holder.taskImage = (ImageView) convertView
					.findViewById(R.id.task_image);
			holder.imageTime = (TextView) convertView
					.findViewById(R.id.tv_take_time);
			holder.client = (TextView) convertView.findViewById(R.id.tv_client);
			holder.deleteBtn = (TextView) convertView
					.findViewById(R.id.delete_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		initView(holder, getItem(position), position);
		return convertView;
	}

	private void initView(ViewHolder holder, HashMap<String, String> item,
			int position) {
		String picPath = item.get("pic_path");
		Bitmap bm = BitmapFactory.decodeFile(picPath);
		holder.taskImage.setImageBitmap(bm);

		Long takeTime = Long.parseLong(item.get("take_time"));
		holder.imageTime.setText("拍摄时间： "
				+ mDateFormat.format(new Date(takeTime)));

		holder.client.setText("店铺：" + clientName);
		holder.deleteBtn.setOnClickListener(new ButtonListener(position,
				holder, picPath));
	}

	public void removeItem(int position) {
		mDataList.remove(position);
		this.notifyDataSetChanged();
	}

	private class ViewHolder {
		ImageView taskImage;
		TextView imageTime;
		TextView client;
		TextView deleteBtn;
	}

	class ButtonListener implements OnClickListener {
		private int position;
		private ViewHolder holder;
		private String picPath;

		ButtonListener(int pos, ViewHolder v, String s) {
			position = pos;
			holder = v;
			picPath = s;
		}

		@Override
		public void onClick(View v) {
			int vid = v.getId();
			if (vid == holder.deleteBtn.getId()) {
				File file = new File(picPath);
				file.delete();
				removeItem(position);
			}

		}

	}
}
