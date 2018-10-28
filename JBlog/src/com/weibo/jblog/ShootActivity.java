package com.weibo.jblog;

import java.io.IOException;

import com.weibo.connect.ConnectManager;
import com.weibo.utils.ConstantUtil;
import com.weibo.utils.PictureUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ShootActivity extends Activity implements OnClickListener {
	public static final int REQUEST_ADDPICTURE = 0;
	SurfaceView surfaceview; // 通过id获取
	SurfaceHolder surfaceholder;
	Camera myCamera;
	byte[] picture_data;
	boolean isViewing = false; // 是否正在预览
	
	/**
	 * 当前是从相册中获取图片上传，没法上传拍照图
	 */
	
	
	ShutterCallback shutter = new ShutterCallback() {
		@Override
		public void onShutter() {
		}
	};
	PictureCallback jpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera arg1) {
			Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			picture_data=data;
			// bm为所拍的照片，可以进行显示或存入sd卡等操作
			ImageView iv = (ImageView) findViewById(R.id.iv_image);
			iv.setImageBitmap(bm);
			isViewing = false;
			myCamera.stopPreview();
			myCamera.release();
			myCamera = null;
			initCamera();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.shoot_activity);
		Button shoot_take = (Button) findViewById(R.id.shoot_take);
		Button shoot_stop = (Button) findViewById(R.id.shoot_stop);
		Button shoot_upload = (Button) findViewById(R.id.shoot_upload);
		Button shoot_init = (Button) findViewById(R.id.shoot_init);
		shoot_take.setOnClickListener(this);
		shoot_stop.setOnClickListener(this);
		shoot_upload.setOnClickListener(this);
		shoot_init.setOnClickListener(this);
		initView();
	}

	public void initView() {
		surfaceview = (SurfaceView) findViewById(R.id.surface);
		surfaceholder = surfaceview.getHolder();

	}

	// 为什么不能在initView中直接使用initCamera()
	public void initCamera() {
		if (!isViewing) {
			myCamera = Camera.open();
		}
		if (myCamera != null && !isViewing) {
			try {
				surfaceholder = surfaceview.getHolder();
				myCamera.setPreviewDisplay(surfaceholder);
			} catch (IOException e) {
			}
			myCamera.startPreview();
			isViewing = true;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shoot_init:
			initCamera();
			
			break;
		case R.id.shoot_stop:
			if (myCamera != null && isViewing) {
				isViewing = false;
				myCamera.stopPreview();
				myCamera.release();
				myCamera = null;
			}
			
			break;
		case R.id.shoot_take:

			myCamera.takePicture(shutter, null, jpegCallback);

			break;

		case R.id.shoot_upload:
//			Intent i=new Intent(ShootActivity.this,UploadActivity.class);
//			i.putExtra("picture_data",picture_data);
//			startActivity(i);
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			startActivityForResult(intent, REQUEST_ADDPICTURE);
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ADDPICTURE && resultCode == RESULT_OK) {
			if (data != null) {
				Uri picuri = data.getData();
				String absolutePath = PictureUtil.getPath(
						getApplicationContext(), picuri);
				// 如果能删除图片还需要从列表中删除
				new ConnectManager().uploadHead(ConstantUtil.user_id, absolutePath, "ff", 2);
			}
		} else
			Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
	}
	
}
