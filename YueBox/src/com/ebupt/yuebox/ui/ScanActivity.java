package com.ebupt.yuebox.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.application.MyApplication;
import com.ebupt.yuebox.net.NetEngine;
import com.ebupt.yuebox.util.Const;
import com.ebupt.yuebox.util.PlanarYUVLuminanceSource;
import com.ebupt.yuebox.view.CameraView;
import com.ebupt.yuebox.view.CustomDialog;
import com.ebupt.yuebox.view.FinderView;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * 扫描条形码，封面
 * 
 * @author xuchang02
 * 
 */

public class ScanActivity extends Activity implements OnClickListener {

	private CameraView mCameraView;// 摄像区域
	private FinderView mFinderView;// 取景框
	private ImageView mTakePictureBtn;// 拍照按钮
	private ImageView mTakePictureBar;// 拍照按钮栏
	private Rect mFinderRect;// 取景框矩形
	private TextView mUploadBtn;
	private TextView mGiveUpBtn;
	private TextView mResultTv;
	private CustomDialog mDialog;

	private DecodeBarcodeTask mDecodeTask;// 解析barcode
	private AutoFocusCallback mAutoFocusCallBack;// 自动对焦回调
	private PreviewCallback mPreviewCallback;// 预览回调
	private Timer mTimer;
	private TimerTask mTimerTask;
	private String mTaskId;
	private Bitmap mTaskPic;
	private String mScanResult;

	private int mScanMode;// 识别模式
	private final int AUTO_FOCUS_INTERVAL = 1500;// 自动对焦间隔

	private String mPicPath;

	@Override
	protected void onResume() {
		super.onResume();
		mCameraView.onResume();
		if (mScanMode == Const.SCAN_VALUE_BOOKCOVER) {
			mTakePictureBtn.setEnabled(true);
		} else {
			startTimer();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	};

	@Override
	protected void onRestart() {
		super.onRestart();
	};

	@Override
	protected void onPause() {
		super.onPause();
		mCameraView.onPause();
		if (mScanMode == Const.SCAN_VALUE_BARCODE) {
			mTimer.cancel();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCameraView.stopPreview();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_scan);
		MyApplication.getInstance();
		initViewById();
		mScanMode = getIntent().getIntExtra(Const.SCAN_KEY, -1);
		mTaskId = getIntent().getStringExtra("task_id");
		if (mScanMode == Const.SCAN_VALUE_BARCODE) {
			initBarcodeView();
			new Thread() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					boolean isStop = false;

					try {
						sleep(500);
						while (!isStop) {
							if (mFinderView != null
									&& mFinderView.getFramingRect() != null) {
								isStop = true;
								myHandler.sendEmptyMessage(0);
							}
							sleep(100);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}.start();
		} else if (mScanMode == Const.SCAN_VALUE_BOOKCOVER) {
			initTakePicView();
		} else {
		}
	}

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mFinderView.getFramingRect();
		}

	};

	private void initViewById() {
		mCameraView = (CameraView) findViewById(R.id.camera_view);
		mFinderView = (FinderView) findViewById(R.id.finder_view);
		mTakePictureBtn = (ImageView) findViewById(R.id.take_pic_btn);
		mTakePictureBar = (ImageView) findViewById(R.id.take_pic_bg);
		mUploadBtn = (TextView) findViewById(R.id.upload_pic_btn);
		mGiveUpBtn = (TextView) findViewById(R.id.giveup_pic_btn);
		mResultTv = (TextView) findViewById(R.id.barcode_result);
	}

	private void initTakePicView() {

		mCameraView.setOnClickListener(this);// 点击屏幕对焦
		mFinderView.setScanMode(mScanMode);
		mTakePictureBtn.setOnClickListener(this);
		mUploadBtn.setOnClickListener(this);
		mGiveUpBtn.setOnClickListener(this);

	}

