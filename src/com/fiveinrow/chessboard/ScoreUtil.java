package com.fiveinrow.chessboard;

import android.graphics.Point;
import com.fiveinrow.datatype.ChessBoard;
import com.fiveinrow.datatype.ChessBoard.ChessPiece;
import com.fiveinrow.util.Util;

final class ScoreUtil
{
	static final int SCORE_OVER_FIVE 	= 1000000; // 超过5子
	static final int SCORE_FIVE 		= 500000; // 5子
	static final int SCORE_FOUR_DOUBLE 	= 50000; // 活4
	static final int SCORE_FOUR 		= 5000; // 单4
	static final int SCORE_THREE_DOUBLE = 4000; // 活3
	static final int SCORE_THREE 		= 200; // 单3
	static final int SCORE_TWO_DOUBLE 	= 100; // 活2
	static final int SCORE_FOUR_DEAD 	= 20; // 死4
	static final int SCORE_TWO 			= 10; // 单2
	static final int SCORE_THREE_DEAD 	= 5; // 死3
	static final int SCORE_TWO_DEAD 	= 2; // 死2

	private static final byte PIECE_COUNT_FIVE 	= 5;
	private static final byte PIECE_COUNT_FOUR 	= 4;
	private static final byte PIECE_COUNT_THREE = 3;
	private static final byte PIECE_COUNT_TWO 	= 2;

	private static final byte INDEX_HEAD  = 0;
	private static final byte INDEX_MAIN  = 1;
	private static final byte INDEX_TRAIL = 2;

	private ScoreUtil( )
	{}

	/**
	 * Value a single possible point for next step.
	 * 
	 * @param chessBoard
	 * @param point
	 * @param isBlackSide
	 * @return
	 */
	static int valuePoint( final ChessBoard chessBoard, final Point point, final boolean isBlackSide )
	{
		final ChessPiece specifyPiece = isBlackSide ? ChessPiece.BLACK : ChessPiece.WHITE;

		int score = 0;
		byte direction = ISearchConstant.DIRECTION_NORTH;
		final int[ ] pieceCounts = new int[ ] { 0, 1, 0 };
		int countIndex = INDEX_MAIN;

		boolean headBlocked = false, tailBlocked = false;
		boolean continueSearch = false;
		boolean getFive = false;

		Point nextPoint = null;
		ChessPiece piece = null;
		do
		{
			pieceCounts[ INDEX_HEAD ] = pieceCounts[ INDEX_TRAIL ] = 0;
			pieceCounts[ INDEX_MAIN ] = 1;
			countIndex = INDEX_MAIN;

			getFive = headBlocked = tailBlocked = false;
			nextPoint = point;
			do
			{
				continueSearch = false;
				nextPoint = Util.getNextSearchPoint( nextPoint, direction );
				piece = chessBoard.getChessPiece( nextPoint );

				if ( piece == specifyPiece )
				{
					++pieceCounts[ countIndex ];
					continueSearch = true;
				}
				else if ( piece == ChessPiece.EMPTY && pieceCounts[ INDEX_HEAD ] == 0 )
				{
					nextPoint = Util.getNextSearchPoint( nextPoint, direction );
					if ( specifyPiece == chessBoard.getChessPiece( nextPoint ) )
					{
						countIndex = INDEX_HEAD;
						++pieceCounts[ countIndex ];
						continueSearch = true;
					}
				}
			}
			while ( continueSearch );

			getFive = ( pieceCounts[ INDEX_MAIN ] >= PIECE_COUNT_FIVE );
			if ( !getFive )
			{
				headBlocked = ( chessBoard.getChessPiece( nextPoint ) != ChessPiece.EMPTY );
			}

			nextPoint = point;
			direction = Util.getOppositeDirection( direction );
			countIndex = INDEX_MAIN;
			do
			{
				continueSearch = false;
				nextPoint = Util.getNextSearchPoint( nextPoint, direction );
				piece = chessBoard.getChessPiece( nextPoint );

				if ( piece == specifyPiece )
				{
					++pieceCounts[ countIndex ];
					continueSearch = true;
				}
				else if ( piece == ChessPiece.EMPTY && pieceCounts[ INDEX_TRAIL ] == 0 )
				{
					nextPoint = Util.getNextSearchPoint( nextPoint, direction );
					if ( specifyPiece == chessBoard.getChessPiece( nextPoint ) )
					{
						countIndex = INDEX_TRAIL;
						++pieceCounts[ countIndex ];
						continueSearch = true;
					}
				}
			}
			while ( continueSearch );

			getFive = ( pieceCounts[ INDEX_MAIN ] >= PIECE_COUNT_FIVE );
			if ( !getFive )
			{
				headBlocked = ( chessBoard.getChessPiece( nextPoint ) != ChessPiece.EMPTY );
			}
			direction = Util.getOppositeDirection( direction );

			score = getFinalScore( score, getScore( pieceCounts, headBlocked, tailBlocked ) );
			direction <<= 1;
		}
		while ( ( isBlackSide || !getFive ) && direction < ISearchConstant.DIRECTION_SOUTH );

		int emptyCount = 0;
		if ( score < SCORE_THREE_DOUBLE )
		{
			direction = ISearchConstant.DIRECTION_NORTH;
			do
			{
				if ( ChessPiece.EMPTY == chessBoard.getChessPiece( 
						Util.getNextSearchPoint( point, direction ) ) )
				{
					++emptyCount;
				}
				direction <<= 1;
			}
			while ( direction != 0 );
		}
		return ( ( score << ChessBoardManager.BITS_EMPTY_POSITIONS ) | emptyCount );
	}

