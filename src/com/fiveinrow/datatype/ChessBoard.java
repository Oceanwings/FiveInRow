package com.fiveinrow.datatype;

import java.util.List;
import android.graphics.Point;

public class ChessBoard
{
	public static enum BoardSize {
		SMALL( 11 ), MIDDLE( 15 ), LARGE( 19 );

		private int size;

		private BoardSize( int size )
		{
			this.size = size;
		}

		public int size( )
		{
			return this.size;
		}
	};

	public static enum ChessPiece {
		/**
		 * BLACK: Black piece.
		 * EMPTY: No piece at this position.
		 * NULL:  Out of chess board.
		 * WHITE: White piece.
		 */
		BLACK, EMPTY, NULL, WHITE;

		public ChessPiece opposite( )
		{
			ChessPiece opposite = this;
			switch ( this )
			{
			case BLACK:
				opposite = ChessPiece.WHITE;
				break;
			case WHITE:
				opposite = ChessPiece.BLACK;
				break;
			}
			return opposite;
		}
	};

	public static enum State {
		WIN_BLACK, WIN_WHITE, DRAW, NEXT_BLACK, NEXT_WHITE, BAN_THREES, BAN_FOURS, BAN_LONG
	};

	private ChessPiece[ ][ ] pieceMatrix;
	private BoardSize size;

	public ChessBoard( )
	{
		this( BoardSize.MIDDLE );
	}

	public ChessBoard( BoardSize size )
	{
		this.size = ( size != null ) ? size : BoardSize.MIDDLE;
		this.init( );
	}

	public ChessBoard( ChessBoard chessBoard )
	{
		if ( chessBoard != null )
		{
			this.size = chessBoard.getSize( );
			int size = this.size.size( );
			this.pieceMatrix = new ChessPiece[ size ][ size ];
			for ( int x = 0; x < size; ++x )
			{
				for ( int y = 0; y < size; ++y )
				{
					this.pieceMatrix[ x ][ y ] = chessBoard.getChessPiece( x, y );
				}
			}
		}
		else
		{
			this.size = BoardSize.MIDDLE;
			this.init( );
		}
	}

	private void init( )
	{
		int size = this.size.size( );
		this.pieceMatrix = new ChessPiece[ size ][ size ];
		for ( int x = 0; x < size; ++x )
		{
			for ( int y = 0; y < size; ++y )
			{
				this.pieceMatrix[ x ][ y ] = ChessPiece.EMPTY;
			}
		}
	}

	public BoardSize getSize( )
	{
		return this.size;
	}

	public ChessPiece getChessPiece( Point point )
	{
		return ( point != null ) ? this.getChessPiece( point.x, point.y ) : ChessPiece.NULL;
	}

	public ChessPiece getChessPiece( int x, int y )
	{
		return ( this.isValidIndex( x, y ) ) ? this.pieceMatrix[ x ][ y ] : ChessPiece.NULL;
	}

	public boolean setChessPiece( Point point, ChessPiece piece )
	{
		if ( point != null )
		{
			return this.setChessPiece( point.x, point.y, piece );
		}
		return false;
	}

	public boolean setChessPiece( int x, int y, ChessPiece piece )
	{
		if ( piece != null && this.isValidIndex( x, y )
				&& ( piece == ChessPiece.EMPTY || this.pieceMatrix[ x ][ y ] == ChessPiece.EMPTY ) )
		{
			this.pieceMatrix[ x ][ y ] = piece;
			return true;
		}
		return false;
	}

	private boolean updateChessPiece( int x, int y, ChessPiece piece )
	{
		if ( piece != null && this.isValidIndex( x, y ) )
		{
			this.pieceMatrix[ x ][ y ] = piece;
			return true;
		}
		return false;
	}

	public void takeBack( Point point )
	{
		if ( point != null )
		{
			this.takeBack( point.x, point.y );
		}
	}

	public void takeBack( int x, int y )
	{
		this.updateChessPiece( x, y, ChessPiece.EMPTY );
	}

	public void takeBack( List< Step > stepList )
	{
		if ( stepList != null && stepList.size( ) > 0 )
		{
			for ( Step step : stepList )
			{
				this.takeBack( step.getPoint( ) );
			}
		}
	}

	private boolean isValidIndex( int x, int y )
	{
		return ( x >= 0 && y >= 0 && x < this.size.size( ) && y < this.size.size( ) );
	}

	public boolean isFull( )
	{
		boolean isFull = true;
		int size = this.size.size( );
		for ( int x = 0; isFull && x < size; ++x )
		{
			for ( int y = 0; isFull && y < size; ++y )
			{
				isFull = ( this.pieceMatrix[ x ][ y ] != ChessPiece.EMPTY );
			}
		}
		return isFull;
	}

	public void clear( )
	{
		this.pieceMatrix = null;
		this.init( );
	}

	public ChessBoard clone( )
	{
		return new ChessBoard( this );
	}
}