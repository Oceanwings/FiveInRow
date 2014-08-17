package com.fiveinrow.datatype;

import android.graphics.Point;

public final class Step
{
	private Point point;
	private boolean isBlack;

	public Step( Point point, boolean isBlack )
	{
		this.point = ( point != null ) ? new Point( point.x, point.y ) : new Point( -1, -1 );
		this.isBlack = isBlack;
	}

	public Step( Point point )
	{
		this( point, true );
	}

	public Point getPoint( )
	{
		return new Point( this.point.x, this.point.y );
	}

	public boolean isBlack( )
	{
		return this.isBlack;
	}

	public String toString( )
	{
		return new StringBuilder( this.point.toString( ) )
				.append( '[' )
				.append( this.isBlack )
				.append( ']' ).toString( );
	}
}