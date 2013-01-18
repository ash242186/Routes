package com.example.routes.beans;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

public class Routes {

	public String[] warnings;
	public String summary = null;
	public String copyrights;
	public ArrayList<Legs> legs;
	public List<GeoPoint> overview_polyline;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		JSONObject js = new JSONObject();
		try {
			js.put("warnings", warnings);
			js.put("summary", summary);
			js.put("copyrights", copyrights);
			js.put("legs", legs);
			js.put("overview_polyline", overview_polyline);
			return js.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return super.toString();
		}
	}
}