	private static int getFinalScore( final int finalScore, final int score )
	{
		return ( finalScore >= SCORE_THREE_DOUBLE && score >= SCORE_THREE_DOUBLE ) ? ( finalScore + score )
				: ( ( score > finalScore ) ? score : finalScore );
	}

	private static int getScore( final int[ ] pieceCounts, final boolean headBlocked,
			final boolean tailBlocked )
	{
		int score = 0;
		if ( pieceCounts[ INDEX_MAIN ] >= PIECE_COUNT_FIVE
				|| ( pieceCounts[ INDEX_HEAD ] == 0 && pieceCounts[ INDEX_TRAIL ] == 0 ) )
		{
			score = scoreByPieceCount( pieceCounts[ INDEX_MAIN ], headBlocked, tailBlocked );
		}
		else if ( pieceCounts[ INDEX_HEAD ] == 0 || pieceCounts[ INDEX_TRAIL ] == 0 )
		{
			score = scoreByTotalCount( pieceCounts[ INDEX_HEAD ] + pieceCounts[ INDEX_MAIN ] + pieceCounts[ INDEX_TRAIL ], 
					headBlocked, tailBlocked );
		}
		else
		{
			// FIXME:
			score = scoreByTotalCount( pieceCounts[ INDEX_HEAD ] + pieceCounts[ INDEX_MAIN ] );
			score += scoreByTotalCount( pieceCounts[ INDEX_TRAIL ] + pieceCounts[ INDEX_MAIN ] );
		}
		return score;
	}

	private static int scoreByPieceCount( final int pieceCount, final boolean headBlocked,
			final boolean tailBlocked )
	{
		int score = 0;
		if ( pieceCount > PIECE_COUNT_FIVE )
		{
			score = SCORE_OVER_FIVE;
		}
		else
		{
			switch ( pieceCount )
			{
			case PIECE_COUNT_FIVE:
				score = SCORE_FIVE;
				break;
			case PIECE_COUNT_FOUR:
				if ( !headBlocked && !tailBlocked )
				{
					score = SCORE_FOUR_DOUBLE;
				}
				else if ( headBlocked && tailBlocked )
				{
					score = SCORE_FOUR_DEAD;
				}
				else
				{
					score = SCORE_FOUR;
				}
				break;
			case PIECE_COUNT_THREE:
				if ( !headBlocked && !tailBlocked )
				{
					score = SCORE_THREE_DOUBLE;
				}
				else if ( headBlocked && tailBlocked )
				{
					score = SCORE_THREE_DEAD;
				}
				else
				{
					score = SCORE_THREE;
				}
				break;
			case PIECE_COUNT_TWO:
				if ( !headBlocked && !tailBlocked )
				{
					score = SCORE_TWO_DOUBLE;
				}
				else if ( headBlocked && tailBlocked )
				{
					score = SCORE_TWO_DEAD;
				}
				else
				{
					score = SCORE_TWO;
				}
				break;
			}
		}
		return score;
	}

	private static int scoreByTotalCount( final int totalCount, final boolean headBlocked,
			final boolean tailBlocked )
	{
		return scoreByPieceCount( ( totalCount > PIECE_COUNT_FOUR ) ? PIECE_COUNT_FOUR : totalCount, 
						headBlocked,
						tailBlocked );
	}

	private static int scoreByTotalCount( final int totalCount )
	{
		int score = ( totalCount >= PIECE_COUNT_FOUR ) ? SCORE_FOUR : 0;
		switch ( totalCount )
		{
		case PIECE_COUNT_TWO:
			score = SCORE_TWO;
			break;
		case PIECE_COUNT_THREE:
			score = SCORE_THREE;
			break;
		case PIECE_COUNT_FOUR:
			score = SCORE_FOUR;
			break;
		}
		return score;
	}
}