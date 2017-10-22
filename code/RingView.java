package com.example.test;
import java.util.ArrayList;
import java.lang.Math;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;


public class RingView extends View implements OnGestureListener,OnTouchListener{
	private MainActivity main;
	private final  Paint paint;  
    private final Context context;  
    private final int piece;  
    private int startangle = 0;
    private int sweepAngle = 0;
    private int speed = 0;
    private boolean enable = true;
    private int ringwidth;
    
    //for conveninence,we save the redius about innercircle and outercircle
    private int innerCircle;
    private int outerCircle;
    
    //the two rectangles for drawing circles
    private RectF oval1,oval2;
    
    //the name of target
    private String target;
    
    //size of screen
    private int width;
    private int height;
    
    //center of screen
    private int center;
    
    //the acceleration of plate
    private int acc = 8;
    
    //the paint for drawing text
    private Paint textPaint;
    
    //the underlying text 
    private TextView tv;
    private ArrayList<String> choices;
    
    //the value for counting(we only choose some points to calculate the direction)
    private int cnt;
    
    //the value for buffering all the touching events
    private int magic = 3;
    
    private GestureDetector gd;
    
    //the color manager
    private ColorGroup cg = new ColorGroup();
    
    //the color we need to use stored here
    private int[] colors= new int[8];
    
    
    
    //since we have to start from the touch events,we need some more state
    
    //-----------------------------------
    //if the ring has been chosen?
    private boolean chosen = false;
    
    //the states of the last event(time,position)
    private long beforetime[] = new long[5];
    
    private int beforeX[] = new int[5];
    
    private int beforeY[] = new int[5];
    
    //store the speed in all time slices 
    private ArrayList<Double> cuts =  new ArrayList<Double>();
    

    
    //----------------------------------
    
    //class for argb: a tuple for 4 elements
    private class Tuple4{
    	public Tuple4(int a,int r,int g,int b){
    		this.a = a;
    		this.r = r;
    		this.g = g;
    		this.b = b;
    	}
    	public int a;
    	public int r;
    	public int g;
    	public int b;
    	
    }
    
    //class for a new thread:intended for control the ring to fade and stop
    private class RingController implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(speed>0){
				
