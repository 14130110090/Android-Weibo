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
		// �ȴ���һ�������ӿڣ�һ���������Ч���򣬱����onTouchBlankSpace������
		// ����true or false��ȷ���Ƿ�����������¼�
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
	 * int expandSpec = MeasureSpec.makeMeasureSpec( ����������������Integer.MAX_VALUE
	 * >> 2,MeasureSpec.AT_MOST); ��ʵ���Ǹ����ṩ�Ĵ�Сֵ��ģʽ����һ������ֵ(��ʽ)��
	 * 
	 * һ��MeasureSpec�ɴ�С��ģʽ��ɡ�
	 * 
	 * ��������ģʽ��
	 * 
	 * UNSPECIFIED(δָ��)����Ԫ�ز�����Ԫ��ʩ���κ���������Ԫ�ؿ��Եõ�������Ҫ�Ĵ�С��
	 * EXACTLY(��ȫ)����Ԫ�ؾ�����Ԫ�ص�ȷ�д�С����Ԫ�ؽ����޶��ڸ����ı߽���������������С��
	 * AT_MOST(����)����Ԫ������ﵽָ����С��ֵ��
	 * �������ǵ�MeasureSpec.AT_MOST����߶�����Ӧ��Ҳ����GridView�ܶ����ж�󡣶���size��
	 * �����ṩһ���ɲ��������ֵ������ȡInteger�����ֵ��ʹ��λ����������λ������Ϊ��
	 * 
	 * MeasureSpec��һ��32λ��intֵ�����и�2λΪ������ģʽ����30λΪ�����Ĵ�С���ڼ�����ʹ��λ�����ԭ����Ϊ����߲��Ż�Ч�ʡ�
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
