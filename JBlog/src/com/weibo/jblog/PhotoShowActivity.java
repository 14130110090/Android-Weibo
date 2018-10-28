package com.weibo.jblog;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.weibo.adapter.PhotoAdapter;
import com.weibo.adapter.PhotoPagerAdapter;
import com.weibo.adapter.UserListAdapter.HolderView;
import com.weibo.connect.ConnectManager;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.MemoryLruCache;
import com.weibo.view.Info;
import com.weibo.view.MyImageView;
import com.weibo.view.PhotoView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PhotoShowActivity extends Activity {
	ConnectManager manager;
   MyHandler handler;
   ArrayList<Integer> photoList;
   private ViewPager mPager;
   MemoryLruCache mLruCache;
FileLruCache fileCache;
List<String> mDatas;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photoshow_activity);
		
//		RecyclerView r=(RecyclerView)findViewById(R.id.recycler);
//		r.setLayoutManager(new LinearLayoutManager(this));
//       r.setAdapter(new HomeAdapter());
		
		initView();
	}
	
	public void initView() {
		fileCache = new FileLruCache();
		mLruCache = new MemoryLruCache();
		photoList=new ArrayList<Integer>();
		 mPager = (ViewPager) findViewById(R.id.pager);
	     //设置每页的间隔，滑动时每页之间有间隔
		 //mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
		try {
			Intent intent=getIntent();
			int position=intent.getIntExtra("position", 0);
			JSONArray array = new JSONArray(intent.getStringExtra("url"));
		    mPager.setAdapter(new PhotoPagerAdapter(PhotoShowActivity.this,mLruCache,fileCache,array ));
		    //设置首先显示的图片
		    mPager.setCurrentItem(position);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    
	}
	protected void initData()
    {
        mDatas = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++)
        {
            mDatas.add("" + (char) i);
        }
    }
	
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			//注意photo_show是另一个控件
			
			//picture.setImageBitmap(BitmapFactory.decodeByteArray(pic, 0, pic.length));
		    
		}
		
	}
	
	
	 class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>
	    {

	        @Override
	        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	        {   System.out.println("创建viewholder");
	            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
	                    PhotoShowActivity.this).inflate(R.layout.photo_item, parent,
	                    false));
	            return holder;
	        }

	        @Override
	        public void onBindViewHolder(MyViewHolder holder, int position)
	        {   System.out.println("position"+position);
	            holder.tv.setText(mDatas.get(position));
	        }

	        @Override
	        public int getItemCount()
	        {
	            return mDatas.size();
	        }

	        class MyViewHolder extends ViewHolder
	        {

	            TextView tv;

	            public MyViewHolder(View view)
	            {
	                super(view);
	                tv = (TextView) view.findViewById(R.id.photo_id);
	            }
	        }
	    }
	
	class MM extends BaseAdapter{

		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View currentView, ViewGroup arg2) {
			HH holderView = null;
			System.out.println("刷新"+position);
			if (currentView == null) {
				System.out.println("创建holderview");
				holderView = new HH();
				currentView = LayoutInflater.from(PhotoShowActivity.this).inflate(
						R.layout.photo_item, null);
				holderView.tv=(TextView)currentView.findViewById(R.id.photo_id);
				currentView.setTag(holderView);

			} else {
				holderView = (HH) currentView.getTag();
			}
			holderView.tv.setText(mDatas.get(position));
			return currentView;
		}
		public class HH{
			private TextView tv;
		}
		
	}
	
}
