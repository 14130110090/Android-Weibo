package com.weibo.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * ʵ����LruCache����һ��Map,�ײ���ͨ��HashMap��ʵ�ֵ� ��������ƶ����Ƿǳ���Ҫ�Ĵ��ڣ� ���������������С������ѹ��.
 * �������ݶ�ȡ�ٶȸ��죬��ҳ�治��հ׼��ٺ��롣 ���������������ṩ���ݡ� ������ݿ⣨����jsonʹ��ʱ�ó�����������
 * ��ר���ļ�����SharedPreference�ȵȣ� Ҳ�����Լ�ʵ��LruCache�� DiskLruCache�����ֻ�����Թ��ɶ������棨�ڴ�ʹ��̣�
 * 
 * @author JiHongHua
 * 
 */
public class MemoryLruCache {
	// ����Cache����,��һ�����ͱ�ʾ����ı�ʶkey���ڶ������ͱ�ʾ��Ҫ����Ķ���
	// ������BitmapDrawable����Bitmap����ʡ�ڴ�
	private LruCache<String, Bitmap> mCaches;

	public MemoryLruCache() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();// ��ȡӦ������ʱ������ڴ�
		// ͨ�������������ʱ����ڴ棬������仺����ڴ�ռ��С
		int cacheSize = maxMemory / 8;// ȡ��������ڴ��1/8;
		mCaches = new LruCache<String, Bitmap>(cacheSize) {
			// ��ÿ�δ��뻺���ʱ�����,������ȷ���ڴ��С,�������д��ֻ�᷵�ػ����bitmap�ĸ���
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}
		};
		
	}

	// ��ͼƬ������LruCache��
	public void addBitmapToCache(String url, Bitmap bitmap) {
		if (getBitmapFromCache(url) == null&&bitmap!=null) {
			// �жϵ�ǰ��Url��Ӧ��Bitmap�Ƿ���Lru�����У�������ڻ����У��Ͱѵ�ǰurl��Ӧ��Bitmap�������Lru����
			mCaches.put(url, bitmap);
		}
         
	}

	// ��ͼƬ��LruCache�ж�ȡ����
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = mCaches.get(url);
		return bitmap;
	}

}
