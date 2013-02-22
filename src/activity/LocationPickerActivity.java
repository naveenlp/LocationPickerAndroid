package com.naveenlp.test;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naveenlp.test.asynctask.GeocodingTask;
import com.naveenlp.test.asynctask.ReverseGeocodingTask;


public class LocationPickerActivity extends BaseActivity {
	SupportMapFragment mMapFragment;
	GoogleMap mMap;
	EditText searchBox;
	final String waitString = "Obtaining location..";
	boolean isStart;
	public static final String LATITUDE = "LATITUDE", LONGITUDE = "LONGITUDE", ADDRESS = "ADDRESS";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_picker);
		isStart = getIntent().getBooleanExtra(CreateUpdateTripsActivity.IS_START, true);
		initializeViews();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setScreenTitle("Pick Your "+(isStart?"Start":"Destination")+" Location");
	}

	private void initializeViews() {

		try {
			MapsInitializer.initialize(this);
			mMapFragment = new SupportMapFragment(){
				@Override
				public void onActivityCreated(Bundle savedInstanceState) {
					super.onActivityCreated(savedInstanceState);
					mMap = mMapFragment.getMap();
					if (mMap != null) {
						CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(1.344890, 103.809190));
						CameraUpdate zoom=CameraUpdateFactory.zoomTo(11);

						mMap.moveCamera(center);
						mMap.moveCamera(zoom);

						mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
							@Override
							public void onMapLongClick(LatLng latlong) {
								addMarker(latlong, waitString);
							}
						});
						mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
							@Override
							public void onInfoWindowClick(Marker marker) {
								String address = marker.getTitle();
								if(address.equals(waitString)) {
									Toast.makeText(getBaseContext(), "Please wait while the address is determined", Toast.LENGTH_LONG).show();
								} else {
									LatLng selectedPosition = marker.getPosition();
									Intent intent = new Intent();
									intent.putExtra(LATITUDE, selectedPosition.latitude);
									intent.putExtra(LONGITUDE, selectedPosition.longitude);
									intent.putExtra(ADDRESS, marker.getTitle());
									
									setResult(1, intent);
									finish();
								}
							}
						});
						
					}
				}
			};
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.add(R.id.map_holder, mMapFragment);
			fragmentTransaction.commit();

		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}

		searchBox = (EditText) findViewById(R.id.et_search_locations);
		findViewById(R.id.iv_search_clear).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchBox.setText("");
			}
		});
		
		findViewById(R.id.iv_search_locations).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performSearch();
			}
		});
		findViewById(R.id.iv_clear_map).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMap.clear();
			}
		});
		searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		            performSearch();
		            return true;
		        }
		        return false;
		    }
		});
	}
	
	private void performSearch() {
		String searchText = searchBox.getText().toString();
		if(searchText==null || searchText.equals("")) {
			Toast.makeText(getBaseContext(), "Please enter some text to search", Toast.LENGTH_LONG).show();
			return;
		} else {
			new GeocodingTask(LocationPickerActivity.this, searchText).execute(null);
		}
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
	}

	private void addMarker(LatLng latlong, String title) {
		Marker mMarker = mMap.addMarker(new MarkerOptions().position(latlong).title(title).snippet( "Tap to select..").
				icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
		mMarker.setPosition(latlong);
		mMarker.setTitle(title);
		if(title.equals(waitString)) 
			new ReverseGeocodingTask(this, latlong, mMarker).execute(null);
	}
	
	public void locationsObtained(List<Address> addresses) {
		mMap.clear();
		for(int i=0;i<addresses.size();i++) {
			Address address = addresses.get(i);
			LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
			addMarker(latlng, address.getAddressLine(0)+"");
		}
	}

}
