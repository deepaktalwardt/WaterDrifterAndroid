package hernandez.robert.drifter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class GeoService extends Service {
	private static final String TAG = "GeoService";
	//Setting up Locationservice
	private LocationManager lm = null;
	private static final int LOCATION_INTERVAL = 5000;
	private static final float LOCATION_DISTANCE = 0;  //TODO: 20f; what default should this be?

	LocationListener[] mLocationListeners = new LocationListener[] {
	        new myLocationListener(LocationManager.GPS_PROVIDER,this),
	        new myLocationListener(LocationManager.NETWORK_PROVIDER,this)
	};
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Toast.makeText(this, "service ended", Toast.LENGTH_SHORT).show();;
	}
	
	@Override
	public void onCreate()
	{
	    Log.d(TAG, "onCreate");
	    setupLocManager();
	    try {
	        lm.requestLocationUpdates(
	                LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
	                mLocationListeners[1]);
	    } catch (java.lang.SecurityException ex) {
	        Log.i(TAG, "fail to request location update, ignore", ex);
	    } catch (IllegalArgumentException ex) {
	        Log.d(TAG, "network provider does not exist, " + ex.getMessage());
	    }
	    try {
	        lm.requestLocationUpdates(
	                LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
	                mLocationListeners[0]);
	    } catch (java.lang.SecurityException ex) {
	        Log.i(TAG, "fail to request location update, ignore", ex);
	    } catch (IllegalArgumentException ex) {
	        Log.d(TAG, "gps provider does not exist " + ex.getMessage());
	    }
	}

	private void setupLocManager() {
		Log.d(TAG, "initializeLocationManager");
	    if (lm == null) {
	        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
	    }		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    Log.d(TAG, "onStartCommand");
		Toast.makeText(this, "service initiated", Toast.LENGTH_SHORT).show();;
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
	class myLocationListener implements LocationListener{
		private static final String TAG = "myListenerInGeoService";
		Location lastloc;
		
		Context context;  //<< declare context here

	    public myLocationListener(String provider, Context context){
	        Log.d(TAG, "LocationListener " + provider);
	        lastloc = new Location(provider);
			  this.context=context;

	    }
		@Override
		public void onLocationChanged(Location location) {
			Log.d(TAG, "onLocationChanged: " + location);
			lastloc.set(location);
			if(lastloc != null){
				double pLong = lastloc.getLongitude();
				double pLat = lastloc.getLatitude();
				//String latLongString = "Lat:" + location.getLatitude() + "\nLong:" + location.getLongitude();
	            Intent intent= new Intent("gpsdata");
	            intent.putExtra("lat", pLat);//putIntegerArrayListExtra("lat", (ArrayList<Integer>) lat);
	            intent.putExtra("long", pLong);//putIntegerArrayListExtra("lon", (ArrayList<Integer>) lon);
	            context.sendBroadcast(intent); 
				
			}
			
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
	        Log.d(TAG, "onStatusChanged: " + provider);			
		}

		@Override
		public void onProviderEnabled(String provider) {
	        Log.d(TAG, "onProviderEnabled: " + provider);
		}

		@Override
		public void onProviderDisabled(String provider) {
	        Log.d(TAG, "onProviderDisabled: " + provider);            		
		}
	}