package com.weibo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MyDialog extends Dialog {

	private static int default_width = 600; // Ĭ�Ͽ��
	private static int default_height = 300;// Ĭ�ϸ߶�

	private boolean noBackPressed = false;

	public MyDialog(Context context, View layout, int style) {
		this(context, default_width, default_height, layout, style);
	}

	public MyDialog(Context context, int width, int height, View layout,
			int style) {
		super(context, style);
		// ���ز���
		setContentView(layout);
		// ����Dialog����
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.gravity = Gravity.CENTER;
		params.width=width;
		params.height=height;
		window.setAttributes(params);
	}

	/**
	 * �����Ƿ�����ʹ�÷��ؼ�
	 * 
	 * @param b
	 */
	public void setBackPressed(boolean b) {
		noBackPressed = b;
	}

	@Override
	public void onBackPressed() {
		if (!noBackPressed) {
			super.onBackPressed();
		}
	}
}