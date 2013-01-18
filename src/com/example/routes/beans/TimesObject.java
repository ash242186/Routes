package com.example.routes.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class TimesObject {

	public String text;
	public String time_zone;
	public Long value;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		JSONObject js = new JSONObject();
		try {
			js.put("text", text);
			js.put("time_zone", time_zone);
			js.put("value", value);
			return js.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return super.toString();
		}
	}
}
