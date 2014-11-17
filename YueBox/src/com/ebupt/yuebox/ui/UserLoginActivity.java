package com.ebupt.yuebox.ui;



import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.application.MyApplication;
import com.ebupt.yuebox.net.NetEngine;
import com.ebupt.yuebox.util.DisplayUtil;
import com.ebupt.yuebox.util.EncryptionUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
/**
 * 
 * @ClassName: UserLoginActivity
 * @Description: 登录页面，只首次登陆出现
 * @author haoxueqi
 * @date 2014-3-11
 * 
 */

public class UserLoginActivity extends Activity  {
private EditText login_username;
private EditText login_password;
private Button user_login_button;
private ProgressDialog pd;
private SharedPreferences sp;
private String userNameValue;
private String userPasswordValue;
private long mExitTime;

	@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_user_login);
	JPushInterface.resumePush(this);  
    DisplayUtil.init(this);
    sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
  //--判断是否首次登陆----
    boolean isFirstRun = sp.getBoolean("isFirstRun", true); 
    //--首次登陆
    if (isFirstRun) 
    { 
    	
        initWidget();

    } else 
    //--非首次登陆，直接自动登陆
    { 
    	initWidget();
    	login_username.setText(sp.getString("USER_NAME", ""));  
        login_password.setText(sp.getString("PASSWORD", "")); 
    	CharSequence strResult="登陆成功！";
        Toast.makeText(UserLoginActivity.this, strResult, Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    } 


    
 
}
    private void initWidget()
    {
        login_username=(EditText)findViewById(R.id.login_username);
        login_password=(EditText)findViewById(R.id.login_password);
        user_login_button=(Button)findViewById(R.id.user_login_button);                
        login_username.setOnFocusChangeListener(new OnFocusChangeListener()
        {
 
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(!hasFocus){
                    String username=login_username.getText().toString().trim();
                    if(username.length()<1){
                    	Toast.makeText(UserLoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            }
             
        });
        login_password.setOnFocusChangeListener(new OnFocusChangeListener()
        {
 
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(!hasFocus){
                    String password=login_password.getText().toString().trim();
                    if(password.length()<6){
                        Toast.makeText(UserLoginActivity.this, "密码不能少于6个字符", Toast.LENGTH_SHORT).show();
                    }
                }
            }
             
        });
        
    user_login_button.setOnClickListener(new View.OnClickListener() {
		
		@Override  
		public void onClick(View v) {
	        // TODO Auto-generated method stub
			//隐藏键盘
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(login_password.getWindowToken(), 0);
			userNameValue = login_username.getText().toString();  
			userPasswordValue =login_password.getText().toString(); 
	            if(checkEdit())
	            {
	            	/* 显示ProgressDialog */
	            	pd = ProgressDialog.show(UserLoginActivity.this, "登录", "正在登陆，请稍后……");
	            	handler_timer.postDelayed(runnable, 6000);
					
	                userLogin();
	
	            }
	        }    
	});
    }

    Handler handler_timer=new Handler();
    Runnable runnable=new Runnable(){
    @Override
    public void run() {
    // TODO Auto-generated method stub
    	pd.dismiss();
    	Toast.makeText(UserLoginActivity.this, "网络链接超时，请检查是否链接网络", Toast.LENGTH_SHORT).show();
   // handler.postDelayed(this, 4000);
    }
    }; 
     
    private boolean checkEdit(){
        if(login_username.getText().toString().trim().length()<1){
            Toast.makeText(UserLoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        }else if(login_password.getText().toString().trim().length()<6){
            Toast.makeText(UserLoginActivity.this, "密码不能少于6个字符", Toast.LENGTH_SHORT).show();
        }else{
            return true;
        }
        return false;
    }

    
//---登陆，服务器-----------    
    public void userLogin()
	{
		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(JSONObject result) {
				try {
					//Log仅作测试用
					if(result.getBoolean("success") == false)
					{
						Log.v("login", result.getString("info"));
						pd.dismiss();
						 handler_timer.removeCallbacks(runnable);
						Toast.makeText(UserLoginActivity.this, result.getString("info"), Toast.LENGTH_SHORT).show();
					}
					else
					{Log.v("login", result.getString("info"));
					pd.dismiss();
					handler_timer.removeCallbacks(runnable);
				    Toast.makeText(UserLoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
					//记住用户名、旧密码
				    sp.edit().putString("USER_NAME", userNameValue).commit();  
				    sp.edit().putString("PASS_WORD", EncryptionUtil.AES_Encrypt(userPasswordValue)).commit(); 
					//userNameValue = sp.getString("USER_NAME", "");
//					userPasswordValue = sp.getString("PASS_WORD", "");
					MyApplication app;
					app = (MyApplication)getApplication();
					app.setUserName(userNameValue);
				    //跳转修改密码
				    Intent intent=new Intent("com.ebupt.yuebox.ui.USERRESET");
				    startActivity(intent);	
					finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}

		}
				};	
		NetEngine.userLogin(handler, userNameValue, EncryptionUtil.AES_Encrypt(userPasswordValue));
		Log.v("加密", EncryptionUtil.AES_Encrypt(userPasswordValue));
		

	}
	
	//--点击两次back键退出程序---------------
    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();

                } else {
                	finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }	
}