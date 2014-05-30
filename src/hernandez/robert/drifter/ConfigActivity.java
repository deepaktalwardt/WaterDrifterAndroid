package hernandez.robert.drifter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConfigActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
		setupReturntoMainButton();
		setupCancelButton();
	}
	
	private void setupReturntoMainButton(){
    	Button messageButton = (Button)findViewById(R.id.btnOnConfig);
    	messageButton.setBackgroundResource(R.drawable.checkmark);
    	messageButton.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(ConfigActivity.this, "Configured accepted~", Toast.LENGTH_SHORT).show();
				//startActivity(new Intent(ConfigActivity.this,ConfigActivity.class));
				finish();
			}
		});
    }
	private void setupCancelButton(){
    	Button messageButton = (Button)findViewById(R.id.cancelBtn);
    	messageButton.setBackgroundResource(R.drawable.cancel);
    	messageButton.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(ConfigActivity.this, "Canceling", Toast.LENGTH_SHORT).show();
				//startActivity(new Intent(ConfigActivity.this,ConfigActivity.class));
				finish();
			}
		});
    }
}
