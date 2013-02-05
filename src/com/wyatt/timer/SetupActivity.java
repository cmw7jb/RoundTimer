//Cameron Wyatt
//cmw7jb@virginia.edu
package com.wyatt.timer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.casadelgato.widgets.NumberPicker;

public class SetupActivity extends Activity implements OnItemSelectedListener {

	private Spinner fight, rest;
	private NumberPicker rounds;

	private Context context;

	private static final String preferenceLocation = "BagAppPreferences";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.setup);

		context = getApplicationContext();

		rounds = (NumberPicker) findViewById(R.id.Rounds);

		SharedPreferences settings = getSharedPreferences(preferenceLocation, 0);
		int fightTimePref = settings.getInt("fightTime", 1);
		int restTimePref = settings.getInt("restTime", 3);
		int roundPref = settings.getInt("rounds", 5);

		populateSpinners(fightTimePref, restTimePref);

		rounds.setValue(roundPref);

		Button submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				SharedPreferences settings = getSharedPreferences(
						preferenceLocation, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("fightTime", (int) fight.getSelectedItemId());
				editor.putInt("restTime", (int) rest.getSelectedItemId());
				editor.putInt("rounds", rounds.getValue());

				editor.commit();

				Intent timerActivityIntent = new Intent(context,
						TimerActivity.class);
				startActivity(timerActivityIntent);
			}
		});
	}

	public void populateSpinners(int fightTime, int restTime) {
		fight = (Spinner) findViewById(R.id.FightPeriod);
		rest = (Spinner) findViewById(R.id.RestPeriod);

		List<CharSequence> fightTimes = new ArrayList<CharSequence>();
		List<CharSequence> restTimes = new ArrayList<CharSequence>();

		for (int i = 30; i < 330; i += 30) {
			String time = "";
			if (i == 60) {
				time = "1 minute";
			} else if (i == 90) {
				time = "1 minute 30 seconds";
			} else if (i < 60) {
				time = i + " seconds";
			} else {
				if (i % 60 == 0) {
					time = i / 60 + " minutes";
				} else {
					time = i / 60 + " minutes " + i % 60 + " seconds";
				}
			}
			fightTimes.add(time);
		}
		ArrayAdapter<CharSequence> fightAdapter = new ArrayAdapter<CharSequence>(
				context, android.R.layout.simple_spinner_item, fightTimes);
		fightAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fight.setAdapter(fightAdapter);
		fight.setSelection(fightTime);
		fight.setOnItemSelectedListener(this);

		for (int i = 30; i < 630; i += 30) {
			String time = "";
			if (i == 60) {
				time = "1 minute";
			} else if (i == 90) {
				time = "1 minute 30 seconds";
			} else if (i < 60) {
				time = i + " seconds";
			} else {
				if (i % 60 == 0) {
					time = i / 60 + " minutes";
				} else {
					time = i / 60 + " minutes " + i % 60 + " seconds";
				}
			}
			restTimes.add(time);
		}
		ArrayAdapter<CharSequence> restAdapter = new ArrayAdapter<CharSequence>(
				context, android.R.layout.simple_spinner_item, restTimes);
		restAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rest.setAdapter(restAdapter);
		rest.setSelection(restTime);
		rest.setOnItemSelectedListener(this);

	}

	//The first item in the spinner list is position 0
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}
}