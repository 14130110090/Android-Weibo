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

	private Paint mPaint; // 画笔

	private int mRadius; // 圆形图片的半径

	private float mScale; // 图片的缩放比例

	private Matrix matrix; // 用于缩小放大
	private Bitmap bitmap;
	/**
	 * 渲染图像，使用图像为绘制图形着色
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
		// 因为是圆形图片，所以应该让宽高保持一致,以小的为准
		int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
		mRadius = size / 2;
		setMeasuredDimension(size, size);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//下面的比例要改一下。有了padding，因为相当于把原先的圈变小了，要把压缩比例改了
		Drawable drawable = getDrawable();
		if (drawable != null) {
			bitmap = drawableToBitmap(drawable);
			if(bitmap==null)return;
			// 初始化BitmapShader，传入bitmap对象
			bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP,
					TileMode.CLAMP);
			// 计算缩放比例,其实给的大小都已经被loadbitmap压缩了，和当前控件是一样大小的
			mScale = ((mRadius) * 2.0f)
					/ Math.max(bitmap.getHeight(), bitmap.getWidth());
			matrix.setScale(mScale, mScale);
			bitmapShader.setLocalMatrix(matrix);
			mPaint.setShader(bitmapShader);
			// 画圆形，指定好中心点坐标、半径、画笔
			canvas.drawCircle(mRadius, mRadius, mRadius-getPaddingBottom(), mPaint);
		//canvas.drawRect(0, 0, mRadius*2, mRadius*2,mPaint);
		}
		
	//是null的画面由background来决定，就不在这里面画了
	}

	// 写一个drawble转BitMap的方法
	private Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) drawable;
			return bd.getBitmap();
		}
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		Bitmap bitmap;
		// 如果是传入颜色无大小
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
