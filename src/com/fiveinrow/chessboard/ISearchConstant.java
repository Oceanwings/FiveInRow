package com.fiveinrow.chessboard;

public interface ISearchConstant
{
	public static final byte DIRECTION_NORTH 		= 1;	// 0000 0001
	public static final byte DIRECTION_NORTH_EAST 	= 2;	// 0000 0010
	public static final byte DIRECTION_EAST 		= 4;	// 0000 0100
	public static final byte DIRECTION_SOUTH_EAST 	= 8;	// 0000 1000
	public static final byte DIRECTION_SOUTH 		= 16;	// 0001 0000
	public static final byte DIRECTION_SOUTH_WEST 	= 32;	// 0010 0000
	public static final byte DIRECTION_WEST 		= 64;	// 0100 0000
	public static final byte DIRECTION_NORTH_WEST 	= -128;	// 1000 0000

	public static final byte WIN_CHECK_PIECE_COUNT 	= 5;
	public static final byte MAX_SEARCH_DEPTH 		= 3;
}