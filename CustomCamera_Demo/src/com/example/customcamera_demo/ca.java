package com.example.customcamera_demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

public class ca extends Activity implements OnClickListener {

	private Camera camera;
	private Camera.Parameters parameters = null;
	Bundle bundle = null; // ����һ��Bundle���������洢����
	Intent intent = null;
	private boolean af;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ��ʾ����
		setContentView(R.layout.activity_picture);
		findViewById(R.id.btn_take_picture).setOnClickListener(this);
		findViewById(R.id.btn_take_cancle).setOnClickListener(this);
		SurfaceView surfaceView = (SurfaceView) this
				.findViewById(R.id.surfaceView_picture);
		surfaceView.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		surfaceView.getHolder().setKeepScreenOn(true);// ��Ļ����
		surfaceView.getHolder().addCallback(new SurfaceCallback());// ΪSurfaceView�ľ�����һ���ص�����
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {// ��Ļ�����¼�
		if (event.getAction() == MotionEvent.ACTION_DOWN) {// ����ʱ�Զ��Խ�
			camera.autoFocus(null);
			af = true;
		}

		return true;
	}

	private final class SurfaceCallback implements Callback {

		// ����״̬�仯ʱ���ø÷���
		@SuppressWarnings("deprecation")
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			parameters = camera.getParameters(); // ��ȡ�������
			parameters.setPictureFormat(PixelFormat.JPEG); // ����ͼƬ��ʽ
			parameters.setPreviewSize(width, height); // ����Ԥ����С
			parameters.setPreviewFrameRate(5); // ����ÿ����ʾ4֡
			parameters.setPictureSize(width, height); // ���ñ����ͼƬ�ߴ�
			parameters.setJpegQuality(100); // ������Ƭ����
		}

		// ��ʼ����ʱ���ø÷���
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera = Camera.open(); // ������ͷ
				camera.setPreviewDisplay(holder); // ����������ʾ����Ӱ���SurfaceHolder����
				camera.setDisplayOrientation(getPreviewDegree(ca.this));
				camera.startPreview(); // ��ʼԤ��
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// ֹͣ����ʱ���ø÷���
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				camera.release(); // �ͷ������
				camera = null;
			}
		}
	}

	// �ṩһ����̬���������ڸ����ֻ����������Ԥ��������ת�ĽǶ�
	public static int getPreviewDegree(Activity activity) {
		// ����ֻ��ķ���
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degree = 0;
		// �����ֻ��ķ���������Ԥ������Ӧ��ѡ��ĽǶ�
		switch (rotation) {
		case Surface.ROTATION_0:
			degree = 90;
			break;
		case Surface.ROTATION_90:
			degree = 0;
			break;
		case Surface.ROTATION_180:
			degree = 270;
			break;
		case Surface.ROTATION_270:
			degree = 180;
			break;
		}
		return degree;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_take_cancle:
			ca.this.finish();
			break;
		case R.id.btn_take_picture:
			if (camera != null) {
				// camera.takePicture(null, null, new MyPictureCallback());
				// ��������ͷ�Զ��Խ��������
				camera.autoFocus(autoFocusCallback); //
			}
			break;

		default:
			break;
		}

	}

	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
		// ���Զ��Խ�ʱ�����÷���
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {
				// takePicture()������Ҫ����3������������
				// ��1�������������û����¿���ʱ�����ü�����
				// ��2�����������������ȡԭʼ��Ƭʱ�����ü�����
				// ��3�����������������ȡJPG��Ƭʱ�����ü�����
				camera.takePicture(new ShutterCallback() {
					public void onShutter() {
						// ���¿���˲���ִ�д˴�����
					}
				}, new PictureCallback() {
					public void onPictureTaken(byte[] data, Camera c) {
						// �˴�������Ծ����Ƿ���Ҫ����ԭʼ��Ƭ��Ϣ
					}
				}, new MyPictureCallback()); // ��
			}
		}
	};

	private final class MyPictureCallback implements PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				intent = new Intent();
				bundle = new Bundle();
				bundle.putByteArray("bytes", data); // ��ͼƬ�ֽ����ݱ�����bundle���У�ʵ�����ݽ���
				intent.putExtras(bundle);
				saveToSDCard(data); // ����ͼƬ��sd����
				Toast.makeText(getApplicationContext(), "chdnhdhhd",
						Toast.LENGTH_SHORT).show();
				// camera.startPreview(); // �����պ����¿�ʼԤ��
				setResult(RESULT_OK, intent);
				ca.this.finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ������������Ƭ�����SD����
	 * 
	 * @param data
	 * @throws IOException
	 */
	public static void saveToSDCard(byte[] data) throws IOException {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // ��ʽ��ʱ��
		String filename = format.format(date) + ".jpg";
		File fileFolder = new File(Environment.getExternalStorageDirectory()
				+ "/finger/");
		if (!fileFolder.exists()) { // ���Ŀ¼�����ڣ��򴴽�һ����Ϊ"finger"��Ŀ¼
			fileFolder.mkdir();
		}
		File jpgFile = new File(fileFolder, filename);
		FileOutputStream outputStream = new FileOutputStream(jpgFile); // �ļ������
		outputStream.write(data); // д��sd����
		outputStream.close(); // �ر������
	}

}
