package com.fiveinrow.chessboard;

import com.fiveinrow.datatype.ChessBoard;
import com.fiveinrow.main.R;
import com.fiveinrow.setting.Preference;
import com.fiveinrow.setting.PreferenceManager;
import com.fiveinrow.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public final class ChessBoardActivity extends Activity implements ChessBoardView.IChessBoardListener
{
	private static final int ID_DIALOG_EXIT 	= -1;
	private static final int ID_DIALOG_NEW_GAME = 0;
	private static final int ID_DIALOG_INFOR 	= 1;
	private static final int ID_DIALOG_WIN 		= 2;
	private static final int ID_DIALOG_LOSE 	= 3;
	private static final int ID_DIALOG_DRAW 	= 4;
	private static final int ID_DIALOG_SETTING 	= 5;
	private static final int ID_DIALOG_BAN 		= 6;

	private static final int DELAY_AI_STEP = 200;
	private static final int DURATION_SETTING_SAVED = 1500;

	private Preference.ChessMode mode;
	private ChessBoardView chessBoardView;
	private boolean gameRunning = false;
	private ChessBoard.State state = ChessBoard.State.NEXT_BLACK;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
		this.setContentView( R.layout.activity_chessboard );
		this.mode = PreferenceManager.getInstance( ).getMode( );
		this.chessBoardView = ( ChessBoardView ) this.findViewById( R.id.chess_board );
		this.chessBoardView.setChessBoard( ChessBoardManager.getInstance( ).getChessBoard( ) );
		this.chessBoardView.setChessBoardListener( this );
		this.init( );
		this.startNewGame( );
	}

	private void init( )
	{
		this.findViewById( R.id.icon_infor ).setOnClickListener( new View.OnClickListener( ) {
			@Override
			public void onClick( View view )
			{
				ChessBoardActivity.this.showDialog( ID_DIALOG_INFOR );
			}
		} );
		this.findViewById( R.id.icon_new_game ).setOnClickListener( new View.OnClickListener( ) {
			@Override
			public void onClick( View view )
			{
				ChessBoardActivity.this.showDialog( ID_DIALOG_NEW_GAME );
			}
		} );
		this.findViewById( R.id.icon_undo ).setOnClickListener( new View.OnClickListener( ) {
			@Override
			public void onClick( View view )
			{
				ChessBoardActivity.this.takeBackLastStep( );
			}
		} );
		this.findViewById( R.id.icon_hint ).setOnClickListener( new View.OnClickListener( ) {
			@Override
			public void onClick( View view )
			{
				if ( PreferenceManager.getInstance( ).isHintEnabled( ) )
				{
					ChessBoardActivity.this.addHintStep( );
				}
			}
		} );
		this.findViewById( R.id.icon_setting ).setOnClickListener( new View.OnClickListener( ) {
			@Override
			public void onClick( View view )
			{
				ChessBoardActivity.this.showDialog( ID_DIALOG_SETTING );
			}
		} );
	}

	private void updateView( )
	{
		switch ( this.mode )
		{
		case MODE_VS_COMPUTER:
			if ( PreferenceManager.getInstance( ).isHumanFirst( ) )
			{
				( ( TextView ) this.findViewById( R.id.label_player1 ) ).setText( R.string.PLAYER_HUMAN );
				( ( TextView ) this.findViewById( R.id.label_player2 ) ).setText( R.string.PLAYER_COMPUTER );
			}
			else
			{
				( ( TextView ) this.findViewById( R.id.label_player1 ) ).setText( R.string.PLAYER_COMPUTER );
				( ( TextView ) this.findViewById( R.id.label_player2 ) ).setText( R.string.PLAYER_HUMAN );
			}
			break;
		case MODE_VS_HUMAN:
		case MODE_INVESTIGATION:
			( ( TextView ) this.findViewById( R.id.label_player1 ) ).setText( R.string.PLAYER_A );
			( ( TextView ) this.findViewById( R.id.label_player2 ) ).setText( R.string.PLAYER_B );
			break;
		}

		if ( PreferenceManager.getInstance( ).isHintEnabled( ) )
		{
			this.findViewById( R.id.icon_hint ).setEnabled( true );
		}
		else
		{
			this.findViewById( R.id.icon_hint ).setEnabled( false );
		}
		this.findViewById( R.id.player_infor ).invalidate( );
		this.findViewById( R.id.icon_hint ).invalidate( );
	}

	protected Dialog onCreateDialog( int id )
	{
		Dialog dialog = null;
		switch ( id )
		{
		case ID_DIALOG_NEW_GAME:
			dialog = this.createNewGameDialog( );
			break;
		case ID_DIALOG_INFOR:
			dialog = this.createAppInforDialog( );
			break;
		case ID_DIALOG_EXIT:
			dialog = this.createExitConfirmDialog( );
			break;
		case ID_DIALOG_WIN:
			dialog = this.createWinDialog( );
			break;
		case ID_DIALOG_LOSE:
			dialog = this.createLoseDialog( );
			break;
		case ID_DIALOG_DRAW:
			dialog = this.createDrawDialog( );
			break;
		case ID_DIALOG_SETTING:
			dialog = this.createSettingDialog( );
			break;
		case ID_DIALOG_BAN:
			dialog = this.createBanDialog( );
			break;
		default:
			dialog = super.onCreateDialog( id );
		}
		return dialog;
	}

	protected void onPrepareDialog( int id, Dialog dialog )
	{
		switch ( id )
		{
		case ID_DIALOG_SETTING:
			this.updateSettingView( dialog );
			break;
		case ID_DIALOG_BAN:
			( ( AlertDialog ) dialog ).setMessage( 
					Util.getResourceString( getBanMsgResId( this.state ) ) );
			break;
		case ID_DIALOG_WIN:
			dialog.setTitle( getWinningTitle( this.mode, this.state ) );
			break;
		}
		super.onPrepareDialog( id, dialog );
	}

	private Dialog createAppInforDialog( )
	{
		View contentView = Util.inflate( R.layout.dialog_about );
		contentView.findViewById( R.id.author_contract ).setOnClickListener(
				new View.OnClickListener( ) {
					@Override
					public void onClick( View view )
					{
						ChessBoardActivity.this.dismissDialog( ID_DIALOG_INFOR );
						ChessBoardActivity.this.runOnUiThread( new Runnable( ) {
							@Override
							public void run( )
							{
								ChessBoardActivity.this.sendEmail( );
							}
						} );
					}
				} );
		return new AlertDialog.Builder( this ).setTitle( R.string.TITLE_ABOUT )
				.setView( Util.inflate( R.layout.dialog_about ) )
				.setPositiveButton( R.string.LABEL_OK, null )
				.create( );
	}

	private Dialog createNewGameDialog( )
	{
		return new AlertDialog.Builder( this ).setTitle( R.string.TITLE_RESTART )
				.setMessage( R.string.MSG_ALERT_RESTART )
				.setPositiveButton( R.string.LABEL_OK, new DialogInterface.OnClickListener( ) {
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						ChessBoardActivity.this.startNewGame( );
					}
				} ).setNegativeButton( R.string.LABEL_CANCEL, null ).create( );
	}

	private Dialog createExitConfirmDialog( )
	{
		return new AlertDialog.Builder( this ).setTitle( R.string.TITLE_EXIT_CONFIRM )
				.setMessage( R.string.MSG_ALERT_EXIT )
				.setPositiveButton( R.string.LABEL_EXIT, new DialogInterface.OnClickListener( ) {
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						ChessBoardActivity.this.finish( );
					}
				} ).setNegativeButton( R.string.LABEL_CANCEL, null ).create( );
	}

	private Dialog createWinDialog( )
	{
		return new AlertDialog.Builder( this ).setTitle( R.string.TITLE_WIN )
				.setMessage( R.string.MSG_YOU_WIN )
				.setPositiveButton( R.string.LABEL_OK, new DialogInterface.OnClickListener( ) {
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						ChessBoardActivity.this.startNewGame( );
					}
				} )
				.setNegativeButton( R.string.LABEL_CANCEL, null )
				.create( );
	}

	private Dialog createDrawDialog( )
	{
		return new AlertDialog.Builder( this ).setTitle( R.string.TITLE_DRAW )
				.setMessage( R.string.MSG_DRAW )
				.setPositiveButton( R.string.LABEL_OK, new DialogInterface.OnClickListener( ) {
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						ChessBoardActivity.this.startNewGame( );
					}
				} )
				.setNegativeButton( R.string.LABEL_CANCEL, null )
				.create( );
	}

	private Dialog createLoseDialog( )
	{
		return new AlertDialog.Builder( this ).setTitle( R.string.TITLE_LOSE )
				.setMessage( R.string.MSG_YOU_LOSE )
				.setPositiveButton( R.string.LABEL_OK, new DialogInterface.OnClickListener( ) {
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						ChessBoardActivity.this.startNewGame( );
					}
				} )
				.setNegativeButton( R.string.LABEL_CANCEL, null )
				.create( );
	}

	private Dialog createBanDialog( )
	{
		return new AlertDialog.Builder( this ).setTitle( R.string.TITLE_LOSE )
				.setMessage( R.string.MSG_YOU_LOSE )
				.setPositiveButton( R.string.LABEL_OK, new DialogInterface.OnClickListener( ) {
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						ChessBoardActivity.this.startNewGame( );
					}
				} )
				.setNegativeButton( R.string.LABEL_CANCEL, null )
				.create( );
	}

	private Dialog createSettingDialog( )
	{
		return new AlertDialog.Builder( this ).setTitle( R.string.TITLE_SETTING )
				.setView( Util.inflate( R.layout.dialog_setting ) )
				.setPositiveButton( R.string.LABEL_OK, new DialogInterface.OnClickListener( ) {
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						ChessBoardActivity.this.updateSetting( ( Dialog ) dialog );
					}
				} )
				.setNegativeButton( R.string.LABEL_CANCEL, null )
				.create( );
	}

	private void updateSettingView( Dialog dialog )
	{
		( ( CheckBox ) dialog.findViewById( R.id.check_player_first ) )
				.setChecked( PreferenceManager.getInstance( ).isHumanFirst( ) );

		( ( CheckBox ) dialog.findViewById( R.id.check_enable_hint ) )
				.setChecked( PreferenceManager.getInstance( ).isHintEnabled( ) );

		( ( CheckBox ) dialog.findViewById( R.id.check_enable_ban ) )
				.setChecked( PreferenceManager.getInstance( ).isBanEnabled( ) );

		switch ( PreferenceManager.getInstance( ).getMode( ) )
		{
		case MODE_VS_COMPUTER:
			( ( RadioGroup ) dialog.findViewById( R.id.mode_group ) ).check( R.id.radio_vs_ai );
			break;
		case MODE_VS_HUMAN:
			( ( RadioGroup ) dialog.findViewById( R.id.mode_group ) ).check( R.id.radio_vs_human );
			break;
		case MODE_INVESTIGATION:
			( ( RadioGroup ) dialog.findViewById( R.id.mode_group ) ).check( R.id.radio_investigation );
			break;
		}
	}

	private void updateSetting( Dialog dialog )
	{
		if ( dialog == null )
		{
			return;
		}
		Preference.ChessMode mode = Preference.ChessMode.MODE_VS_COMPUTER;
		switch ( ( ( RadioGroup ) dialog.findViewById( R.id.mode_group ) )
				.getCheckedRadioButtonId( ) )
		{
		case R.id.radio_vs_ai:
			mode = Preference.ChessMode.MODE_VS_COMPUTER;
			break;
		case R.id.radio_vs_human:
			mode = Preference.ChessMode.MODE_VS_HUMAN;
			break;
		case R.id.radio_investigation:
			mode = Preference.ChessMode.MODE_INVESTIGATION;
			break;
		}
		PreferenceManager.getInstance( ).update( mode,
				( ( CheckBox ) dialog.findViewById( R.id.check_player_first ) ).isChecked( ),
				( ( CheckBox ) dialog.findViewById( R.id.check_enable_hint ) ).isChecked( ),
				( ( CheckBox ) dialog.findViewById( R.id.check_enable_ban ) ).isChecked( ) );
		Toast.makeText( this, R.string.MSG_SETTING_SAVED, DURATION_SETTING_SAVED ).show( );
	}

	private void startNewGame( )
	{
		this.mode = PreferenceManager.getInstance( ).getMode( );
		this.gameRunning = true;
		this.chessBoardView.clear( );
		this.updateView( );
	}

	private void takeBackLastStep( )
	{
		this.chessBoardView.takeBack( );
		this.gameRunning = true;
	}

	private void addHintStep( )
	{
		this.chessBoardView.addStep( 
				ChessBoardManager.getInstance( ).getHintForNextStep( 
						this.chessBoardView.getStepList( ) ) );
	}

	public boolean onTouchEvent( MotionEvent event )
	{
		if ( this.gameRunning )
		{
			this.checkBoardTouch( event );
		}
		return super.onTouchEvent( event );
	}

	private void checkBoardTouch( MotionEvent event )
	{
		int x = ( int ) event.getX( );
		int y = ( int ) event.getY( );
		if ( x >= this.chessBoardView.getLeft( ) && x <= this.chessBoardView.getRight( )
				&& y >= this.chessBoardView.getTop( ) && y <= this.chessBoardView.getBottom( ) )
		{
			x -= this.chessBoardView.getLeft( );
			y -= this.chessBoardView.getTop( );
			y -= Util.getResourceDimension( R.dimen.DEFAULT_PADDING );
			this.chessBoardView.handleTouchAction( event.getAction( ), x, y );
		}
	}

	public void onBackPressed( )
	{
		this.showDialog( ID_DIALOG_EXIT );
	}

	protected void onDestroy( )
	{
		this.chessBoardView.clear( );
		super.onDestroy( );
	}

	@Override
	public void badMovement( )
	{
		// TODO Auto-generated method stub
		System.out.println( "It's a bad movement, isn't it?" );
	}

	@Override
	public void notifyStatus( final ChessBoard.State status )
	{
		this.state = status;
		switch ( status )
		{
		case WIN_BLACK:
			this.gameRunning = false;
			if ( this.mode == Preference.ChessMode.MODE_VS_COMPUTER
					&& !PreferenceManager.getInstance( ).isHumanFirst( ) )
			{
				this.aiWin( );
			}
			else
			{
				this.playerWin( );
			}
			break;
		case WIN_WHITE:
			this.gameRunning = false;
			if ( this.mode == Preference.ChessMode.MODE_VS_COMPUTER
					&& PreferenceManager.getInstance( ).isHumanFirst( ) )
			{
				this.aiWin( );
			}
			else
			{
				this.playerWin( );
			}
			break;
		case DRAW:
			this.gameRunning = false;
			this.runOnUiThread( new Runnable( ) {
				@Override
				public void run( )
				{
					ChessBoardActivity.this.showDialog( ID_DIALOG_DRAW );
				}
			} );
			break;
		case NEXT_BLACK:
			if ( this.mode == Preference.ChessMode.MODE_VS_COMPUTER
					&& !PreferenceManager.getInstance( ).isHumanFirst( ) )
			{
				this.runAI( );
			}
			break;
		case NEXT_WHITE:
			if ( this.mode == Preference.ChessMode.MODE_VS_COMPUTER
					&& PreferenceManager.getInstance( ).isHumanFirst( ) )
			{
				this.runAI( );
			}
			break;
		case BAN_THREES:
		case BAN_FOURS:
		case BAN_LONG:
			this.gameRunning = false;
			this.runOnUiThread( new Runnable( ) {
				@Override
				public void run( )
				{
					ChessBoardActivity.this.showDialog( ID_DIALOG_BAN );
				}
			} );
			break;
		}
	}

	private void playerWin( )
	{
		this.runOnUiThread( new Runnable( ) {
			@Override
			public void run( )
			{
				ChessBoardActivity.this.showDialog( ID_DIALOG_WIN );
			}
		} );
	}

	private void aiWin( )
	{
		this.runOnUiThread( new Runnable( ) {
			@Override
			public void run( )
			{
				ChessBoardActivity.this.showDialog( ID_DIALOG_LOSE );
			}
		} );
	}

	private void runAI( )
	{
		new Thread( new Runnable( ) {
			@Override
			public void run( )
			{
				try
				{
					Thread.sleep( DELAY_AI_STEP );
				}
				catch ( Exception e )
				{
					e.printStackTrace( );
				}
				finally
				{
					ChessBoardActivity.this.runOnUiThread( new Runnable( ) {
						@Override
						public void run( )
						{
							ChessBoardActivity.this.addHintStep( );
						}
					} );
				}
			}
		} ).start( );
	}

	private static int getBanMsgResId( final ChessBoard.State state )
	{
		int resId = R.string.MSG_YOU_LOSE;
		switch ( state )
		{
		case BAN_LONG:
			resId = R.string.MSG_BAN_LONG;
			break;
		case BAN_FOURS:
			resId = R.string.MSG_BAN_FOURS;
			break;
		case BAN_THREES:
			resId = R.string.MSG_BAN_THREES;
			break;
		}
		return resId;
	}

	private static String getWinningTitle( final Preference.ChessMode mode,
			final ChessBoard.State state )
	{
		StringBuilder buffer = new StringBuilder( );
		if ( mode != null && mode != Preference.ChessMode.MODE_VS_COMPUTER )
		{
			switch ( state )
			{
			case WIN_BLACK:
				buffer.append( Util.getResourceString( R.string.PLAYER_A ) );
				buffer.append( Util.getResourceString( R.string.TITLE_WIN_VS_HUMAN ) );
				break;
			case WIN_WHITE:
				buffer.append( Util.getResourceString( R.string.PLAYER_B ) );
				buffer.append( Util.getResourceString( R.string.TITLE_WIN_VS_HUMAN ) );
				break;
			}
		}
		return buffer.toString( ).trim( );
	}

	private void sendEmail( )
	{
		this.startActivity( new Intent( Intent.ACTION_SENDTO, 
				Uri.parse( Util.getResourceString( R.string.MSG_AUTHOT_CONTRACT ) ) ) );
	}
}