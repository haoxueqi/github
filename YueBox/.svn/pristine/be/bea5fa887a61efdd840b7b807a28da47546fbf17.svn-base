package com.ebupt.yuebox.ui;
/**
 * 
 * @ClassName: UserDetailActivity
 * @Description: 联系人详情页
 * @author haoxueqi
 * @date 2014-3-17
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.application.MyApplication;
import com.ebupt.yuebox.database.AppSetupUserDao.Properties;
import com.ebupt.yuebox.database.DbUtil;
import com.ebupt.yuebox.model.AppSetupUser;
public class UserDetailActivity extends Activity {

	private TableRow phone_call;
	private ImageView image_back;
	private List<AppSetupUser> appSetupUsers;
	private MyApplication app;
	private AppSetupUser user_detail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_user_detail);
		//接收上一个活动传递的参数user_id
		 Intent intent = getIntent();
	     String user_id = intent.getStringExtra("user_id");
		app = (MyApplication)getApplication();
		appSetupUsers=app.getAppSetupUsers();
		//利用greenDao的方法queryBuilder()取出user_id对应的对象
		user_detail=DbUtil.getAppSetupUserDao().queryBuilder().where(Properties.User_id.eq(user_id)).list().get(0);

		SetUserDetail();
		
		//--退出联系人详情界面
		image_back = (ImageView) this.findViewById(R.id.image_back);
		image_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		//--按下电话按钮拨出电话
		phone_call=(TableRow)findViewById(R.id.tableRow_phone);
		phone_call.setOnClickListener(new View.OnClickListener() {
			
			@Override  
			public void onClick(View v) {
		        // TODO Auto-generated method stub
				TextView text = (TextView) findViewById(R.id.PhoneText);
				String PhoneNum = text.getText().toString();
				Intent intent = new Intent();
                intent.setAction("android.intent.action.CALL");
                intent.setData(Uri.parse("tel:"+PhoneNum));
                startActivity(intent);
		        }    
		});
		
		
	}
	//--设置联系人详细信息
	private void SetUserDetail() {
		//设置textview的值
				TextView NameText=(TextView)findViewById(R.id.NameText);      
				NameText.setText(user_detail.getUser_name());
				TextView PhoneText=(TextView)findViewById(R.id.PhoneText);      
				PhoneText.setText(user_detail.getUser_tel());
				
				//绑定XML中的ListView，作为Item的容器  
			    ListView list = (ListView) findViewById(R.id.MyListView);  
			      
			    //生成动态数组，并且转载数据  
			    ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
			    HashMap<String, String> map = new HashMap<String, String>(); 
			    map.put("ItemTitle", "所有积分");  
		        map.put("ItemText", user_detail.getUser_total_credits()+"");  
		        mylist.add(map); 
		        map = new HashMap<String, String>(); 
			    map.put("ItemTitle", "周度积分");  
		        map.put("ItemText", user_detail.getUser_week_credits()+"");  
		        mylist.add(map); 
		        map = new HashMap<String, String>(); 
			    map.put("ItemTitle", "月度积分");  
		        map.put("ItemText", user_detail.getUser_month_credits()+"");  
		        mylist.add(map); 
		        map = new HashMap<String, String>(); 
			    map.put("ItemTitle", "年度积分");  
		        map.put("ItemText", user_detail.getUser_year_credits()+"");  
		        mylist.add(map); 
		        map = new HashMap<String, String>(); 
			    map.put("ItemTitle", "部门职位");  
			    String department;
			    String job;
			    if(user_detail.getUser_department().equals("null")||
			       user_detail.getUser_department()==null||
			       user_detail.getUser_department().equals(""))
			    	department="未知";
			    else
			    	department=user_detail.getUser_department();
			    if(user_detail.getUser_job().equals("null")||
		    		user_detail.getUser_job()==null||
		    				user_detail.getUser_job().equals(""))
				    	job="未知";
			    else
			    	job=user_detail.getUser_job();	
			    if((department.equals("未知"))&&(job.equals("未知")))
			    	map.put("ItemText", job);  
			    else
		            map.put("ItemText", department+"-"+job);  
		        mylist.add(map); 
		        map = new HashMap<String, String>(); 
			    map.put("ItemTitle", "电子邮箱");  
		        map.put("ItemText", user_detail.getUser_email());  
		        mylist.add(map);  
		  
			    //生成适配器，数组===》ListItem  
			    SimpleAdapter mSchedule = new SimpleAdapter(this, //没什么解释  
			                                                mylist,//数据来源   
			                                                R.layout.item_user_detail,//ListItem的XML实现  
			                                                  
			                                                //动态数组与ListItem对应的子项          
			                                                new String[] {"ItemTitle", "ItemText"},   
			                                                  
			                                                //ListItem的XML文件里面的两个TextView ID  
			                                                new int[] {R.id.ItemTitle,R.id.ItemText});  
			    //添加并且显示  
			    list.setAdapter(mSchedule);  
				
		
	}
}