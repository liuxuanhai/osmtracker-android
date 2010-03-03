package me.guillaumin.android.osmtracker.activity;

import me.guillaumin.android.osmtracker.db.WaypointListAdapter;
import me.guillaumin.android.osmtracker.service.gps.GPSLogger;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.CursorAdapter;

/**
 * Activity that list the previous waypoints tracked
 * by the user.
 * 
 * @author Nicolas Guillaumin
 *
 */
public class WaypointList extends ListActivity {

	/**
	 * GPS Logger service.
	 */
	private GPSLogger gpsLogger;
	
	/**
	 * Service connection to the GPS logger service.
	 */
	private ServiceConnection gpsLoggerConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			gpsLogger = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			gpsLogger = ((GPSLogger.GPSLoggerBinder) service).getService();
			Cursor wpCursor = gpsLogger.getDataHelper().getWaypointsCursor();
			if (wpCursor != null) {
				setListAdapter(new WaypointListAdapter(WaypointList.this, wpCursor));
			}
		}
	};

	@Override
	protected void onResume() {
		// Bind on service, to be able to get data.
		bindService(new Intent(this, GPSLogger.class), gpsLoggerConnection, 0);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// Unbind to service
		unbindService(gpsLoggerConnection);
		CursorAdapter adapter = (CursorAdapter) getListAdapter();
		if (adapter != null) {
			// Properly close the adapter cursor
			adapter.getCursor().close();
			setListAdapter(null);
		}
		
		super.onPause();
	}
	
}
