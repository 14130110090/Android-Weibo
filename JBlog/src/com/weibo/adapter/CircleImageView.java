package com.weibo.adapter;

import com.weibo.jblog.R;

import android.R.raw;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

	private Paint mPaint; // ����

	private int mRadius; // Բ��ͼƬ�İ뾶

	private float mScale; // ͼƬ�����ű���

	private Matrix matrix; // ������С�Ŵ�
	private Bitmap bitmap;
	/**
	 * ��Ⱦͼ��ʹ��ͼ��Ϊ����ͼ����ɫ
	 */
	BitmapShader bitmapShader;
	
	public CircleImageView(Context context) {
		super(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		matrix = new Matrix();
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// ��Ϊ��Բ��ͼƬ������Ӧ���ÿ�߱���һ��,��С��Ϊ׼
		int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
		mRadius = size / 2;
		setMeasuredDimension(size, size);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//����ı���Ҫ��һ�¡�����padding����Ϊ�൱�ڰ�ԭ�ȵ�Ȧ��С�ˣ�Ҫ��ѹ����������
		Drawable drawable = getDrawable();
		if (drawable != null) {
			bitmap = drawableToBitmap(drawable);
			if(bitmap==null)return;
			// ��ʼ��BitmapShader������bitmap����
			bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP,
					TileMode.CLAMP);
			// �������ű���,��ʵ���Ĵ�С���Ѿ���loadbitmapѹ���ˣ��͵�ǰ�ؼ���һ����С��
			mScale = ((mRadius) * 2.0f)
					/ Math.max(bitmap.getHeight(), bitmap.getWidth());
			matrix.setScale(mScale, mScale);
			bitmapShader.setLocalMatrix(matrix);
			mPaint.setShader(bitmapShader);
			// ��Բ�Σ�ָ�������ĵ����ꡢ�뾶������
			canvas.drawCircle(mRadius, mRadius, mRadius-getPaddingBottom(), mPaint);
		//canvas.drawRect(0, 0, mRadius*2, mRadius*2,mPaint);
		}
		
	//��null�Ļ�����background���������Ͳ��������滭��
	}

	// дһ��drawbleתBitMap�ķ���
	private Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) drawable;
			return bd.getBitmap();
		}
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		Bitmap bitmap;
		// ����Ǵ�����ɫ�޴�С
		if (w <= 0 || h <= 0) {
			return null;
		} else
			bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}
}
