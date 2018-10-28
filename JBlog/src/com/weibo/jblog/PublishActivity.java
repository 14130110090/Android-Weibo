package com.weibo.jblog;

import java.io.FileNotFoundException;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.MyGridViewAdapter;

import com.weibo.connect.PublishTask;
import com.weibo.connect.PublishTask.PublishListener;
import com.weibo.listener.ListenerManager;

import com.weibo.utils.ConstantUtil;
import com.weibo.utils.MemoryLruCache;
import com.weibo.utils.PictureUtil;
import com.weibo.utils.StringUtil;

import static com.weibo.utils.ConstantUtil.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;

public class PublishActivity extends Activity implements OnClickListener {
	public static final int REQUEST_ADDPICTURE = 0;
	public static final int DIARY_MAXLENGTH = 140;
	public static final int PICTURE_MAXCOUNT = 9;
	ProgressDialog pd = null;
	ArrayList<String> uriList;
	MyGridViewAdapter adapter;
	// 设置一次字符串传输的最大长度
	int MAXLENGTH = 20000;
	
	MemoryLruCache mLruCache;
	PublishListener listener = new PublishListener() {

		@Override
		public void publish(JSONObject result) {
			try {
				if (result == null) {
					Toast.makeText(PublishActivity.this, "无法连接服务器",
							Toast.LENGTH_SHORT).show();
				} else if (result.getString("action").equals(PUBLISH_FAIL)) {
					Toast.makeText(PublishActivity.this, "发布失败",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(PublishActivity.this, "发布成功",
							Toast.LENGTH_SHORT).show();
                    //用来向用户页面传递发布微博成功的信息从而更改微博数
					ListenerManager.getInstance().onCountChanged(PUBLISH_SUCCESS, 1);
					
					/**
					 * 注意要更新本地数据库
					 */
					finish();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.publish_activity);
		initView();
	}

	public void initView() {
		Button publish = (Button) findViewById(R.id.publish);
		Button cancel = (Button) findViewById(R.id.cancel);
		EditText inputDiary = (EditText) findViewById(R.id.publish_diary);
		Button addPhoto = (Button) findViewById(R.id.publish_addphoto);
		Button takePic = (Button) findViewById(R.id.publish_takepic);
		final TextView textCount = (TextView) findViewById(R.id.textcount);
		mLruCache = new MemoryLruCache();
		uriList = new ArrayList<String>();
		GridView gridview = (GridView) findViewById(R.id.mygridview);
		adapter = new MyGridViewAdapter(this, mLruCache, uriList);
		gridview.setAdapter(adapter);
		gridview.setSelector(new ColorDrawable(Color.GRAY));
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent i=new Intent(PublishActivity.this,PhotoShowActivity.class);
				i.putExtra("position", position);
				JSONArray array=new JSONArray();
				for(int j=0;j<uriList.size();j++){
					array.put(uriList.get(j));
				}
				i.putExtra("url", array.toString());
				startActivity(i);
			}
		});
		addPhoto.setOnClickListener(this);
		takePic.setOnClickListener(this);
		publish.setOnClickListener(this);
		cancel.setOnClickListener(this);
		TextWatcher watcher = new TextWatcher() {
			/**
			 * @param s
			 *            更改以后现存的字符串
			 * @param start
			 *            新增字符：该值为新增字符所在的下标 删除字符：该值为删掉字符所在的下标
			 * @param before
			 *            新增字符：该值为0 删除字符：该值为删掉的字符数
			 * @param count
			 *            新增字符：值为新增字符数 删除字符：值为0
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				 textCount.setText(s.length()+"");
	                if(s.length()>DIARY_MAXLENGTH){
	                	textCount.setTextColor(PublishActivity.this.getResources().getColor(R.color.red));
	                }else textCount.setTextColor(PublishActivity.this.getResources().getColor(R.color.black));
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable edit) {
			}
		};
		inputDiary.addTextChangedListener(watcher);
	}

	/**
	 * 检查输入是否为空或超出字数限制
	 * 
	 * @param input
	 * @return
	 */
	public boolean checkString(String input) {
		boolean ifBlank = true;
		if (StringUtil.isBlank(input)) {
			Toast.makeText(this, "内容为空", Toast.LENGTH_SHORT).show();
			ifBlank = false;
		} else if (input.length() > DIARY_MAXLENGTH) {
			Toast.makeText(this,
					"超出" + (input.length() - DIARY_MAXLENGTH) + "个字",
					Toast.LENGTH_SHORT).show();
			ifBlank = false;
		}
		return ifBlank;
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
				
				uriList.add(absolutePath);
				// ContentResolver cr = this.getContentResolver();
				// // 由于在主线程中加载bitmap，所以会很卡。
				// Bitmap bitmap = null;
				// try {
				// bitmap = BitmapFactory.decodeStream(cr
				// .openInputStream(picuri));
				// } catch (FileNotFoundException e) {
				// e.printStackTrace();
				// }
				// pictureList.add(bitmap);
				adapter.notifyDataSetChanged();
			}
		} else
			Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.publish:
			EditText publish_diary = (EditText) findViewById(R.id.publish_diary);
			String diary = publish_diary.getText().toString();
			if (checkString(diary)) {

				if (ConstantUtil.user_id != 0) {
					PublishTask task = new PublishTask(this, listener, diary,
							uriList);
					task.execute(ConstantUtil.user_id);
				} else {
					// 跳出登录页面
					Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.cancel:
			finish();
			break;
		case R.id.publish_addphoto:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			startActivityForResult(intent, REQUEST_ADDPICTURE);
			
			break;
		case R.id.publish_takepic:
			break;
		default:
			;
		}

	}
}
