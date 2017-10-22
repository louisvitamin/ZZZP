package com.example.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


//ChooseText is extended from editText. It can be modified when it's empty
public class ChooseText extends EditText{

	//we need a id to know which it is
	private int id;
	private Context context;
	private int sroke_width = 2;

	
	//why we need this? because android doesn't have the method to get the background color
	private int color;
	
	public void setColor(int color){
		this.color = color;
	}
	
	public int getColor(){
		return this.color;
	}
	
	@Override
	/*
	 * name:setId
	 * usage:we need this setId to mark the position of this one in the whole view
	 */
	public void setId(int id){
		this.id = id;
	}
	
	//if the view has been chosen
	private boolean chosen = false;
	
	public boolean isChosen(){
		return this.chosen;
	}
	
	public void setChosen(boolean c){
		this.chosen = c;
		//invalidate();
	}
	
	public ChooseText(Context context, int id) {
		super(context);
		// TODO Auto-generated constructor stub
		this.id = id;
		this.context = context;
		
		//set the touchevent handler(because of the weird click-twice problem of edittext)
		if(context.getClass().getName().equals("com.example.test.Target")){
			this.setOnTouchListener(new ctouchListener());
		}
		
		this.setOnClickListener(new ctListener());
		
		//set the font
		this.setTypeface(Typeface.SERIF);
		

		
		
		//set something for ime
		this.setOnEditorActionListener(new OnEditorActionListener(){
			 @Override  
	            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {  
	                return false;  
	            }  
		});
	}

	   
    @Override  
    protected void onDraw(Canvas canvas) {
    	
    	//LinearGradient mLinearGradient = new LinearGradient(0,0,100,100,  
          //      new int[]{Color.RED,Color.GREEN,Color.BLUE,Color.WHITE},  
            //    null,Shader.TileMode.REPEAT);  
    	
    	Paint paint = new Paint();
    	paint.setColor(Color.RED);
    	
        super.onDraw(canvas);  
    } 
	
    private class ctouchListener implements OnTouchListener{

		@Override
        public boolean onTouch(View v, MotionEvent event) {
			Log.d("a","touch");
			Target t = (Target)context;
			
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
				//setEnabled(false);
				//invalidate();
				
				//it is empty.edit it
				if(getText().toString().equals("")){
					Log.d("a","empty");
					//setEnabled(true);
					t.setCurrent(id);
					return false;
				}
				
				else{
					t.setSelected(id);
				}
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
            	if(getText().toString().equals("")){
            		return false;
            	}
            }
            
            return true; // return is important...
        }
    
    }
    
    
	private class ctListener implements View.OnClickListener{
	
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			//NOTE: we cannot let an empty entry to be chosen!
			
			Log.d("a","click");
			if(context.getClass().getName().equals("com.example.test.Target")){
				Target t = (Target)context;
				//setEnabled(false);
				//invalidate();
				
				//it is empty.edit it
				if(getText().toString().equals("")){
					Log.d("a","empty");
					setEnabled(true);
					//t.setCurrent(id);
				}
				
				else{
					t.setSelected(id);
				}
			}
			
		}
		
	}
}
