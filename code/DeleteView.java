/*
 * deleteView is used to delete the corresponding entry
 */
package com.example.test;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

public class DeleteView extends ImageView{

	//the id is used for identify the textview(or the linear layout)
	private int id;
	private Context main;
	public DeleteView(Context context,int id) {
		super(context);
		
		// TODO Auto-generated constructor stub
		this.id = id;
		this.main = context;
		this.setOnClickListener(new DeleteListener());
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public class DeleteListener implements View.OnClickListener{

		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(main.getClass().getName().equals("com.example.test.Target")){
				Target t = (Target)main;
				t.deleteOne(id);
			}
			else{
				Event e = (Event)main;
				e.deleteOne(id);
			}
		}

	}
}
