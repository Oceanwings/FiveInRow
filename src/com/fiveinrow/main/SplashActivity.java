package com.fiveinrow.main;

import java.util.Timer;
import java.util.TimerTask;
import com.fiveinrow.chessboard.ChessBoardActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;

public class SplashActivity extends Activity
{
	private static final int SPLASH_DURATION_MS = 2000; // ms

	private Timer timer;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
		this.setContentView( R.layout.activity_splash );

		this.timer = new Timer( true );
		this.timer.schedule( new TimerTask( ) {
			@Override
			public void run( )
			{
				SplashActivity.this.startApplication( );
			}
		}, SPLASH_DURATION_MS );
	}

	private void startApplication( )
	{
		// FIXME: may need to do something else.
		this.startActivity( new Intent( this, ChessBoardActivity.class ) );
		this.finish( );
	}

	protected void onDestroy( )
	{
		if ( this.timer != null )
		{
			this.timer.cancel( );
		}
		super.onDestroy( );
	}
}