package com.example.routes;


import java.util.ArrayList;
import java.util.List;

import com.example.routes.beans.Routes;
import com.example.routes.beans.Steps;
import com.example.routes.beans.Transit_Detail;
import com.example.routes.helper.Functions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;




@SuppressLint("HandlerLeak")
public class DirectionActivity extends Activity implements OnClickListener{

	//private final String LOG_TAG  = getClass().getSimpleName();
	private String mode, origin, destination;
	private TextView from, to;
	private LinearLayout suggestions, routelayout;
	private ArrayList<TableLayout> index = new ArrayList<TableLayout>();
	private List<Routes> routes;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_direction);
	
		dialog = new ProgressDialog(this);
	    dialog.setMessage("Fetching Routes...");
		mode = getIntent().getStringExtra("mode");
		origin = getIntent().getStringExtra("origin");
		destination = getIntent().getStringExtra("destination");
		
		/*origin = "espire";
		destination = "hauz khas metro";
		mode =  "transit";*/
		
		routelayout = (LinearLayout) findViewById(R.id.routes);
		from = (TextView) findViewById(R.id.from);
		to = (TextView) findViewById(R.id.to);
		suggestions = (LinearLayout) findViewById(R.id.suggestions);
		dialog.show();
		 new Thread(){

			@Override
			public void run() {
				Functions function = new Functions();
				mhandle.sendMessage(mhandle.obtainMessage(1, function.Directions(origin, destination, mode)));
			}}.start();
		
	}
	
	private Handler mhandle =  new Handler(){

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			if(msg.what==1){
				List<Object> result = (List<Object>) msg.obj;
				if(String.valueOf(result.get(0)).equalsIgnoreCase("OK")){
					routes = (List<Routes>) result.get(1);
					for(int i=0;i<routes.size();i++){
						Routes ru = routes.get(i);
						from.setText("From: "+ru.legs.get(0).start_address);
						to.setText("To: "+ru.legs.get(0).end_address);
						index.add(Addtable(Viaroutes(ru), ru.legs.get(0).distance, ru.legs.get(0).duration));
					}
				}else
					Toast.makeText(DirectionActivity.this, "Some problem in Direction api ", Toast.LENGTH_SHORT).show();
			}
		}};
	
	//add table-layout in linear-layout for suggestions
	private TableLayout Addtable(String via, String distance, String duration){
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.suggestion_item, suggestions, false);
		TextView _via = (TextView) view.findViewById(R.id._via);
		TextView _distance = (TextView) view.findViewById(R.id._distance);
		TextView _duration = (TextView) view.findViewById(R.id._duration);
		_via.setText(via);
		_distance.setText(distance);
		_duration.setText(duration);
		view.setOnClickListener(this);
		suggestions.addView(view);
		return (TableLayout) view;
	}

	private void Addroutes(List<Steps> objects){
		for(Steps ss : objects){
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.steps_item, routelayout, false);
			TextView instructions = (TextView) rowView.findViewById(R.id.html_instructions);
			TextView distance = (TextView) rowView.findViewById(R.id.distances);
			instructions.setText(Html.fromHtml(ss.html_instructions));
			distance.setText(Html.fromHtml(ss.distance));
			rowView.setBackgroundResource(R.drawable.step_border);
			routelayout.addView(rowView);
			if(mode.equalsIgnoreCase("TRANSIT")){
				if(ss.travel_mode.equalsIgnoreCase("WALKING"))
					for(Steps s: ss.substeps){
						rowView = inflater.inflate(R.layout.steps_item, routelayout, false);
						rowView.setPadding(10, 3, 10, 3);
						instructions = (TextView) rowView.findViewById(R.id.html_instructions);
						distance = (TextView) rowView.findViewById(R.id.distances);
						if(s.html_instructions!=null){
							instructions.setText(Html.fromHtml(s.html_instructions));
							distance.setText(Html.fromHtml(s.distance));
							rowView.setBackgroundResource(R.drawable.substep_border);
							routelayout.addView(rowView);
						}
					}
				
				if(ss.travel_mode.equalsIgnoreCase("TRANSIT")){
					Transit_Detail t = ss.transit_details;
					rowView = inflater.inflate(R.layout.steps_item, routelayout, false);
					rowView.setPadding(10, 3, 10, 3);
					instructions = (TextView) rowView.findViewById(R.id.html_instructions);
					distance = (TextView) rowView.findViewById(R.id.distances);
					instructions.setText(t.arrival_stop.name+"-"+t.departure_stop.name 
							+"\n"+(t.arrival_time.value-t.departure_time.value)/60+" mins " +t.num_stops+" stops");
					distance.setText("Service run by "+t.line.agencies+"\n"+t.arrival_time.text+"-"+t.departure_time.text);
					rowView.setBackgroundResource(R.drawable.substep_border);
					routelayout.addView(rowView);
				}
			}
			
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(index.contains(v)){
			System.out.println(index.indexOf((TableLayout)v));
			//Toast.makeText(DirectionActivity.this, String.valueOf(index.indexOf(v)), Toast.LENGTH_SHORT).show();
			//list.setAdapter(new RouteRowAdapter(this, R.layout.steps_item, routes.get(index.indexOf(v)).legs.get(0).steps));
			routelayout.removeAllViews();
			Addroutes(routes.get(index.indexOf(v)).legs.get(0).steps);
		}
	}
	
	private String Viaroutes(Routes routes){
		String text = "";
		if(routes.summary!=null)
			return "Via "+routes.summary;
		List<Steps> steps = routes.legs.get(0).steps;
		ArrayList<String> ret = new ArrayList<String>();
		for(Steps ss : steps){
			if(ss.travel_mode.equalsIgnoreCase("WALKING"))
				ret.add("walking");
			if(ss.travel_mode.equalsIgnoreCase("TRANSIT")){
				ret.add(ss.transit_details.line.vehicle);
			}
				text = ArrayList_Join(ret, "->");
		}
		return  text;
	}
	 
	private String ArrayList_Join(List<String> coll, String delimiter)
	{
	    if (coll.isEmpty())
		return "";
	 
	    StringBuilder sb = new StringBuilder();
	    for (String x : coll)
		sb.append(x + delimiter);
	    sb.delete(sb.length()-delimiter.length(), sb.length());
	    return sb.toString();
	}
	

}
