package com.controlanything.ncdbtrelay;
import com.controlanything.ncdbtrelay.R;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class ControlPanel extends Application {

	final String PREFS_NAME = "Settings";

	private NCDBTRelayActivity mActivity;
	int x = 1;

	android.bluetooth.BluetoothSocket mmSocket;
	private BluetoothAdapter mBtAdapter;
	BluetoothDevice device;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;

	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	String connected_name;
	String address;
	byte[] outBuffer = new byte[6];

	// These are variables jacob built into this list
	int bankRelayStatus = 0; // this holds the value of inBuffer[0] which is the
	// first byte returned from the board for the
	// relaystatus command
	int[] individualRelayStatus = { 0, 0, 0, 0, 0, 0, 0, 0, 0 }; // this holds the array of indivdual relay status returned from the board
	byte[] inBuffer = new byte[6]; // this holds the values returned from the board
	
	boolean fusion = false;

	public void saveString(String ident, String sSave) {

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // Create	 SharedPreferences	 Object
		// Create Editor object for SharedPreferences Object
		SharedPreferences.Editor editor = settings.edit(); 
		
		editor.putString(ident, sSave); // Store String into Preferences file
		// settings.

		editor.commit(); // Save
	}

	public void saveName(String ident, String sSave) {

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // Create
		// SharedPreferences
		// Object
		SharedPreferences.Editor editor = settings.edit(); // Create Editor
		// object for
		// SharedPreferences
		// Object
		editor.putString(ident, sSave); // Store String into Preferences file
		// settings.

		editor.commit(); // Save
	}

	public void saveInt(String ident, int iSave) {

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // Create
		// SharedPreferences
		// Object
		SharedPreferences.Editor editor = settings.edit(); // Create Editor
		// object for
		// SharedPreferences
		// Object
		editor.putInt(ident, iSave); // Store Int into Preferences file
		// settings.

		editor.commit(); // Save

	}

	public String getStoredString(String ident) {

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // Create
		// SharedPreferences
		// Object
		String returnString = settings.getString(ident, "n/a"); // Read String
		// from Shared
		// Preferences
		// Object

		return returnString; // Return Requested String from Preferences File

	}

	public String getStoredName(String ident) {

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // Create
		// SharedPreferences
		// Object
		String returnString = settings.getString(ident, "No Device Selected"); // Read
		// String
		// from
		// Shared
		// Preferences
		// Object

		return returnString; // Return Requested String from Preferences File

	}

	public int getStoredInt(String ident) {

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // Create
		// SharedPreferences
		// Object
		int returnInt = settings.getInt(ident, 0); // Read String from Shared
		// Preferences Object

		return returnInt; // Return Requested Int from Preferences File

	}

	public boolean testConnection() {
		System.out.println("in test connection");
		byte[] buffer = new byte[1024];
		buffer[0] = (byte) 254;
		buffer[1] = (byte) 33;
		return false;

	}

	public ArrayAdapter getPairedDevices() {
		ArrayAdapter stuff = new ArrayAdapter(this, R.layout.device_name);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

		if (pairedDevices.size() > 0) {

			for (BluetoothDevice device : pairedDevices) {
				stuff.add(device.getName() + "\n" + device.getAddress());
			}
		} else {
			String noDevices = ("No Devices Available");
			stuff.add(noDevices);
		}
		return stuff;
	}

	// Calls Async Task to disconnect the socket, then prints line.
	public boolean checkMac() {

		if (getStoredString("address") == "n/a") {
			return false;
		}
		return true;

	}

	public boolean checkFusion(){
		byte[] buffer = new byte[3];
		buffer[0] = (byte)254;
		buffer[1] = (byte)53;
		buffer[2] = (byte)245;
		
		try {
			String deviceType = new getDeviceType().get();
			if(deviceType == "fusion"){
				fusion = true;
				return true;
			}else{
				if(deviceType == "proxr"){
					fusion = false;
					return false;
				}else{
					return false;
				}
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	public int[] TurnOnRelayFusion(int relay, int bank){
		int[] fail = {260, 260};
		
		outBuffer[0] = (byte) 170;
		outBuffer[1] = (byte) 3;
		outBuffer[2] = (byte) 254;
		outBuffer[3] = (byte) (relay + 108);
		outBuffer[4] = (byte) bank;
		outBuffer[5] = (byte) ((outBuffer[0]+outBuffer[1]+outBuffer[2]+outBuffer[3]+outBuffer[4])&255);
		
		try {
			int[] bankStatus = (new sendCommandFusion().execute(outBuffer).get());
			return bankStatus;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return fail;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return fail;
		}
	}
	
	public int[] TurnOffRelayFusion(int relay, int bank){
		int[] fail = {260, 260};
		
		outBuffer[0] = (byte) 170;
		outBuffer[1] = (byte) 3;
		outBuffer[2] = (byte) 254;
		outBuffer[3] = (byte) (relay + 100);
		outBuffer[4] = (byte) bank;
		outBuffer[5] = (byte) ((outBuffer[0]+outBuffer[1]+outBuffer[2]+outBuffer[3]+outBuffer[4])&255);
		
		try {
			int bankStatus[] = (new sendCommandFusion().execute(outBuffer).get());
			return bankStatus;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return fail;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return fail;
		}
	}
	
	// this method creates a byte array to send to the board to turn off a relay
	// and if successful returns true
	public boolean TurnOnRelay(int relay, int bank)

	{
		

			outBuffer[0] = (byte) 170;
			outBuffer[1] = (byte) 3;
			outBuffer[2] = (byte) 254;
			outBuffer[3] = (byte) (relay + 108);
			outBuffer[4] = (byte) bank;
			outBuffer[5] = (byte) ((outBuffer[0]+outBuffer[1]+outBuffer[2]+outBuffer[3]+outBuffer[4])&255);

			try {
				if (new sendCommand().execute(outBuffer).get() == true){
					return true;
				}
			} catch (InterruptedException e) {
				System.out.println("Interrupted Exception");
				if (isConnected() == false)
				{
					return false;
				}
			} catch (ExecutionException e) {
				System.out.println("Execution Exception");
				if (isConnected() == false)
				{
					return false;
				}
			}
			//		}
			return false;


	}

	// this method creates a byte array to send to the board to turn on a relay
	// and if successful returns true
	public boolean TurnOffRelay(int relay, int bank) {	 

		outBuffer[0] = (byte) 170;
		outBuffer[1] = (byte) 3;
		outBuffer[2] = (byte) 254;
		outBuffer[3] = (byte) (relay + 100);
		outBuffer[4] = (byte) bank;
		outBuffer[5] = (byte) ((outBuffer[0]+outBuffer[1]+outBuffer[2]+outBuffer[3]+outBuffer[4])&255);
		try {
			if (new sendCommand().execute(outBuffer).get() == true){
				return true;
			}
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {
			if (isConnected() == false)
			{
				return false;
			}
		}

		return false;

	}

	// jacob built
	public void queryBoardRelayBankStatus(int bank) {
//		outBuffer[0] = (byte) 254;
//		outBuffer[1] = (byte) 124;
//		outBuffer[2] = (byte) bank;
		
		outBuffer[0] = (byte) 170;
		outBuffer[1] = (byte) 3;
		outBuffer[2] = (byte) 254;
		outBuffer[3] = (byte) 124;
		outBuffer[4] = (byte) bank;
		outBuffer[5] = (byte) ((outBuffer[0]+outBuffer[1]+outBuffer[2]+outBuffer[3]+outBuffer[4])&255);

		try {
			bankRelayStatus = new readRelayStatus().execute(outBuffer).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int[] getBankStatus(int bank) {
		queryBoardRelayBankStatus(bank);
		// this line makes the negative return values that android sometimes
		// returns and makes it positive
		if (bankRelayStatus == 260){
			individualRelayStatus[0] = 260;
			return individualRelayStatus;
		}
		System.out.println("Doing Math in get Bank Status Method");
		if (bankRelayStatus < 0) {
			bankRelayStatus = bankRelayStatus + 256;
		}
		individualRelayStatus[8] = bankRelayStatus;
		if (bankRelayStatus > 127) {
			individualRelayStatus[7] = 128;
			bankRelayStatus = bankRelayStatus - 128;
		} else {
			individualRelayStatus[7] = 0;
		}
		if (bankRelayStatus > 63) {
			individualRelayStatus[6] = 64;
			bankRelayStatus = bankRelayStatus - 64;
		} else {
			individualRelayStatus[6] = 0;
		}
		if (bankRelayStatus > 31) {
			individualRelayStatus[5] = 32;
			bankRelayStatus = bankRelayStatus - 32;
		} else {
			individualRelayStatus[5] = 0;
		}
		if (bankRelayStatus > 15) {
			individualRelayStatus[4] = 16;
			bankRelayStatus = bankRelayStatus - 16;
		} else {
			individualRelayStatus[4] = 0;
		}
		if (bankRelayStatus > 7) {
			individualRelayStatus[3] = 8;
			bankRelayStatus = bankRelayStatus - 8;
		} else {
			individualRelayStatus[3] = 0;
		}
		if (bankRelayStatus > 3) {
			individualRelayStatus[2] = 4;
			bankRelayStatus = bankRelayStatus - 4;
		} else {
			individualRelayStatus[2] = 0;
		}
		if (bankRelayStatus > 1) {
			individualRelayStatus[1] = 2;
			bankRelayStatus = bankRelayStatus - 2;
		} else {
			individualRelayStatus[1] = 0;
		}
		if (bankRelayStatus > 0) {
			individualRelayStatus[0] = 1;
		} else {
			individualRelayStatus[0] = 0;
		}
		System.out.println("returning result of math in get relay status Method.");
		return individualRelayStatus;

	}

	public void disconnect(){
		if (mmSocket != null){
			try {
				mmSocket.close();
				mmSocket = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public boolean connect() {
		System.out.println("Just landed in connect() method");
		if (isConnected() == false) {

			mBtAdapter = BluetoothAdapter.getDefaultAdapter();
			if (getStoredString("address") != null) {
				System.out.println("Connecting to" + getStoredString("address"));
				device = mBtAdapter.getRemoteDevice(getStoredString("address"));
				try {
					System.out.println("Trying to Connect!!!!!!");
					mmSocket = device
							.createRfcommSocketToServiceRecord(java.util.UUID
									.fromString("00001101-0000-1000-8000-00805F9B34FB"));
					mmSocket.connect();
					System.out.println("Connected!!!!");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return true;
				} catch (java.io.IOException connectionException) {
					try {
						System.out.println("Could Not Connect!!!");
						mmSocket.close();
						return false;
					} catch (java.io.IOException closeException) {
						return false;
					}
				}
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public boolean isConnected() {
		System.out.println("in is connected");
		if (mmSocket == null) {
			return false;
		}
		if(device.getAddress() != getStoredString("address")){
			try {
				mmSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		outBuffer[0] = (byte) 170;
		outBuffer[1] = (byte) 2;
		outBuffer[2] = (byte) 254;
		outBuffer[3] = (byte) 33;
		outBuffer[4] = (byte) ((outBuffer[0]+outBuffer[1]+outBuffer[2]+outBuffer[3])&255);
		try {
			if (new sendCommand().execute(outBuffer).get() == true){
				return true;
			}
		} catch (InterruptedException e) {
			return false;
		} catch (ExecutionException e) {
			return false;
		}
		return false;

	}

	public class sendCommand extends AsyncTask<byte[], Integer, Boolean> {

		@Override
		protected Boolean doInBackground(byte[]... params) {

			try 
			{
				for(int i = 0; i < outBuffer.length; i++){
					System.out.println(outBuffer[i]);
				}
				mmSocket.getOutputStream().write(outBuffer, 0, outBuffer.length);
				
			} 

			catch (IOException e) 
			{
				System.out.println("sendCommand returning False1");
				return false;
			}

			try 
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Trying to read bytes from controller.");
				byte[] inputBuffer = new byte[4];
				mmSocket.getInputStream().read(inputBuffer);
				System.out.println("after read input");
				if(inputBuffer[0]==-86 && inputBuffer[1] != 0){
					if (((inputBuffer[0]+inputBuffer[1]+inputBuffer[2])&255)==inputBuffer[3]) 
					{
						if(inputBuffer[2]==85 || inputBuffer[2]==86){
							return true;
						}
					}
				}else{
					if(inputBuffer[0]==85 || inputBuffer[0]==86){
						return true;
					}
				}
			} 

			catch (IOException e) 
			{
				System.out.println("sendCommand returning False2");
				return false;
			}			
			System.out.println("sendCommand returning true2");
			return true;

		}

	}
	
	public class sendCommandFusion extends AsyncTask<byte[], Integer, int[]>{

		@Override
		protected int[] doInBackground(byte[]... command) {
			try 
			{
				for(int i = 0; i < outBuffer.length; i++){
					System.out.println(outBuffer[i]);
				}
				mmSocket.getOutputStream().write(outBuffer, 0, outBuffer.length);
				
			} 

			catch (IOException e) 
			{
				System.out.println("sendCommand returning False1");
				int[]fail = {260, 260};
				return fail;
			}

			try 
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Trying to read bytes from controller.");
				byte[] inputBuffer = new byte[5];
				mmSocket.getInputStream().read(inputBuffer);
				int[] bankStatus = null;
				
				for(int i = 2; i < 4; i++){
					if(inputBuffer[i]<0){
						bankStatus[i-2] = inputBuffer[i]+256;
					}else{
						bankStatus[i-2] = inputBuffer[i];
					}
					
				}
				
				return bankStatus;
				
				
			} 

			catch (IOException e) 
			{
				System.out.println("sendCommand returning False2");
				int[]fail = {260, 260};
				return fail;
			}			
		}
		
	}

	public class readRelayStatus extends AsyncTask<byte[], Integer, Integer>{

		@Override
		protected Integer doInBackground(byte[]... arg0) {
			int fail = 260;
			try 
			{
				for(int i = 0; i < outBuffer.length; i++){
					System.out.println(outBuffer[i]);
				}
				
				mmSocket.getOutputStream().write(outBuffer, 0, 6);
			} 

			catch (IOException e) 
			{
				System.out.println("isConnected returning False1");
				return fail;
			}

			try 
			{
				byte[] inputBuffer = new byte[4];
				System.out.println("Trying to read bank status from controller.");
				mmSocket.getInputStream().read(inputBuffer);

				if(inputBuffer[0]==-86 && inputBuffer[1] != 0){
					System.out.println("got here");
					for(int i = 0; i < inputBuffer.length; i ++){
						System.out.println(inputBuffer[i]);
					}
//					if(((inputBuffer[0]+inputBuffer[1]+inputBuffer[2])&255)==inputBuffer[3]){
						System.out.println("got here 2");
						int temp = inputBuffer[2];
						System.out.println("got here 3");
						return temp;
//					}
				}else{
					System.out.println("Not API");
					int temp = inputBuffer[0];
					System.out.println(temp);
					return temp;
				}
				
			} 

			catch (IOException e) 
			{
				System.out.println("isConnected returning False2");
				return fail;
			}
//			return null;
		}

	}

	public class getDeviceType extends AsyncTask<byte[], Integer, String>{

		@Override
		protected String doInBackground(byte[]... params) {
			
			byte[] buffer = new byte[3];
			buffer[0] = (byte)254;
			buffer[1] = (byte)53;
			buffer[2] = (byte)245;
			
			byte[] returnBuffer = new byte[1];
			try {
				mmSocket.getOutputStream().write(buffer, 0, buffer.length);
				
				mmSocket.getInputStream().read(returnBuffer);
				
				if((returnBuffer[0]&128) == 128){
					return "fusion";
				}else{
					return "proxr";
				}
			} catch (IOException e) {
				e.printStackTrace();
				return "fail";
			}
		}
		
	}
}
