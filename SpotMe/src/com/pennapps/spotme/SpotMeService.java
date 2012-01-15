package com.pennapps.spotme;

import android.service.*;
import android.util.Log;
import android.widget.Toast;
import android.app.*;
import android.content.*;
import android.location.*;
import android.os.*;
import org.json.*;


public class SpotMeService extends Service{
	private LocationManager mlocManager; 
	private LocationListener mlocListener;
	private NotificationManager mNM;
	
	private int NOTIFICATION = R.string.app_name;
	
	public class LocalBinder extends Binder{
		 SpotMeService getService(){
			return SpotMeService.this;
		}
	}

	@Override
	public void onCreate(){
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		SharedPreferences settings = getSharedPreferences(Settings.PREFS_NAME,0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("activated",true);
		editor.commit();
		System.out.println(settings.getAll());
		showStartNotification();
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new MyLocationListener();
		mlocManager = locationManager;
		mlocListener = locationListener;
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,mlocListener);
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SpotMeService", "Received start id " + startId + ": " + intent);
		
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, "SpotMe stopped" , Toast.LENGTH_SHORT).show();
        SharedPreferences settings = getSharedPreferences(Settings.PREFS_NAME,0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("activated",false);
		editor.commit();
		System.out.println(settings.getAll());
		mlocManager.removeUpdates(mlocListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    private void showStartNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "SpotMe started";

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SpotMeActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "SpotMe activity",
                       text, contentIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
	
    private void showNotification(){
    	CharSequence text = "Spotted";
    	CharSequence info = "insert info";
    	
    	Notification notification = new Notification (R.drawable.ic_launcher, info, System.currentTimeMillis());
    	
    }

    public class MyLocationListener implements LocationListener{
    	public void onLocationChanged(Location loc){
    		JSONObject object = new JSONObject();
    			try{
    				object.put("lat", loc.getLatitude());
    				object.put("long",loc.getLongitude());
    			}catch(JSONException e){
    				e.printStackTrace();
    			}
    			System.out.println(object);
    		/*loc.getLatitude();

    		loc.getLongitude();

    		String Text = "My current location is:" +

    		"Latitude = " + loc.getLatitude() +

    		"Logitude = " + loc.getLongitude();


    		Toast.makeText( getApplicationContext(),

    		Text,

    		Toast.LENGTH_SHORT).show();
    		*/
        	}
        	public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {
            	Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
            }
            public void onProviderDisabled(String provider) {
            	Toast.makeText(getApplicationContext(),"GPS Disabled", Toast.LENGTH_SHORT).show();
            }

        };
        	
        }


