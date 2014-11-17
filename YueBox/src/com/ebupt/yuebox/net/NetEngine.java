package com.ebupt.yuebox.net;


import java.io.File;
import java.io.FileNotFoundException;

import android.util.Log;

import com.ebupt.yuebox.application.MyApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NetEngine {
	private static AsyncHttpClient client = new AsyncHttpClient();
	private final static String TAG = "netTest";
	private static String logInfo;

	// 获取用户信息
	static String username;
	static String pwd;

	public static void initUserData() {
		username = MyApplication.getInstance().userName;
		pwd = MyApplication.getInstance().password;
	}

	// 待部署现网后，将修改为现网访问地址
	private final static String BASE_URL = "http://jxc.100101.cn/LFF/public/api/index.php";

	/*
	 * 2.1 获取数据时间戳，不含参数，返回工单时间戳和施工人员信息时间戳
	 */
	public static void getTimeStamp(JsonHttpResponseHandler handler) {
		String url = "/Account/getTimeStamp";
		client.get(BASE_URL + url, handler);
	}

	/*
	 * 2.2 登陆，参数msisdn，psw，返回登陆成功/失败信息
	 */
	public static void userLogin(JsonHttpResponseHandler handler,
			String login_username, String login_password) {
		String url = "/Account/login";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("msisdn", login_username);
		params.put("psw", login_password);
		client.post(BASE_URL + url, params, handler);
	}

	/*
	 * 2.3 设置密码，参数msisdn，psw，newpsw ，返回修改密码成功/失败信息
	 */
	public static void userResetpsw(JsonHttpResponseHandler handler,
			String login_username, String login_password, String reset_passwd) {
		String url = "/Account/setpsw";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("msisdn", login_username);
		params.put("psw", login_password);
		params.put("newpsw", reset_passwd);
		client.post(BASE_URL + url, params, handler);
	}

	/*
	 * 2.4 上报设备类型，参数msisdn，psw，type ，pushid,返回上报成功/失败信息
	 */
	public static void userDevice(JsonHttpResponseHandler handler,
			String login_username, String login_password, String device_type,
			String push_id) {
		String url = "/Account/Updatedeviceinfo";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("msisdn", login_username);
		params.put("psw", login_password);
		params.put("type", device_type);
		params.put("pushid", push_id);
		client.post(BASE_URL + url, params, handler);
	}

	/*
	 * 2.5.1 获取展示工单，参数 msisdn psw timestamp，返回展示工单
	 */
	public static void getOrderList(JsonHttpResponseHandler handler,
			String login_username, String login_password, String timestamp) {
		String url = "/Apporder/GetDisplayWorkOrderList";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("msisdn", login_username);
		params.put("psw", login_password);
		Log.v("NetEngine", timestamp+"");
		params.put("timestamp", timestamp);
		client.get(BASE_URL + url, params, handler);
	}

	/*
	 * 2.5.2 获取用户工单，参数 msisdn psw userid，返回某个用户的工单列表
	 */
	public static void getUserOrderList(JsonHttpResponseHandler handler,
			String login_username, String login_password, String userid) {
		String url = "/Appuser/GetUserWorkOrderList";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("msisdn", login_username);
		params.put("psw", login_password);
		params.put("userid", userid);
		client.get(BASE_URL + url, params, handler);
	}

	/*
	 * 2.5.3 抢单，参数 msisdn psw ordered，返回抢单成功/失败信息
	 */
	public static void grabWorkOrder(JsonHttpResponseHandler handler,
			String login_username, String login_password, String grab_taskid) {
		String url = "/Appuser/GrabWorkOrder";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("msisdn", login_username);
		params.put("psw", login_password);
		params.put("ordered", grab_taskid);
		client.post(BASE_URL + url, params, handler);
	}

	/*
	 * 2.5.4 修改订单状态，参数 msisdn psw orderid boxid orderStatus client name tel mobile
	 * 返回修改订单成功/失败信息
	 */
	public static void modifyOrderStatus(JsonHttpResponseHandler handler,
			String task_id, String task_boxids, String task_status,
			String task_client_name, String task_client_person,
			String task_client_tel, String task_client_mobile) {
		String url = "/Apporder/ModifyOrderStatus";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("msisdn", username);
		params.put("psw", pwd);
		params.put("orderid", task_id);
		params.put("boxid", task_boxids);
		params.put("orderStatus", task_status);
		params.put("client", task_client_name);
		params.put("name", task_client_person);
		params.put("tel", task_client_tel);
		params.put("mobil", task_client_mobile);
		client.post(BASE_URL + url, params, handler);
	}

	/*
	 * 2.5.5 上传工单照片，参数 msisdn psw orderid file filename 返回上传照片成功/失败信息
	 */
	public static void uploadPicture(JsonHttpResponseHandler handler,
			String task_id, String mPicPath,String fileName) {
		String url = "/Apppicture/Uploadphoto";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("msisdn", username);
		params.put("psw", pwd);
		params.put("orderid", task_id);
		File file = new File(mPicPath);
		try {
			params.put("file", file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.put("filename", fileName);
		client.post(BASE_URL + url, params, handler);
	}

	/*
	 * 2.5.6 更改用户接单状态，参数 msisdn psw status，返回更改成功/失败信息
	 */
	public static void modifyUserStatus(JsonHttpResponseHandler handler,
			String login_username, String login_password, String user_status) {
		String url = "/Appuser/ModifyUserStatus";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("msisdn", login_username);
		params.put("psw", login_password);
		params.put("status", user_status);
		client.post(BASE_URL + url, params, handler);
	}

	/*
	 * 2.5.7 获取用户接单状态，参数 msisdn psw，返回用户在线/离线信息
	 */
	public static void getUserStatus(JsonHttpResponseHandler handler,
			String login_username, String login_password) {
		String url = "/Appuser/GetUserStatus";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("msisdn", login_username);
		params.put("psw", login_password);
		client.get(BASE_URL + url, params, handler);
	}
	
	/*
	 * 2.6 获取施工人员排行榜数据，参数 msisdn psw，返回施工人员数据
	 */
	public static void getUserData(JsonHttpResponseHandler handler,
			String login_username, String login_password) {
		String url = "/Appuser/GetUserData";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("msisdn", login_username);
		params.put("psw", login_password);
		client.get(BASE_URL + url, params, handler);
	}

	/*
	 * 2.7 获取所有数据，参数 msisdn psw status，返回更改成功/失败信息
	 */
	public static void getAllData(JsonHttpResponseHandler handler,
			String login_username, String login_password) {
		String url = "/Appuser/GetAllData";
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("msisdn", login_username);
		params.put("psw", login_password);
		client.get(BASE_URL + url, params, handler);
	}
}
