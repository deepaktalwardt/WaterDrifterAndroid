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
import android.hardware.Camera;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.hardware.Camera.CameraInfo;



public class GeoService extends Service {
	private static final String TAG = "GeoService";
	//the one that request the GPS request
	private LocationManager lm; 
	//the one that grabs the GPS requests
	private myLocationListener loclisten; 
	//Default time as request by Drifter project
	private static final int LOCATION_INTERVAL = 5000; 
	//TODO: 20f; Requested 0, but maybe so distance would be good
	private static final float LOCATION_DISTANCE = 0;  
	private String driftername;
	private Integer interval;
	//if we need to use both network and GPS, provided an array
	//to test both connections
	private LocationListener[] mLocationListeners;
	private Camera camera;
	private int cameraId = 0;
	//needed for Broadcast
	private GeoService context;
	
	@Override
	public void onCreate()
	{
	    Log.d(TAG, "onCreate GeoService");
	    lm = null;
	    loclisten=null;
	    this.context=this;


	}
	
	@Override
	public void onDestroy() {
	    Log.d(TAG, "onDestroy GeoService");
		super.onDestroy();
		try{
			lm.removeUpdates(loclisten);//tells location manager to stop requests
			lm=null;//prevents any further calls
		}catch(Exception e){
			Log.d(TAG,"onDestroy GeoService failed");
		}
		Toast.makeText(this, "service ended", Toast.LENGTH_SHORT).show();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//calls the GPS and the camera to operate
	    Log.d(TAG, "onStartCommand");
	    driftername = intent.getStringExtra("dname");
	    interval = intent.getIntExtra("interval", LOCATION_INTERVAL);
	    Log.d(TAG, "The driftername selected is "+driftername+"and the interval set is "+interval);
	    setupLocManager();
	    gpadata(interval);
		Toast.makeText(this, "The interval is set at:"+interval+"for Drifter:"+driftername, Toast.LENGTH_SHORT).show();
	    /*
	     * If we are including a aysnch camera, call it here.
	     * Removed for now-Robert
	     */
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
   //**********non built in functions**********

	
	private void setupLocManager() {
		//Sets the location manager with the driftername and binds it to the GSP provider(or network if we select it)
		//TODO: we can make this optional with a parameter if we so choose to do so
		Log.d(TAG, "in setupLocManager");
	    if (lm == null) {
	        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
	    }
	    this.loclisten =  new myLocationListener(LocationManager.GPS_PROVIDER,context,driftername);
	    //TODO:The following line can be added to the array above to also include network besides GPS
	    /**Removed right now because we are testing on land
	     * 
	     * myLocationLister network = new myLocationListener(LocationManager.NETWORK_PROVIDER,this, driftername) 
	     * 
	     * //sets up the array variable
	    	this.mLocationListeners = new LocationListener[] {
		        this.loclisten, network
			};
	     * 
	     */
	}
	
	private void gpadata(int interval){
		//Sets the interval for the provided LocationManager
		//AKA this method sets the request and how frequent to make these requests
	    Log.d(TAG, "in gpadata");
		try {
			//TODO: instead of loclisten, you would use mLocationListener[0] and 1 for the second
	        lm.requestLocationUpdates(
	                LocationManager.GPS_PROVIDER, interval, LOCATION_DISTANCE,
	                loclisten);
		    Log.d(TAG, "gpadata gps has been selected");
	    } catch (java.lang.SecurityException ex) {
	        Log.d(TAG, "fail to request location update, ignore", ex);
	    } catch (IllegalArgumentException ex) {
	        Log.d(TAG, "gps provider does not exist " + ex.getMessage());
	    }
	}
	/* ----------
	 * The following is commented out as it is temporary disabled.
	 * 
	 * Deprecated until we test in the water. GPS is sufficient on land
	private void networkGPS(){
		try {
			loclisten = (myLocationListener) mLocationListeners[1];
	        lm.requestLocationUpdates(
	                LocationManager.NETWORK_PROVIDER, interval, LOCATION_DISTANCE,
	                loclisten);
		    Log.d(TAG, "gpadata network");
	    } catch (java.lang.SecurityException ex) {
	        Log.d(TAG, "fail to request location update, ignore", ex);
	    } catch (IllegalArgumentException ex) {
	        Log.d(TAG, "network provider does not exist, " + ex.getMessage());
	    } 
		
	}
	*/

	/* For the time being, we are blocking camera until we can 
	 * A) get it successfully working or 
	 * B) get a special camera censor
	private void startCamera() {
	    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
	      Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
	          .show();
	    } else {
	      cameraId = findBackFacingCamera();
	      if (cameraId < 0) {
	        Toast.makeText(this, "No front facing camera found.",
	            Toast.LENGTH_LONG).show();
	      } else {
	        camera = Camera.open(cameraId);
	    	Log.d(TAG, "camera open");
	        Toast.makeText(this, "front facing camera found.",
		            Toast.LENGTH_LONG).show();
	        //maybe pass the camera inside to take the photo?
	      }
	    }
	}
	
	private void asynchPhoto(){
		Log.d(TAG,"aychphoto");
		boolean safeToTakePicture = false;
		camera.startPreview();
		safeToTakePicture = true;
        if (safeToTakePicture) {
        	PhotoHandler handle = new PhotoHandler(context);
        	Log.d(TAG,"taking photo");
        	 try {
        		    SurfaceView mview = new SurfaceView(context);
        	        camera.setPreviewDisplay(mview.getHolder());
        	        camera.startPreview();
		        	Log.d(TAG,"take photo method");
        	        camera.takePicture(null,null,handle);
        	    } catch (IOException e) {
        	        // TODO Auto-generated catch block
        	        e.printStackTrace();
        	    }
        }
		// new CountDownTimer(5000,1000){

		//     @Override
		//     public void onFinish() {

		//     }

		//     @Override
		//     public void onTick(long millisUntilFinished) {
		//     }

		// }.start();
	}
	
	private int findBackFacingCamera() {
	    int cameraId = -1;
	    // Search for the front facing camera
	    int numberOfCameras = Camera.getNumberOfCameras();
	    for (int i = 0; i < numberOfCameras; i++) {
	      CameraInfo info = new CameraInfo();
	      Camera.getCameraInfo(i, info);
	      if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
	        Log.d(TAG, "Camera found");
	        cameraId = i;
	        break;
	      }
	    }
	    return cameraId;
	  }
	  *----------
	  */
//End of GeoService	
}

class myLocationListener implements LocationListener{
		//Modified LocationListener so that we can do aditional functions
		private static final String TAG = "myListenerInGeoService";
		Location lastloc;
		// so that we can broadcast
		Context context; 
		//name of drifter
		String drifterName;
		DBHelper mydb;

