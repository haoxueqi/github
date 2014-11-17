package com.ebupt.yuebox.model;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.ebupt.yuebox.fragment.OrderFragment;

/**
 * 
 * @ClassName: MapWorkOrderTag 
 * @Description: 在地图模式中添加工单大头针，点击弹出泡泡窗口
 * @author ZhouZheChen
 * @date 2014-3-6
 * 
 */

 /*
 * 要处理overlay点击事件时需要继承ItemizedOverlay
 * 不处理点击事件时可直接生成ItemizedOverlay.
 */
public class MapWorkOrderPop extends ItemizedOverlay<OverlayItem> {
    //用MapView构造ItemizedOverlay
	private OrderFragment fragment;
    public MapWorkOrderPop(Drawable mark,MapView mapView,OrderFragment fragment){
       	super(mark,mapView);
        this.fragment = fragment;
    }
    protected boolean onTap(int index) {
        //在此处理item点击事件
    	fragment.showPopView(index);
        return true;
    }
    public boolean onTap(GeoPoint pt, MapView mapView){
        //在此处理MapView的点击事件，当返回 true时
        super.onTap(pt,mapView);
        return false;
    } 
}        