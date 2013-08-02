package com.example.touchsample003;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		// クラスのインスタンスを生成
		MyView mView = new MyView( this );
		// Viewに設定
		setContentView( mView );
	}
}
