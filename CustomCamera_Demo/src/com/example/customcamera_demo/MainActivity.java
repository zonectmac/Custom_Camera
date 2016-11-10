package com.example.customcamera_demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity {
	private ImageView imageView1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ca.class);
				startActivityForResult(intent, 1);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {// 如果相机拍照成功。返回的结果必RESULT_OK
			switch (requestCode) {
			case 1://
				Bundle extras = data.getExtras();
				byte[] bytes = extras.getByteArray("bytes");

				setImageBitmap(bytes);
				break;

			}

		}
	}

	/**
	 * 将MainActivity传过来的图片显示在界面当中
	 * 
	 * @param bytes
	 */
	public void setImageBitmap(byte[] bytes) {
		Bitmap cameraBitmap = BitmapFactory.decodeByteArray(bytes, 0,
				bytes.length);
		;
		// 根据拍摄的方向旋转图像（纵向拍摄时要需要将图像选择90度)
		Matrix matrix = new Matrix();
		matrix.setRotate(ca.getPreviewDegree(this));
		cameraBitmap = Bitmap
				.createBitmap(cameraBitmap, 0, 0, cameraBitmap.getWidth(),
						cameraBitmap.getHeight(), matrix, true);
		imageView1.setImageBitmap(cameraBitmap);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
