/*
 * Notice is the 2nd activity.It serves as a notice to tell the user it is a turntable! 
 */

package com.example.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.EventLog.Event;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Notice extends Activity{
	
	private String target;
	
	
	/*
	 * name:next
	 * usage:the callback function of next button
	 */
	public void next(View view){
		
		
		
		//check the existence of log first. if not,just simply return.
  		File file = new File(this.getFilesDir().getPath().toString()+"/"+this.target+"_log"+".txt");
  		
  		if(file.exists()){
  	
  			Log.d("a","exist");
  			//initialize the intent
  			Intent intent = new Intent(this,MainActivity.class);
  			ArrayList<String> texts = new ArrayList<String>();
  			
  			
  			try {
  				
  				//first get the size
  				BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
  				String line = reader.readLine();
  				int size = Integer.parseInt(line);
  				
				//construct all the content in log
				for(int i=0;i<size;i++){
					line = reader.readLine();
					texts.add(line);
				}
				
				//pass the parameters
				intent.putExtra("size",size);
				intent.putExtra("choices",texts);
				intent.putExtra("target", this.target);
				
				startActivity(intent);
  				
  			} catch (FileNotFoundException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			} catch (IOException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			} 
  		}
	}
	
	/*
	 * name:jump
	 * usage:jump to the page 3(Event Activity) with target
	 */
	public void jump(){
		Intent intent = new Intent(this,com.example.test.Event.class);
		
		
		//just put the target's name
		intent.putExtra("target", this.target);
		
		startActivity(intent);
		//this.finish();
		overridePendingTransition(R.anim.event_open, 0);
		
	}
	
	/*
	 * now we consider the whole linearlayout as the zone for you to scroll up.
	 * so we put the listener here
	 */
	public class UpListener implements OnGestureListener,OnTouchListener{
		
		private GestureDetector gd = new GestureDetector(this); 
	

		@Override
		public boolean onDown(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1,
				float arg2, float arg3) {
			// TODO Auto-generated method stub
			Log.d("a","fling");
			//main.jump();
			return true;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			Log.d("a","longpress");
		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1,
				float arg2, float arg3) {
			// TODO Auto-generated method stub
			Log.d("a","scroll");
			jump();
			return true;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			Log.d("a","touch");
			return gd.onTouchEvent(arg1);
		}

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		this.target = intent.getStringExtra("target");
		
		BlankRing br = new BlankRing(this,this.target);
		//this.(br);
		this.setContentView(R.layout.notice);
		LinearLayout ll = (LinearLayout)this.findViewById(R.id.llo);
		ll.addView(br);
		
		
		UpText ut = new UpText(this);
		LinearLayout ll2 = (LinearLayout)this.findViewById(R.id.ll2);
		ll2.setBackgroundColor(Color.rgb(83, 87, 102));
		

		//check if we have some log. If not, we don't want to show the next button
		File file = new File(this.getFilesDir().getPath().toString()+"/"+this.target+"_log"+".txt");
		if(!file.exists()){
			//Button but = (Button) this.findViewById(R.id.button2);
			LinearLayout ll1 = (LinearLayout) this.findViewById(R.id.ll3);
			//but.setVisibility(View.GONE);
			ll1.setVisibility(View.GONE);
		}
		
		//LinearLayout.LayoutParams llp =new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,0,(float) 0.4);
		//ll2.setLayoutParams(llp);
		
		ll2.addView(ut);
		
		//listen on scrolling up
		ll2.setOnTouchListener(new UpListener());
		
		//add it into the stack
		ActivityController.push(this);
	}
}
