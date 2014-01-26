package com.controlanything.ncdbtrelay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class about extends Activity {

	TextView link;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		/** set time to splash out */
		link = (TextView) findViewById(R.id.textView8);
		link.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String url = "http://www.teamaerodynamix.com";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

	}

}