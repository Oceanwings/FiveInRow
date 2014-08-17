package com.fiveinrow.chessboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import com.fiveinrow.datatype.ChessBoard;
import com.fiveinrow.datatype.Step;
import com.fiveinrow.main.R;
import com.fiveinrow.util.Util;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public final class ChessBoardView extends View
{
	private static final Bitmap PIECE_BLACK = Util.getResourceBitmap( R.drawable.black );
	private static final Bitmap PIECE_WHITE = Util.getResourceBitmap( R.drawable.white );
	private static final Bitmap PIECE_TEMP 	= Util.getResourceBitmap( R.drawable.piece_temp );

	private ChessBoard chessBoard = new ChessBoard( );
	private Stack< Step > stack = new Stack< Step >( );
	private boolean isBlackTurn = true;

	private IChessBoardListener listener;

	private Point tempPosition = new Point( -1, -1 );

	// For drawing chess board
	private int startX;
	private int startY;
	private int pixelLength;
	private int unitWidth;

	public ChessBoardView( Context context )
	{
		super( context );
		this.init( );
	}

	public ChessBoardView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		this.init( );
	}

	public ChessBoardView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		this.init( );
	}

	private void init( )
	{
		this.startX = this.startY = this.pixelLength = this.unitWidth = 0;
	}
	
	public void setChessBoardListener( IChessBoardListener listener )
	{
		this.listener = listener;
	}

	public void setChessBoard( ChessBoard board )
	{
		if ( board != null )
		{
			this.chessBoard = board;
			this.invalidate( );
		}
	}

	public ChessBoard getChessBoard( )
	{
		return this.chessBoard.clone( );
	}

	public Step getLastStep( )
	{
		return ( !this.stack.isEmpty( ) ) ? this.stack.peek( ) : null;
	}

	public List< Step > getStepList( )
	{
		return new ArrayList< Step >( this.stack );
	}

	public void takeBack( )
	{
		if ( this.chessBoard != null && this.stack.size( ) > 0 )
		{
			Step step = this.stack.pop( );
			if ( step != null )
			{
				this.chessBoard.takeBack( step.getPoint( ) );
				this.isBlackTurn = !this.isBlackTurn;
				this.invalidate( );
			}
		}
	}

	public void handleTouchAction( int action, int x, int y )
	{
		if ( x >= 0 && x <= this.getWidth( ) && y >= 0 && y <= this.getHeight( ) )
		{
			boolean added = false;
			x -= this.startX;
			x += ( this.unitWidth >> 1 );
			x /= this.unitWidth;

			y -= this.startY;
			y -= this.unitWidth;
			y /= this.unitWidth;
			switch ( action )
			{
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				this.tempPosition.set( y, x );
				break;
			case MotionEvent.ACTION_UP:
				this.tempPosition.set( -1, -1 );
				added = this.addChessPiece( y, x );
				break;
			}
			this.invalidate( );

			if ( added )
			{
				this.checkBoard( );
			}
		}
	}

	public boolean addStep( Step step )
	{
		System.out.println( step );
		boolean result = ( step != null ) ? this.addChessPiece( step.getPoint( ) ) : false;
		this.invalidate( );
		if ( result )
		{
			this.checkBoard( );
		}
		return result;
	}

	private void checkBoard( )
	{
		if ( this.listener != null )
		{
			this.listener.notifyStatus( ChessBoardManager.getInstance( ).judgeBoardState(
					this.chessBoard, this.getLastStep( ) ) );
		}
	}

	protected void onLayout( boolean changed, int left, int top, int right, int bottom )
	{
		super.onLayout( changed, left, top, right, bottom );
		this.calculateBoardParams( );
	}

	private boolean addChessPiece( Point point )
	{
		return ( point != null ) ? this.addChessPiece( point.x, point.y ) : false;
	}

	private boolean addChessPiece( int x, int y )
	{
		if ( this.chessBoard.setChessPiece( x, y, this.isBlackTurn ? 
				ChessBoard.ChessPiece.BLACK : ChessBoard.ChessPiece.WHITE ) )
		{
			this.stack.push( new Step( new Point( x, y ), this.isBlackTurn ) );
			this.isBlackTurn = !this.isBlackTurn;
			return true;
		}
		return false;
	}

	private boolean isParamsValid( )
	{
		return ( this.startX > 0 && this.startY > 0 && this.unitWidth > 0 && this.pixelLength > this.unitWidth );
	}

	private void calculateBoardParams( )
	{
		final int width = this.getWidth( );
		final int height = this.getHeight( );
		final int size = this.chessBoard.getSize( ).size( );
		this.unitWidth = ( width <= height ) ? width / size : height / size;
		this.pixelLength = this.unitWidth * ( size - 1 );
		this.startX = ( width - this.pixelLength ) >> 1;
		this.startY = ( height - this.pixelLength ) >> 1;
	}

	private void drawChessBoard( Canvas canvas )
	{
		final int size = this.chessBoard.getSize( ).size( );
		Paint paint = new Paint( );
		paint.setColor( Color.BLACK );
		int end = this.startY + this.pixelLength;
		for ( int x = this.startX, i = 0; i < size; x += this.unitWidth, ++i )
		{
			canvas.drawLine( x, this.startY, x, end, paint );
		}

		end = this.startX + this.pixelLength;
		for ( int y = this.startY, i = 0; i < size; y += this.unitWidth, ++i )
		{
			canvas.drawLine( this.startX, y, end, y, paint );
		}
	}

	private void drawChessPieces( Canvas canvas )
	{
		final int size = this.chessBoard.getSize( ).size( );
		final int offset = PIECE_WHITE.getWidth( ) >> 1;
		int centerX = this.startX;
		int centerY = this.startY;
		Paint paint = new Paint( );
		for ( int x = 0; x < size; ++x )
		{
			centerY = this.startY;
			for ( int y = 0; y < size; ++y )
			{
				switch ( this.chessBoard.getChessPiece( y, x ) )
				{
				case BLACK:
					canvas.drawBitmap( PIECE_BLACK, centerX - offset, centerY - offset, paint );
					break;
				case WHITE:
					canvas.drawBitmap( PIECE_WHITE, centerX - offset, centerY - offset, paint );
					break;
				}
				centerY += this.unitWidth;
			}
			centerX += this.unitWidth;
		}
		
		if ( this.tempPosition.x >= 0 && this.tempPosition.x < size 
				&& this.tempPosition.y >= 0 && this.tempPosition.y < size )
		{
			centerX = this.startX + this.unitWidth * this.tempPosition.y;
			centerY = this.startY + this.unitWidth * this.tempPosition.x;
			canvas.drawBitmap( PIECE_TEMP, centerX - offset, centerY - offset, paint );
		}
	}

	public void draw( Canvas canvas )
	{
		super.draw( canvas );
		if ( this.isParamsValid( ) )
		{
			this.drawChessBoard( canvas );
			this.drawChessPieces( canvas );
		}
	}

	public void clear( )
	{
		this.stack.removeAllElements( );
		this.chessBoard.clear( );
		this.isBlackTurn = true;
		this.invalidate( );
	}

	public static interface IChessBoardListener
	{
		public void notifyStatus( final ChessBoard.State status );

		public void badMovement( );
	}
}