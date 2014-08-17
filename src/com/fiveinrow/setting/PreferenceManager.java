package com.fiveinrow.setting;

import com.localstorage.database.DataManager;

public final class PreferenceManager
{
	private static final PreferenceManager instance = new PreferenceManager( );

	public static PreferenceManager getInstance( )
	{
		return instance;
	}

	private static final String KEY_PREFERENCE = "KEY_PREFERENCE";

	private Preference preference;

	private PreferenceManager( )
	{
		this.init( );
	}

	private void init( )
	{
		this.preference = load( );
		if ( this.preference == null )
		{
			this.preference = new Preference( );
			this.save( );
		}
	}

	public boolean isHumanFirst( )
	{
		return ( this.preference != null ) ? this.preference.isHumanFirst( ) : false;
	}

	public boolean isHintEnabled( )
	{
		return ( this.preference != null ) ? this.preference.isHintEnabled( ) : false;
	}

	public boolean isBanEnabled( )
	{
		return ( this.preference != null ) ? this.preference.isBanEnabled( ) : false;
	}

	public Preference.ChessMode getMode( )
	{
		return ( this.preference != null ) ? this.preference.getMode( )
				: Preference.ChessMode.MODE_VS_COMPUTER;
	}

	public void update( final Preference.ChessMode mode, final boolean humanFirst,
			final boolean enableHint, final boolean enableBan )
	{
		if ( this.preference == null )
		{
			this.preference = new Preference( );
		}
		this.preference.setHumanFirst( humanFirst );
		this.preference.setEnableHint( enableHint );
		this.preference.setEnablBan( enableBan );
		this.preference.setMode( mode );
		this.save( );
	}

	public void save( )
	{
		save( this.preference );
	}

	private static Preference load( )
	{
		return Preference.fromByte( ( Byte ) DataManager.getInstance( ).get( KEY_PREFERENCE ) );
	}

	private static void save( Preference preference )
	{
		DataManager.getInstance( ).put( KEY_PREFERENCE, preference.toByte( ) );
	}
}