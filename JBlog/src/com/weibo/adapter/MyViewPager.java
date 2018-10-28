package com.weibo.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {
	
	private boolean scrollable = true;

	public MyViewPager(Context context) {
		super(context);
	}
   //该构造器不写，不能调用要添加参数的构造方法
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return scrollable && super.onTouchEvent(arg0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return scrollable && super.onInterceptTouchEvent(arg0);
	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		super.setCurrentItem(item, smoothScroll);
	}

	@Override
	public void setCurrentItem(int item) {
		// false 去除滚动效果
		super.setCurrentItem(item, false);
	}
}
