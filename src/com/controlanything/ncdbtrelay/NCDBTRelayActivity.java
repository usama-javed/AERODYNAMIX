package com.controlanything.ncdbtrelay;

import com.controlanything.ncdbtrelay.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class NCDBTRelayActivity extends Activity {
	/** Called when the activity is first created. */
	ControlPanel cPanel;
	private TableLayout table;
	public TableLayout tableContainer;
	ScrollView sView;
	ScrollView sViewRelays;
	private TableRow tableRow;
	private TableRow momentaryTableRow;
	EditText[] relayLabelEdits;
	String[] relayLabels;
	public int[] relayStatusArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	// n is the number of relays
	int n = 1;
	ImageButton[] controlButtons;
	CheckBox[] momentaryChecked;
	TextView[] relayTextLabels;
	TableLayout relayTableContainer;
	TableLayout relaysTableContainer;
	TableLayout deviceListTable;
	public ArrayAdapter<String> mPairedDevicesArrayAdapter;
	TextView deviceInfo;
	public int[] relayIndex = { 1, 2, 4, 8, 16, 32, 64, 128 };
	public TextView tvSocketConnection;
	public TextView tvSocketConnectionRelays;
	private int REQUEST_ENABLE_BT = 3;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	private Vibrator myVib;
	public int[] momentaryCheckedIntArray;

	boolean fusion = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvSocketConnection = new TextView(this);
		cPanel = ((ControlPanel) getApplicationContext());
		n = cPanel.getStoredInt("numberOfRelays");
		controlButtons = new ImageButton[n];
		relayTextLabels = new TextView[n];
		momentaryChecked = new CheckBox[n];
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		createSettingsPage();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			cPanel.disconnect();
			createSettingsPage();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		ViewGroup contentFrame = (ViewGroup) findViewById(android.R.id.content);
		if (contentFrame.getChildAt(0) == sView) {
			// createMainContainer();
			// setContentView(sViewRelays);
			// getRelayNames();
			// displayRelayNames();
			// updateButtonText();
			onResume();
		} else {
			System.exit(0);
		}

		return;
	}

	public void createSettingsPage() {
		tvSocketConnection = new TextView(this);

		sView = new ScrollView(this); // Instanciate the scroll view for the
										// settings page
		// sView.setBackgroundResource(R.drawable.backtest4); // set the
		// background
		// image of the
		// Scroll view

		sView.setBackgroundColor(Color.WHITE);

		table = new TableLayout(this); // Instanciates the table to hold our
										// buttons, textviews, edit texts, etc.
		table.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		tvSocketConnection.setText("Please Input the Device Information Below");
		tvSocketConnection.setTextSize(15);
		tvSocketConnection.setTextColor(Color.BLACK);
		tvSocketConnection.setBackgroundColor(Color.GREEN);
		table.addView(tvSocketConnection);

		TextView mainTitle = new TextView(this); // create a text view called
													// mainTitle
		mainTitle.setText("Edit Configuration");
		mainTitle.setTextColor(Color.BLACK);
		mainTitle.setTextSize(30);
		table.addView(mainTitle); // Add MainTitle to the Table View

		TextView availableDevices = new TextView(this);
		availableDevices.setText("Selected Device:");
		availableDevices.setTextColor(Color.BLACK);
		availableDevices.setTextSize(20);
		table.addView(availableDevices);

		// This May cause a problem in Shared Preferences.
		TextView deviceName = new TextView(this);
		deviceName.setText(cPanel.getStoredName("deviceName"));
		deviceName.setTextColor(Color.BLACK);
		deviceName.setTextSize(20);
		table.addView(deviceName);

		final Button changeDevice = new Button(this);

		changeDevice.setBackgroundResource(R.drawable.back_button);
		changeDevice.setText("Select Device");
		// changeDevice.setBackgroundResource(0);
		// changeDevice.setText("Change Selected Device");
		changeDevice.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				System.out.println("inside on click listener");
				createDeviceListPage();
			}
		});
		table.addView(changeDevice);

		// Drop Down box for number of relays
		tableRow = new TableRow(this);
		tableRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		TextView spinnerText = new TextView(this);
		spinnerText.setWidth(300);
		spinnerText.setTextSize(20);
		spinnerText.setTextColor(Color.BLACK);
		spinnerText.setText("Number of Relays");
		Spinner spinner = new Spinner(this);
		spinner.setSelection(2);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.numberOfRelays,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
		spinner.setAdapter(adapter);
		int tempInt = adapter.getPosition(String.valueOf(n));
		spinner.setSelection(tempInt);
		tableRow.addView(spinnerText);
		tableRow.addView(spinner);
		table.addView(tableRow);

		relaysTableContainer = new TableLayout(this);
		relaysTableContainer.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		table.addView(relaysTableContainer);

		final Button saveSettings = new Button(this);
		saveSettings.setBackgroundResource(R.drawable.back_button);
		saveSettings.setText("Save Settings");
		// saveSettings.setBackgroundResource(0);
		// saveSettings.setText("Save Settings");
		saveSettings.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (cPanel.getStoredString("address") == "n/a") {
					changeTitleToRedNoAddress();
				} else {
					System.out.println(cPanel.getStoredString("address"));
					if (cPanel.connect() == true) {
						System.out.println("Connected!!!");
						// if (cPanel.checkFusion()) {
						// System.out.println("Fusion");
						// fusion = true;
						// }
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						saveRelayLabels();
						createMainContainer();
						setContentView(sViewRelays);

					} else {
						changeTitleToRed();
					}
				}
			}
		});
		table.addView(saveSettings);

		sView.addView(table); // add the table to sView

		setContentView(sView);

	}

	private void saveRelayLabels() {
		EditText[] saveTargets = relayLabelEdits;

		// Creates string to create the database names on the fly
		String relayToSave;
		String momentaryToSave;

		int myNum = 0;
		cPanel.saveInt("numberOfRelays", n);
		for (int i = 0; i < n; i++) {
			relayToSave = "relay" + (i + 1);
			cPanel.saveString(relayToSave, relayLabelEdits[i].getText()
					.toString());

			// TODO make sure saving the ints here for momentarys work.
			momentaryToSave = "momentary" + (i + 1);
			if (momentaryChecked[i].isChecked()) {
				cPanel.saveInt(momentaryToSave, 1);
			} else {
				cPanel.saveInt(momentaryToSave, 0);
			}

		}

		// if (cPanel.connect() == true){

		createMainContainer();
		setContentView(sViewRelays);
		getRelayNames();
		displayRelayNames();
		// updateButtonText();
		// }
		// else {
		// changeTitleToRed();
		// }
	}

	// this creates the main table container that everything else is stored in.
	public void createMainContainer() {

		tvSocketConnection = new TextView(this);

		relayLabels = new String[n];
		momentaryCheckedIntArray = new int[n];
		getRelayNames();
		sViewRelays = new ScrollView(this);
		sViewRelays.setBackgroundColor(Color.WHITE);
		// sViewRelays.setBackgroundResource(R.drawable.backtest4);

		tableContainer = new TableLayout(this);

		tableContainer.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		TextView mainTitle = new TextView(this);

		changeTitleToGreen();
		tvSocketConnection.setTextSize(15);
		tvSocketConnection.setTextColor(Color.BLACK);
		tableContainer.addView(tvSocketConnection);
		mainTitle.setText("Relay Control");
		mainTitle.setTextSize(20);
		mainTitle.setTextColor(Color.BLACK);
		// tableContainer.addView(mainTitle);

		createTableContainer();

		sViewRelays.addView(tableContainer);
		getRelayNames();
		displayRelayNames();
		// updateButtonText();

	}

	public void displayRelayNames() {
		for (int i = 0; i < n; i++) {
			// M_Changed relayTextLabels[i].setText(relayLabels[i]);
			// relayTextLabels[i].setText("");

		}
	}

	// creates the table rows, relaylabels, and buttons. right now only gets the
	// status for bank 1
	public void createTableContainer() {

		for (int i = 0; i < n; i++) {

			tableRow = new TableRow(this);
			tableRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			if (i <= 7) {
				createButton(i, 1);
			} else {
				if (i <= 15) {
					createButton(i, 2);
				} else {
					if (i <= 23) {
						createButton(i, 3);
					} else {
						createButton(i, 4);
					}
				}
			}

			// updateButtonText(1);
			final TextView relayLabel = new TextView(this);
			relayLabel.setTextSize(20);
			relayLabel.setTextColor(Color.BLACK);
			relayLabel.setText("Relay" + i);
			tableRow.addView(relayLabel);
			relayTextLabels[i] = relayLabel;
			((TextView) relayTextLabels[i]).setTextSize(15);
			relayTextLabels[i].setHeight(120);
			relayTextLabels[i].setGravity(0x10);
			relayTextLabels[i].setWidth(300);

			tableContainer.addView(tableRow);
		}

		if (n <= 8) {
			updateButtonText(1);
		} else {
			if (n == 16) {
				updateButtonText(1);
				updateButtonText(2);
			} else {
				if (n == 24) {
					updateButtonText(1);
					updateButtonText(2);
					updateButtonText(3);
				} else {
					updateButtonText(1);
					updateButtonText(2);
					updateButtonText(3);
					updateButtonText(4);
				}
			}
		}
		System.out.println("here");
		final Button editButton = new Button(this);
		// editButton.setImageResource(R.drawable.editconfigbtv6);
		// editButton.setBackgroundResource(0);
		editButton.setBackgroundResource(R.drawable.back_button);
		editButton.setText("Edit Configuration");
		editButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				cPanel.disconnect();
				createSettingsPage();
			}
		});
		tableContainer.addView(editButton);

	}

	// creates the buttons that control the relays
	public void createButton(final int relayNumber, final int bankNumber) {

		final ImageButton relayButton = new ImageButton(this);
		relayButton.setAdjustViewBounds(true);
		relayButton.setImageResource(R.drawable.onbtn);
		relayButton.setBackgroundResource(0);
		if (fusion) {

			if (momentaryCheckedIntArray[relayNumber] == 0) {
				relayButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (relayStatusArray[relayNumber
								- ((bankNumber - 1) * 8)] == 0) {
							int[] status = cPanel.TurnOnRelayFusion(
									(relayNumber - ((bankNumber - 1) * 8)),
									bankNumber);

							if (status[0] == 260) {
								changeTitleToRed();
							} else {
								changeTitleToGreen();
							}
						} else {
							if (cPanel.TurnOffRelay(
									(relayNumber - ((bankNumber - 1) * 8)),
									bankNumber) == false) {
								changeTitleToRed();
							} else {
								changeTitleToGreen();
							}
						}
						System.out.println("About to get bank status");
						updateButtonText(bankNumber);
					}
				});

			}

		} else {
			if (momentaryCheckedIntArray[relayNumber] == 0) {
				relayButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (relayStatusArray[relayNumber
								- ((bankNumber - 1) * 8)] == 0) {
							if (cPanel.TurnOnRelay(
									(relayNumber - ((bankNumber - 1) * 8)),
									bankNumber) == false) {
								changeTitleToRed();
							} else {
								changeTitleToGreen();
							}
						} else {
							if (cPanel.TurnOffRelay(
									(relayNumber - ((bankNumber - 1) * 8)),
									bankNumber) == false) {
								changeTitleToRed();
							} else {
								changeTitleToGreen();
							}
						}
						System.out.println("About to get bank status");
						updateButtonText(bankNumber);
					}
				});

			} else {
				relayButton.setOnTouchListener(new View.OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							System.out.println("action down called");
							if (tvSocketConnection.getText().toString() == "Could Not Connect, Please Check Network Settings") {
								System.out.println("calling cPanel.connect");
								if (cPanel.connect() == true) {
									changeTitleToGreen();
								}
							} else {
								if (cPanel.TurnOnRelay(
										(relayNumber - ((bankNumber - 1) * 8)),
										bankNumber) == false) {
									changeTitleToRed();
								}
								updateButtonText(bankNumber);
							}
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							System.out.println("action up called");
							System.out.println("Sending command to turn relay"
									+ relayNumber + "Off");
							if (tvSocketConnection.getText().toString() == "Could Not Connect, Please Check Network Settings") {
								System.out.println("calling cPanel.connect");
								if (cPanel.connect() == true) {
									changeTitleToGreen();
								}
							}
							if (cPanel.TurnOffRelay(
									(relayNumber - ((bankNumber - 1) * 8)),
									bankNumber) == false) {
								changeTitleToRed();
							} else {
								System.out.println("Relay" + relayNumber
										+ "is now off");
							}
							updateButtonText(bankNumber);
						}
						return false;
					}
				});
			}
		}
		// Add created button to view
		tableRow.addView(relayButton);
		controlButtons[relayNumber] = relayButton;
	}

	// This changes the text in the relay control toggle buttons to either turn
	// on or turn off
	private void updateButtonText(int bankNumber) {
		relayStatusArray = cPanel.getBankStatus(bankNumber);
		if (n < 8) {
			for (int i = 0; i < n; i++) {

				if (relayStatusArray[i] != 0) {
					controlButtons[i + ((bankNumber - 1) * 8)]
							.setImageResource(R.drawable.onbtn);
					relayTextLabels[i].setText("Relay Status : On");

				} else {
					controlButtons[i + ((bankNumber - 1) * 8)]
							.setImageResource(R.drawable.offbtn);
					relayTextLabels[i].setText("Relay Status : Off");
				}
			}
		} else {
			for (int i = 0; i < 8; i++) {

				if (relayStatusArray[i] != 0) {
					controlButtons[i + ((bankNumber - 1) * 8)]
							.setImageResource(R.drawable.onbtn);
					relayTextLabels[i].setText("Relay Status : On");
				} else {
					controlButtons[i + ((bankNumber - 1) * 8)]
							.setImageResource(R.drawable.offbtn);
					relayTextLabels[i].setText("Relay Status : Off");
				}
			}
		}
	}

	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			n = Integer.parseInt(parent.getItemAtPosition(pos).toString());
			System.out.println(n);
			cPanel.saveInt("numberOfRelays", n);

			relayLabelEdits = new EditText[n];
			momentaryChecked = new CheckBox[n];
			relayLabels = new String[n];
			momentaryCheckedIntArray = new int[n];
			controlButtons = new ImageButton[n];
			relayTextLabels = new TextView[n];
			momentaryChecked = new CheckBox[n];
			getRelayNames();
			createRelayEditTexts();
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}

	public void getRelayNames() {
		System.out.println("getRelayNames Called");
		// This is called when settings page loads in order to get the names of
		// relays and whether or not they are momentary

		String relayToGet;
		String momentaryToGet;

		relayLabels = new String[n];
		momentaryCheckedIntArray = new int[n];
		for (int i = 0; i < n; i++) {
			relayToGet = "relay" + (i + 1);
			momentaryToGet = "momentary" + (i + 1);
			relayLabels[i] = cPanel.getStoredString(relayToGet);
			// TODO check stored momentary check and set the int Array
			momentaryCheckedIntArray[i] = cPanel.getStoredInt(momentaryToGet);
		}
	}

	private void createRelayEditTexts() {
		if (relayTableContainer == null) {
			relayTableContainer = new TableLayout(this);
		} else {
			relaysTableContainer.removeView(relayTableContainer);
			relayTableContainer = new TableLayout(this);
		}
		relayTableContainer.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		for (int i = 0; i < n; i++) {
			tableRow = new TableRow(this);
			tableRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			TextView relayNumberView = new TextView(this);
			relayNumberView.setTextSize(20);
			relayNumberView.setTextColor(Color.BLACK);
			relayNumberView.setText("Relay " + (i + 1));
			relayNumberView.setWidth(300);

			relayLabelEdits[i] = new EditText(this);
			relayLabelEdits[i].setText(relayLabels[i]);
			relayLabelEdits[i].setGravity(0x10);
			relayLabelEdits[i].setWidth(200);
			relayLabelEdits[i].setSingleLine();
			relayLabelEdits[i].setImeOptions(EditorInfo.IME_ACTION_DONE);

			tableRow.addView(relayNumberView);

			tableRow.addView(relayLabelEdits[i]);

			// createEditTextReturnListeners(i);

			// Second Table row for Momentary check box
			momentaryTableRow = new TableRow(this);
			momentaryTableRow.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			momentaryTableRow.setPadding(0, 0, 0, 40);

			// Text Label for Momentary Option
			TextView momentaryCheckTextLabel = new TextView(this);
			momentaryCheckTextLabel.setTextSize(15);
			momentaryCheckTextLabel.setTextColor(Color.GREEN);
			momentaryCheckTextLabel.setText("Button is Momentary?");
			momentaryCheckTextLabel.setWidth(300);

			// Checkbox for Momentary Option
			// TODO Finish This
			momentaryChecked[i] = new CheckBox(this);
			if (momentaryCheckedIntArray[i] == 1) {
				momentaryChecked[i].setChecked(true);
			}

			momentaryChecked[i].setHapticFeedbackEnabled(true);
			momentaryChecked[i].setHeight(20);

			momentaryTableRow.addView(momentaryCheckTextLabel);
			momentaryTableRow.addView(momentaryChecked[i]);

			relayTableContainer.addView(tableRow);
			relayTableContainer.addView(momentaryTableRow);

		}
		relaysTableContainer.addView(relayTableContainer);
	}

	private void createEditTextReturnListeners(final int relayNumber) {
		relayLabelEdits[relayNumber].setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					if (relayNumber == (n - 1)) {

						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								relayLabelEdits[(relayNumber)].getWindowToken(),
								0);
						return true;
					} else {
						relayLabelEdits[(relayNumber + 1)].requestFocus();
						return true;
					}
				}
				return false;
			}
		});
	}

	public void createDeviceListPage() {
		System.out.println("inside createDeviceList Method");
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_name);
		mPairedDevicesArrayAdapter = cPanel.getPairedDevices();

		ListView pairedListView = new ListView(this);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);

		pairedListView.setOnItemClickListener(mDeviceClickListener);

		cPanel.getPairedDevices();
		deviceListTable = new TableLayout(this);
		deviceListTable.addView(pairedListView);
		setContentView(deviceListTable);
		System.out.println("createDeviceList Method Complete");
	}

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// Get the device MAC address, which is the last 17 chars in the
			// View
			String info = ((TextView) v).getText().toString();
			String address = info.substring(info.length() - 17);
			String deviceName = info.substring(0, info.length() - 17);

			System.out.println(deviceName);
			System.out.println(address);
			cPanel.saveString("address", address);
			cPanel.saveName("deviceName", deviceName);
			System.out.println(deviceName);
			System.out.println(cPanel.getStoredString("address"));
			createSettingsPage();

		}
	};

	public void changeTitleToRed() {
		System.out.println("In Change Title Red Method");
		tvSocketConnection.setBackgroundColor(Color.RED);
		tvSocketConnection
				.setText("Could Not Connect, Please Check Network Settings");

	}

	public void changeTitleToRedNoAddress() {
		System.out.println("In Change Title Red Method");
		tvSocketConnection.setBackgroundColor(Color.RED);
		tvSocketConnection.setText("Please Select a Valid Device");

	}

	public void changeTitleToGreen() {
		System.out.println("In Change Title Green Method");
		tvSocketConnection.setBackgroundColor(Color.GREEN);
		tvSocketConnection.setText("Connected to Device:"
				+ " "
				+ cPanel.getStoredName("deviceName").substring(0,
						cPanel.getStoredName("deviceName").length() - 2));

	}

	public void changeTitleToYellow() {
		System.out.println("In Change Title Yellow Method");
		tvSocketConnection.setBackgroundColor(Color.YELLOW);
		tvSocketConnection.setText("NCD TCP Relay: Connecting....");
		System.out.println("Endof Yellow");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		if (cPanel.checkMac() == true) {
			if (cPanel.connect() == true) {
				System.out.println("Value of n: " + n);
				System.out.println("This is the stored Device Name"
						+ cPanel.getStoredName("deviceName"));
				createMainContainer();
				setContentView(sViewRelays);

			}
		} else {
			createSettingsPage();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		cPanel.disconnect();

	}

}