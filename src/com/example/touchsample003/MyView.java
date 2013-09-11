/**
 * 
 */
package com.example.touchsample003;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

/**
 * 描画用のクラス。
 */
public class MyView extends View {

	/** 描画用のメッセージを保持する */
	private String str = "開始";
	/** 描画用の画像を保持する */
	private Bitmap mBitmap;
	/** タッチ移動時の画像を保持する */
	private Bitmap bMove;
	/** タッチアップ時の画像を保持する */
	private Bitmap bUp;
	/** タッチダウン時の画像を保持する */
	private Bitmap bDown;
	/** タッチ数を保持する */
	private int count;
	/** 描画スタイル/色を保持する */
	private Paint mPaint;
	/** タッチ座標を保持する */
	private ArrayList<MyPoint> points = new ArrayList<MyPoint>();
	/** Move時のindex検出用に前回のタッチ座標を保持する */
	private ArrayList<MyPoint> oldpoints = new ArrayList<MyPoint>();
	/** どのタッチポイントに対するイベントであるかを保持する */
	private ArrayList<Integer> index = new ArrayList<Integer>();

	/**
	 * コンストラクタ。<br>
	 * Paintと画像の初期化を行う。
	 * 
	 * @param context
	 *            コンテキスト
	 */
	public MyView(Context context) {
		super(context);
		// setFocusable( true );
		// setFocusableInTouchMode( true );

		// 描画スタイル・色の設定
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setARGB(255, 255, 255, 100);
		mPaint.setTextSize(48);

		// Resourceインスタンスの生成
		Resources res = this.getContext().getResources();
		// 画像の読み込み(res/drawable/xxxx.png)
		bMove = BitmapFactory.decodeResource(res, R.drawable.move);
		bUp = BitmapFactory.decodeResource(res, R.drawable.up);
		bDown = BitmapFactory.decodeResource(res, R.drawable.down);

	}

	/**
	 * 描画処理。
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 背景色を設定
		canvas.drawColor(Color.BLUE);

		// タッチ数だけタッチ座標にBitmapを描画する
		for (MyPoint point : points) {
			canvas.drawBitmap(mBitmap, point.x, point.y, mPaint);
			canvas.drawText("index:" + String.valueOf(point.index),
					point.x + 100, point.y - 50, mPaint);
			canvas.drawText("id:" + String.valueOf(point.id), point.x + 100,
					point.y, mPaint);
		}
		// タッチイベントタイプを表示
		canvas.drawText(str, 20, 50, mPaint);
		// タッチ数を表示
		canvas.drawText("count:" + count, 20, 100, mPaint);
		// どのタッチポイントに対するイベントであるかを表示
		canvas.drawText("index:" + index, 20, 150, mPaint);
	}

	/**
	 * タッチ時の動作。
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// タッチ数を取得する
		count = event.getPointerCount();

		// タッチ座標を初期化してから新たに取得する
		oldpoints.clear();
		oldpoints.addAll(points);
		index.clear();
		// イベントタイプを取得する
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		// どのタッチポイントに対するイベントかを取得する
		// index = event.getActionIndex(); // API8
		index.add((event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT); // API8以前でもおｋ
		switch (action) {
		case MotionEvent.ACTION_DOWN: // シングルタッチ時
			str = "ACTION_DOWN";
			mBitmap = bDown;
			addPoint(event, index.get(0));
			break;
		case MotionEvent.ACTION_POINTER_DOWN: // マルチタッチ時
			str = "ACTION_POINTER_DOWN";
			mBitmap = bDown;
			addPoint(event, index.get(0));
			break;
		case MotionEvent.ACTION_UP: // シングルタッチ時
			str = "ACTION_UP";
			mBitmap = bUp;
			removePoint(event, index.get(0));
			break;
		case MotionEvent.ACTION_POINTER_UP: // マルチタッチ時
			str = "ACTION_POINTER_UP";
			mBitmap = bUp;
			removePoint(event, index.get(0));
			break;
		case MotionEvent.ACTION_MOVE: // シングルタッチ、マルチタッチ共通
			// Moveはマルチポイント移動を1イベントで取得してくる。つまり、2本指を同時移動させると、2つのポイントの値が同時に変わって取得できる
			str = "ACTION_MOVE";
			mBitmap = bMove;
			movePoint(event);
			break;
		default:
			break;
		}

		// 再描画の指示
		invalidate();
		return true;
	}

	/**
	 * タッチポイントを保存する。
	 * 
	 * @param event
	 *            モーションイベント
	 * @param index
	 *            インデックス
	 */
	private void addPoint(MotionEvent event, int index) {
		MyPoint point = new MyPoint();
		point.index = index;
		point.id = event.getPointerId(index);
		point.x = (int) event.getX(index);
		point.y = (int) event.getY(index);
		points.add(point);
	}

	/**
	 * タッチ移動時のタッチ座標を取得する。<br>
	 * タッチ移動時は複数の値が同時に変更されてくるため、ひとつのindexに特定はできない。
	 * 
	 * @param event
	 *            モーションイベント
	 */
	private void movePoint(MotionEvent event) {
		points.clear();
		index.clear();
		for (int i = 0; i < count; i++) {
			MyPoint point = new MyPoint();
			point.index = i;
			point.id = event.getPointerId(i);
			point.x = (int) event.getX(i);
			point.y = (int) event.getY(i);
			points.add(point);
			for (MyPoint p : oldpoints) {
				if (point.id == p.id) {
					if (point.x != p.x || point.y != p.y) {
						index.add(i);
					}
				}
			}
		}
	}

	/**
	 * タッチアップ時にタッチポイントを削除する。
	 * 
	 * @param event
	 *            モーションイベント
	 * @param index
	 *            インデックス
	 */
	private void removePoint(MotionEvent event, int index) {
		MyPoint point = new MyPoint();
		point.index = index;
		point.id = event.getPointerId(index);
		point.x = (int) event.getX(index);
		point.y = (int) event.getY(index);
		for (int i = 0; i < points.size(); i++) {
			if (point.id == points.get(i).id) {
				points.remove(i);
				break;
			}
		}
		// タッチアップした瞬間にindexを修正する
		for (int i = index; i < points.size(); i++) {
			points.get(i).index--;
		}
	}
}
