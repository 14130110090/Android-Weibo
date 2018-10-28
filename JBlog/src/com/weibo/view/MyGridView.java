package com.weibo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class MyGridView extends GridView {

	private onTouchBlankSpaceListener listener;

	public MyGridView(Context context) {
		super(context);
	}

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnTouchBlankSpaceListener(onTouchBlankSpaceListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// 先创建一个监听接口，一旦点击了无效区域，便调用onTouchBlankSpace方法，
		// 返回true or false来确认是否消费了这个事件
		if (listener != null) {
			if (!isEnabled()) {
				return isClickable() || isLongClickable();
			}
			int motionPosition = pointToPosition((int) ev.getX(),
					(int) ev.getY());
			if (ev.getAction() == MotionEvent.ACTION_UP
					&& motionPosition == INVALID_POSITION) {
				super.onTouchEvent(ev);
				return listener.onTouchBlankSpace(motionPosition);
			}
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * int expandSpec = MeasureSpec.makeMeasureSpec( 　　　　　　　　Integer.MAX_VALUE
	 * >> 2,MeasureSpec.AT_MOST); 其实就是根据提供的大小值和模式创建一个测量值(格式)；
	 * 
	 * 一个MeasureSpec由大小和模式组成。
	 * 
	 * 它有三种模式：
	 * 
	 * UNSPECIFIED(未指定)，父元素不对子元素施加任何束缚，子元素可以得到任意想要的大小；
	 * EXACTLY(完全)，父元素决定自元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；
	 * AT_MOST(至多)，子元素至多达到指定大小的值。
	 * 这里我们的MeasureSpec.AT_MOST代表高度自适应，也就是GridView能多大就有多大。而”size”
	 * 就是提供一个可测量的最大值。我们取Integer的最大值并使用位运算右移两位，是因为：
	 * 
	 * MeasureSpec是一个32位的int值，其中高2位为测量的模式，低30位为测量的大小。在计算中使用位运算的原因是为了提高并优化效率。
	 */

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	public interface onTouchBlankSpaceListener {
		public boolean onTouchBlankSpace(int motionPosition);
	}

}
