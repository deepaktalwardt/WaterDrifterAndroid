package hernandez.robert.drifter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final String TAG = "MainActivity";
    private BroadcastReceiver gpsreceiver;
    private String driftername;
    private Integer interval_val;
    private TextView textlat;
    private TextView textlong;
    private TextView displayname;


	
	//when activity is established, calls all these functions to set up activity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textlat = (TextView)findViewById(R.id.lattext);
		textlong = (TextView)findViewById(R.id.longtext);
		displayname = (TextView)findViewById(R.id.driftnametext);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setupEndService();
        setupService();
        setupRecieveVal();
		Log.d(TAG,"Main activity finish creating");
	}
    

	//establishes options like the configuration that we have
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
   
   //allows for the configuration buttons to do something when clicked
   @Override
   public boolean onOptionsItemSelected(MenuItem item){
	   switch(item.getItemId()){
	   		case R.id.configureMenu:
		   		Log.d(TAG,"Config menu click");
	   			Intent intent = new Intent(MainActivity.this,ConfigActivity.class);
	   			//switches activity to configuration
				startActivityForResult(intent,42);
	   			return true;
	   		default:
	   			return super.onOptionsItemSelected(item);
	   }
   }
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult with requestCode="+requestCode+" And resultCode is"+resultCode);
		if(requestCode == 42){
			if(resultCode == Activity.RESULT_OK){
				Log.d(TAG, "Update fields");
				driftername = data.getStringExtra("spinnerval");
				interval_val = data.getIntExtra("interval_num",5000);
				displayname.setText(driftername.toString()); 
			}
			else if(resultCode == Activity.RESULT_CANCELED){
				Log.d(TAG, "Cancle was clicked so don't update anything");
			}
		}
	}

   
   //**********non built in functions**********
   
   
    //helper functions to set up cancel button
    private void setupEndService(){
    	Button messageButton = (Button)findViewById(R.id.messageButton);
    	messageButton.setBackgroundResource(R.drawable.stop);//sets the picture to X
    	messageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
	   			Log.d(TAG,"ended service");
	   			//call stop on the service
   				stopService(new Intent(getBaseContext(),GeoService.class));
   				try{
   					//we put this under a try catch in case the user
   					//click cancel more than once
   					unregisterReceiver(gpsreceiver);
   				}
   				catch(Exception e){
   					Log.d(TAG, "already removed");
   				}
			}
		});
    }
    //set up start button
    private void setupService(){
    	Button messageButton = (Button)findViewById(R.id.configbutton);
    	messageButton.setBackgroundResource(R.drawable.start);
    	messageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
	   			Log.d(TAG,"started service");
				Intent intent = new Intent(getBaseContext(),GeoService.class);
				if(driftername==null){
					Toast.makeText(MainActivity.this, "please set the name in the configurations", Toast.LENGTH_SHORT).show();
				}else if(interval_val == null){
					Toast.makeText(MainActivity.this, "please set the interval in the configurations", Toast.LENGTH_SHORT).show();
				}
				else{
					//once this has been set in config, send this values to the GeoService
					intent.putExtra("dname", driftername);
					intent.putExtra("interval", interval_val);
		   			startService(intent);
				}
			}
		});
    }
    
    
    private void setupRecieveVal() {
		Log.d(TAG,"Registering GPS data on main screen");
		IntentFilter intentFilter = new IntentFilter("gpsdata");
        gpsreceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
	   			Log.d(TAG,"Recieiving values from GeoService");
	   			//grab the values of the current location and set them to the view
				Double glong = intent.getDoubleExtra("lat", 0);
				Double lat = intent.getDoubleExtra("long", 0);
				textlat.setText(Double.toString(lat));  
				textlong.setText(Double.toString(glong));
				//this is just a handy UI to show it changed
				//This can be removed
				Toast.makeText(MainActivity.this, "GPS recorded to DB", Toast.LENGTH_SHORT).show();
			}
        };
        this.registerReceiver(gpsreceiver, intentFilter);
		
	}
}
