package ryan.view;

import java.util.HashSet;
import java.util.Iterator;

import ryan.activity.R;
import ryan.entity.Ball;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class BallView extends View implements OnTouchListener {
	// 一个HashSet(无法重复元素)，存放要用来添加的小球
	private HashSet<Ball> basket;
	// 上下文对象，即将要使用此视图的对象
	private Context context;
	private float mX;
	private float mY;
	private Ball drag;

	public BallView(Context context) {
		super(context);
		this.context = context;
		basket = new HashSet<Ball>();
		setOnTouchListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 绘制logo
		canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
				R.drawable.title), 0, 0, null);
		for (Iterator<Ball> it = basket.iterator(); it.hasNext();) {
			Ball ball = it.next();
			// 解码成Bitmap
			Bitmap bitmap = BitmapFactory.decodeResource(
					context.getResources(), ball.drawableId);
			// 图片50*50
			
			canvas.drawBitmap(bitmap, ball.x, ball.y, null);

		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mX = event.getX();
		mY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 若为按下鼠标，判断拖拽点在哪个球的上面
			getDragBall(mX, mY);
			break;
		case MotionEvent.ACTION_MOVE:
			// 如果超出了界限的话，不进行下面的操作

			if (mX >= (getWidth() - 50) || mY >= (getHeight() - 50)) {
				break;
			}
			// 设置被拖动球的位置
			if (drag != null) {
				drag.x = mX;
				drag.y = mY;
			}
			// 重绘
			break;
		case MotionEvent.ACTION_UP:
			// 不在按下状态时，清空drag对象
			drag = null;
			break;

		}
		invalidate();
		((Activity) context).setTitle("DragTheBall:x:"
				+ (float) (Math.round(mX * 100)) / 100 + " y:"
				+ (float) (Math.round(mY * 100)) / 100);
		return true;
	}

	public boolean addComponent(Ball ball) {
		// true when this HashSet did not already contain the object, false
		// otherwise
		return basket.add(ball);
	}

	public void getDragBall(float x, float y) {
		for (Iterator<Ball> it = basket.iterator(); it.hasNext();) {

			Ball ball = it.next();
			// 如果拖拽点在球的区域内，返回该球
			if (x > ball.x && y > ball.y && x < (ball.x + 50)
					&& y < (ball.y + 50)) {
				this.drag = ball;
				// 震动提示用户已经触摸到球
				try {
					Vibrator vibrator = (Vibrator) context
							.getApplicationContext().getSystemService(
									Service.VIBRATOR_SERVICE);

					//vibrator.vibrate(new long[] { 100, 10, 100, 1000 }, -1);
					vibrator.vibrate(50);
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		}
	}
}