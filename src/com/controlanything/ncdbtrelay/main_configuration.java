package com.controlanything.ncdbtrelay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class main_configuration extends Activity {

	private Button screen_main;
	private Button settings_sentences;
	private Button default_times;
	private Button sync_time;
	private Button air_test_mode;
	private Button bluetooth_device;
	private Button default_formation;
	private Button check_flight;
	private Button about;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_configuration);
		/** set time to splash out */
		screen_main = (Button) findViewById(R.id.screen_main);
		settings_sentences = (Button) findViewById(R.id.settings_sentences);
		default_times = (Button) findViewById(R.id.default_times);
		sync_time = (Button) findViewById(R.id.sync_time);
		air_test_mode = (Button) findViewById(R.id.air_test_mode);
		bluetooth_device = (Button) findViewById(R.id.bluetooth_device);
		default_formation = (Button) findViewById(R.id.default_formation);
		check_flight = (Button) findViewById(R.id.check_flight);
		about = (Button) findViewById(R.id.about);

		screen_main.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(main_configuration.this,
						MainActivity.class);
				startActivity(in);
			}
		});

		settings_sentences.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(main_configuration.this,
						SenetenceSettingsActivity.class);
				startActivity(in);
			}
		});
		default_times.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(main_configuration.this,
						time_settings.class);
				startActivity(in);
			}
		});
		sync_time.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(main_configuration.this,
						sync_time_settings.class);
				startActivity(in);
			}
		});

		air_test_mode.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(main_configuration.this,
						AirTestMode.class);
				startActivity(in);
			}
		});

		bluetooth_device.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(main_configuration.this,
						bluetooth_device.class);
				startActivity(in);
			}
		});

		default_formation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(main_configuration.this,
						formation_settings.class);
				startActivity(in);
			}
		});

		check_flight.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(main_configuration.this,
						check_flight_settings.class);
				startActivity(in);
			}
		});

		about.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(main_configuration.this, about.class);
				startActivity(in);
			}
		});

	}

}