	    public myLocationListener(String provider, Context context, String driftname){
	        Log.d(TAG, "LocationListener provider is:" + provider+"for drifter"+driftname);
	        //sets default to 0
	        lastloc = new Location(provider);
			this.context=context;
			this.drifterName=driftname;
			//constructs the db only once, after that you have full access using mydb
		    this.mydb = new DBHelper(context);

	    }
	    
	    private void sendDataToMain(double pLat, double pLong,Context con){
	    	Log.d(TAG,"in sendingdatato Main");
	    	Intent intent= new Intent("gpsdata");
            intent.putExtra("lat", pLat);
            intent.putExtra("long", pLong);
            con.sendBroadcast(intent);//updates the phone
	    }
	      
		@Override
		//The heart of this app, the changing of GPS, and sending of data
		//Todo:before we set this, we can do something with the previous 
		//data if we so choose to do so
		public void onLocationChanged(Location location) {
			Log.d(TAG, "onLocationChanged for loc: "+ location);
			lastloc.set(location);
			if(lastloc != null){
				double pLong = lastloc.getLongitude();
				double pLat = lastloc.getLatitude();
				//the broadcaster established to be seen on MainActivity
				sendDataToMain(pLong,pLat,context);
				//sends the data to the server
	            postData(location);
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
	        Log.d(TAG, "driftername is"+this.drifterName);
		}
		
		//adding the post data to Visualizer
		public String postData(Location loc) {
		    // Create a new HttpClient and Post Header
			//TODO: the string can be formed for whatever you would like to check under the hood
		    String status="";
		    try {
		        // Add your data here
		        Log.d(TAG,"adding data for JSON");
		        String lat = Double.toString(loc.getLatitude());
		        String glong = Double.toString(loc.getLongitude());
		        String time = Double.toString(loc.getTime());
		        String valid = "true";
		        String speed = Double.toString(loc.getSpeed());
		        Log.d(TAG,"Saving to SQLite");
		        boolean backup = this.mydb.insertData(lat, glong, time, valid, this.drifterName, speed);
		        if(backup){
		        	Log.d(TAG, "backup was successful");
		        }
		        Log.d(TAG, "constructing the JSON");
		    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		        nameValuePairs.add(new BasicNameValuePair("latitude", lat));
		        nameValuePairs.add(new BasicNameValuePair("longitude", glong));
		        nameValuePairs.add(new BasicNameValuePair("time", time));
		        nameValuePairs.add(new BasicNameValuePair("valid_input", valid));
		        nameValuePairs.add(new BasicNameValuePair("drifter_name", this.drifterName));
		        nameValuePairs.add(new BasicNameValuePair("gps_speed", speed));
		        // Execute HTTP Post Request
		        Void doInBackground = new Connection().doInBackground(nameValuePairs);
		       	status += "post sent!";
		    }finally{}
		            
		    return status;
		} 
		
	}	
	
class Connection extends AsyncTask<ArrayList<NameValuePair>, Void, Void>{
			//this class connects to Rails to send the json data

		    protected Void doInBackground(ArrayList<NameValuePair>... nameValuePairs){
		        // get zero index of nameValuePairs and use that to post
		        ArrayList<NameValuePair> nvPairs = nameValuePairs[0];
		        try{
		            HttpClient httpclient = new DefaultHttpClient();
		            //where I mapped the create for the data being sent over
		            HttpPost httppost = new HttpPost("http://driftersensor.herokuapp.com/data");
		            //HttpPost httppost = new HttpPost("http://0.0.0.0:3000/data");
		            httppost.setEntity(new UrlEncodedFormEntity(nvPairs));
		            // Execute HTTP Post Request
		            HttpResponse response = httpclient.execute(httppost);
		            Log.d("postData", "the values sent"+nameValuePairs[0].toString());
		            Log.d("postData", "the response given"+response.getStatusLine().toString());
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