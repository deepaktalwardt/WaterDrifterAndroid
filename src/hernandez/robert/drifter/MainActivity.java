package hernandez.robert.drifter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final String TAG = "Main Activity";
    private BroadcastReceiver gpsreceiver;

	
	//when activity is established, calls all these functions to set up activitiy
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupEndService();
        setupService();
        setupRecieveVal();
	}
    
	private void setupRecieveVal() {
		// TODO Auto-generated method stub
		IntentFilter intentFilter=new IntentFilter("gpsdata");
        gpsreceiver=new BroadcastReceiver()
        {
			@Override
			public void onReceive(Context context, Intent intent) {
	   			Log.i(TAG,"Recieiving values");
				TextView textlat = (TextView)findViewById(R.id.lattext);
				TextView textlong = (TextView)findViewById(R.id.longtext);
				Double glong = intent.getDoubleExtra("lat", 0);
				Double lat = intent.getDoubleExtra("long", 0);
				textlat.setText(""+lat);  
				textlong.setText(""+glong); 
			}
        };
        this.registerReceiver(gpsreceiver, intentFilter);
		
	}

	//establishes options like the config that we have
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
   
   //allows for the config buttons to do something when clicked
   @Override
   public boolean onOptionsItemSelected(MenuItem item){
	   switch(item.getItemId()){
	   		case R.id.configureMenu:
	   			Log.i(TAG,"Config menu click");
	   			Intent intent = new Intent(MainActivity.this,ConfigActivity.class);//switches the activity
				startActivityForResult(intent,42);
	   			return true;
	   		default:
	   			return super.onOptionsItemSelected(item);
	   }
   }
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "onActivityResult");
		Log.i(TAG, "requestCode is"+requestCode);
		Log.i(TAG, "resultCode is"+resultCode);
		if(resultCode == Activity.RESULT_CANCELED){
			Intent intent = getIntent();
			String drifter_name = intent.getStringExtra("spinnerval");
			Log.i(TAG, "data is "+drifter_name);
		}else{
			switch(requestCode){
			case 42:
				//bind correct name
				String drifter_name = data.getStringExtra("spinnerval");
				TextView displayname = (TextView)findViewById(R.id.driftnametext);
				displayname.setText(""+drifter_name);    		
				
			}			
		}
	}

   
   //**********non built in functions**********
   
   
    //helper functions to set up buttons part 1
    private void setupEndService(){
    	Button messageButton = (Button)findViewById(R.id.messageButton);
    	messageButton.setBackgroundResource(R.drawable.stop);
    	messageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	   			//Log.i(TAG,"ended service");
				//Toast.makeText(MainActivity.this, "Turning off GPS!", Toast.LENGTH_SHORT).show();
				stopService(new Intent(getBaseContext(),GeoService.class));
			}
		});
    }
    //part 2 button
    private void setupService(){
    	Button messageButton = (Button)findViewById(R.id.configbutton);
    	messageButton.setBackgroundResource(R.drawable.start);
    	messageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	   			Log.i(TAG,"started service");
				//Toast.makeText(MainActivity.this, "starting GPS!", Toast.LENGTH_SHORT).show();
				//startActivity(new Intent(MainActivity.this,ConfigActivity.class));
	   			startService(new Intent(getBaseContext(),GeoService.class));
			}
		});
    }
}
