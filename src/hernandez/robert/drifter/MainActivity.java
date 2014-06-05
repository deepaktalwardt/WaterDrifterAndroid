package hernandez.robert.drifter;

import android.app.Activity;
import android.content.Intent;
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
	static MainActivity me;
	
	//when activity is established, calls all these functions to set up activitiy
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupMessageButton();
        setupConfigButton();
        setupView();
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
				startActivity(new Intent(MainActivity.this,ConfigActivity.class));
	   			return true;
	   		default:
	   			return super.onOptionsItemSelected(item);
	   }
   }
    private void setupView(){
    	//Get the intent that was called by configuration
    	Intent intent = getIntent();
    	//bind correct name
    	String drifter_name = intent.getStringExtra("spinnerval");
    	TextView displayname = (TextView)findViewById(R.id.driftnametext);
    	if(drifter_name == null || drifter_name.isEmpty()){
    		//preset value?
    	}else{
    		displayname.setText(""+drifter_name);    		
    	}
    }
   
   //**********non built in functions**********
   
    //helper functions to set up buttons part 1
    private void setupMessageButton(){
    	Button messageButton = (Button)findViewById(R.id.messageButton);
    	messageButton.setBackgroundResource(R.drawable.stop);
    	messageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "Turning off GPS!", Toast.LENGTH_SHORT).show();
			}
		});
    }
    //part 2 button
    private void setupConfigButton(){
    	Button messageButton = (Button)findViewById(R.id.configbutton);
    	messageButton.setBackgroundResource(R.drawable.start);
    	messageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	   			Log.i(TAG,"logggg");
				Toast.makeText(MainActivity.this, "starting GPS!", Toast.LENGTH_SHORT).show();
				//startActivity(new Intent(MainActivity.this,ConfigActivity.class));
			}
		});
    }
}
