package com.example.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class Event extends Activity {
	
	//all the target to be chosed
	private ArrayList<ChooseText> cts = new ArrayList();
	
	//all the image corresponding to targets
	private ArrayList<DeleteView> dvs = new ArrayList();
	
	//all the ticks correspoinding to the targets(to mark that they have been chosen)
	private ArrayList<TickView> ticks = new ArrayList();
	
	//add one target
	private PlusView pv;
	
	//the layout for all text underlying
	private LinearLayout.LayoutParams lp;
	
	//will be set when some choosetext is selected
	private int selected = -1;
	
	//the current target 
	private String target;
	
	//is the edit mode on?
	private boolean edit=false;
	
	//we need a color group to manage all the color
	private ColorGroup cg = new ColorGroup();
	
	//the layout for tick:go to the rightside a little bit
	private LinearLayout.LayoutParams lpr;
	
	//the layout for cross:go to the leftside a little bit
	private LinearLayout.LayoutParams lpl;
	
	/*
	 * name:isEdit
	 * usage:get the edit state
	 */
	public boolean isEdit(){
		return this.edit;
	}
	

	
	/*
	 * name:setSelected
	 * usage:set the selected chosed target()
	 */

	/*public void setSelected(int s){
		this.selected = s;
		if(this.cts.get(s).isChosen()){
			Log.d("a","haha");
			this.ticks.get(s).setVisibility(View.GONE);
			this.cts.get(s).setChosen(false);
		}
		else{
			this.ticks.get(s).setVisibility(View.VISIBLE);
			this.cts.get(s).setChosen(true);
		}
	}*/
	
	/*
	 * name:initlayout
	 * usage:just set several layout parameters for later use
	 */
	private void initlayout(){
		int roff = Convertor.dip2px(this, 15);
		int loff = Convertor.dip2px(this, 5);
		this.lpr = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.lpr.setMargins(0, 0, roff, 0);
		this.lpl = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.lpl.setMargins(loff, 0, 0, 0);
	}
	
	/*
	 * name:next
	 * usage:callback function of next button.jump to page 4
	 */

	public void next(View view){
		//put the params
		Intent intent = new Intent(this,MainActivity.class);
		//intent.putExtra("target",this.cts.get(this.selected).getText().toString());
		
		ArrayList<String> texts = new ArrayList<String>();
		
		int cnt=0;
		
		for(int i=0;i<this.cts.size();i++){
			
			//add all chosen choices with non-empty content(so the cnt may be smaller than the size of this list)
			TickView tv = (TickView) this.ticks.get(i);
			ChooseText ct = this.cts.get(i);
			String content = ct.getText().toString();
			if(tv.isChosen() && !content.equals("")){
				texts.add(content);
				cnt++;
			}
		}
		
		if(cnt<2){
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("提示");
			ab.setMessage("请至少选择两个选项");
			ab.setPositiveButton("ok", null);
			ab.show();
			return;
		}
		
		//now that we can continue, we just store all these entries for later use
		this.store();
		
		//store the log so that we will use it as a 'prefetching' in page 2
		this.storeLog();
		
		intent.putExtra("size",cnt);
		intent.putExtra("choices",texts);
		intent.putExtra("target", this.target);
		
		startActivity(intent);		
	}
	
	
	/*
	 * overload keydown
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if((keyCode == KeyEvent.KEYCODE_BACK)&&(event.getRepeatCount()==0)){
			
			//if it is in edit mode ,act as we push edit button again
			if(this.edit == true){
				for(int i=0;i<this.cts.size();i++){
					
					//hide all crosses
					dvs.get(i).setVisibility(View.GONE);
					
					//enable all the choosetext
					this.cts.get(i).setFocusableInTouchMode(false);
					this.cts.get(i).setCursorVisible(false);
					this.cts.get(i).clearFocus();
					
					//show all the ticks
					this.ticks.get(i).setImageDrawable(getResources().getDrawable(R.drawable.empty));
					this.ticks.get(i).setVisibility(View.VISIBLE);
					((TickView) this.ticks.get(i)).setChosen(false);
					
					//enable the finish button
					Button finish = (Button)findViewById(R.id.button2);
					ImageView tick = (ImageView)findViewById(R.id.imageView3);
					tick.setEnabled(true);
					finish.setClickable(true);
					
				}
				this.edit = false;
				return true;
			}
			
			else{
				return super.onKeyDown(keyCode,event);
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	
	/*
	 * the execution unit of the edit function
	 */
	public void edit(){
		if(this.edit == false){
			for(int i=0;i<this.cts.size();i++){
				
				//clear all ticks(which means that now none entry is chosen)
				this.ticks.get(i).setVisibility(View.GONE);
				this.cts.get(i).setChosen(false);
				
				//disable the finish button
				Button finish = (Button)findViewById(R.id.button2);
				ImageView tick = (ImageView)findViewById(R.id.imageView3);
				tick.setEnabled(false);
				finish.setClickable(false);
				
				//make the cross visible
				dvs.get(i).setVisibility(View.VISIBLE);
				
				this.cts.get(i).setFocusableInTouchMode(true);
				this.cts.get(i).setCursorVisible(true);
				//this.cts.get(i).clearFocus();
			}
			this.edit = true;
		}
		else{
			for(int i=0;i<this.cts.size();i++){
				
				//hide all crosses
				dvs.get(i).setVisibility(View.GONE);
				
				//enable all the choosetext
				this.cts.get(i).setFocusableInTouchMode(false);
				this.cts.get(i).setCursorVisible(false);
				this.cts.get(i).clearFocus();
				
				//show all the ticks
				this.ticks.get(i).setImageDrawable(getResources().getDrawable(R.drawable.empty));
				this.ticks.get(i).setVisibility(View.VISIBLE);
				((TickView) this.ticks.get(i)).setChosen(false);
				
				//enable the finish button
				Button finish = (Button)findViewById(R.id.button2);
				ImageView tickImage = (ImageView)findViewById(R.id.imageView3);
				tickImage.setEnabled(true);
				finish.setClickable(true);
				
			}
			this.edit = false;
		}
	}
	
	/*
	 * name:edit
	 * usage:the callback funtion of the edit button.Just make the crosses visible
	 */
	public void edit(View view){
		edit();
	}
	

	
	
	/*
	 * name:store
	 * usage:the callback function of the store button.Store the choices of this target 
	 * 		 the format is like:
	 * 		 --number of choices
	 * 		 choice1
	 * 		 choice2
	 * 		 ...
	 */
	public void store(View view){
		try {
			
			//it seems that only FileOutputStream can work
			String filename = this.target+".txt";
			FileOutputStream fos = openFileOutput(filename,Context.MODE_PRIVATE);
			fos.write((""+this.cts.size()+"\n").getBytes());
			
			for(int i=0;i<this.cts.size();i++){
				fos.write((this.cts.get(i).getText().toString()+"\n").getBytes());
			}
			
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("提示");
			ab.setMessage("保存完毕！");
			ab.setPositiveButton("ok", null);
			ab.show();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * name:storeLog
	 * usage:store the log of this target. The differences between xxx and xxxlog is that log only store the entries
	 *       which have been chosen
	 */
	public void storeLog(){
		try {
			
			//it seems that only FileOutputStream can work
			String filename = this.target+"_log"+".txt";
			FileOutputStream fos = openFileOutput(filename,Context.MODE_PRIVATE);
			
			//first calculate the count
			int cnt = 0;
			for(int i=0;i<this.cts.size();i++){
				if((!this.cts.get(i).getText().toString().equals(""))&&(this.ticks.get(i).isChosen()))	cnt++;
			}
			
			fos.write((""+cnt+"\n").getBytes());
			
			for(int i=0;i<this.cts.size();i++){
				if((!this.cts.get(i).getText().toString().equals(""))&&(this.ticks.get(i).isChosen())) 	fos.write((this.cts.get(i).getText().toString()+"\n").getBytes());
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * name:store
	 * usage:this version has no input parameters.You can invoke it in the code
	 */
	public void store(){
		try {
			
			//it seems that only FileOutputStream can work
			String filename = this.target+".txt";
			FileOutputStream fos = openFileOutput(filename,Context.MODE_PRIVATE);
			fos.write((""+this.cts.size()+"\n").getBytes());
			
			for(int i=0;i<this.cts.size();i++){
				fos.write((this.cts.get(i).getText().toString()+"\n").getBytes());
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * name:addone(View)
	 * usage:the Onclick version for addone
	 */

	public void addone(View view){
		
		//NOTE:for we have only 8 differnt colors,we require that the number of events cannot be more than 8
		if (this.cts.size()==8) return;
		
		//the color we will use
		int color = cg.get();
		
		LinearLayout ll4 =(LinearLayout)this.findViewById(R.id.ll7);
		
		//now add one linearlayout
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		//ll.setLayoutParams(lp);
		ll.setBackgroundColor(color);
		ll.setGravity(Gravity.CENTER);
		ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,Convertor.dip2px(this,70)));
		
		
		//modification:now we need to put the tick after the editable view.And we can do editing at anytime
		
		
		//the edit text
		ChooseText ct = new ChooseText(this,this.cts.size());
		
		ct.setBackgroundColor(color);
		ct.setTextColor(Color.rgb(255, 255, 255));
		ct.setTag("ct");
		
		//store the background color here
		ct.setColor(color);
		
		//text is at center
		ct.setGravity(Gravity.CENTER);
		
		//set the limit of length
		ct.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
		ct.setSingleLine(true);
		ct.setEllipsize(TextUtils.TruncateAt.END);
		
		ll.addView(ct);
		ct.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,1));
		//ct.getLayoutParams().width = getWindowManager().getDefaultDisplay().getWidth()*3/4;
		
		//if it's in edit mode,give user the right to edit
		/*if(this.edit){
			ct.setFocusableInTouchMode(true);
			ct.setCursorVisible(true);
		}
		else{
			ct.setFocusableInTouchMode(false);
			ct.setCursorVisible(false);
		}*/
		
		//the image view(for ticking)
		TickView tick = new TickView(this,0);
		tick.setImageDrawable(getResources().getDrawable(R.drawable.empty));
		//tick.setVisibility(View.GONE);
		tick.setBackgroundColor(color);
		tick.setLayoutParams(this.lpl);
		
		ll.addView(tick);
		
		//the image for delete
		DeleteView iv = new DeleteView(this,this.cts.size());
		iv.setImageDrawable(this.getResources().getDrawable(R.drawable.delete3));
		iv.setTag("dv");
		iv.setBackgroundColor(color);
		iv.setLayoutParams(this.lpr);
		if(!this.edit) iv.setVisibility(View.GONE);
		else iv.setVisibility(View.VISIBLE);
		ll.addView(iv);
		
		
		ll4.addView(ll);
		
		this.cts.add(ct);
		this.dvs.add(iv);
		this.ticks.add(tick);
		
	}
	
	
	
	/*
	 * name:addone
	 * usage:add a new choosetext
	 */

	/*public void addone(){
		
		LinearLayout ll4 =(LinearLayout)this.findViewById(R.id.ll7);
		ll4.removeView(this.pv);
		
		//now add one linearlayout
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		//ll.setLayoutParams(lp);
		ll.setBackgroundColor(ColorGroup.get(this.cts.size()));
		ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,Convertor.dip2px(this,70)));
		ll.setGravity(Gravity.CENTER);
		
		//the image view(for ticking)
		ImageView tick = new ImageView(this);
		tick.setImageDrawable(getResources().getDrawable(R.drawable.chosen));
		tick.setVisibility(View.GONE);
		tick.setBackgroundColor(ColorGroup.get(this.cts.size()));
		ll.addView(tick);
		
		
		//the edit text
		ChooseText ct = new ChooseText(this,this.cts.size());
		
		ct.setBackgroundColor(ColorGroup.get(this.cts.size()));
		ct.setTextColor(Color.rgb(255, 255, 255));
		ct.setTag("ct");
		//text is at center
		ct.setGravity(Gravity.CENTER);
		
		//set the limit of length
		ct.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
		ct.setSingleLine(true);
		ct.setEllipsize(TextUtils.TruncateAt.END);
		
		ll.addView(ct);
		ct.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,1));
		//ct.getLayoutParams().width = getWindowManager().getDefaultDisplay().getWidth()*3/4;
		
		//the image
		DeleteView iv = new DeleteView(this,this.cts.size());
		iv.setImageDrawable(this.getResources().getDrawable(R.drawable.delete1));
		iv.setTag("dv");
		iv.setBackgroundColor(Color.rgb(0, 0, 0));
		iv.setBackgroundColor(ColorGroup.get(this.cts.size()));
		if(!this.edit) iv.setVisibility(View.GONE);
		else iv.setVisibility(View.VISIBLE);
		ll.addView(iv);
		
		
		ll4.addView(ll);
		
		this.cts.add(ct);
		this.dvs.add(iv);
		this.ticks.add(tick);
		
		//don't forget to adjust the position	
		ll4.addView(this.pv);
	}*/
	
	
	/*
	 * name:addEntry
	 * usage:addentry is a little different from addone.It is invoked on initialization
	 */

	public void addEntry(String content){
		
		LinearLayout ll4 =(LinearLayout)this.findViewById(R.id.ll7);
		
		//the color we will use
		int color = cg.get();
		
		//what we need is a tick, an edittext plus one image(for delete). so we need one more layer
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setBackgroundColor(color);
		ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,Convertor.dip2px(this,70)));
		ll.setGravity(Gravity.CENTER);
		
		//modification:the same as addone()
		
		//the edit text
		ChooseText ct = new ChooseText(this,this.cts.size());
		
		ct.setBackgroundColor(color);
		ct.setTextColor(Color.rgb(255, 255, 255));
		ct.setTag("ct");
		
		//store the background color here
		ct.setColor(color);
		
		//text is at center
		ct.setGravity(Gravity.CENTER);
		
		//set the limit of length
		ct.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
		ct.setSingleLine(true);
		ct.setEllipsize(TextUtils.TruncateAt.END);
		
		ll.addView(ct);
		ct.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,1));
		
		
		
		ct.setText(content.toCharArray(),0,content.length());
		
		//if it's in edit mode,give user the right to edit
		/*if(this.edit){
			ct.setFocusableInTouchMode(true);
			ct.setCursorVisible(true);
		}
		else{
			ct.setFocusableInTouchMode(false);
			ct.setCursorVisible(false);
		}*/
		
		//the image view(for ticking)
		TickView tick = new TickView(this,0);
		tick.setImageDrawable(getResources().getDrawable(R.drawable.empty));
		//tick.setVisibility(View.GONE);
		tick.setBackgroundColor(color);
		tick.setLayoutParams(this.lpl);
		ll.addView(tick);
		
		//now the image
		DeleteView iv = new DeleteView(this,this.cts.size());
		iv.setImageDrawable(this.getResources().getDrawable(R.drawable.delete3));
		iv.setTag("dv");
		iv.setBackgroundColor(color);
		iv.setLayoutParams(this.lpr);
		if(!edit)	iv.setVisibility(View.GONE);
		else iv.setVisibility(View.VISIBLE);
		ll.addView(iv);
		
		
		
		ll4.addView(ll);
		
		this.ticks.add(tick);
		this.cts.add(ct);
		this.dvs.add(iv);
	}
	
	
	/*
	 * deleteOne is opposite from addOne. it will delete some view and cause the whole to redraw
	 */
	public void deleteOne(int id){
		LinearLayout ll4 =(LinearLayout)this.findViewById(R.id.ll7);
		
		//if there are only 2 entries,just clean the data(the ring must contain at least two parts)
		if(this.cts.size()==2){
			this.cts.get(id).setText("".toCharArray(),0,0);
			return;
		}
		
		//remove someone
		ll4.removeView((View) this.cts.get(id).getParent());
		
		//claim this color
		cg.claim(this.cts.get(id).getColor());
		
		//remove chooseView from the arraylist
		this.cts.remove(id);
		this.dvs.remove(id);
		this.ticks.remove(id);
		
		//now update id
		for(int i=0;i<this.cts.size();i++){
			dvs.get(i).setId(i);
			this.cts.get(i).setId(i);
		}
		
		
		/*
		//first remove the plus
		ll4.removeView(this.pv);
		
		//now,layout bigger than id should be removed all
		for(int i=id;i<this.cts.size();i++){
			ll4.removeView((View) this.cts.get(i).getParent());
		}*/
		
		
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.event);
		
		initlayout();
		
		LinearLayout ll4 =(LinearLayout)this.findViewById(R.id.ll7);
		
		ScrollView sv = (ScrollView)this.findViewById(R.id.sv1);
		
		this.lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); 
		sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1));
		sv.getLayoutParams().width = getWindowManager().getDefaultDisplay().getWidth();
		this.lp.setMargins(0, 5, 0, 5);
		
		//also set margin for ll4
		//ll4.setLayoutParams(lp);
		//ll4.setLayoutParams(this.lp);
		
		//get the current target
		Intent intent = getIntent();
		this.target = intent.getStringExtra("target");
		
		//now we need to check if the target has been stored before. if so, we fetch the choices from the file
		File file = new File(this.getFilesDir().getPath().toString()+"/"+this.target+".txt");
		
		if(file.exists()){
	
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
				String line = reader.readLine();
				Log.d("a",line);
				int size = Integer.parseInt(line);
				
				//construct all the content and attach them to screen
				for(int i=0;i<size;i++){
					line = reader.readLine();
					this.addEntry(line);
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} else {
			if(this.target.equals("今天玩什么")){
				this.addEntry("仔细思考我有多失败");
				this.addEntry("");
			}
			for(int i=0;i<2;i++){
				this.addEntry("");
			}
			
		}
		
		
		//add it into the stack
		ActivityController.push(this);

		//now we add the plusView
		/*this.pv = new PlusView(this);
		pv.setBackgroundColor(Color.rgb(0, 0, 0));
		pv.setTextColor(Color.rgb(255, 255, 255));
		pv.setLayoutParams(lp);
		pv.getLayoutParams().width = getWindowManager().getDefaultDisplay().getWidth();
		ll4.addView(pv);*/
		

	}
}
