package com.naveenlp.test.asynctask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class ReverseGeocodingTask extends AsyncTask<Void, Void, Void> {
    Context mContext;
    LatLng loc;
   String addressText = "Location not determined..";
   Marker mMarker;
   
    public ReverseGeocodingTask(Context context, LatLng loc, Marker mMarker) {
        super();
        mContext = context;
        this.loc = loc;
        this.mMarker = mMarker;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        List<Address> addresses = null;
        try {
            // Call the synchronous getFromLocation() method by passing in the lat/long values.
            addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            // Format the first line of address (if available), city, and country name.
            addressText = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : addressText;
        }
        return null;
    }
    
    @Override
	protected void onPostExecute(Void params) {
		super.onPostExecute(params);
		if(mMarker!=null) {
			mMarker.setTitle(addressText);
			mMarker.hideInfoWindow();
			mMarker.showInfoWindow();
		}
	}
    
    
}