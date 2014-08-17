package com.fiveinrow.chessboard;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Point;
import com.fiveinrow.datatype.ChessBoard;
import com.fiveinrow.datatype.ChessBoard.ChessPiece;
import com.fiveinrow.datatype.Step;
import com.fiveinrow.setting.PreferenceManager;
import com.fiveinrow.util.Util;

public final class ChessBoardManager
{
	public static enum SearchDirection {
		NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST;

		public SearchDirection next( )
		{
			SearchDirection next = NORTH;
			switch ( this )
			{
			case NORTH:
				next = NORTH_EAST;
				break;
			case NORTH_EAST:
				next = EAST;
				break;
			case EAST:
				next = SOUTH_EAST;
				break;
			case SOUTH_EAST:
				next = SOUTH;
				break;
			case SOUTH:
				next = SOUTH_WEST;
				break;
			case SOUTH_WEST:
				next = WEST;
				break;
			case WEST:
				next = NORTH_WEST;
				break;
			}
			return next;
		}

		public SearchDirection opposite( )
		{
			SearchDirection opposite = NORTH;
			switch ( this )
			{
			case NORTH:
				opposite = SOUTH;
				break;
			case NORTH_EAST:
				opposite = SOUTH_WEST;
				break;
			case EAST:
				opposite = WEST;
				break;
			case SOUTH_EAST:
				opposite = NORTH_WEST;
				break;
			case SOUTH:
				opposite = NORTH;
				break;
			case SOUTH_WEST:
				opposite = NORTH_EAST;
				break;
			case WEST:
				opposite = EAST;
				break;
			case NORTH_WEST:
				opposite = SOUTH_EAST;
				break;
			}
			return opposite;
		}
	};

	private static enum Ban {
		NULL, BAN_THREES, BAN_FOURS, BAN_LONG
	};

	static final int BITS_EMPTY_POSITIONS 			= 4;
	private static final int MASK_EMPTY_POSITIONS 	= 0xF;

	private static ChessBoardManager instance = new ChessBoardManager( );

	public static ChessBoardManager getInstance( )
	{
		return instance;
	}

	private ChessBoard chessBoard;

	private ChessBoardManager( )
	{}

	public synchronized ChessBoard getChessBoard( )
	{
		if ( this.chessBoard == null )
		{
			this.chessBoard = new ChessBoard( );
		}
		return this.chessBoard;
	}

	private ChessBoard getCurrentChessBoard( )
	{
		return this.chessBoard;
	}
	
	public ChessBoard.State judgeBoardState( final ChessBoard chessBoard, final Step step )
	{
		if ( chessBoard == null || step == null )
		{
			return null;
		}

		ChessBoard.State state = null;
		int score = ScoreUtil.valuePoint( chessBoard, step.getPoint( ), step.isBlack( ) );
		score = score >> BITS_EMPTY_POSITIONS;
		switch ( judgeChessBan( score, step.isBlack( ) ) )
		{
		case NULL:
			state = ChessBoard.State.DRAW;
			if ( !chessBoard.isFull( ) )
			{
				state = ( score >= ScoreUtil.SCORE_FIVE ) ? 
						( step.isBlack( ) ? ChessBoard.State.WIN_BLACK : ChessBoard.State.WIN_WHITE )
						: ( step.isBlack( ) ? ChessBoard.State.NEXT_WHITE : ChessBoard.State.NEXT_BLACK );
			}
			break;
		case BAN_LONG:
			state = ChessBoard.State.BAN_LONG;
			// System.out.println( score );
			break;
		case BAN_FOURS:
			state = ChessBoard.State.BAN_FOURS;
			// System.out.println( score );
			break;
		case BAN_THREES:
			state = ChessBoard.State.BAN_THREES;
			// System.out.println( score );
			break;
		}
		return state;
	}

