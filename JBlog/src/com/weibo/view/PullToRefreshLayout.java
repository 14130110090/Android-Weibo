package com.weibo.view;

import java.util.Timer;
import java.util.TimerTask;

import com.weibo.jblog.R;

import android.content.Context;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PullToRefreshLayout extends RelativeLayout {

	public static final String TAG = "PullToRefreshLayout";
	// ��ʼ״̬
	public static final int INIT = 0;
	// �ͷ�ˢ��
	public static final int RELEASE_TO_REFRESH = 1;
	// ����ˢ��
	public static final int REFRESHING = 2;
	// �ͷż���
	public static final int RELEASE_TO_LOAD = 3;
	// ���ڼ���
	public static final int LOADING = 4;
	// �������
	public static final int DONE = 5;
	// ��ǰ״̬
	private int state = INIT;
	// ˢ�»ص��ӿ�
	private OnRefreshListener mListener;
	// ����Y���꣬��һ���¼���Y����
	private float downY, lastY;

	// �����ľ��롣ע�⣺pullDownY��pullUpY֮������һ��ֵΪ0��
	// ��Ϊ�������غ�����ˢ�²���ͬʱ����
	public float pullDownY = 0;
	// �����ľ���
	private float pullUpY = 0;

	// �ͷ�ˢ�µľ���
	private float refreshDist = 110;
	// �ͷż��صľ���
	private float loadmoreDist = 110;

	private MyTimer timer;
	// �ع��ٶ�
	public float MOVE_SPEED = 4;
	// ��һ��ִ�в���
	private boolean isLayout = false;
	// �Ƿ���ˢ�¹����н����˻�������
	private boolean isTouch = false;
	// ��ָ��������������ͷ�Ļ�������ȣ��м�������к����仯
	private float radio = 2;

	// ������ͷ��ת180�㶯��
	private RotateAnimation rotateAnimation;
	// ������ת����
	private RotateAnimation refreshingAnimation;

	// ����ͷ
	private View refreshView;
	// �����ļ�ͷ
	private View pullView;
	// ����ˢ�µ�ͼ��
	private View refreshingView;
	// ˢ�½��ͼ��
	private View refreshStateImageView;
	// ˢ�½�����ɹ���ʧ��
	private TextView refreshStateTextView;

	// ����ͷ
	private View loadmoreView;
	// �����ļ�ͷ
	private View pullUpView;
	// ���ڼ��ص�ͼ��
	private View loadingView;
	// ���ؽ��ͼ��
	private View loadStateImageView;
	// ���ؽ�����ɹ���ʧ��
	private TextView loadStateTextView;

	// ʵ����Pullable�ӿڵ�View
	private View pullableView;
	// ���˶�㴥��
	private int mEvents;
	// ������������������pull�ķ���������ӿ��ƣ����������������ֿ�����ʱû������
	private boolean canPullDown = true;
	private boolean canPullUp = true;

	/**
	 * ִ���Զ��ع���handler
	 */
	Handler updateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// �ص��ٶ�����������moveDeltaY���������
			MOVE_SPEED = (float) (4 + 10 * Math.tan(Math.PI / 2
					/ getMeasuredHeight() * (pullDownY + Math.abs(pullUpY))));
			if (!isTouch) {
				// ����ˢ�£���û�������ƵĻ�����ͣ����ʾ����ˢ��...
				if (state == REFRESHING && pullDownY <= refreshDist) {
					pullDownY = refreshDist;
					timer.cancel();
				} else if (state == LOADING && -pullUpY <= loadmoreDist) {
					pullUpY = -loadmoreDist;
					timer.cancel();
				}

			}
			if (pullDownY > 0)
				pullDownY -= MOVE_SPEED;
			else if (pullUpY < 0)
				pullUpY += MOVE_SPEED;
			// ����ͷ����ɻص�
			if (pullDownY < 0) {
				pullDownY = 0;
				pullView.clearAnimation();
				// ��������ͷʱ�п��ܻ���ˢ�£�ֻ�е�ǰ״̬��������ˢ��ʱ�Ÿı�״̬
				if (state != REFRESHING && state != LOADING)
					changeState(INIT);
				timer.cancel();
			}
			// ����ͷ����ɻص�
			if (pullUpY > 0) {
				pullUpY = 0;
				pullUpView.clearAnimation();
				// ��������ͷʱ�п��ܻ���ˢ�£�ֻ�е�ǰ״̬��������ˢ��ʱ�Ÿı�״̬
				if (state != REFRESHING && state != LOADING)
					changeState(INIT);
				timer.cancel();
			}
			// ˢ�²���,���Զ�����onLayout
			requestLayout();
		}

	};

	public void setOnRefreshListener(OnRefreshListener listener) {
		mListener = listener;
	}

	public PullToRefreshLayout(Context context) {
		super(context);
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		timer = new MyTimer(updateHandler);
		rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
				context, R.anim.reverse_anim);
		refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
				context, R.anim.rotating);
		// �������ת������
		LinearInterpolator lir = new LinearInterpolator();
		rotateAnimation.setInterpolator(lir);
		refreshingAnimation.setInterpolator(lir);
	}

	private void hide() {
		// ÿ��5������updateHandler������Ϣ����������ͷ
		timer.schedule(5);
	}

	/**
	 * ���ˢ�²�������ʾˢ�½����ע�⣺ˢ����ɺ�һ��Ҫ�����������
	 */
	/**
	 * @param refreshResult
	 *            PullToRefreshLayout.SUCCEED����ɹ���PullToRefreshLayout.FAIL����ʧ��
	 */
	public void refreshFinish(int refreshResult) {
		refreshingView.clearAnimation();
		refreshingView.setVisibility(View.GONE);
		switch (refreshResult) {
		case 0:// ˢ��ʧ��
			refreshStateImageView.setVisibility(View.VISIBLE);
			refreshStateTextView.setText("ˢ��ʧ��");
			refreshStateImageView
					.setBackgroundResource(R.drawable.refresh_failed);
			break;
		case 1:
		default:
			// ˢ�³ɹ�
			refreshStateImageView.setVisibility(View.VISIBLE);
			refreshStateTextView.setText("ˢ�³ɹ�");
			refreshStateImageView
					.setBackgroundResource(R.drawable.refresh_succeed);
			break;
		}
		// ˢ�½��ͣ��1��
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				changeState(DONE);
				hide();
			}
		}.sendEmptyMessageDelayed(0, 1000);
	}

	/**
	 * ������ϣ���ʾ���ؽ����ע�⣺������ɺ�һ��Ҫ�����������
	 * 
	 * @param refreshResult
	 *            PullToRefreshLayout.SUCCEED����ɹ���PullToRefreshLayout.FAIL����ʧ��
	 */
	public void loadmoreFinish(int refreshResult) {
		loadingView.clearAnimation();
		loadingView.setVisibility(View.GONE);
		switch (refreshResult) {
		case 0:// ����ʧ��
			loadStateImageView.setVisibility(View.VISIBLE);
			loadStateTextView.setText("����ʧ��");
			loadStateImageView.setBackgroundResource(R.drawable.load_failed);
			break;
		case 1:
		default:
			// ���سɹ�
			loadStateImageView.setVisibility(View.VISIBLE);
			loadStateTextView.setText("���سɹ�");
			loadStateImageView.setBackgroundResource(R.drawable.load_succeed);
			break;
		}
		// ˢ�½��ͣ��1��
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				changeState(DONE);
				hide();
			}
		}.sendEmptyMessageDelayed(0, 1000);
	}

	private void changeState(int to) {
		state = to;
		switch (state) {
		// ˢ�����״̬��ת����ʼ״̬,�����ͷ�ˢ��Ҳ���ܻᵽ��ʼ״̬
		case INIT:
			// �������ֳ�ʼ״̬
			refreshStateImageView.setVisibility(View.INVISIBLE);
			refreshStateTextView.setText("����ˢ��");
			pullView.clearAnimation();
			pullView.setVisibility(View.VISIBLE);
			// �������ֳ�ʼ״̬
			loadStateImageView.setVisibility(View.INVISIBLE);
			loadStateTextView.setText("��������");
			pullUpView.clearAnimation();
			pullUpView.setVisibility(View.VISIBLE);
			break;
		case RELEASE_TO_REFRESH:
			// �ͷ�ˢ��״̬
			refreshStateTextView.setText("�ͷ�ˢ��");
			pullView.startAnimation(rotateAnimation);
			break;
		case REFRESHING:
			// ����ˢ��״̬
			pullView.clearAnimation();
			refreshingView.setVisibility(View.VISIBLE);
			pullView.setVisibility(View.INVISIBLE);
			refreshingView.startAnimation(refreshingAnimation);
			refreshStateTextView.setText("����ˢ��");
			break;
		case RELEASE_TO_LOAD:
			// �ͷż���״̬
			loadStateTextView.setText("�ͷż���");
			pullUpView.startAnimation(rotateAnimation);
			break;
		case LOADING:
			// ���ڼ���״̬
			pullUpView.clearAnimation();
			loadingView.setVisibility(View.VISIBLE);
			pullUpView.setVisibility(View.INVISIBLE);
			loadingView.startAnimation(refreshingAnimation);
			loadStateTextView.setText("���ڼ���");
			break;
		case DONE:
			// ˢ�»������ϣ�ɶ������
			break;
		}
	}

	/**
	 * ����������������
	 */
	private void releasePull() {
		canPullDown = true;
		canPullUp = true;
	}

	/*
	 * �ɸ��ؼ������Ƿ�ַ��¼�����ֹ�¼���ͻ
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			downY = ev.getY();
			lastY = downY;
			timer.cancel();
			mEvents = 0;
			releasePull();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_POINTER_UP:
			// ���˶�㴥��
			mEvents = -1;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mEvents == 0) {
				if (((Pullable) pullableView).canPullDown() && canPullDown
						&& state != LOADING) {
					// �������������ڼ���ʱ��������
					// ��ʵ�ʻ�����������С������������ĸо�
					pullDownY = pullDownY + (ev.getY() - lastY) / radio;
					if (pullDownY < 0) {
						pullDownY = 0;
						requestLayout();
						canPullDown = false;
						canPullUp = true;
					}
					if (pullDownY > getMeasuredHeight())
						pullDownY = getMeasuredHeight();
					if (state == REFRESHING) {
						// ����ˢ�µ�ʱ�����ƶ�
						isTouch = true;
					}
				} else if (((Pullable) pullableView).canPullUp() && canPullUp
						&& state != REFRESHING) {
					// ��������������ˢ��ʱ��������
					pullUpY = pullUpY + (ev.getY() - lastY) / radio;
					if (pullUpY > 0) {
						pullUpY = 0;
						// �Է�����̫�죬pullUpY�Ӹ�ֵ(��ˢ��)ֱ�ӱ䵽��ֵ(��ˢ�£���Ϊ��������0��)��
						// ����ˢ�²��ˣ�������Ϊmove�¼��н���Ƶ�ʣ���һ��move�¼�ʱ��pullUPYΪ
						// ���ģ�������һ��move�¼�ʱ���ڻ����죬������ġ��������������ˢ��һ��
						requestLayout();
						canPullDown = true;
						canPullUp = false;
					}
					if (pullUpY < -getMeasuredHeight())
						pullUpY = -getMeasuredHeight();
					if (state == LOADING) {
						// ���ڼ��ص�ʱ�����ƶ�
						isTouch = true;
					}
				} else
					releasePull();
			} else
				mEvents = 0;
			lastY = ev.getY();
			// ������������ı����
			radio = (float) (2 + 4 * Math.tan(Math.PI / 2 / getMeasuredHeight()
					* (pullDownY + Math.abs(pullUpY))));
			// ��ֹmovelistviewʱ����ˢ�²���
			if (pullDownY != 0 || pullUpY != 0) {
				requestLayout();
			}
			if (pullDownY <= refreshDist && state == RELEASE_TO_REFRESH) {
				// �����������û�ﵽˢ�µľ����ҵ�ǰ״̬���ͷ�ˢ�£��ı�״̬Ϊ����ˢ��
				changeState(INIT);
			}
			if (pullDownY >= refreshDist && state == INIT) {
				// �����������ﵽˢ�µľ����ҵ�ǰ״̬�ǳ�ʼ״̬ˢ�£��ı�״̬Ϊ�ͷ�ˢ��
				changeState(RELEASE_TO_REFRESH);
			}
			// �������ж��������صģ�ͬ�ϣ�ע��pullUpY�Ǹ�ֵ
			if (-pullUpY <= loadmoreDist && state == RELEASE_TO_LOAD) {
				changeState(INIT);
			}
			if (-pullUpY >= loadmoreDist && state == INIT) {
				changeState(RELEASE_TO_LOAD);
			}
			// ��Ϊˢ�ºͼ��ز�������ͬʱ���У�����pullDownY��pullUpY��������һ��Ϊ0�����������(pullDownY +
			// Math.abs(pullUpY))�Ϳ��Բ��Ե�ǰ״̬��������
			if ((pullDownY + Math.abs(pullUpY)) > 8) {
				// ��ֹ�����������󴥷������¼��͵���¼�
				ev.setAction(MotionEvent.ACTION_CANCEL);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (pullDownY > refreshDist || -pullUpY > loadmoreDist)
				// ����ˢ��ʱ�����������ڼ���ʱ�����������ͷź�����ͷ������ͷ��������
				isTouch = false;
			if (state == RELEASE_TO_REFRESH) {
				changeState(REFRESHING);
				// ˢ�²���
				if (mListener != null) {
					mListener.onRefresh();
				}
			} else if (state == RELEASE_TO_LOAD) {
				changeState(LOADING);
				// ���ز���
				if (mListener != null)
					mListener.onLoadMore();
			}
			// ��ֹ�ڻ���listviewʱҲ����ˢ�²���
			if (pullDownY != 0 || pullUpY != 0)
				hide();
		default:
			break;
		}
		// �¼��ַ���������
		super.dispatchTouchEvent(ev);
		return true;
	}

	private void initView() {
		// ��ʼ����������
		pullView = refreshView.findViewById(R.id.pull_icon);
		refreshStateTextView = (TextView) refreshView
				.findViewById(R.id.state_tv);
		refreshingView = refreshView.findViewById(R.id.refreshing_icon);
		refreshStateImageView = refreshView.findViewById(R.id.state_iv);
		// ��ʼ����������
		pullUpView = loadmoreView.findViewById(R.id.pullup_icon);
		loadStateTextView = (TextView) loadmoreView
				.findViewById(R.id.loadstate_tv);
		loadingView = loadmoreView.findViewById(R.id.loading_icon);
		loadStateImageView = loadmoreView.findViewById(R.id.loadstate_iv);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!isLayout) {
			// �����ǵ�һ�ν�����ʱ����һЩ��ʼ��
			refreshView = getChildAt(0);
			pullableView = getChildAt(1);
			loadmoreView = getChildAt(2);
			isLayout = true;
			initView();
		}

		// �ı��ӿؼ��Ĳ��֣�����ֱ����(pullDownY + pullUpY)��Ϊƫ�����������Ϳ��Բ��Ե�ǰ״̬������
		// �����getMeasuredHeight���ǵ�ǰrelativelayout�Ĳ��ֵĿ��ø߶�
		refreshView.layout(0,
				(int) (pullDownY + pullUpY) - refreshView.getMeasuredHeight(),
				refreshView.getMeasuredWidth(), (int) (pullDownY + pullUpY));
		pullableView.layout(0, (int) (pullDownY + pullUpY),
				pullableView.getMeasuredWidth(), (int) (pullDownY + pullUpY)
						+ pullableView.getMeasuredHeight());
		loadmoreView.layout(0,
				(int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight(),
				loadmoreView.getMeasuredWidth(),
				(int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight()
						+ loadmoreView.getMeasuredHeight());
	}

	class MyTimer {
		private Handler handler;
		private Timer timer;
		private MyTask mTask;

		public MyTimer(Handler handler) {
			this.handler = handler;
			timer = new Timer();
		}

		public void schedule(long period) {
			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
			mTask = new MyTask(handler);
			timer.schedule(mTask, 0, period);
		}

		public void cancel() {
			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
		}

		class MyTask extends TimerTask {
			private Handler handler;

			public MyTask(Handler handler) {
				this.handler = handler;
			}

			@Override
			public void run() {
				handler.obtainMessage().sendToTarget();
			}

		}
	}

	/**
	 * ˢ�¼��ػص��ӿ�
	 * 
	 * @author chenjing
	 * 
	 */
	public interface OnRefreshListener {
		/**
		 * ˢ�²���
		 */
		void onRefresh();

		/**
		 * ���ز���
		 */
		void onLoadMore();
	}

	public interface Pullable {
		/**
		 * �ж��Ƿ�����������������Ҫ�������ܿ���ֱ��return false
		 * 
		 * @return ���������������true�����򷵻�false
		 */
		boolean canPullDown();

		/**
		 * �ж��Ƿ�����������������Ҫ�������ܿ���ֱ��return false
		 * 
		 * @return ���������������true�����򷵻�false
		 */
		boolean canPullUp();
	}

	private class AutoRefreshAndLoadTask extends
			AsyncTask<Integer, Float, String> {

		@Override
		protected String doInBackground(Integer... params) {

			pullDownY = refreshDist + 1;
			// ����Ҫ�ȵ�onlayout����֮��Ҳ���������沼����ſ���ˢ��
			publishProgress(pullDownY);

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			changeState(REFRESHING);
			// ˢ��
			if (mListener != null)
				mListener.onRefresh();

			hide();
		}

		// ��ȻҲ�������߳�ִ�У�������Ҫ�����̵߳�ui������Ż�ִ�����е����ݣ������ڸ÷�������֮ǰ��onlayout�ͻᱻִ��
		@Override
		protected void onProgressUpdate(Float... values) {
			changeState(REFRESHING);
			// changeState(RELEASE_TO_REFRESH);
			// requestLayout();

		}

	}

	/**
	 * �Զ�ˢ��
	 */
	public void autoRefresh() {
//		AutoRefreshAndLoadTask task = new AutoRefreshAndLoadTask();
//		task.execute();
		
		changeState(REFRESHING);
		pullDownY = refreshDist + 1;
		if (mListener != null)
			mListener.onRefresh();

		hide();
	}

	/**
	 * �Զ����أ�����ѡ��
	 */
	public void autoLoad() {
		pullUpY = -loadmoreDist;
		requestLayout();
		changeState(LOADING);
		// 加载操作
		if (mListener != null)
			mListener.onLoadMore();
	}

}
