package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class Result extends Activity{
	
	
	/*
	 * overload onKeyDown
	 * when back key is down, go back to the first page(target page)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if((keyCode == KeyEvent.KEYCODE_BACK)&&(event.getRepeatCount()==0)){
			Intent i = new Intent(this,Target.class);
			
			
			//this.finish();
			startActivity(i);
			
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	/*
	 * name:back
	 * usage:callback function to back to page 1
	 */
	public void back(View view){
		Intent i = new Intent(this,Target.class);
		startActivity(i);
	}
	
	@Override
	public void onWindowFocusChanged (boolean hasFocus){
	    super.onWindowFocusChanged(hasFocus);
	    if(hasFocus){
	    }
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		Intent i = getIntent();
		String result = i.getStringExtra("result");
		Button but = (Button)this.findViewById(R.id.button1);
		but.getLayoutParams().width = Convertor.dip2px(this, 200); 
		TextPaint tp = but.getPaint(); 
		tp.setFakeBoldText(true); 
		
    	//set the fontsize in advance
		
		Log.d("a","length:"+result.length());
		
		if(result.length()<=5){
	    	int fontsize = Convertor.dip2px(this, 30);
	    	but.setTextSize(fontsize);
	    	but.setText(result.toCharArray(),0,result.length());
		}
		
		//longer than 5: use 2 lines(add a '\n' to reach this goal)
		else{
			String result2 = result.substring(0, 5)+"\n"+result.substring(5,result.length());
			int fontsize = Convertor.dip2px(this,20);
			but.setTextSize(fontsize);
			but.setText(result2.toCharArray(),0,result2.length());
		}
		
		
		
    	but.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadow5));
        
		//add it into the stack
		ActivityController.push(this);
		
	}
}
