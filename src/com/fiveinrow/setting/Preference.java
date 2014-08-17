package com.fiveinrow.setting;

public final class Preference
{
	public static enum ChessMode {
		MODE_VS_COMPUTER, MODE_VS_HUMAN, MODE_INVESTIGATION
	};

	private static final byte MASK_MODE_VS_COM 	= 0x01;
	private static final byte MASK_MODE_VS_HUM 	= 0x02;
	private static final byte MASK_MODE_VS_INV 	= 0x04;
	private static final byte MASK_MODE 		= MASK_MODE_VS_COM | MASK_MODE_VS_HUM | MASK_MODE_VS_INV;
	private static final byte MASK_HINT 		= 0x10;
	private static final byte MASK_FIRST 		= 0x20;
	private static final byte MASK_FORBIDDEN 	= 0x40;

	private boolean humanFirst = true;
	private boolean enableHint = true;
	private boolean enableBan = false;
	private ChessMode mode = ChessMode.MODE_VS_COMPUTER;

	public Preference( )
	{}

	public Preference( boolean humanFirst, boolean enableHint, ChessMode mode )
	{
		this.setHumanFirst( humanFirst );
		this.setEnableHint( enableHint );
		this.setMode( mode );
	}

	public boolean isHumanFirst( )
	{
		return this.humanFirst;
	}

	public boolean isHintEnabled( )
	{
		return this.enableHint;
	}

	public boolean isBanEnabled( )
	{
		return this.enableBan;
	}

	public ChessMode getMode( )
	{
		return this.mode;
	}

	public void setHumanFirst( boolean humanFirst )
	{
		this.humanFirst = humanFirst;
	}

	public void setEnableHint( boolean enableHint )
	{
		this.enableHint = enableHint;
	}

	public void setEnablBan( boolean enableBan )
	{
		this.enableBan = enableBan;
	}

	public void setMode( ChessMode mode )
	{
		this.mode = ( mode != null ) ? mode : this.mode;
	}

	public Byte toByte( )
	{
		return toByte( this );
	}

	private static Byte toByte( Preference preference )
	{
		if ( preference == null )
		{
			return new Byte( ( byte ) 0 );
		}

		byte result = 0;
		if ( preference.isHumanFirst( ) )
		{
			result |= MASK_FIRST;
		}

		if ( preference.isHintEnabled( ) )
		{
			result |= MASK_HINT;
		}

		if ( preference.isBanEnabled( ) )
		{
			result |= MASK_FORBIDDEN;
		}

		switch ( preference.getMode( ) )
		{
		case MODE_VS_COMPUTER:
			result |= MASK_MODE_VS_COM;
			break;
		case MODE_VS_HUMAN:
			result |= MASK_MODE_VS_HUM;
			break;
		case MODE_INVESTIGATION:
			result |= MASK_MODE_VS_INV;
			break;
		}
		return new Byte( result );
	}

	public static Preference fromByte( Byte data )
	{
		if ( data == null )
		{
			return new Preference( );
		}

		byte value = data.byteValue( );
		Preference preference = new Preference( );
		preference.setHumanFirst( ( value & MASK_FIRST ) == MASK_FIRST );
		preference.setEnableHint( ( ( value & MASK_HINT ) == MASK_HINT ) );
		preference.setEnablBan( ( value & MASK_FORBIDDEN ) == MASK_FORBIDDEN );
		switch ( value & MASK_MODE )
		{
		case MASK_MODE_VS_COM:
			preference.setMode( ChessMode.MODE_VS_COMPUTER );
			break;
		case MASK_MODE_VS_HUM:
			preference.setMode( ChessMode.MODE_VS_HUMAN );
			break;
		case MASK_MODE_VS_INV:
			preference.setMode( ChessMode.MODE_INVESTIGATION );
			break;
		}
		return preference;
	}
}