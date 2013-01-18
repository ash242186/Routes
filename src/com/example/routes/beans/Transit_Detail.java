package com.example.routes.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class Transit_Detail {

	public LocationsObject arrival_stop;
	public TimesObject arrival_time;
	public LocationsObject departure_stop;
	public TimesObject departure_time;
	public String headsign;
	public LinesObject line;
	public int num_stops;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		JSONObject js = new JSONObject();
		try {
			js.put("arrival_stop", arrival_stop);
			js.put("arrival_time", arrival_time);
			js.put("departure_stop", departure_stop);
			js.put("departure_time", departure_time);
			js.put("line", line);
			js.put("num_stops", num_stops);
			return js.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return super.toString();
		}
	}
}
