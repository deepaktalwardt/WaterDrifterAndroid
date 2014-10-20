package hernandez.robert.drifter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "Drifter.db";
	public static final String DATA_TABLE_NAME = "data";
	public static final String DATA_COLUMN_ID = "id";
	public static final String DATA_COLUMN_LATITUDE = "latitude";
	public static final String DATA_COLUMN_LONGITUDE = "longitude";
	public static final String DATA_COLUMN_TIME = "time";
	public static final String DATA_COLUMN_VALID_INPUT = "valid_input";
	public static final String DATA_COLUMN_DRIFTER_NAME = "drifter_name";
	public static final String DATA_COLUMN_GPS_SPEED = "gps_speed";

	
	public DBHelper(Context context){
		super(context, DATABASE_NAME,null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"create table data"+
				"(id integer primary key, latitude text, longitude text, time text, valid_input text, drifter_name text, gps_speed text)"
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//if you change the version number, this method is called
		//db.execSQL("DROP TABLE IF EXISTS data");
		//onCreate(db);
		
	}
	public boolean insertData(String latitude, String longitude, String time, String valid, String drif, String speed){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cV = new ContentValues();
		try{
			cV.put("latitude",latitude);
			cV.put("longitude", longitude);
			cV.put("time", time);
			cV.put("valid_input", valid);
			cV.put("drifter_name",drif);
			cV.put("gps_speed", speed);
			db.close();
			return true;
			
		}catch(Exception e){
			db.close();
			return false;
		}
	}
	public int numOfRows(){
		SQLiteDatabase db= this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, DATA_TABLE_NAME);
		return numRows;
	}
	
	public ArrayList<String> getAllData(){
		ArrayList<String> arr = new ArrayList<String>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor resp = db.rawQuery("select * from data", null);
		resp.moveToFirst();
		while(resp.isAfterLast()==false){
			String datasample = resp.getString(resp.getColumnIndex(DATA_COLUMN_LATITUDE)) +
								resp.getString(resp.getColumnIndex(DATA_COLUMN_LONGITUDE)) +
								resp.getString(resp.getColumnIndex(DATA_COLUMN_TIME)) +
								resp.getString(resp.getColumnIndex(DATA_COLUMN_VALID_INPUT)) +
								resp.getString(resp.getColumnIndex(DATA_COLUMN_DRIFTER_NAME)) +
								resp.getString(resp.getColumnIndex(DATA_COLUMN_GPS_SPEED));
			arr.add(datasample);
			resp.moveToNext();
		}
		return arr;
	}
}
