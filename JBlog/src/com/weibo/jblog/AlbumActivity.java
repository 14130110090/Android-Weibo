package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.*;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.HomePictureGridViewAdapter;
import com.weibo.adapter.PhotoAdapter;
import com.weibo.connect.ConnectManager.OnJsonReturnListener;
import com.weibo.connect.ConnectToServer;
import com.weibo.connect.ConnectManager;
import com.weibo.connect.ConnectManager.GetPhotoListListener;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.MemoryLruCache;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AlbumActivity extends Activity implements OnJsonReturnListener {
	ProgressDialog pd = null;
	int albumid;
	JSONArray jsonArray;
	MemoryLruCache mLruCache=new MemoryLruCache();
	FileLruCache fileLruCache=new FileLruCache();
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 必须要在handler中设置adapter。否则adapter中的联网操作无法实现
//			PhotoAdapter adapter = new PhotoAdapter(AlbumActivity.this,
//					(JSONArray) msg.obj);
			jsonArray=(JSONArray) msg.obj;
			HomePictureGridViewAdapter adapter=new HomePictureGridViewAdapter(mLruCache, fileLruCache, AlbumActivity.this, (JSONArray) msg.obj);
			GridView photoList = (GridView) findViewById(R.id.photolist);
			photoList.setAdapter(adapter);

			photoList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					Intent i = new Intent(AlbumActivity.this, PhotoShowActivity.class);

					try {//将路径提取出来
						JSONArray array=new JSONArray();
						for(int j=0;j<jsonArray.length();j++){
							array.put(jsonArray.getJSONObject(j).getString("photo_data"));
						}
						i.putExtra("url", array.toString());
						i.putExtra("position", position);
						startActivity(i);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			photoList.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						final int arg2, long arg3) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							AlbumActivity.this);
					builder.setTitle("hello");
					builder.setItems(R.array.array,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									switch (arg1) {
									case 0:
										// int id = 0;
										// try {
										// id = photoItem
										// .getInt("id");
										// } catch (JSONException e)
										// {
										// // TODO Auto-generated
										// // catch block
										// e.printStackTrace();
										// }
										// manager.deletePhoto(id,
										// albumid);
										break;
									case 1:
										break;
									case 2:
										break;
									}

								}
							});
					builder.create().show();
					return true;
				}
			});

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.album_activity);
		initView();

	}

	public void initView() {

		// albumid = i.getIntExtra("albumid", 0);
        Button back=(Button)findViewById(R.id.album_back);
        back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			   finish();
			}
		});
		ConnectManager manager = new ConnectManager();
		manager.setOnJsonReturnListener(this);
		int other_id=getIntent().getIntExtra("other_id", 0);
		if(other_id!=0)
		manager.getPhotoOfUser(other_id);
		else manager.getPhotoOfUser(user_id);
	}

	@Override
	public void onJsonReturn(JSONObject result) {
		try {
			if (result != null) {

				if (result.getString("action").equals(ALBUM_SUCCESS)) {
					JSONArray array = result.getJSONArray("JSONArray");
					Message message = handler.obtainMessage();
					message.obj = array;
					handler.sendMessage(message);  
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
