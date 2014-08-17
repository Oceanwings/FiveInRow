package com.fiveinrow.chessboard;

import android.graphics.Point;

final class JudgeStep
{
	private Point point;
	private int score;
	private int emptyAround;

	JudgeStep( Point point, int score, int empty )
	{
		this.point = point;
		this.score = score;
		this.emptyAround = empty;
	}

	Point getPoint( )
	{
		return this.point;
	}

	int getScore( )
	{
		return this.score;
	}

	int getEmptyAround( )
	{
		return this.emptyAround;
	}

	public String toString( )
	{
		return new StringBuilder( this.getPoint( ).toString( ) )
				.append( '[' )
				.append( this.getScore( ) )
				.append( ']' )
				.append( '(' )
				.append( this.getEmptyAround( ) )
				.append( ')' ).toString( );
	}
}