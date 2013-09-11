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
 * �`��p�̃N���X�B
 */
public class MyView extends View {

	/** �`��p�̃��b�Z�[�W��ێ����� */
	private String str = "�J�n";
	/** �`��p�̉摜��ێ����� */
	private Bitmap mBitmap;
	/** �^�b�`�ړ����̉摜��ێ����� */
	private Bitmap bMove;
	/** �^�b�`�A�b�v���̉摜��ێ����� */
	private Bitmap bUp;
	/** �^�b�`�_�E�����̉摜��ێ����� */
	private Bitmap bDown;
	/** �^�b�`����ێ����� */
	private int count;
	/** �`��X�^�C��/�F��ێ����� */
	private Paint mPaint;
	/** �^�b�`���W��ێ����� */
	private ArrayList<MyPoint> points = new ArrayList<MyPoint>();
	/** Move����index���o�p�ɑO��̃^�b�`���W��ێ����� */
	private ArrayList<MyPoint> oldpoints = new ArrayList<MyPoint>();
	/** �ǂ̃^�b�`�|�C���g�ɑ΂���C�x���g�ł��邩��ێ����� */
	private ArrayList<Integer> index = new ArrayList<Integer>();

	/**
	 * �R���X�g���N�^�B<br>
	 * Paint�Ɖ摜�̏��������s���B
	 * 
	 * @param context
	 *            �R���e�L�X�g
	 */
	public MyView(Context context) {
		super(context);
		// setFocusable( true );
		// setFocusableInTouchMode( true );

		// �`��X�^�C���E�F�̐ݒ�
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setARGB(255, 255, 255, 100);
		mPaint.setTextSize(48);

		// Resource�C���X�^���X�̐���
		Resources res = this.getContext().getResources();
		// �摜�̓ǂݍ���(res/drawable/xxxx.png)
		bMove = BitmapFactory.decodeResource(res, R.drawable.move);
		bUp = BitmapFactory.decodeResource(res, R.drawable.up);
		bDown = BitmapFactory.decodeResource(res, R.drawable.down);

	}

	/**
	 * �`�揈���B
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// �w�i�F��ݒ�
		canvas.drawColor(Color.BLUE);

		// �^�b�`�������^�b�`���W��Bitmap��`�悷��
		for (MyPoint point : points) {
			canvas.drawBitmap(mBitmap, point.x, point.y, mPaint);
			canvas.drawText("index:" + String.valueOf(point.index),
					point.x + 100, point.y - 50, mPaint);
			canvas.drawText("id:" + String.valueOf(point.id), point.x + 100,
					point.y, mPaint);
		}
		// �^�b�`�C�x���g�^�C�v��\��
		canvas.drawText(str, 20, 50, mPaint);
		// �^�b�`����\��
		canvas.drawText("count:" + count, 20, 100, mPaint);
		// �ǂ̃^�b�`�|�C���g�ɑ΂���C�x���g�ł��邩��\��
		canvas.drawText("index:" + index, 20, 150, mPaint);
	}

	/**
	 * �^�b�`���̓���B
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// �^�b�`�����擾����
		count = event.getPointerCount();

		// �^�b�`���W�����������Ă���V���Ɏ擾����
		oldpoints.clear();
		oldpoints.addAll(points);
		index.clear();
		// �C�x���g�^�C�v���擾����
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		// �ǂ̃^�b�`�|�C���g�ɑ΂���C�x���g�����擾����
		// index = event.getActionIndex(); // API8
		index.add((event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT); // API8�ȑO�ł�����
		switch (action) {
		case MotionEvent.ACTION_DOWN: // �V���O���^�b�`��
			str = "ACTION_DOWN";
			mBitmap = bDown;
			addPoint(event, index.get(0));
			break;
		case MotionEvent.ACTION_POINTER_DOWN: // �}���`�^�b�`��
			str = "ACTION_POINTER_DOWN";
			mBitmap = bDown;
			addPoint(event, index.get(0));
			break;
		case MotionEvent.ACTION_UP: // �V���O���^�b�`��
			str = "ACTION_UP";
			mBitmap = bUp;
			removePoint(event, index.get(0));
			break;
		case MotionEvent.ACTION_POINTER_UP: // �}���`�^�b�`��
			str = "ACTION_POINTER_UP";
			mBitmap = bUp;
			removePoint(event, index.get(0));
			break;
		case MotionEvent.ACTION_MOVE: // �V���O���^�b�`�A�}���`�^�b�`����
			// Move�̓}���`�|�C���g�ړ���1�C�x���g�Ŏ擾���Ă���B�܂�A2�{�w�𓯎��ړ�������ƁA2�̃|�C���g�̒l�������ɕς���Ď擾�ł���
			str = "ACTION_MOVE";
			mBitmap = bMove;
			movePoint(event);
			break;
		default:
			break;
		}

		// �ĕ`��̎w��
		invalidate();
		return true;
	}

	/**
	 * �^�b�`�|�C���g��ۑ�����B
	 * 
	 * @param event
	 *            ���[�V�����C�x���g
	 * @param index
	 *            �C���f�b�N�X
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
	 * �^�b�`�ړ����̃^�b�`���W���擾����B<br>
	 * �^�b�`�ړ����͕����̒l�������ɕύX����Ă��邽�߁A�ЂƂ�index�ɓ���͂ł��Ȃ��B
	 * 
	 * @param event
	 *            ���[�V�����C�x���g
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
	 * �^�b�`�A�b�v���Ƀ^�b�`�|�C���g���폜����B
	 * 
	 * @param event
	 *            ���[�V�����C�x���g
	 * @param index
	 *            �C���f�b�N�X
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
		// �^�b�`�A�b�v�����u�Ԃ�index���C������
		for (int i = index; i < points.size(); i++) {
			points.get(i).index--;
		}
	}
}
