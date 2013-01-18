package com.example.routes.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.routes.beans.Legs;
import com.example.routes.beans.LinesObject;
import com.example.routes.beans.LocationsObject;
import com.example.routes.beans.Routes;
import com.example.routes.beans.Steps;
import com.example.routes.beans.TimesObject;
import com.example.routes.beans.Transit_Detail;
import com.google.android.maps.GeoPoint;

public class Functions {
	private final String DIRECTION_API_BASE = "http://maps.googleapis.com/maps/api/directions/json";
	//private final String HTTPTAG = getClass().getSimpleName();
	
	private String httpcall(String url) throws ClientProtocolException, IOException{
		System.out.println(url);
		HttpClient client = new DefaultHttpClient();
		StringBuilder jsonResults = new StringBuilder();
			/*Uri url = new Uri.Builder()
								    .scheme("https")
								    .authority("maps.googleapis.com")
								    .path("maps/api/place/details/json")
								    .appendQueryParameter("reference", referString)
								    .appendQueryParameter("key", getString(R.string.place_api))
								    .appendQueryParameter("sensor", "true")
								    .build();
			HttpGet httpGet = new HttpGet(url.toString());*/
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = client.execute(httpGet);
			System.out.println(response.getStatusLine());
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			int read;
            char[] buff = new char[1024];
            while ((read = reader.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            //Log.w(HTTPTAG, jsonResults.toString());
		return jsonResults.toString();
	}
	
	
	//direction API parser
	public List<Object> Directions(String origin, String destination, String mode){
		List<Object> result = new ArrayList<Object>();
		List<Routes> routes = new ArrayList<Routes>();
		
        try {
        	StringBuilder sb = new StringBuilder(DIRECTION_API_BASE);
            sb.append("?sensor=false&alternatives=true&origin=" + URLEncoder.encode(origin, "utf8"));
			sb.append("&destination="+ URLEncoder.encode(destination, "utf8"));
			sb.append("&mode="+ URLEncoder.encode(mode, "utf8"));
			if(mode.equalsIgnoreCase("transit"))
           	 	sb.append("&departure_time="+ String.valueOf(System.currentTimeMillis()/1000));
			
			JSONObject jsonObj = new JSONObject(httpcall(sb.toString()));
			System.out.println("parser called");
			result.add(jsonObj.getString("status"));
			if(jsonObj.getString("status").equalsIgnoreCase("OK")){
				JSONArray routesjson = jsonObj.getJSONArray("routes");
				for(int i=0;i<routesjson.length();i++){
					JSONObject rujs = routesjson.getJSONObject(i);
					Routes ru = new Routes();
					ru.copyrights = rujs.getString("copyrights");
					if(!mode.equalsIgnoreCase("TRANSIT"))
						ru.summary = rujs.getString("summary");
					JSONArray warningsjson = rujs.getJSONArray("warnings");
					ArrayList<String> warnings = new ArrayList<String>();
					for(int w=0;w<warningsjson.length();w++)
						warnings.add(warningsjson.getString(w));
					ru.warnings = warnings.toArray(new String[warnings.size()]);
					ru.overview_polyline = decodePoly(rujs.getJSONObject("overview_polyline").getString("points"));
					
					//parse legs tag :- no waypoints so single legs exists
					ArrayList<Legs> legss = new ArrayList<Legs>();
					JSONArray legsjson = rujs.getJSONArray("legs");
					for(int l=0;l<legsjson.length();l++){
						JSONObject legsjs = legsjson.getJSONObject(l);
						Legs legs = new Legs();
						if(mode.equalsIgnoreCase("TRANSIT")){
							TimesObject tim = new TimesObject();
							tim.text = legsjs.getJSONObject("arrival_time").getString("text");
							tim.time_zone = legsjs.getJSONObject("arrival_time").getString("time_zone");
							tim.value = legsjs.getJSONObject("arrival_time").getLong("value");
							legs.arrival_time = tim;
							tim = new TimesObject();
							tim.text = legsjs.getJSONObject("departure_time").getString("text");
							tim.time_zone = legsjs.getJSONObject("departure_time").getString("time_zone");
							tim.value = legsjs.getJSONObject("departure_time").getLong("value");
							legs.departure_time = tim;
						}
						legs.distance = legsjs.getJSONObject("distance").getString("text");
						legs.duration = legsjs.getJSONObject("duration").getString("text");
						legs.end_address = legsjs.getString("end_address");
						legs.end_location = legsjs.getJSONObject("end_location").getDouble("lat")+","
												+legsjs.getJSONObject("end_location").getDouble("lng");
						legs.start_address = legsjs.getString("start_address");
						legs.start_location = legsjs.getJSONObject("start_location").getDouble("lat")+","
												+legsjs.getJSONObject("start_location").getDouble("lng");
						
						//parser steps tag
						ArrayList<Steps> stepss = new ArrayList<Steps>();
						JSONArray stepjson = legsjs.getJSONArray("steps");
						for(int s=0;s<stepjson.length();s++){
							JSONObject stepjs = stepjson.getJSONObject(s);
							Steps steps = new Steps();
							steps.distance = stepjs.getJSONObject("distance").getString("text");
							steps.duration = stepjs.getJSONObject("duration").getString("text");
							steps.start_location = stepjs.getJSONObject("start_location").getDouble("lat")+","
														+stepjs.getJSONObject("start_location").getDouble("lng");
							steps.end_location = stepjs.getJSONObject("end_location").getDouble("lat")+","
														+stepjs.getJSONObject("start_location").getDouble("lng");
							steps.html_instructions = stepjs.getString("html_instructions");
							steps.travel_mode = stepjs.getString("travel_mode");
							steps.polyline = decodePoly(stepjs.getJSONObject("polyline").getString("points"));
							if(mode.equalsIgnoreCase("TRANSIT")){
								if(stepjs.getString("travel_mode").equalsIgnoreCase("WALKING")){
									ArrayList<Steps> subsets = new ArrayList<Steps>();
									JSONArray substepjson = stepjs.getJSONArray("steps");
									for(int ss=0; ss<substepjson.length();ss++){
										JSONObject substepjs = substepjson.getJSONObject(ss);
										Steps substeps = new Steps();
										substeps.distance = substepjs.getJSONObject("distance").getString("text");
										substeps.duration = substepjs.getJSONObject("duration").getString("text");
										substeps.start_location = substepjs.getJSONObject("start_location").getDouble("lat")+","
																	+substepjs.getJSONObject("start_location").getDouble("lng");
										substeps.end_location = substepjs.getJSONObject("end_location").getDouble("lat")+","
																	+substepjs.getJSONObject("start_location").getDouble("lng");
										if(substepjs.has("html_instructions"))
											substeps.html_instructions = substepjs.getString("html_instructions");
										substeps.travel_mode = substepjs.getString("travel_mode");
										substeps.polyline = decodePoly(substepjs.getJSONObject("polyline").getString("points"));
										subsets.add(substeps);
									}
									steps.substeps = subsets;
								}
								if(stepjs.getString("travel_mode").equalsIgnoreCase("TRANSIT")){
									Transit_Detail trans = new Transit_Detail();
									JSONObject transit = stepjs.getJSONObject("transit_details");
									trans.arrival_stop = new LocationsObject(transit.getJSONObject("arrival_stop").getJSONObject("location").getDouble("lat")+","
											+transit.getJSONObject("arrival_stop").getJSONObject("location").getDouble("lng"), transit.getJSONObject("arrival_stop").getString("name"));
									TimesObject tim = new TimesObject();
									tim.text = transit.getJSONObject("arrival_time").getString("text");
									tim.time_zone = transit.getJSONObject("arrival_time").getString("time_zone");
									tim.value = transit.getJSONObject("arrival_time").getLong("value");
									trans.arrival_time = tim;
									trans.departure_stop = new LocationsObject(transit.getJSONObject("departure_stop").getJSONObject("location").getDouble("lat")+","
											+transit.getJSONObject("departure_stop").getJSONObject("location").getDouble("lng"), transit.getJSONObject("departure_stop").getString("name"));
									tim = new TimesObject();
									tim.text = transit.getJSONObject("departure_time").getString("text");
									tim.time_zone = transit.getJSONObject("departure_time").getString("time_zone");
									tim.value = transit.getJSONObject("departure_time").getLong("value");
									trans.departure_time = tim;
									trans.headsign = transit.getString("headsign");
									trans.num_stops = transit.getInt("num_stops");
									//System.out.println(stepjs.getString("travel_mode")+" "+transit.getJSONObject("line").getString("short_name") );
									LinesObject lines = new LinesObject(transit.getJSONObject("line").getJSONArray("agencies").getJSONObject(0).getString("name"), 
											transit.getJSONObject("line").getString("name"), transit.getJSONObject("line").getJSONObject("vehicle").getString("name"));
									if(transit.getJSONObject("line").has("short_name"))
										lines.short_name = transit.getJSONObject("line").getString("short_name");
									trans.line =lines;
									steps.transit_details = trans;
								}
							}
							//Log.w(HTTPTAG+i+""+l, steps.toString());
							stepss.add(steps);
						}
						legs.steps = stepss;
						legss.add(legs);
						//Log.w(HTTPTAG+l, l.toString());
					}
					ru.legs = legss;
					routes.add(ru);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        result.add(routes);
		return result;
	}
	
	
	//Decoding Polylines from Google Maps Direction API
	private List<GeoPoint> decodePoly(String encoded) {

	    List<GeoPoint> poly = new ArrayList<GeoPoint>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
	             (int) (((double) lng / 1E5) * 1E6));
	        poly.add(p);
	    }

	    return poly;
	}
}
