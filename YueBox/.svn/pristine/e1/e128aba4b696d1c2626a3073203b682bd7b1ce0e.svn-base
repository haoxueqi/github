package com.ebupt.yuebox.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.util.Const;

/**
 * 条形码扫描和封面扫描的蒙层
 * 
 * @author xuchang02
 * 
 */
public class FinderView extends View {

	private final Paint paint;
	private Rect finderRect;// 对于UI而言的取景区域
	private Rect finderRectInPreview;// 对于预览图像而言的取景区域
	private final int maskColor;// 蒙层色值
	private Point screenResolution;
	private Point cameraResolution;
	private Bitmap bitmapRightTop;
	private Bitmap bitmapRightBottom;
	private Bitmap bitmapLeftTop;
	private Bitmap bitmapLeftBottom;

	// 条形码取景框大小约束
	private static final int MIN_FRAME_WIDTH_BARCODE = 240;
	private static final int MIN_FRAME_HEIGHT_BARCODE = 180;
	private static final int MAX_FRAME_WIDTH_BARCODE = 720;
	private static final int MAX_FRAME_HEIGHT_BARCODE = 640;
	private static final int BARCODE_SCALE_H = 2;

	// 封面取景框大小约束
	private static final int MIN_FRAME_WIDTH_BOOK = 360;
	private static final int MIN_FRAME_HEIGHT_BOOK = 480;
	private static final int MAX_FRAME_WIDTH_BOOK = 720;
	private static final int MAX_FRAME_HEIGHT_BOOK = 1080;
	private static final int BOOKCOVER_SCALE_H = 3;

	private int scanMode;

	public FinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		maskColor = context.getResources().getColor(R.color.viewfinder_mask);
		bitmapRightTop = BitmapFactory.decodeResource(getResources(),
				R.drawable.right_up);
		bitmapRightBottom = BitmapFactory.decodeResource(getResources(),
				R.drawable.right_down);
		bitmapLeftTop = BitmapFactory.decodeResource(getResources(),
				R.drawable.left_up);
		bitmapLeftBottom = BitmapFactory.decodeResource(getResources(),
				R.drawable.left_down);
	}

	public void setResolutionInfo(Point screenResolution, Point cameraResolution) {
		this.screenResolution = screenResolution;
		this.cameraResolution = cameraResolution;

		Log.d("TAG", "screenResolution:" + screenResolution.x + "x"
				+ screenResolution.y);
		Log.d("TAG", "cameraResolution:" + cameraResolution.x + "x"
				+ cameraResolution.y);
	}

	public void setScanMode(int scanMode) {
		this.scanMode = scanMode;
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		paint.setColor(maskColor);
		if (finderRect == null) {
			finderRect = getFramingRect(width, height);
		}
		// 四角的图片
		canvas.drawBitmap(bitmapLeftTop, finderRect.left, finderRect.top, paint);
		canvas.drawBitmap(bitmapLeftBottom, finderRect.left, finderRect.bottom
				- bitmapLeftBottom.getHeight() + 1, paint);
		canvas.drawBitmap(bitmapRightTop,
				finderRect.right - bitmapRightTop.getWidth() + 1,
				finderRect.top, paint);
		canvas.drawBitmap(bitmapRightBottom, finderRect.right
				- bitmapRightBottom.getWidth() + 1, finderRect.bottom
				- bitmapRightBottom.getHeight() + 1, paint);

		// 矩形外的蒙层
		canvas.drawRect(0, 0, width, finderRect.top, paint);
		canvas.drawRect(0, finderRect.top, finderRect.left,
				finderRect.bottom + 1, paint);
		canvas.drawRect(finderRect.right + 1, finderRect.top, width,
				finderRect.bottom + 1, paint);
		canvas.drawRect(0, finderRect.bottom + 1, width, height, paint);

	}

	public Rect getFramingRect() {
		return finderRect;
	}

	// 获取对UI而言的取景区域
	public Rect getFramingRect(int wid, int hei) {
		screenResolution = new Point(wid, hei);
		int minWid, minHei, maxWid, maxHei;
		int heightScale;
		if (scanMode == Const.SCAN_VALUE_BARCODE) {// 条形码的大小约束
			minWid = MIN_FRAME_WIDTH_BARCODE;
			minHei = MIN_FRAME_HEIGHT_BARCODE;
			maxWid = MAX_FRAME_WIDTH_BARCODE;
			maxHei = MAX_FRAME_HEIGHT_BARCODE;
			heightScale = BARCODE_SCALE_H;
		} else {// 图书封面大小约束
			minWid = MIN_FRAME_WIDTH_BOOK;
			minHei = MIN_FRAME_HEIGHT_BOOK;
			maxWid = MAX_FRAME_WIDTH_BOOK;
			maxHei = MAX_FRAME_HEIGHT_BOOK;
			heightScale = BOOKCOVER_SCALE_H;
		}
		int width = screenResolution.x * 4 / 5;
		if (width < minWid) {
			width = minWid;
		} else if (width > maxWid) {
			width = maxWid;
		}
		int height = screenResolution.y * heightScale / 5;
		if (height < minHei) {
			height = minHei;
		} else if (height > maxHei) {
			height = maxHei;
		}
		int leftOffset = (screenResolution.x - width) / 2;
		int topOffset = (screenResolution.y - height) / 2;
		Rect framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
				topOffset + height);
		Log.d("TAG", "Calculated framing rect: " + framingRect);
		return framingRect;
	}

	// 获取对预览图像而言的取景区域
	public Rect getFramingRectInPreview() {
		if (finderRectInPreview == null) {
			Rect rect = new Rect(getFramingRect());

			// 横向屏幕时的转换策略
			// rect.left = rect.left * cameraResolution.x / screenResolution.x;
			// rect.right = rect.right * cameraResolution.x /
			// screenResolution.x;
			// rect.top = rect.top * cameraResolution.y / screenResolution.y;
			// rect.bottom = rect.bottom * cameraResolution.y /
			// screenResolution.y;

			// 竖向屏幕时的转换策略
			rect.left = rect.left * cameraResolution.y / screenResolution.x;
			rect.right = rect.right * cameraResolution.y / screenResolution.x;
			rect.top = rect.top * cameraResolution.x / screenResolution.y;
			rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;

			finderRectInPreview = rect;
		}
		Log.d("TAG","Calculated framing rect in preview: "
				+ finderRectInPreview.width() + " "
				+ finderRectInPreview.height());
		return finderRectInPreview;
	}

}
