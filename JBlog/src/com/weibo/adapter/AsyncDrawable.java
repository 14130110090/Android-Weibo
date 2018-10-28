package com.weibo.adapter;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;

/**
 * 首先创建一个实现了Drawable接口的类，用来存储AsyncTask的引用。
 * 在本例中，选择了继承ColorDrawable，也可以选择BitmapDrawable用来给ImageView设置一个预留的占位图， 这个占位图用于在AsyncTask执行完毕之前的显示。
 * 还不如在布局中使用背景图
 * @author JiHongHua
 * 
 */
public class AsyncDrawable extends ColorDrawable {
	private final WeakReference<BitmapLoadTask> bitmapLoadTaskReference;

	public AsyncDrawable(BitmapLoadTask bitmapLoadTask) {
		bitmapLoadTaskReference = new WeakReference<BitmapLoadTask>(
				bitmapLoadTask);
	}
	
	public BitmapLoadTask getBitmapWorkerTask() {
		return bitmapLoadTaskReference.get();
	}
}
