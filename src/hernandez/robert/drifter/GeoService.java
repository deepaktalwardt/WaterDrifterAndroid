package hernandez.robert.drifter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class GeoService extends Service {
	private static final String TAG = "GeoService";
	//Setting up Locationservice
	private LocationManager lm = null;
	private static final int LOCATION_INTERVAL = 5000;
	private static final float LOCATION_DISTANCE = 0;  //TODO: 20f; what default should this be?
	String driftername;

	LocationListener[] mLocationListeners;
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Toast.makeText(this, "service ended", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onCreate()
	{
	    Log.d(TAG, "onCreate");
	    setupLocManager();
	}
	
	private void gpadata(){
		try {
	        lm.requestLocationUpdates(
	                LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
	                mLocationListeners[0]);
	    } catch (java.lang.SecurityException ex) {
	        Log.i(TAG, "fail to request location update, ignore", ex);
	    } catch (IllegalArgumentException ex) {
	        Log.d(TAG, "network provider does not exist, " + ex.getMessage());
	    }
	    try {
	        lm.requestLocationUpdates(
	                LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
	                mLocationListeners[1]);
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
	    driftername = intent.getStringExtra("dname");
	    this.mLocationListeners = new LocationListener[] {
		        new myLocationListener(LocationManager.GPS_PROVIDER,this,driftername),
		        new myLocationListener(LocationManager.NETWORK_PROVIDER,this,driftername)
		};
	    gpadata();
	    Toast.makeText(this, "service initiated"+driftername, Toast.LENGTH_SHORT).show();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
 class Connection extends AsyncTask<ArrayList<NameValuePair>, Void, Void>{

    protected Void doInBackground(ArrayList<NameValuePair>... nameValuePairs){
        // get zero index of nameValuePairs and use that to post
        ArrayList<NameValuePair> nvPairs = nameValuePairs[0];

        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://drifter-visualization.herokuapp.com/data");
            httppost.setEntity(new UrlEncodedFormEntity(nvPairs));

         // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            Log.d("postData", nameValuePairs[0].toString());

            Log.i("postData", response.getStatusLine().toString());
        } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{}
		return null;

    }

}

	class myLocationListener implements LocationListener{
		private static final String TAG = "myListenerInGeoService";
		Location lastloc;
		
		Context context; // so that we can broadcast
		String Driftername;//name of drifter

	    public myLocationListener(String provider, Context context, String driftname){
	        Log.d(TAG, "LocationListener " + provider);
	        lastloc = new Location(provider);
			this.context=context;
			this.Driftername=driftname;

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
	            String status = postData(location);
	            intent.putExtra("status",status);
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
		//adding the post data to Ruby
		public String postData(Location loc) {
		    // Create a new HttpClient and Post Header
		    //HttpClient httpclient = new DefaultHttpClient();
		    //HttpPost httppost = new HttpPost("http://0.0.0.0:3000/data");
		    String status="";
		    try {
		        // Add your data
		    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		        nameValuePairs.add(new BasicNameValuePair("latitude", loc.getLatitude()+""));
		        //status += " lat= "+lat.toString();
		        nameValuePairs.add(new BasicNameValuePair("longitude", loc.getLongitude()+""));
		        //status += " long= "+lon.toString();
		        Time now = new Time();
		        now.setToNow();
		        now.format2445();
		        String time = now.toString();
		        //status += " time= "+time;
		        System.out.println(time);
		        nameValuePairs.add(new BasicNameValuePair("time", time));
		        nameValuePairs.add(new BasicNameValuePair("valid_input", "true"));
		        //status += " valid= "+ true;
		        nameValuePairs.add(new BasicNameValuePair("drifter_name", this.Driftername));
		        status += " name= "+ this.Driftername;
		        nameValuePairs.add(new BasicNameValuePair("gps_speed", loc.getSpeed()+""));
		        status += " speed= "+ loc.getSpeed()+" ";
		        //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        // Execute HTTP Post Request
		        //status += "and before http post";
		        //HttpResponse response = httpclient.execute(httppost);
		        new Connection().doInBackground(nameValuePairs);
		       	status += "post sent!";
		    }finally{}
		        

		  
		    
		    return status;
		} 
		
	}