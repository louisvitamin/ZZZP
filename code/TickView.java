/*
 * TickView is used to stand for a tick, which will be shown when the whole entry is chosen
 */

package com.example.test;

import com.example.test.DeleteView.DeleteListener;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class TickView extends ImageView{
	//the id is used for identify the textview(or the linear layout)
	private int id;
	private Context main;
	
	//every tick is unchosen when initialized
	private boolean chosen = false;
	
	//getter/setter for chosen
	boolean isChosen(){
		return this.chosen;
	}
	
	void setChosen(boolean chosen){
		this.chosen = chosen;
	}
	
	//constructor
	public TickView(Context context,int id) {
		super(context);
		
		// TODO Auto-generated constructor stub
		this.id = id;
		this.main = context;
		this.setOnClickListener(new TickListener());
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public class TickListener implements View.OnClickListener{

		
		@Override
		public void onClick(View arg0) {
			
			// TODO Auto-generated method stub		
			Log.d("a","clicked");
			if(chosen){
				TickView.this.setImageDrawable(getResources().getDrawable(R.drawable.empty));
				chosen = false;
			}
			else{
				TickView.this.setImageDrawable(getResources().getDrawable(R.drawable.chosen3));
				chosen = true;
			}
		}

	}
}
