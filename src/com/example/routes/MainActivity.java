package com.example.routes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.routes.adapter.PlacesAutoCompleteAdapter;
import com.example.routes.beans.Places;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity{

	private AutoCompleteTextView autoCompView, autoCompView2;
	private RadioGroup groupvalue;
	private Button submit;
	private Places place1, place2;
	private Stack<Places> references = new Stack<Places>();
	private String mode = "driving";
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dialog = new ProgressDialog(this);
	    dialog.setMessage("Fetching Routes...");
		autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
	    autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
	    autoCompView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				// TODO Auto-generated method stub
				place1 = (Places) adapterView.getItemAtPosition(position);
				autoCompView.setText(place1.description);
				//autoCompView.clearFocus();
				submit.requestFocus();
			}
		});
	    
	    autoCompView2 = (AutoCompleteTextView) findViewById(R.id.autocomplete2);
	    autoCompView2.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
	    autoCompView2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				// TODO Auto-generated method stub
				place2 = (Places) adapterView.getItemAtPosition(position);
				autoCompView2.setText(place2.description);
				//autoCompView2.clearFocus();
				submit.requestFocus();
			}
		});
	    
	    
	    groupvalue = (RadioGroup) findViewById(R.id.groupvalue);
	    submit = (Button) findViewById(R.id.submit);
	    submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.show();
				switch (groupvalue.getCheckedRadioButtonId()) {
				case R.id.travel_mode_car:
					Toast.makeText(MainActivity.this, "travel_mode_car", Toast.LENGTH_SHORT).show();
					mode = "driving";
					break;
				case R.id.travel_mode_bus:
					Toast.makeText(MainActivity.this, "travel_mode_bus", Toast.LENGTH_SHORT).show();
					mode = "transit";
					break;
				case R.id.travel_mode_walk:
					Toast.makeText(MainActivity.this, "travel_mode_walk", Toast.LENGTH_SHORT).show();
					mode = "walking";
					break;
				}
				
				references.push(place1);
				references.push(place2);
				new Thread(){

					@Override
					public void run() {
						/*PlaceDetials(place1.reference);*/
						boolean continues = true;
						while(!references.isEmpty()){
							String results = PlaceDetials(references.pop());
							if(results == null){
								continues =  false;
								mhandle.sendEmptyMessage(0);
								break;
							}
						}
						if(continues)
								mhandle.sendEmptyMessage(1);
					}}.start();
				
			}
		});
	}

	private Handler mhandle = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			if(msg.what==0)
				Toast.makeText(MainActivity.this, "some issue in api", Toast.LENGTH_SHORT).show();
			
			if(msg.what==1){
				Intent intent = new Intent(MainActivity.this, DirectionActivity.class);
				intent.putExtra("mode", mode);
				intent.putExtra("origin", place1.LatLng);
				intent.putExtra("destination", place2.LatLng);
				startActivity(intent);
			}
		}};
	
	private String PlaceDetials(Places referString){
		HttpClient client = new DefaultHttpClient();
		StringBuilder jsonResults = new StringBuilder();
		try {
			/*Uri url = new Uri.Builder()
								    .scheme("https")
								    .authority("maps.googleapis.com")
								    .path("maps/api/place/details/json")
								    .appendQueryParameter("reference", referString)
								    .appendQueryParameter("key", getString(R.string.place_api))
								    .appendQueryParameter("sensor", "true")
								    .build();
			HttpGet httpGet = new HttpGet(url.toString());*/
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("reference", referString.reference));
		    params.add(new BasicNameValuePair("key", getString(R.string.place_api)));
		    params.add(new BasicNameValuePair("sensor", "true"));
		    String url = "https://maps.googleapis.com/maps/api/place/details/json?"+URLEncodedUtils.format(params, "utf-8");
		    System.out.println(url);
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = client.execute(httpGet);
			System.out.println(response.getStatusLine());
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			int read;
            char[] buff = new char[1024];
            while ((read = reader.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			System.out.println(jsonObj.getString("status"));
			JSONObject predsJsonArray = jsonObj.getJSONObject("result")
											.getJSONObject("geometry").getJSONObject("location");
			
			referString.LatLng = predsJsonArray.getDouble("lat")+","+predsJsonArray.getDouble("lng");
			return "done";
		} catch (JSONException e) {
			System.out.println("Cannot process JSON results" + e);
		}
		return null;
	}

}