	private static Ban judgeChessBan( final int score, final boolean isBlack )
	{
		Ban ban = Ban.NULL;
		if ( isBlack && PreferenceManager.getInstance( ).isBanEnabled( ) )
		{
			if ( score >= ScoreUtil.SCORE_OVER_FIVE )
			{
				ban = Ban.BAN_LONG;
			}
			else if ( score >= ScoreUtil.SCORE_FIVE || score == ScoreUtil.SCORE_FOUR_DOUBLE
					|| score == ( ScoreUtil.SCORE_FOUR_DOUBLE + ScoreUtil.SCORE_THREE_DOUBLE )
					|| score == ( ScoreUtil.SCORE_FOUR + ScoreUtil.SCORE_THREE_DOUBLE ) )
			{
				// Do nothing, just keeping the Ban.NULL value is OK.
			}
			else if ( score >= ( ScoreUtil.SCORE_FOUR_DOUBLE << 1 ) )
			{
				ban = Ban.BAN_FOURS;
			}
			else if ( score >= ( ScoreUtil.SCORE_THREE_DOUBLE << 1 ) )
			{
				ban = Ban.BAN_THREES;
			}
		}
		return ban;
	}

	/**
	 * Check whether player/AI win the game for current step.
	 * 
	 * @param chessBoard
	 * @param step
	 *            current step
	 * @return true for win, otherwise false.
	 */
	public boolean isWin( final Step step )
	{
		return isWin( this.getChessBoard( ), step );
	}

	private static boolean isWin( final ChessBoard chessBoard, final Step step )
	{
		if ( chessBoard == null || step == null )
		{
			return false;
		}

		byte direction = ISearchConstant.DIRECTION_NORTH, pieceCount = 1;
		Point nextPoint = null;
		boolean getFive = false;
		boolean find = false;
		final ChessPiece specifyPiece = step.isBlack( ) ? ChessPiece.BLACK : ChessPiece.WHITE;
		do
		{
			getFive = false;
			nextPoint = step.getPoint( );
			pieceCount = 1;
			do
			{
				nextPoint = Util.getNextSearchPoint( nextPoint, direction );
				find = ( specifyPiece == chessBoard.getChessPiece( nextPoint ) );
				if ( find )
				{
					++pieceCount;
				}
			}
			while ( find );
			getFive = ( pieceCount >= ISearchConstant.WIN_CHECK_PIECE_COUNT );

			if ( !getFive )
			{
				nextPoint = step.getPoint( );
				direction = Util.getOppositeDirection( direction );
				do
				{
					nextPoint = Util.getNextSearchPoint( nextPoint, direction );
					find = ( specifyPiece == chessBoard.getChessPiece( nextPoint ) );
					if ( find )
					{
						++pieceCount;
					}
				}
				while ( find );
				getFive = ( pieceCount >= ISearchConstant.WIN_CHECK_PIECE_COUNT );
				direction = Util.getOppositeDirection( direction );
			}
			direction <<= 1;
		}
		while ( !getFive && direction < ISearchConstant.DIRECTION_SOUTH );
		return getFive;
	}

	public Step getHintForNextStep( List< Step > stepList )
	{
		return getBestNextStep( this.getCurrentChessBoard( ), stepList );
	}

	private static Step getBestNextStep( final ChessBoard chessBoard, final List< Step > stepList )
	{
		if ( !stepList.isEmpty( ) )
		{
			return getBestNextStep( chessBoard, stepList, !stepList.get( stepList.size( ) - 1 ).isBlack( ) );
		}
		else
		{
			int x = chessBoard.getSize( ).size( ) >> 1;
			return new Step( new Point( x, x ), true );
		}
	}

