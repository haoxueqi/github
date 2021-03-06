package com.ebupt.yuebox.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.ebupt.yuebox.util.GetOrderList;

public class MyService extends Service {

	final static long pullTime = 30000;
	private Handler handlerPull;
	private GetOrderList pull;
	private final static String TAG = "MyService";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
		
	}

	@Override
	public void onCreate() {
		super.onCreate();
		handlerPull = new Handler();
		pull = new GetOrderList(handlerPull, true);
		handlerPull.postDelayed(pull, pullTime);
	}

	@Override
	public void onDestroy() {
		handlerPull.removeCallbacks(pull);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}
}
