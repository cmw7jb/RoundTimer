package com.wyatt.timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TimerActivity extends Activity implements OnClickListener {

	private FightCountDownTimer countDownTimer;
	private boolean fighting;
	private Button startButton;

	private TextView timeRemainingTextView;
	private TextView roundsRemainingTextView;
	private TextView fightingIndicatorTextView;

	private static final String preferenceLocation = "BagAppPreferences";
	private int roundsRemaining;
	private long fightTime;
	private long restTime;

	/**
	 * TODO: 
	 * Stop the activity if the user goes back from the timer activity (on back button pressed) 
	 * Sounds 
	 * Count down from 5 (custom time eventually?) before starting 
	 * Get start button working (switch to view on click listener?) 
	 * Change start button to pause, resume, and reset when the time runs out 
	 * No minutes if 0
	 * 
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.timer);

		SharedPreferences settings = getSharedPreferences(preferenceLocation, 0);
		fightTime = convertToMillis(settings.getInt("fightTime", 1)); //Grab the flight time from the user preferences, and if it can't be found then default it to 30 seconds
		restTime = convertToMillis(settings.getInt("restTime", 2)); // Grab the rest time from the user preferences, and if it can't be found then default it to 60 seconds
		roundsRemaining = settings.getInt("rounds", 3); //Get the number of rounds from the user preferendes, and if it can't find it then default it to 3 rounds

		countDownTimer = new FightCountDownTimer(fightTime, 1); // start time, interval

		startButton = (Button) findViewById(R.id.StartButton);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		timeRemainingTextView = (TextView) findViewById(R.id.timeRemaining);
		roundsRemainingTextView = (TextView) findViewById(R.id.roundsRemaining);
		fightingIndicatorTextView = (TextView) findViewById(R.id.fighting);

		fighting = true;
		timeRemainingTextView.setText("Time remaining: " + (fightTime / 1000)
				+ " seconds");
		roundsRemainingTextView.setText("Rounds remaining: " + roundsRemaining);
		fightingIndicatorTextView
				.setText((fighting == true) ? "You are fighting"
						: "You are resting");
		countDownTimer.start();

	}

	// Each position represents 30 seconds, so take the position and add 1
	// (because it starts at 0) and multiply it by 30
	// Then multiply it by 1000 to convert to milliseconds
	public long convertToMillis(int position) {
		return 1000L * (position + 1) * 30L;
	}

	/**
	 * Converts the milliseconds passed in to a string array that contains the milliseconds broken up into minutes, seconds, and milliseconds
	 * @param millis
	 * @return
	 */
	public String[] convertToTimeString(long millis) {
		int minutes = 0;
		int seconds = 0;
		int milliseconds = 0;

		minutes = (int) (millis / 60000);
		seconds = (int) (millis % 60000) / 1000;
		milliseconds = (int) ((millis % 60000) - (seconds * 1000));

		String[] ret = { String.valueOf(minutes) + " minutes",
				String.valueOf(seconds) + " seconds",
				String.valueOf(milliseconds) + " milli" };

		return ret;

	}

	@Override
	public void onBackPressed() {
		if (roundsRemaining == 0) { // No need to prompt to quit
			// go back to setup activity
			// TODO: keep it running, or start a new one?
			TimerActivity.super.onBackPressed();
		} else {
			new AlertDialog.Builder(this)
					.setTitle("Really Stop?")
					.setMessage("Are you sure you want to stop the timer?")
					.setNegativeButton(android.R.string.no, null)
					.setPositiveButton(android.R.string.yes,
							new OnClickListener() {

								public void onClick(DialogInterface arg0,
										int arg1) {
									countDownTimer.cancel();
									TimerActivity.super.onBackPressed();
								}
							}).create().show();
		}
	}

	public class FightCountDownTimer extends CountDownTimer {

		public FightCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		@Override
		public void onFinish() {
			countDownTimer.cancel();
			if (fighting) { // if they were fighting before, its time to rest
				fighting = false; // they are no longer fighting
				if (roundsRemaining > 0) {
					roundsRemaining--; // make it so they fight one less time
					roundsRemainingTextView.setText("Fight rounds remaining: "
							+ roundsRemaining);
					fightingIndicatorTextView
							.setText((fighting == true) ? "You are fighting"
									: "You are resting");
					countDownTimer = new FightCountDownTimer(restTime, 1); // if they have more rounds to go, then they are resting
					countDownTimer.start(); //start the count down with the rest time
				}
			} else {
				fighting = true;
				if (roundsRemaining > 0) {
					roundsRemainingTextView.setText("Fight rounds remaining: "
							+ roundsRemaining);
					fightingIndicatorTextView
							.setText((fighting == true) ? "You are fighting"
									: "You are resting");
					countDownTimer = new FightCountDownTimer(fightTime, 1);
					countDownTimer.start();
				} else if (roundsRemaining == 0) {
					// The user is done, congratulate them
					roundsRemainingTextView.setText("No rounds remain!");
					fightingIndicatorTextView.setText("You're all done!");
					timeRemainingTextView.setText("0 seconds");
				}
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			String[] times = convertToTimeString(millisUntilFinished);
			timeRemainingTextView.setText("Time remaining: " + times[0] + " "
					+ times[1] + " " + times[2]);

		}

	}

	public void onClick(DialogInterface arg0, int arg1) {
		if (fighting) {
			countDownTimer.cancel();
			fighting = false;
			startButton.setText("Reset");
		} else {
			System.out.println("Trying to start the timer");
			countDownTimer.start();
			fighting = true;
			startButton.setText("Stop");
		}

	}

}