				while(speed!=0){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(speed<=acc) speed -= acc/2;
					else speed -= acc;
					
					//stop routine
					if(speed<=0) {
						speed = 0;
					
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						main.stop();
						return;
					}
					
				}
			}
			else{
				while(speed!=0){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(speed>=acc) speed += acc/2;
					else speed += acc;
					
					if(speed >= 0){
						speed = 0;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						main.stop();
						return;
					}
	
				}
			}
		}
			
    	
    }
    
    
    
    
    public RingView(Context context,int piece,String target) {  
          
        // TODO Auto-generated constructor stub  
        this(context, piece, target, null);  
    }  
  
    

 
	public RingView(Context context, int piece, String target, AttributeSet attrs) {  
	    super(context, attrs);  
	    // TODO Auto-generated constructor stub  
	    
	    this.main = (MainActivity)context;
	    this.context = context;  
	    this.target = target;
	    this.paint = new Paint();  
	    this.paint.setAntiAlias(true); //消除锯齿  
	    this.paint.setStyle(Paint.Style.STROKE); //绘制空心圆  
	    this.piece = piece;
	
	    //store all the color we need to use
	    for(int i = 0;i<piece;i++){
	    	colors[i] = cg.get();
	    }
	    
	    //gesture listening
	    this.setOnTouchListener(this);
	    gd = new GestureDetector(this); 
	    
	    initVars();
	    
	}  

	//get the underlying textview for the future operation
	public void getText(TextView tv){
		this.tv = tv;
	}
	
	//get the text for choices
	public void getChoices(ArrayList<String> choices){
		this.choices = choices;
	}
	
    //speed up
    public void faster(){
    	this.speed +=10;
    	
    }
    
    public int stop(){
    	//this.speed = 0;
    	//Thread::run(){
    	int avg = 360/this.piece;
    	return (this.startangle+(avg-90)+720)%360/avg;

    }
    
    //update the text by the current state of ring
    private void updateText(){
    	int avg = 360/this.piece;
    	int who = (this.startangle+(avg-90)+360)%360/avg;
    	String choice = choices.get(who);
    	this.tv.setText(choice.toCharArray(), 0, choice.length());
    }
    
    
    /*
     * name:initVars
     * usage:initialize some variables we will use in ondraw() (to reduce memory overhead)
     */
    private void initVars(){
	    //settings of text drawer
        this.textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int fontsize = Convertor.dip2px(getContext(), 30);
        textPaint.setTextSize(fontsize);
        textPaint.setColor(Color.BLACK);
        Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/segoe_script.ttf");
        textPaint.setTypeface(typeFace);
    }
    
    

    
	@Override  
	protected void onDraw(Canvas canvas) {  
	    // TODO Auto-generated method stub  
	    this.center = getWidth()/2;
	    this.width = getWidth();
	    this.height = this.getHeight();
	    this.ringwidth = dip2px(context, 50); //设置圆环宽度  
	    this.innerCircle = dip2px(context, 83);
	    this.outerCircle = innerCircle+ringwidth/2+dip2px(context,4);
	    this.startangle += this.speed;
	    this.startangle += 360;
	    this.startangle %= 360;
	    this.sweepAngle = (int)360/this.piece;
	    this.oval1 = new RectF(center-innerCircle,center-this.innerCircle,center+this.innerCircle,center+this.innerCircle);
	    //RectF oval2 = new RectF(center/2,center/2,width-center/2,width-center/2);
	    this.oval2 = new RectF(center-outerCircle,center-outerCircle,center+outerCircle,center+outerCircle);
	    

	    
	    
	    
	    int argb = 255;
	    
	    for(int i=0;i<this.piece;i++){
		    
	    	//the color we will use
	    	
	    	//draw the inner circle. How to act like a shadow(gray and the color!)?
	    	
	    	this.paint.setColor(colors[i]);
	    	this.paint.setStrokeWidth(10);
	    	canvas.drawArc(oval1, this.startangle+ sweepAngle*i, sweepAngle, false, paint);
	    	
	    	this.paint.setARGB(100, 0, 0, 0);
	    	
	    	canvas.drawArc(oval1, this.startangle+ sweepAngle*i, sweepAngle, false, paint);

		    this.paint.setColor(colors[i]);  
		    this.paint.setStrokeWidth(ringwidth);
		    //canvas.drawArc(oval2, sweepAngle*i, sweepAngle, false, paint);
		    canvas.drawArc(oval2, this.startangle+ sweepAngle*i, sweepAngle, false, paint);
		    //canvas.drawArc(oval2, 0, 10, false, paint);
	    }
	    
	    //draw the name of target
        int length = this.target.length();
        
        //if length is larger than 5,we need to divide the string into two lines
        if(length>5){
        	String s1 = this.target.substring(0, 5);
        	String s2 = this.target.substring(5);
        	int x = center - (int)(textPaint.measureText(s1)/2);
        	int x2 = center - (int)(textPaint.measureText(s2)/2);
        		
        	FontMetrics fm = textPaint.getFontMetrics();
        	float textHeight = fm.descent-fm.top;
        	
        	canvas.drawText(s1, x, center, textPaint);
        	canvas.drawText(s2, x2, center+textHeight, textPaint);
        }else{        
	        int x = center - (int)(textPaint.measureText(this.target)/2);
	        canvas.drawText(this.target, x, center, textPaint);
        }
	    
	    updateText();
	    
	    super.onDraw(canvas);  
	    if(this.speed!=0)	invalidate();
	}  
	  
	  
	/** 
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
	 */  
	public static int dip2px(Context context, float dpValue) {  
	    final float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (dpValue * scale + 0.5f);  
	}

	 @Override  
	    public boolean onDown(MotionEvent e)  
	    {  
	        Log.i("@fred", "触摸手势：onDown");  
	        return true;  
	    }  
	  
	    @Override  
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY)  
	    {  
	        //if (e2.getX() - e1.getX() > 50)  
	        //{  
	            //Log.i("@fred", "触摸手势：onFling");  
	        //}
	    	
	    	//Log.i("@fred", "触摸手势：onFling");
	    	
	    	/*if(!enable) return true;
	    	

	    	//int speed = 0;
	    	int center = this.width/2;
	    	
	    	int x1 = (int) e1.getX(), y1 = (int) e1.getY();
	    	int d = (x1-center)*(x1-center)+(y1-center)*(y1-center);
	    	Log.d("d", ""+d);
	    	Log.d("max",""+(center/2*center/2));
	    	Log.d("min",""+((center/2-this.ringwidth)*(center/2-this.ringwidth)));
	    	if((d>(center/2)*(center/2))||(d<(center/2-this.ringwidth)*(center/2-this.ringwidth))) return true;
	    	
	    	
	    	//update:we may as well use vectors to give a dot multiplication
	    	int dx = (int)(e2.getX()-e1.getX()), dy = (int)(e2.getY()-e1.getY());
	    	
	    	int px = (int)(-e1.getX()+center),py = (int)(-e1.getY()+center);

	    	Log.d("dx",""+dx);
	    	Log.d("dy",""+dy);
	    	Log.d("px",""+px);
	    	Log.d("py",""+py);
	    	Log.d("e1x",""+e1.getX());
	    	Log.d("e2x",""+e2.getX());

	    	
	    	
	    	int value = px*dx+py*dy;
	    
	    	if(value>0) speed = 10;
	    	else speed = -10;
	    	
	    	//invalidate();
	    	//add speed now
	    	double tmp = vX*vX+vY*vY;
	    	double times = (java.lang.Math.sqrt(tmp))/1000;
	    	this.speed = (int) ((double)(this.speed)*times);
	    	
	    	//if speed is not 0,mean that the plate has rotated
	    	if(this.speed != 0)	this.enable = false;
	    	
	    	//now create a new controller to handle the plate
	    	RingController rc = new RingController();
	    	Thread control = new Thread(rc);
	    	control.start();*/
	    	
	        return true;  
	    }  
	  
	    @Override  
	    public void onLongPress(MotionEvent e)  
	    {  
	        Log.i("@fred", "触摸手势：onLongPress");  
	  
	    }  
	  
	    @Override  
	    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)  
	    {  
	        Log.i("@fred", "触摸手势：onScroll"); 
	        //this.faster();
	        return true;  
	    }  
	  
	    @Override  
	    public void onShowPress(MotionEvent e)  
	    {  
	        Log.i("@fred", "触摸手势：onShowPress");  
	    }  
	  
	    @Override  
	    public boolean onSingleTapUp(MotionEvent e)  
	    {  
	        Log.i("@fred", "触摸手势：onSingleTapUp");  
	        return true;  
	    }  
	  
	    /*
	     * name:addOne
	     * usage:add one sample of the trail into the record vector
	     */
	    private void addOne(int x, int y){
	    	
	    	int center  = this.width/2;
	    	
    		//now we have two vector:(x[0]-center,x[1]-center),(x-center,y-center).Use this to calculate the central angle
    		int vx1 = this.beforeX[0]-center,  vy1 = this.beforeY[0]-center;
    		int vx2 = x - center, vy2 = y - center;
    		
	    	//the direction:use cross product(>0 means clockwise)
	    	int product = vx1*vy2-vy1*vx2;

	    	//now get the central angle:first the module of vector
	    	double mod1 = Math.sqrt((double)(vx1*vx1+vy1*vy1)), mod2 = Math.sqrt(vx2*vx2+vy2*vy2);
	    	
	    	int dot = vx1*vx2+vy1*vy2;
	    	
	    	double cosa = dot/(mod1*mod2);
	    	
	    	double a;
	    	//you may want to protect a for cosa can be bigger than 1
	    	if(cosa>=1) a = 0;
	    	
	    	//a must be converted to angle!
	    	else a = Math.acos(cosa)/Math.PI*180;
	    	
	    	if(product<0) a = -a;
	    	
	    	
	    	Log.d("a","v1:"+vx1+" "+vy1);
	    	Log.d("a","v2:"+vx2+" "+vy2);
	    	Log.d("a","dot:"+dot);
	    	Log.d("a","cosa:"+cosa);
	    	Log.d("a","a:"+a);
	    	
	    	this.cuts.add(new Double(a));
	    	
	    	//now store x and y into vx1 and vx2
	    	this.beforeX[0] = x;
	    	this.beforeY[0] = y;
	    }
	  
	    @Override  
	    public boolean onTouch(View v, MotionEvent event)  
	    {  
	           
	         //return  this.gd.onTouchEvent(event);  
	    	  
	    	//onFling can't satisfy our requirements. We need to start from every touch event
	    	
	    	if(!this.enable) return true;
	    	
	    	int action = event.getAction();
	    	int x = (int)event.getX();
	    	int y = (int)event.getY();
	    	int center  = this.width/2;
	    	
	    	
	    	switch(action){
	    	case MotionEvent.ACTION_DOWN:
	    		//judge if it is in the circle
	    		int d = (x-center)*(x-center)+(y-center)*(y-center);
		    	Log.w("a", "d:"+d);
		    	Log.w("a","x:"+x);
		    	Log.w("a","y:"+y);
		    	
		    	//Log.d("max",""+(center/2*center/2));
		    	//Log.d("min",""+((center/2-this.ringwidth)*(center/2-this.ringwidth)));
	    		
		    	if((d>(this.outerCircle + this.ringwidth/2)*(this.outerCircle + this.ringwidth/2))||(d<(this.outerCircle - this.ringwidth/2)*(this.outerCircle - this.ringwidth/2))) return true;
		    	else{
		    		//now it is chosen!
		    		Log.d("a","down");
		    		this.beforetime[0] = System.currentTimeMillis();
		    		this.chosen = true;
		    		this.beforeX[0] = x;
		    		this.beforeY[0] = y;
		    		this.cnt = 0;
		    	}
		    	break;
		    	
	    	case MotionEvent.ACTION_MOVE:
	    		Log.w("a","move");
	    		
	    		//if it is not be chosen,just throw away
	    		if(!this.chosen) return true;
	    		
	    		addOne(x,y);
	    		
		    	
	    		break;
		    	
	    	case MotionEvent.ACTION_UP:
	    		
	    		Log.d("a","up");
	    		
	    		//if it is not be chosen,just throw away
	    		if(!this.chosen) return true;
	    		
	    		//first add the last one into the record array
	    		addOne(x,y);
	    		
	    		//now get the average speed
	    		double sum = 0;
	    		for(int i=0; i<this.cuts.size();i++){
	    			sum+=this.cuts.get(i);
	    		}
	    		
	    		//get the elapse time
	    		double elapse = (double)(System.currentTimeMillis() - this.beforetime[0])/1000;
	    		
	    		this.speed = (int)(sum/elapse/10);
	    		
	    		//adjust the speed
	    		if(speed<-30){
	    			speed +=20;
	    			speed /=3;
	    		}
	    		else if(speed<30){
	    			speed /=3;
	    		}
	    		else{
	    			speed -=20;
	    			speed /=3;
	    		}
	    		
	    		
	    		Log.d("a","speed:"+this.speed);
	    		
	    		
	    		//if speed is 0,we should throw this result away and clear the vector
	    		if(this.speed == 0){
	    			this.chosen = false;
	    			this.cuts.clear();
	    			return true;
	    		}
	    		
		    	if(Math.abs(this.speed) < acc){
		    		if(this.speed>0) this.speed = acc;
		    		else this.speed = -acc;
		    	}
		    	
		    	Log.w("a","speed:"+this.speed);
		    	
		    	
		    	
		    	//now create a new controller to handle the plate
		    	RingController rc = new RingController();
		    	Thread control = new Thread(rc);
		    	control.start();
		    	
		    	//let the plate to begin drawing
		    	invalidate();
		    	
		    	//make it unable to rotate again
		    	this.enable = false;
		    	
		    	
		    	Log.w("a","first"+this.beforeX[0]+" "+this.beforeY[0]);
		    	Log.d("center",""+center+" "+" "+center);
		    	Log.w("a","speed:"+this.speed);
		    	
	    		break;
	    	}
	    	return true;
	    }  
	  
	}  


