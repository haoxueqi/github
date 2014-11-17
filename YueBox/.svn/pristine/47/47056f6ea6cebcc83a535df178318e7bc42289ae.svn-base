package com.ebupt.yuebox.jpush;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.ebupt.yuebox.net.NetEngine;
import com.ebupt.yuebox.util.GetOrderList;
import com.loopj.android.http.JsonHttpResponseHandler;

public class JPushReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.v("JPush","get it");
		Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) 
        {        	
    		SharedPreferences sp = context.getSharedPreferences("userInfo", 0);
			sp.edit().putString("PUSH_ID", JPushInterface.getRegistrationID(context)).commit();
			//上报设备类型-----
			String push_id = sp.getString("PUSH_ID", "");
			String userNameValue = sp.getString("USER_NAME", "");
			String userPasswordValue = sp.getString("PASS_WORD", "");
			userDevice(userNameValue,userPasswordValue,"1",push_id); 
        } 
        else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) 
        {
            Log.v("JPush", "收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // 具体操作，视服务器发来具体数据还是TAG而定
        }
        else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction()))
        {
        	Log.v("JPush","新的订单");
        	Handler handler = new Handler();
        	handler.post(new GetOrderList(handler, false));
        }
	}
	
 	//---上报设备类型----------
  	public void userDevice(String login_username, String login_password, String device_type, String push_id)
  	{
  		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
  			
  			@Override
  			public void onSuccess(JSONObject result) {
  				try {
  					//具体操作稍后添加，Log仅作测试用
  					if(result.getBoolean("success") == true)
  					{
  						Log.v("device", result.getString("info"));
  					}
  					else
  					{
  						Log.v("device", result.getString("info"));
  					}   
  				} catch (JSONException e) {
  					// TODO Auto-generated catch block
  					e.printStackTrace();
  				}
  		}
  				};	
  		NetEngine.userDevice(handler, login_username, login_password, device_type, push_id);
  		Log.v("加密设备", login_password);
  	}
}
