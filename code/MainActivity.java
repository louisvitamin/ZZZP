package com.example.test;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity{
	
	private String target;
	
	public void jump(View view){
		Intent intent = new Intent(this,EventList.class);
		startActivity(intent);
		Log.d("a","tt");
	}
	
	public void stop(){
		RingView rv = (RingView)((LinearLayout)findViewById(R.id.ll2)).findViewWithTag("Ring");
		int choice = rv.stop();
		Intent i = getIntent();
		ArrayList<String> choices = i.getStringArrayListExtra("choices");
		Intent result = new Intent(this,Result.class);
		result.putExtra("result", choices.get(choice));
		startActivity(result);
	}
	
	public void start(View view){
		RingView rv = (RingView)((LinearLayout)findViewById(R.id.ll2)).findViewWithTag("Ring");
		rv.faster();
	}
	

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//LinearLayout ll = (LinearLayout)findViewById(R.layout.activity_main);
		//TextView tv = new TextView(this);
		//setContentView(tv);
		
		//get the parameter from event(page 3)
		Intent i = getIntent();
		int size = i.getIntExtra("size", -1);
		ArrayList<String> choices = i.getStringArrayListExtra("choices");
		this.target = i.getStringExtra("target");
		
		//draw the text in the center
		
		
		RingView rv = new RingView(this,size,this.target);
		rv.setTag("Ring");
		LinearLayout ll = (LinearLayout)findViewById(R.id.ll2);
		LinearLayout ll3 = (LinearLayout)findViewById(R.id.ll3);
		TextView tv = (TextView)ll3.findViewById(R.id.textView3); 
		ll.addView(rv);
		rv.getText(tv);
		rv.getChoices(choices);
		String t = "";
		tv.setText(t.toCharArray(), 0, t.length());
		tv.setTextSize(20);

		//add it into the stack
		ActivityController.push(this);
		
		
		//FrameLayout fl =new FrameLayout(this);
		//TextView tv = new TextView(this);
		//fl.addView(tv);
		//fl.addView(iv);
		
		
	}


}
