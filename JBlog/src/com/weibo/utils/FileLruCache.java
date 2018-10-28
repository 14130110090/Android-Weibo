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
	 * sd����Ŀ¼������Ϊ/storage/emulated/0
	 */
	private static String mSdRootPath = Environment
			.getExternalStorageDirectory().getPath();
	/**
	 * �ֻ��Ļ����Ŀ¼,����Ϊ/data/user/0/com.weibo.Jblog/cache
	 */
	private static String mDataRootPath = null;
	/**
	 * ����Image��Ŀ¼��
	 */
	private final static String FOLDER_NAME = "/JBlog/";

	public FileLruCache(Context context) {
		mDataRootPath = context.getCacheDir().getPath();
	}

	public FileLruCache() {
	}

	/**
	 * ��ȡ����Image��Ŀ¼,���������sd���ʹ浽sd���У�û�оʹ���Ӧ�õĻ���Ŀ¼��
	 * 
	 * @return
	 */
	public String getStorageDirectory() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME
				: mDataRootPath + FOLDER_NAME;
	}

	/**
	 * ����Image�ķ�������sd���洢��sd����û�оʹ洢���ֻ�Ŀ¼
	 * 
	 * @param fileName
	 * @param bitmap
	 * @throws IOException
	 */
	public void savaBitmap(String fileName, Bitmap bitmap) throws IOException {
		if (bitmap == null) {
			return;
		}
		// ȥ�����з����֣�ĩβ��.jpg��׺
		String name = fileName.replaceAll("[^\\d]+", "") + ".jpg";
		String path = getStorageDirectory();
		File folderFile = new File(path);
		if (!folderFile.exists()) {
			folderFile.mkdir();
		}
		// ������ھͲ�Ӧ���ٱ���
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
	 * ���ֻ�����sd����ȡBitmap
	 * 
	 * @param fileName
	 * @return
	 */
	public Bitmap getBitmap(String fileName) {
		// �費��Ҫ�첽д
		String name = fileName.replaceAll("[^\\d]+", "") + ".jpg";
		if (!isFileExists(name))return null;
		return BitmapFactory.decodeFile(getStorageDirectory() + name);

	}

	/**
	 * �ж��ļ��Ƿ����
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean isFileExists(String fileName) {
		return new File(getStorageDirectory() + fileName).exists();
	}

	/**
	 * ��ȡ�ļ��Ĵ�С
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
			// ��ȡ�������ļ�������
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				sum+=new File(dirFile, children[i]).length();
			}
		}
		return sum/1024;
	}
	/**
	 * ɾ��SD�������ֻ��Ļ���ͼƬ��Ŀ¼,ֻ��ɾ����Ŀ¼�µ�һ�����ļ�
	 */
	public void deleteFile() {
		File dirFile = new File(getStorageDirectory());
		if (!dirFile.exists()) {
			return;
		}
		if (dirFile.isDirectory()) {
			// ��ȡ�������ļ�������
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}

		dirFile.delete();
	}
}
