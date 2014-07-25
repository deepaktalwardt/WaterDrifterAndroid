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
	EditText inputval;//text to input the interval
	
	@Override//all the work happens in onCreate, sets up the activity
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);//made separate functions to set buttons for clean code
		setupSpinnVal();
		setupCancelButton();
		setupConfigButton();
	}
	
	//**********non built in functions**********

	
	//a helper function that sets up the spinner and value pointers
	private void setupSpinnVal() {
		configSpinner = (Spinner)findViewById(R.id.spinnerDrifter);
		inputval = (EditText)findViewById(R.id.inputfor_interval);//presented to 5000
	}

	//a helper function to set configuration to send values over to Main
	private void setupConfigButton(){
		configbtn = (Button)findViewById(R.id.btnOnConfig);
		configbtn.setBackgroundResource(R.drawable.checkmark);
		configbtn.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO replace this with values to be sent over
				String spinnerval = configSpinner.getSelectedItem().toString();//value grabbed by this time
				int interval = Integer.parseInt(inputval.getText().toString());
				Intent intent = new Intent();
				intent.putExtra("interval_num", interval);
				intent.putExtra("spinnerval", spinnerval);
				setResult(Activity.RESULT_OK,intent);	
				Log.i(TAG, "data is "+spinnerval);
				Toast.makeText(ConfigActivity.this, "Configured accepted~", Toast.LENGTH_SHORT).show();
				//startActivity(intent);
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
