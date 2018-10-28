package com.weibo.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 实际上LruCache就是一个Map,底层是通过HashMap来实现的 缓存对于移动端是非常重要的存在： 减少请求次数，减小服务器压力.
 * 本地数据读取速度更快，让页面不会空白几百毫秒。 在无网络的情况下提供数据。 存进数据库（保存json使用时拿出来解析），
 * 存专有文件，或SharedPreference等等， 也可以自己实现LruCache和 DiskLruCache这两种缓存策略构成二级缓存（内存和磁盘）
 * 
 * @author JiHongHua
 * 
 */
public class MemoryLruCache {
	// 创建Cache缓存,第一个泛型表示缓存的标识key，第二个泛型表示需要缓存的对象
	// 好像用BitmapDrawable比用Bitmap更节省内存
	private LruCache<String, Bitmap> mCaches;

	public MemoryLruCache() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();// 获取应用运行时的最大内存
		// 通过获得最大的运行时候的内存，合理分配缓存的内存空间大小
		int cacheSize = maxMemory / 8;// 取最大运行内存的1/8;
		mCaches = new LruCache<String, Bitmap>(cacheSize) {
			// 在每次存入缓存的时候调用,加载正确的内存大小,如果不重写，只会返回缓存的bitmap的个数
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}
		};
		
	}

	// 将图片保存在LruCache中
	public void addBitmapToCache(String url, Bitmap bitmap) {
		if (getBitmapFromCache(url) == null&&bitmap!=null) {
			// 判断当前的Url对应的Bitmap是否在Lru缓存中，如果不在缓存中，就把当前url对应的Bitmap对象加入Lru缓存
			mCaches.put(url, bitmap);
		}
         
	}

	// 将图片从LruCache中读取出来
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = mCaches.get(url);
		return bitmap;
	}

}
