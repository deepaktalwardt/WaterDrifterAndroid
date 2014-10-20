package hernandez.robert.drifter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ConfigActivity extends Activity {
	private final String TAG = "ConfigActivity";
	Spinner configSpinner;//variable that holds choices(choices stored in strings-inside values-)
	Button cancelbtn;//a simple redirect button
	Button configbtn;//a button that passes values depending on its configuration
	Button wifi_btn;//a window to allow the bluetooth config
	EditText inputval;//text to input the interval
	
	@Override//all the work happens in onCreate, sets up the activity
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);//made separate functions to set buttons for clean code
		setupSpinnVal();
		setupCancelButton();
		setupConfigButton();
		setupBluetoothBtn();
	}
	
	

	//**********non built in functions**********

	
	//a helper function that sets up the spinner and value pointers
	private void setupSpinnVal() {
		configSpinner = (Spinner)findViewById(R.id.spinnerDrifter);
		inputval = (EditText)findViewById(R.id.inputfor_interval);//presented to 5000
	}
	
	//setup Bluetooth new feature
	private void setupBluetoothBtn() {
		// TODO Auto-generated method stub
		wifi_btn = (Button)findViewById(R.id.Bluetooth_id);
		wifi_btn.setBackgroundResource(R.drawable.stat_sys_data_bluetooth);
		wifi_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG,"bluetooth clicked");
				Intent intent = new Intent(getBaseContext(),BluetoothSetup.class);
				startActivity(intent);
			}
		});
	}

	//a helper function to set configuration to send values over to Main
	private void setupConfigButton(){
		configbtn = (Button)findViewById(R.id.btnOnConfig);
		configbtn.setBackgroundResource(R.drawable.checkmark);
		configbtn.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				Log.d(TAG,"clicked config confirm");
				String spinnerval = configSpinner.getSelectedItem().toString();
				int interval = Integer.parseInt(inputval.getText().toString());
				Intent intent = new Intent();
				intent.putExtra("interval_num", interval);
				intent.putExtra("spinnerval", spinnerval);
				setResult(Activity.RESULT_OK,intent);	
				Toast.makeText(ConfigActivity.this, "Configured accepted~", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
    }
	
	//a simple helper function to return back to Main
	private void setupCancelButton(){
    	cancelbtn = (Button)findViewById(R.id.cancelBtn);
    	cancelbtn.setBackgroundResource(R.drawable.cancel);
    	cancelbtn.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO remove this and set a log for this
				Toast.makeText(ConfigActivity.this, "Canceling", Toast.LENGTH_SHORT).show();
				//startActivity(new Intent(ConfigActivity.this,ConfigActivity.class));
				Intent intent = new Intent();
				setResult(Activity.RESULT_CANCELED,intent);
				finish();
			}
		});
    }
}
