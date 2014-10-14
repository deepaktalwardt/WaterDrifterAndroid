package hernandez.robert.drifter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CapPhoto extends Service
{
    private SurfaceHolder sHolder;    
    private Camera mCamera;
    private Parameters parameters;
    private static final String TAG = "CapPhoto";

    
	@Override
	public void onDestroy() {
		//default removing service
	    Log.d(TAG, "onDestroy  CapService");
		super.onDestroy();
		Toast.makeText(this, "cap service ended", Toast.LENGTH_SHORT).show();
	}


  @Override
    public void onCreate()
    {
      super.onCreate();
      Log.d(TAG, "on create");

      if (android.os.Build.VERSION.SDK_INT > 9) {
          StrictMode.ThreadPolicy policy = 
               new StrictMode.ThreadPolicy.Builder().permitAll().build();
          StrictMode.setThreadPolicy(policy);};
          Thread myThread = null;


  }
  
  @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//calls the GPS and the camera to operate
	   Log.d(TAG, "onStartCommand in cap");
     Camera.PictureCallback mCall = new PhotoHandler(this);
     Toast.makeText(this, "service photo start", Toast.LENGTH_SHORT).show();
     if (Camera.getNumberOfCameras() >= 2) { 

      mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT); }

    if (Camera.getNumberOfCameras() < 2) { 

      mCamera = Camera.open(); }
      SurfaceView sv = new SurfaceView(getApplicationContext());
     try {
               mCamera.setPreviewDisplay(sv.getHolder());
               parameters = mCamera.getParameters();
               parameters.setJpegQuality(100);  
               mCamera.setParameters(parameters);
               mCamera.startPreview();
               Log.d(TAG,"taking photo");
               mCamera.takePicture(null, null, mCall);
               Log.d(TAG,"photo taken");
         } catch (Exception e) { e.printStackTrace(); 
         }
         Log.d(TAG,"after try catch of taking photo");
        sHolder = sv.getHolder();
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    return START_STICKY;
  }
  
  //new Camera.PictureCallback()
//  {
//
//     public void onPictureTaken(final byte[] data, Camera camera)
//     {
//
//        FileOutputStream outStream = null;
//                try{
//
//                    File sd = new File(Environment.getExternalStorageDirectory(), "A");
//                    if(!sd.exists()) {                                 
//                      sd.mkdirs();
//                      Log.i("FO", "folder" + Environment.getExternalStorageDirectory());
//                    } 
//
//                        Calendar cal = Calendar.getInstance();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//                        String tar = (sdf.format(cal.getTime()));
//
//                        outStream = new FileOutputStream(sd+tar+".jpg");
//                        outStream.write(data);  outStream.close();
//
//                        Log.i("CAM", data.length + " byte written to:"+sd+tar+".jpg");
//                        camkapa(sHolder);               
//
//
//                 } catch (FileNotFoundException e){
//                    Log.d("CAM", e.getMessage());
//                } catch (IOException e){
//                    Log.d("CAM", e.getMessage());
//                }}
//  };

    public void camkapa(SurfaceHolder sHolder) {

        if (null == mCamera)
            return;
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        Log.i("CAM", " closed");
        }
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

    }