	private static Step getBestNextStep( final ChessBoard chessBoard, final List< Step > stepList,
			final boolean isBlackSide )
	{
		List< List< Point > > list = separateSteps( stepList );
		int blackHighScore = 0, whiteHighScore = 0;
		JudgeStep blackStep = null, whiteStep = null;
		List< JudgeStep > blackList = judgeNextSteps( chessBoard,
				getSearchPoints( chessBoard, list.get( 0 ), true ), true );
		for ( JudgeStep step : blackList )
		{
			if ( step.getScore( ) > blackHighScore
					|| ( step.getScore( ) == blackHighScore && blackStep != null 
							&& step.getEmptyAround( ) > blackStep.getEmptyAround( ) ) )
			{
				blackHighScore = step.getScore( );
				blackStep = step;
			}
		}

		List< JudgeStep > whiteList = judgeNextSteps( chessBoard,
				getSearchPoints( chessBoard, list.get( 1 ), false ), false );
		for ( JudgeStep step : whiteList )
		{
			if ( step.getScore( ) > whiteHighScore
					|| ( step.getScore( ) == whiteHighScore && whiteStep != null 
							&& step.getEmptyAround( ) > whiteStep.getEmptyAround( ) ) )
			{
				whiteHighScore = step.getScore( );
				whiteStep = step;
			}
		}

		if ( isBlackSide )
		{
			blackStep = ( whiteHighScore > ScoreUtil.SCORE_THREE_DOUBLE && whiteHighScore > blackHighScore ) ? 
					whiteStep : blackStep;
		}
		else
		{
			if ( whiteStep != null )
			{
				blackStep = ( blackHighScore > ScoreUtil.SCORE_THREE_DOUBLE && blackHighScore > whiteHighScore ) ?
						blackStep : whiteStep;
			}
			else
			{
				whiteStep = blackStep;
			}
		}
		return new Step( ( blackStep != null ) ? blackStep.getPoint( ) : new Point( -1, -1 ), isBlackSide );
	}

	/**
	 * Separate current steps to two lists, based on the side (piece color: black/white)
	 * 
	 * @param stepList
	 *            All current steps
	 * @return
	 */
	private static List< List< Point > > separateSteps( final List< Step > stepList )
	{
		int size = stepList.size( );
		List< Point > blackPoints = new ArrayList< Point >( size >> 1 );
		List< Point > whitePoints = new ArrayList< Point >( size >> 1 );
		for ( Step step : stepList )
		{
			if ( step.isBlack( ) )
			{
				blackPoints.add( step.getPoint( ) );
			}
			else
			{
				whitePoints.add( step.getPoint( ) );
			}
		}

		List< List< Point > > listArray = new ArrayList< List< Point > >( 2 );
		listArray.add( blackPoints );
		listArray.add( whitePoints );
		return listArray;
	}

	private static List< Point > getSearchPoints( final ChessBoard chessBoard,
			final List< Point > piecePointList, final boolean isBlackSide )
	{
		int size = piecePointList.size( );
		List< Point > pointList = new ArrayList< Point >( size );
		byte direction = 0;
		Point nextPoint = null;
		for ( int i = size - 1; i >= 0; --i )
		{
			direction = ISearchConstant.DIRECTION_NORTH;
			do
			{
				nextPoint = Util.getNextSearchPoint( piecePointList.get( i ), direction );
				if ( ChessPiece.EMPTY == chessBoard.getChessPiece( nextPoint )
						&& !pointList.contains( nextPoint ) )
				{
					pointList.add( nextPoint );
				}
				direction <<= 1;
			}
			while ( direction != 0 );
		}
		return pointList;
	}

	/**
	 * Judge all possible points for next step, value them by score.
	 * 
	 * @param chessBoard
	 * @param pointList
	 * @param isBlackSide
	 * @return
	 */
	private static List< JudgeStep > judgeNextSteps( final ChessBoard chessBoard,
			final List< Point > pointList, final boolean isBlackSide )
	{
		int size = pointList.size( );
		List< JudgeStep > stepList = new ArrayList< JudgeStep >( size );
		for ( Point point : pointList )
		{
			int score = ScoreUtil.valuePoint( chessBoard, point, isBlackSide );
			int emptyCount = ( score & MASK_EMPTY_POSITIONS );
			score = score >> BITS_EMPTY_POSITIONS;
			
			if ( judgeChessBan( score, isBlackSide ) == Ban.NULL )
			{
				if ( score >= ScoreUtil.SCORE_FIVE )
				{
					stepList.clear( );
					stepList.add( new JudgeStep( point, score, emptyCount ) );
					break;
				}
				else
				{
					stepList.add( new JudgeStep( point, score, emptyCount ) );
				}
			}
		}
		return stepList;
	}
}