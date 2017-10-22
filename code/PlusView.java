/*
 * PlusView is used to add more edittext. itself is just a textview with some onClick event
 */

package com.example.test;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class PlusView extends TextView{

	private int cnt = 3;
	private Context main;
	public PlusView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		String plus = "+";
		this.setGravity(Gravity.CENTER);
		this.setText(plus.toCharArray(),0,plus.length());
		this.setTextSize(40);
		
		//store the activity which this view belongs to
		this.main = context;
		
		//when click,try to add a new target
		this.setOnClickListener(new PlusListener());

	}

	public class PlusListener implements View.OnClickListener{


		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			if(main.getClass().getName().equals("com.example.test.Target")){
				Target t = (Target)main;
				t.addone();
			}
			else{
				Event e = (Event)main;
				//e.addone();
			}
		}

	}
	
}

