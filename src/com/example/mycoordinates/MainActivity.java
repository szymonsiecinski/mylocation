package com.example.mycoordinates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;
import android.provider.Settings;

public class MainActivity extends Activity implements LocationListener {

	boolean mGPSEnabled = false, mRunGPS = true;;
	long minTime = 1000;
	float minDistance = 10;
	static TextView mTextLat;
	static TextView mTextLongt;
	LocationManager mLocManag;
	double latitude;
	double longtitude;
	static Button mRunButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mLocManag = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		mGPSEnabled = mLocManag.isProviderEnabled(LocationManager.GPS_PROVIDER);
		//mLocManag.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
		
		//obsłuż zdażenie odświeżania lokalizacji

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		longtitude = savedInstanceState.getDouble("Longtitude");
		latitude = savedInstanceState.getDouble("Latitude");
		mRunGPS = savedInstanceState.getBoolean("RunGPS");
		mLocManag = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		mGPSEnabled = mLocManag.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		String[] zapisWspolrzednych = mZamienKoordynaty(latitude,
				longtitude);

		mTextLat.setText(zapisWspolrzednych[1]);
		mTextLongt.setText(zapisWspolrzednych[0]);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		
		outState.putDouble("Latitude", latitude);
		outState.putDouble("Longtitude", longtitude);
		outState.putBoolean("RunGPS", mRunGPS);
		super.onSaveInstanceState(outState);
	}

	public void onRunGPSClick(View v) {

		if (mRunGPS) {
			mLocManag = (LocationManager) this.getSystemService(LOCATION_SERVICE);
			mGPSEnabled = mLocManag.isProviderEnabled(LocationManager.GPS_PROVIDER);
			
			try {
				if (mGPSEnabled) {
					mLocManag.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, minTime, minDistance,
							this);

					Location loc = mLocManag
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);

					longtitude = loc.getLongitude();
					latitude = loc.getLatitude();

					String[] zapisWspolrzednych = mZamienKoordynaty(latitude,
							longtitude);

					mTextLat.setText(zapisWspolrzednych[1]);
					mTextLongt.setText(zapisWspolrzednych[0]);
					mRunGPS = false;
					mRunButton.setText("Wyłącz GPS");
				} else
					showSettingsAlert();
			} catch (Exception ex) {
				AlertDialog.Builder adlg = new AlertDialog.Builder(this);
				adlg.setMessage("Nie można ustalić lokalizacji");
				adlg.setNeutralButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}
						});
				adlg.show();
			}
		} else {
			mLocManag.removeUpdates(this);
			mLocManag = null;
			mRunGPS = true;
			mRunButton.setText("Uruchom GPS");
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 1) {
			switch (resultCode) {
			case 1:
				onResume();
				break;
			}
		}
	}

	private String[] mZamienKoordynaty(double latitude, double longtitude)
	{
		String[] koordynaty = new String[2];
		String ns, we;
		int stopniWE, stopniNS, minutWE, minutNS;
		double sekundWE, sekundNS;
		
		if(latitude < 0)
		{
			ns = "S";
		}
		else
		{
			ns = "N";
		}
		
		if(longtitude < 0)
		{
			we = "W";
		}
		else
		{
			we = "E";
		}
		
		//długość geograficzna
		stopniWE = (int)Math.abs(longtitude);
		minutWE = (int)Math.abs((60*(longtitude - (int)longtitude)));
		sekundWE = 60*Math.abs(((60*(longtitude - (int)longtitude)) - (int)(60*(longtitude - (int)longtitude))));
		
		//szerokość geograficzna
		stopniNS = (int)Math.abs(latitude);
		minutNS = (int)Math.abs(60*(latitude - (int)latitude));
		sekundNS = 60*Math.abs((60*(latitude - (int)latitude)-(int)(60*(latitude - (int)latitude))));
		
		koordynaty[0] = String.format("%d°%d\'%.2f\" %s", stopniWE, minutWE, sekundWE, we);
		koordynaty[1] = String.format("%d°%d\'%.2f\" %s", stopniNS, minutNS, sekundNS, ns);
		
		return koordynaty;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			//ustawienie czasu
			Time czas = new Time();
			czas.setToNow();
			
			//ustawienie zawartosci do zapisania
			String formatCzasu = String.format("%d-%d-%d %d:%d:%d %s",
					czas.year, czas.month, czas.monthDay,
					czas.hour, czas.minute, czas.second, czas.timezone);
			
			String[] wspolrzedne = mZamienKoordynaty(latitude, longtitude);
			
			String zawartosc = String.format
					("Długość geograficzna: %s\nSzerokość gegoraficzna: %s\nZarejestrowano: %s",
							wspolrzedne[0], wspolrzedne[1], formatCzasu);
			
			//zapis pliku do pamięci zewnętrznej
			String filename = String.format("Punkt %d-%d-%d %d.%d.%d.txt",
					czas.year, czas.month, czas.monthDay,
					czas.hour, czas.minute, czas.second);
			try {
				File pplik = new File(Environment.getExternalStorageDirectory(), filename);
				FileOutputStream plik = new FileOutputStream(pplik);
				
				plik.write(zawartosc.getBytes());
				plik.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Log.d(STORAGE_SERVICE, "Nie ma takiego pliku", e);
			} catch (IOException e) {
				Log.d(STORAGE_SERVICE, "Błąd zapisu pliku", e);
			}
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			mTextLat = (TextView) rootView.findViewById(R.id.textViewLat);
			mTextLongt = (TextView) rootView.findViewById(R.id.textViewLongt);
			mRunButton = (Button) rootView.findViewById(R.id.button1);
			
			return rootView;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		
		longtitude = location.getLongitude();
		latitude = location.getLatitude();
		
		String[] zapisWspolrzednych = mZamienKoordynaty(latitude, longtitude);
		
		mTextLat.setText(zapisWspolrzednych[1]);
		mTextLongt.setText(zapisWspolrzednych[0]);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	public void showSettingsAlert(){
		final Context context = this.getApplicationContext();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
      
        // Setting Dialog Title
        alertDialog.setTitle("Ustawienia GPS");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS nie jest włączony. Czy chcesz włączyć GPS?");
  
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }

	@Override
	public void onResume()
	{
		mGPSEnabled = mLocManag.isProviderEnabled(LocationManager.GPS_PROVIDER);
		super.onResume();
	}
}
