package com.weibo.adapter;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;

/**
 * ���ȴ���һ��ʵ����Drawable�ӿڵ��࣬�����洢AsyncTask�����á�
 * �ڱ����У�ѡ���˼̳�ColorDrawable��Ҳ����ѡ��BitmapDrawable������ImageView����һ��Ԥ����ռλͼ�� ���ռλͼ������AsyncTaskִ�����֮ǰ����ʾ��
 * �������ڲ�����ʹ�ñ���ͼ
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
