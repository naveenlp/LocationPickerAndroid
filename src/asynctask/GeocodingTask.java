package com.naveenlp.test.asynctask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.Marker;
import com.naveenlp.test.LocationPickerActivity;

public class GeocodingTask extends AsyncTask<Void, Void, Void> {
	Context mContext;
	String searchString;
	Marker mMarker;
	List<Address> addresses = null;
   
    public GeocodingTask(Context context, String searchString) {
        super();
        mContext = context;
        this.searchString = searchString;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        try {
            // Call the synchronous getFromLocation() method by passing in the lat/long values.
            addresses = geocoder.getFromLocationName(searchString, 20, 1.232586, 103.601685, 1.468726, 104.034271);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
	protected void onPostExecute(Void params) {
		super.onPostExecute(params);
		if(mContext!=null) {
			((LocationPickerActivity) mContext).locationsObtained(addresses);
		}
	}
    
    
}