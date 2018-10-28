package com.weibo.adapter;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.weibo.connect.ConnectManager;
import com.weibo.utils.ConstantUtil;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.MemoryLruCache;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class BitmapLoadTask extends AsyncTask<Integer, Void, Bitmap> {
	private final WeakReference<ImageView> reference;
	private String path = null;
	MemoryLruCache mLruCache;
	FileLruCache fileCache;
	
	public BitmapLoadTask(ImageView imageView, String path,
			MemoryLruCache mLruCache,FileLruCache fileCache) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		reference = new WeakReference<ImageView>(imageView);
		this.mLruCache = mLruCache;
		this.path = path;
		this.fileCache=fileCache;
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(Integer... params) {
		if (path == null)
			return null;
		Bitmap bitmap = null;
		try {
			//���ر�������е�ͼƬ
			if(!path.startsWith("images")){				
				int targetHeight = 1280;
				int targetWidth = 720;
				if (reference != null) {
					ImageView image = reference.get();
					if (image != null) {
						targetHeight = image.getHeight();
						targetWidth = image.getWidth();
					}
				}
				bitmap = ConnectManager.readBitmapFromFile(path, targetWidth,
						targetHeight);
				if (mLruCache != null)
					mLruCache.addBitmapToCache(path, bitmap);
				//�������ļ�����
			}else {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			//Ҫ�����·��תΪ����url
			String url=ConstantUtil.ROOTDIR+path;
			HttpPost httppost = new HttpPost(url);
			HttpResponse httpResponse = httpClient.execute(httppost);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
			// ���������ȡ�豸����Ļ���ֵ
			int targetHeight = 1280;
			int targetWidth = 720;
			if (reference != null) {
				ImageView image = reference.get();
				if (image != null) {
					targetHeight = image.getHeight();
					targetWidth = image.getWidth();
				}
			}
			bitmap = ConnectManager.readBitmapFromInputStream(is, targetWidth,
					targetHeight);
			// �Ѿ����������������ϲ��ܺ����費����imageview��Ҫ������
			if (mLruCache != null)
				mLruCache.addBitmapToCache(path, bitmap);
			if (fileCache != null)
				fileCache.savaBitmap(path, bitmap);
			is.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return bitmap;
	}

	public String getPath() {
		return path;
	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}
		if (reference != null && bitmap != null) {
			final ImageView imageView = reference.get();
			// �жϸ�imageView��task�ǲ��Ǳ�task
			final BitmapLoadTask bitmapWorkerTask = ConnectManager
					.getBitmapLoadTask(imageView);
			if (this == bitmapWorkerTask && imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}
	}

}
