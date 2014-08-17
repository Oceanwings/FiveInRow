package com.fiveinrow.util;

import com.fiveinrow.chessboard.ISearchConstant;
import com.fiveinrow.main.FiveInRowApplication;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public final class Util
{
	private static final Resources _resource = FiveInRowApplication.getInstance( ).getResources( );

	private Util( )
	{}

	public static String getResourceString( int resId )
	{
		return _resource.getString( resId );
	}

	public static Drawable getResourceDrawable( int resId )
	{
		return _resource.getDrawable( resId );
	}

	public static float getResourceDimension( int resId )
	{
		return _resource.getDimension( resId );
	}

	public static Bitmap getResourceBitmap( int resId )
	{
		return BitmapFactory.decodeResource( _resource, resId );
	}

	public static View inflate( int resId )
	{
		return LayoutInflater.from( FiveInRowApplication.getInstance( ) ).inflate( resId, null );
	}

	public static View inflate( int resId, ViewGroup root )
	{
		return LayoutInflater.from( FiveInRowApplication.getInstance( ) ).inflate( resId, root );
	}

	public static Point getNextSearchPoint( Point point, byte direction )
	{
		int deltaX = 0, deltaY = 0;
		switch ( direction )
		{
		case ISearchConstant.DIRECTION_NORTH:
			--deltaX;
			break;
		case ISearchConstant.DIRECTION_NORTH_EAST:
			--deltaX;
			++deltaY;
			break;
		case ISearchConstant.DIRECTION_EAST:
			++deltaY;
			break;
		case ISearchConstant.DIRECTION_SOUTH_EAST:
			++deltaX;
			++deltaY;
			break;
		case ISearchConstant.DIRECTION_SOUTH:
			++deltaX;
			break;
		case ISearchConstant.DIRECTION_SOUTH_WEST:
			++deltaX;
			--deltaY;
			break;
		case ISearchConstant.DIRECTION_WEST:
			--deltaY;
			break;
		case ISearchConstant.DIRECTION_NORTH_WEST:
			--deltaX;
			--deltaY;
			break;
		}
		return new Point( point.x + deltaX, point.y + deltaY );
	}

	public static byte getOppositeDirection( byte direction )
	{
		switch ( direction )
		{
		case ISearchConstant.DIRECTION_NORTH:
			direction = ISearchConstant.DIRECTION_SOUTH;
			break;
		case ISearchConstant.DIRECTION_NORTH_EAST:
			direction = ISearchConstant.DIRECTION_SOUTH_WEST;
			break;
		case ISearchConstant.DIRECTION_EAST:
			direction = ISearchConstant.DIRECTION_WEST;
			break;
		case ISearchConstant.DIRECTION_SOUTH_EAST:
			direction = ISearchConstant.DIRECTION_NORTH_WEST;
			break;
		case ISearchConstant.DIRECTION_SOUTH:
			direction = ISearchConstant.DIRECTION_NORTH;
			break;
		case ISearchConstant.DIRECTION_SOUTH_WEST:
			direction = ISearchConstant.DIRECTION_NORTH_EAST;
			break;
		case ISearchConstant.DIRECTION_WEST:
			direction = ISearchConstant.DIRECTION_EAST;
			break;
		case ISearchConstant.DIRECTION_NORTH_WEST:
			direction = ISearchConstant.DIRECTION_SOUTH_EAST;
			break;
		}
		return direction;
	}
}