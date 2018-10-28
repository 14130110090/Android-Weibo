package com.weibo.connect;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class LoadPicture {
	String url;
	Bitmap bitmap;

	public LoadPicture(String url) {
		this.url = url;
	}

	public Bitmap getBitmap() {
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse httpResponse = httpClient.execute(httppost);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return bitmap;
	}
}
