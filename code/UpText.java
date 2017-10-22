/*
 * UpText is derived from TextView and will launch the page 3 after being clicked
 */

package com.example.test;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class UpText extends ImageView{
	
	private Notice main;
	
	public UpText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	
		
		this.setImageDrawable(getResources().getDrawable(R.drawable.up));
		this.setBackgroundColor(Color.rgb(83, 87, 102));
		
		//store the activity which this view belongs to
		this.main = (Notice)context;

	}



}
