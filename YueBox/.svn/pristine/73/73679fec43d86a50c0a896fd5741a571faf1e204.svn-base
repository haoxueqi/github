package com.ebupt.yuebox.database;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ebupt.yuebox.database.DaoMaster.DevOpenHelper;
import com.ebupt.yuebox.database.SetupTaskDao.Properties;
import com.ebupt.yuebox.model.AppSetupUser;
import com.ebupt.yuebox.model.SetupTask;
import com.ebupt.yuebox.model.WodAppPicture;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @ClassName DbUtil
 * @Description 与数据库操作有关的公用方法
 * @author ZhouZheChen
 * @date 2014-03-12
 * @RevisionHistory
 */

public class DbUtil {
	
	private static SQLiteDatabase db;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;
	private static SetupTaskDao setupTaskDao;
	private static AppSetupUserDao appSetupUserDao;
	private static WodAppPictureDao wodAppPictureDao;
	private static DevOpenHelper helper;
	
	public static void confirmDB(Context context)
	{	
	    helper = new DaoMaster.DevOpenHelper(context, "YueBox", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        setupTaskDao = daoSession.getSetupTaskDao();
        appSetupUserDao = daoSession.getAppSetupUserDao();
        wodAppPictureDao = daoSession.getWodAppPictureDao();
        //添加工单信息测试数据
//        if(setupTaskDao.count() == 0 )
//        	setupTaskDao.insert(TestData.setupTask5);
//        {
//        	setupTaskDao.insert(TestData.setupTask1);
//        	setupTaskDao.insert(TestData.setupTask2);
//        	setupTaskDao.insert(TestData.setupTask3);
//        	setupTaskDao.insert(TestData.setupTask4);        	
//        }
        //添加用户个人信息测试数据
//        if(appSetupUserDao.count() == 0 )
//        { appSetupUserDao.insert(TestData.AppUser1);
//        }
	}
	public static void deleteOrderWithNoUser()
	{
		//本地仅缓存用户自己的工单，所以退出程序和每次轮询后删除未完成工单
		QueryBuilder<SetupTask> qb = setupTaskDao.queryBuilder();
		qb.where(qb.or(Properties.Task_get_userid.eq(""), 
					   Properties.Task_get_userid.isNull(),
					   Properties.Task_get_userid.eq("null")));
		List<SetupTask> temp = qb.list();
		for(int i=0;i<temp.size();i++)
		{
			setupTaskDao.delete(temp.get(i));
		}
	}
	
//	public static void tempTest()
//	{
	//不与服务器交互时使用
//		List<SetupTask> temp = setupTaskDao.queryBuilder().where(Properties.Task_id.eq("4")).list();
//		if(temp.size() != 0)
//			setupTaskDao.delete(temp.get(0));		
//	}	
	
	public static void addSetupTask(List<SetupTask> tasks)
	{
		for(int i=0;i<tasks.size();i++)
		{
			setupTaskDao.insertOrReplace(tasks.get(i));
		}
	}
	
	public static void addAppSetupUser(List<AppSetupUser> users)
	{
		for(int i=0;i<users.size();i++)
		{
			appSetupUserDao.insertOrReplace(users.get(i));
		}
	}
	
	public static List<SetupTask> getSetupTasks()
	{
	//按照工单发布时间排序；
      return setupTaskDao.queryBuilder().orderDesc(Properties.Task_edittime).list();
	}
	
	public static List<AppSetupUser> getAppSetupUsers()
	{
      return appSetupUserDao.loadAll();
	}
	
	public static List<WodAppPicture> getWodAppPictures()
	{
      return wodAppPictureDao.loadAll();
	}
	
	public static SetupTaskDao getSetupTaskDao(){
		return setupTaskDao;
	}
	
	public static AppSetupUserDao getAppSetupUserDao(){
		return appSetupUserDao;
	}
}
