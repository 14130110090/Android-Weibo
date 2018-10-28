package com.weibo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileLruCache {
	/**
	 * sd卡根目录，本机为/storage/emulated/0
	 */
	private static String mSdRootPath = Environment
			.getExternalStorageDirectory().getPath();
	/**
	 * 手机的缓存根目录,本机为/data/user/0/com.weibo.Jblog/cache
	 */
	private static String mDataRootPath = null;
	/**
	 * 保存Image的目录名
	 */
	private final static String FOLDER_NAME = "/JBlog/";

	public FileLruCache(Context context) {
		mDataRootPath = context.getCacheDir().getPath();
	}

	public FileLruCache() {
	}

	/**
	 * 获取储存Image的目录,如果搭载了sd卡就存到sd卡中，没有就存在应用的缓存目录下
	 * 
	 * @return
	 */
	public String getStorageDirectory() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME
				: mDataRootPath + FOLDER_NAME;
	}

	/**
	 * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
	 * 
	 * @param fileName
	 * @param bitmap
	 * @throws IOException
	 */
	public void savaBitmap(String fileName, Bitmap bitmap) throws IOException {
		if (bitmap == null) {
			return;
		}
		// 去掉所有非数字，末尾加.jpg后缀
		String name = fileName.replaceAll("[^\\d]+", "") + ".jpg";
		String path = getStorageDirectory();
		File folderFile = new File(path);
		if (!folderFile.exists()) {
			folderFile.mkdir();
		}
		// 如果存在就不应该再保存
		File file = new File(path + name);
		if (file.exists()) {
			return;
		}
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG, 100, fos);
		fos.flush();
		fos.close();
	}

	/**
	 * 从手机或者sd卡获取Bitmap
	 * 
	 * @param fileName
	 * @return
	 */
	public Bitmap getBitmap(String fileName) {
		// 需不需要异步写
		String name = fileName.replaceAll("[^\\d]+", "") + ".jpg";
		if (!isFileExists(name))return null;
		return BitmapFactory.decodeFile(getStorageDirectory() + name);

	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean isFileExists(String fileName) {
		return new File(getStorageDirectory() + fileName).exists();
	}

	/**
	 * 获取文件的大小
	 * 
	 * @param fileName
	 * @return
	 */
	public long getFileSize(String fileName) {
		String name = fileName.replaceAll("[^\\d]+", "") + ".jpg";
		if (!isFileExists(name))return 0;
		return new File(getStorageDirectory() + name).length();
	}
	public long getAllFileSize() {
		long sum=0;
		File dirFile = new File(getStorageDirectory());
		if (!dirFile.exists()) {
			return sum;
		}
		if (dirFile.isDirectory()) {
			// 获取所有子文件的名字
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				sum+=new File(dirFile, children[i]).length();
			}
		}
		return sum/1024;
	}
	/**
	 * 删除SD卡或者手机的缓存图片和目录,只能删除该目录下的一级子文件
	 */
	public void deleteFile() {
		File dirFile = new File(getStorageDirectory());
		if (!dirFile.exists()) {
			return;
		}
		if (dirFile.isDirectory()) {
			// 获取所有子文件的名字
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}

		dirFile.delete();
	}
}
