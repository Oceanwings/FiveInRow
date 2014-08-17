package com.fiveinrow.main;

import com.fiveinrow.setting.PreferenceManager;
import com.localstorage.database.DataManager;
import android.app.Application;

public final class FiveInRowApplication extends Application
{
	private static FiveInRowApplication applaction;

	public static FiveInRowApplication getInstance( )
	{
		return applaction;
	}

	public void onCreate( )
	{
		super.onCreate( );
		applaction = this;
		this.init( );
	}

	private void init( )
	{
		DataManager.getInstance( ).init( applaction );
		PreferenceManager.getInstance( );
	}
}