	private PictureCallback jpegCallback = new PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {
			Parameters ps = camera.getParameters();
			if (ps.getPictureFormat() == PixelFormat.JPEG) {
				// 存储拍照获得的图片
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inSampleSize = 2;
				mTaskPic = BitmapFactory.decodeByteArray(data, 0, data.length,
						opt);
				int rotateDegree = mCameraView.getCameraOritation();
				if (rotateDegree != 0) {
					mTaskPic = rotateBitmap(mTaskPic, rotateDegree);
				}
				mUploadBtn.setVisibility(View.VISIBLE);
				mGiveUpBtn.setVisibility(View.VISIBLE);
				mTakePictureBtn.setVisibility(View.INVISIBLE);
			}
		}

		private Bitmap rotateBitmap(Bitmap bm, int rotateDegree) {
			Matrix m = new Matrix();
			m.setRotate(rotateDegree, (float) bm.getWidth() / 2,
					(float) bm.getHeight() / 2);

			try {
				Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bm1;
			} catch (OutOfMemoryError ex) {
			}
			return null;
		}
	};

	public boolean saveMyBitmap(Bitmap mBitmap) {
		String mPicName = getPicName();
		mPicPath = Const.PIC_DIR + mPicName + ".jpg";
		File destDir = new File(Const.PIC_DIR);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		File f = new File(mPicPath);
		try {
			f.createNewFile();
		} catch (IOException e) {
			return false;
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();

		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private String getPicName() {
		File destDir = new File(Const.PIC_DIR);
		File[] files = null;
		if (destDir.isDirectory()) {
			files = destDir.listFiles(new MyFileFilter());
		}
		if (files == null)
			return mTaskId + "_image0";
		else {
			return mTaskId + "_image" + files.length;
		}
	}

	class MyFileFilter implements FileFilter {

		/**
		 * 匹配文件名称
		 */
		public boolean accept(File file) {
			try {
				Pattern pattern = Pattern.compile(mTaskId + "_image\\d+.*");
				Matcher match = pattern.matcher(file.getName());
				return match.matches();
			} catch (Exception e) {
				return true;
			}
		}
	}

	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	private void initBarcodeView() {
		mFinderView.setScanMode(mScanMode);
		mTakePictureBtn.setVisibility(View.INVISIBLE);
		mTakePictureBar.setVisibility(View.INVISIBLE);
		mResultTv.setVisibility(View.VISIBLE);
		String text = getIntent().getStringExtra("result");
		mResultTv.setText("已扫描结果：" + text);
	}

	public void startTimer() {
		mPreviewCallback = new PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera arg1) {
				if (null != mDecodeTask) {
					switch (mDecodeTask.getStatus()) {
					case RUNNING:
						return;
					case PENDING:
						mDecodeTask.cancel(false);
						break;
					case FINISHED:
						break;
					default:
						break;
					}
				}
				mDecodeTask = new DecodeBarcodeTask(data);
				mDecodeTask.execute((Void) null);
			}

		};

		mAutoFocusCallBack = new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if (success) {
					mCameraView.setOneShotPreviewCallback(mPreviewCallback);
					Log.d("TAG", "onAutoFocus success");
				} else {
					Log.d("TAG", "focus failed");
				}
			}
		};

		mTimer = new Timer();
		mTimerTask = new CameraTimerTask();
		mTimer.schedule(mTimerTask, 0, AUTO_FOCUS_INTERVAL);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.camera_view:
			mCameraView.autoFocus();
			break;
		case R.id.take_pic_btn:
			takePicture();
			break;
		case R.id.giveup_pic_btn:
			setResult(Const.BACK);
			finish();
			break;
		case R.id.upload_pic_btn:
			savePic();
			break;
		case R.id.ok_btn:
			mDialog.dismiss();
			Intent intent = new Intent();
			intent.putExtra("box_id", mScanResult);
			setResult(Const.GET_BOX_ID_OK, intent);
			finish();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(Const.BACK);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void savePic() {
		if (saveMyBitmap(mTaskPic) == true) {
			Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
			setResult(Const.BACK);
			finish();
		} else {
			Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
		}
	}

	private void takePicture() {
		mCameraView.takePicture(jpegCallback);

	}

	// 解析Barcode线程
	private class DecodeBarcodeTask extends AsyncTask<Void, Void, String> {

		private byte[] mData;

		// 构造函数
		DecodeBarcodeTask(byte[] data) {
			this.mData = data;
		}

		@Override
		protected String doInBackground(Void... params) {
			System.currentTimeMillis();
			Size size = mCameraView.getCameraParameters().getPreviewSize();
			String result = null;
			// 获取预览大小
			final int w = size.width;
			final int h = size.height;
			Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
			hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
			hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
			try {
				result = decode(mData, w, h, hints);
				if (result != null) {
					// mTimer.cancel();
					Log.d("TAG", "result:" + result);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.currentTimeMillis();
			return result;
		}

		protected void onPostExecute(String result) {
			if (result != null) {

				Log.d("TAG", "decode success");
				mDialog = new CustomDialog(ScanActivity.this,
						R.layout.dialog_scan, R.style.MyDialog);
				TextView tv = (TextView) mDialog.findViewById(R.id.result);
				mDialog.findViewById(R.id.ok_btn).setOnClickListener(
						ScanActivity.this);
				tv.setText(result);
				mScanResult = result;
				mDialog.show();

			} else {
				Log.d("TAG", "decode failed");
			}
		}
	}

	class CameraTimerTask extends TimerTask {
		@Override
		public void run() {
			if (mCameraView != null) {
				mCameraView.autoFocus(mAutoFocusCallBack);
			}
		}
	}

	/**
	 * 解码barcode
	 * 
	 * @param data
	 *            预览数据
	 * @param width
	 *            预览宽度
	 * @param height
	 *            预览高度
	 * @param hints
	 *            barcode类型
	 * @return 识别结果
	 * @throws Exception
	 */
	public String decode(byte[] data, int width, int height,
			Map<DecodeHintType, Object> hints) throws Exception {

		PlanarYUVLuminanceSource source = getRotatedPlanarYUVLuminanceSource(
				data, width, height);
		BinaryBitmap binaryBitmap = new BinaryBitmap(
				new HybridBinarizer(source));

		MultiFormatReader barcodeReader = new MultiFormatReader();
		Result result;
		String finalResult = null;
		try {
			if (hints != null && !hints.isEmpty())
				result = barcodeReader.decode(binaryBitmap, hints);
			else
				result = barcodeReader.decode(binaryBitmap);
			// 获得识别结果
			finalResult = String.valueOf(result.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return finalResult;
	}

	// 获得剪切过后的图书封面
	protected Bitmap getCroppedBookCover(byte[] data) {
		Size size = mCameraView.getCameraParameters().getPreviewSize();// 获取预览大小
		PlanarYUVLuminanceSource source = getRotatedPlanarYUVLuminanceSource(
				data, size.width, size.height);
		// 保存识别图像
		Bitmap bm = source.renderCroppedGreyscaleBitmap();
		return bm;
	}

	// 获得旋转90°后的图像数据
	private PlanarYUVLuminanceSource getRotatedPlanarYUVLuminanceSource(
			byte[] data, int width, int height) {
		mFinderView.setResolutionInfo(
				mCameraView.mConfigManager.getScreenResolution(),
				mCameraView.mConfigManager.getCameraResolution());
		mFinderRect = mFinderView.getFramingRectInPreview();

		// 在解码前先旋转byte流中的图像数据
		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				rotatedData[x * height + height - y - 1] = data[x + y * width];
			}
		}

		// 宽高互换
		int tmp = width;
		width = height;
		height = tmp;

		PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
				rotatedData, width, height, mFinderRect.left, mFinderRect.top,
				mFinderRect.width(), mFinderRect.height());

		return source;
	}

}
