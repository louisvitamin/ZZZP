/*
 * blankring is just a ring with nothing
 */

package com.example.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

public class BlankRing extends View{

    private final  Paint paint;
    private final Context context;
    
    //the name of target
    private String target;
    
    //the color we need to use stored here
    private int[] colors= new int[8];
    
    //the size of saved result
    private int size = 0;
    
	public BlankRing(Context context, String target) {
		super(context);

		// TODO Auto-generated constructor stub
	    this.context = context;
		this.paint = new Paint();  
	    this.paint.setAntiAlias(true);   
	    this.paint.setStyle(Paint.Style.STROKE); 		
	    this.target = target;
	    
	    //now that we want to show the last saved result, we need to check if the data has been saved
  		File file = new File(this.getContext().getFilesDir().getPath().toString()+"/"+this.target+"_log"+".txt");
  		
  		if(file.exists()){
  	
  			try {
  				//if exist,just get the size
  				BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
  				String line = reader.readLine();
  				this.size = Integer.parseInt(line);
  				
  			} catch (FileNotFoundException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			} catch (IOException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			} 
  		} 
	}

	@Override  
	protected void onDraw(Canvas canvas) {  
	    // TODO Auto-generated method stub 
		
		//offset to put the ring a little down
		int offset  = Convertor.dip2px(context,30);
		
		//initialize the necessary parameters
	    int center = getWidth()/2;
	    int width = getWidth();
	    int height = this.getHeight();
	    ColorGroup cg =  new ColorGroup();
	    
	    
        int innerCircle = dip2px(context, 83); //设置内圆半径  
        int ringWidth = dip2px(context, 50); //设置圆环宽度  
        int outerCircle = innerCircle+ringWidth/2+dip2px(context,4);
          
	    RectF oval1 = new RectF(center-innerCircle,center-innerCircle,center+innerCircle,center+innerCircle);

	    RectF oval2 = new RectF(center-outerCircle,center-outerCircle,center+outerCircle,center+outerCircle);
        
	    
	    /*
	     * modification:
	     * if we have stored the result, we won't draw a pure-colored ring. We draw several arcs instead
	     */
	    if(this.size>0){
	    	int sweepAngle = (int)360/this.size;
		    for(int i=0;i<this.size;i++){
			    
		    	//the color we will use
		    	int color  = cg.get();
		    	
		    	//draw the inner circle. How to act like a shadow(gray and the color!)?
		    	
		    	this.paint.setColor(color);
		    	this.paint.setStrokeWidth(10);
		    	canvas.drawArc(oval1, sweepAngle*i, sweepAngle, false, paint);
		    	
		    	this.paint.setARGB(100, 0, 0, 0);
		    	
		    	canvas.drawArc(oval1, sweepAngle*i, sweepAngle, false, paint);
	
			    this.paint.setColor(color);  
			    this.paint.setStrokeWidth(ringWidth);
			    //canvas.drawArc(oval2, sweepAngle*i, sweepAngle, false, paint);
			    canvas.drawArc(oval2, sweepAngle*i, sweepAngle, false, paint);
			    //canvas.drawArc(oval2, 0, 10, false, paint);
		    }
	    }
        
        
	    else{

	    	//show the original ring
	        this.paint.setARGB(255, 110, 110, 110);  
	        this.paint.setStrokeWidth(10);  
	        canvas.drawCircle(center,center, innerCircle, this.paint);        
	
	        this.paint.setARGB(255, 164 ,164, 164);  
	        this.paint.setStrokeWidth(ringWidth);  
	        canvas.drawCircle(center,center, innerCircle+dip2px(context,4)+ringWidth/2, this.paint);  
	    }

	    
	    //draw the name of target
        Paint textPaint = new Paint( Paint.ANTI_ALIAS_FLAG);
        int fontsize = Convertor.dip2px(getContext(), 30);
        textPaint.setTextSize(fontsize);
        textPaint.setColor(Color.BLACK);
        Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/segoe_script.ttf");
        textPaint.setTypeface(typeFace);
        
        int length = this.target.length();
    	FontMetrics fm = textPaint.getFontMetrics();
    	float textHeight = fm.descent-fm.top;
        
        //if length is larger than 5,we need to divide the string into two lines
        if(length>5){
        	String s1 = this.target.substring(0, 5);
        	String s2 = this.target.substring(5);
        	int x = center - (int)(textPaint.measureText(s1)/2);
        	int x2 = center - (int)(textPaint.measureText(s2)/2);
        	canvas.drawText(s1, x, center, textPaint);
        	canvas.drawText(s2, x2, center+textHeight, textPaint);
        }else{        
	        int x = center - (int)(textPaint.measureText(this.target)/2);
	        canvas.drawText(this.target, x, center, textPaint);
        }
	    
	    super.onDraw(canvas);  
	} 
	
	/** 
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
	 */  
	public static int dip2px(Context context, float dpValue) {  
	    final float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (dpValue * scale + 0.5f);  
	}
}
