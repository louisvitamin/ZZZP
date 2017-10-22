package com.example.test;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

@SuppressLint("ServiceCast")
public class Target extends Activity{
	
	//all the target to be chosed
	private ArrayList<ChooseText> cts = new ArrayList();
	
	//all the image corresponding to targets
	private ArrayList<DeleteView> dvs = new ArrayList();
	
	//all the borders
	private ArrayList<TextView> borders = new ArrayList();
	
	//all the ticks correspoinding to the targets(to mark that they have been chosen)
	private ArrayList<ImageView> ticks = new ArrayList();
	
	//add one target
	private LinearLayout pv;
	
	//the layout for all textview underlying
	private LinearLayout.LayoutParams lp;
	
	//the layout for linearlayout
	private LinearLayout.LayoutParams lp2;
	
	//the layout for tick:go to the rightside a little bit
	private LinearLayout.LayoutParams lpr;
	
	//the layout for cross:go to the leftside a little bit
	private LinearLayout.LayoutParams lpl;
	
	//will be set when some choosetext is selected
	private int selected = -1;
	
	//will be set when some choosetext is clicked(NOTE: it is different from selected because it will set
	//when some choosetext is BEING EDITED)
	private int current = -1;
	
	private boolean edit;
	
	

	
	/*
	 * name:initlayout
	 * usage:just set several layout parameters for later use
	 */
	private void initlayout(){
		this.lp2 =new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,Convertor.dip2px(this,50));
		lp2.setMargins(0, 0, 0, 0);
		int roff = Convertor.dip2px(this, 15);
		int loff = Convertor.dip2px(this, 10);
		this.lpr = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.lpr.setMargins(0, 0, roff, 0);
		this.lpl = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.lpl.setMargins(loff, 0, 0, 0);
	}
	
	
	public void setCurrent(int c){
		this.current = c;
		Log.d("a","current:"+c);
	}
	
	/*
	 * name:setSelected
	 * usage:set the selected chosed target
	 * NOTE:before setting, we need to check the last clicked view. If it has just been edited,we should make it uneditable later,
	 *      otherwise we need to clear the focus
	 */
	public void setSelected(int s){
		
		//first check the previous (clicked) one
		if(this.current!=-1){
			Log.d("a","not -1");
			
			//current may have been deleted
			if(this.current<=this.cts.size()){
			
				cts.get(this.current).clearFocus();
				if(!(cts.get(this.current).getText().toString().equals(""))){
					Log.d("a","clear "+current);
					cts.get(this.current).setFocusable(false);
					cts.get(this.current).setCursorVisible(false);
				}
			}
		}
		
		//now consider about the current one:if it has been chosen,we have to cancel it
		if(this.cts.get(s).isChosen() == true){
			this.cts.get(s).setChosen(false);
			this.ticks.get(s).setVisibility(View.GONE);
			this.selected = -1;
		}
		//else:remove other's chosen state and mark itself as chosen
		else{
			this.selected = s;
			ticks.get(s).setVisibility(View.VISIBLE);
			for(int i=0;i<cts.size();i++){
				if(i!=s){
					cts.get(i).setChosen(false);
					ticks.get(i).setVisibility(View.GONE);
				}
			}
			this.cts.get(s).setChosen(true);
		}
		
		//don't forget to set the "current" clicked one
		this.setCurrent(s);
	}
	
	/*
	 * name:onKeyDown
	 * usage:@see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 *       we just need to handle the back button
	 */
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if((keyCode == KeyEvent.KEYCODE_BACK)&&(event.getRepeatCount()==0)){
			
			//if in edit mode,just back to the normal mode
			if(this.edit == true){
				for(int i=0;i<this.cts.size();i++){
					dvs.get(i).setVisibility(View.GONE);
				}
				this.edit = false;
				return true;
			}
			//not in edit mode:just quit(to the home page)!
			else{
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				startActivity(startMain);
				
				//quit! 
				System.exit(0);		
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	/*
	 * name:next
	 * usage:callback function of next button.jump to page 2
	 */
	public void next(View view){
		
		//first check if some target has been chosen
		if(this.selected == -1){
			Log.d("a","no");
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("提示");
			ab.setMessage("请选择一个目标");
			ab.setPositiveButton("ok", null);
			ab.show();
		}else{
			//put the params
			Intent intent = new Intent(this,Notice.class);
			intent.putExtra("target",this.cts.get(this.selected).getText().toString());
			startActivity(intent);
		}
	}
	
	/*
	 * name:edit
	 * usage:the callback funtion of the edit button.Just make the crosses visible
	 */
	public void edit(View view){
		if(this.edit == false){
			for(int i=0;i<this.cts.size();i++){
				dvs.get(i).setVisibility(View.VISIBLE);
			}
			this.edit = true;
		}
		else{
			for(int i=0;i<this.cts.size();i++){
				dvs.get(i).setVisibility(View.GONE);
			}
			this.edit = false;
		}
	}
	
	
	/*
	 * name:addone
	 * usage:add a new choosetext
	 */
	public void addone(){
		
		LinearLayout ll4 =(LinearLayout)this.findViewById(R.id.ll7);
		ll4.removeView(this.pv);
		
		//now add one linearlayout
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		//ll.setBackgroundColor(Color.rgb(49, 54, 68));
		ll.setLayoutParams(this.lp2);
		ll.setGravity(Gravity.CENTER);
		
		/*the content in this layout
		 * a tick as chosen
		 * the text to be editted
		 * the image for deleting
		 */
		
		//the image view(for ticking)
		ImageView tick = new ImageView(this);
		tick.setImageDrawable(getResources().getDrawable(R.drawable.chosen));
		tick.setVisibility(View.GONE);
		tick.setBackgroundColor(Color.argb(0, 49, 54, 68));
		tick.setLayoutParams(lpl);
		ll.addView(tick);
		
		//the edit text
		ChooseText ct = new ChooseText(this,this.cts.size());
		
		ct.setBackgroundColor(Color.argb(0, 49, 54, 68));
		ct.setTextColor(Color.rgb(255, 255, 255));
		ct.setTag("ct");
		
		//text is at center
		ct.setGravity(Gravity.CENTER);
		
		ct.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,1));
		ct.getLayoutParams().width = getWindowManager().getDefaultDisplay().getWidth()*3/4;
		
		//set the limit of length
		ct.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
		ct.setSingleLine(true);
		ct.setEllipsize(TextUtils.TruncateAt.END);
		
		ll.addView(ct);
		

		
		//the image
		DeleteView iv = new DeleteView(this,this.cts.size());
		iv.setImageDrawable(this.getResources().getDrawable(R.drawable.delete1));
		iv.setTag("dv");
		iv.setBackgroundColor(Color.argb(0,49,54,68));
		iv.setLayoutParams(lpr);
		if(!this.edit) iv.setVisibility(View.GONE);
		else iv.setVisibility(View.VISIBLE);
		ll.addView(iv);
		
		//set the background. If it is the first one, use the one with upper shadow
		if(this.cts.size()==0){
			ll.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadowup3));
		}
		else ll.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadowlr3));		
		
		ll4.addView(ll);

		//now one more thing: an empty view as a border
		/*TextView tv = new TextView(this);
		tv.setBackgroundColor(Color.rgb(55, 61, 81));
		LinearLayout.LayoutParams lptmp =new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,2);
		lptmp.setMargins(0, 0, 0, 0);
		
		tv.setLayoutParams(lptmp);
		ll4.addView(tv);*/
		
		
		this.cts.add(ct);
		this.dvs.add(iv);
		//this.borders.add(tv);
		this.ticks.add(tick);

		
		//don't forget to adjust the position	
		ll4.addView(this.pv);
	}
	
	
	/*
	 * name:addEntry
	 * usage:addentry is a little different from addone.It is invoked on initialization
	 * note:it is also a little different from Event.addEntry 'cause we don't want user to edit their name
	 */
	public void addEntry(String content){
		
		LinearLayout ll4 =(LinearLayout)this.findViewById(R.id.ll7);
		
		//what we need is a tick, a edittext and one image(for delete). so we need one more layer
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		//ll.setBackgroundColor(Color.rgb(49, 54, 68));
		//ll.setLayoutParams(lp);
		ll.setLayoutParams(this.lp2);
		ll.setGravity(Gravity.CENTER);
		
		//the image view(for ticking)
		ImageView tick = new ImageView(this);
		tick.setImageDrawable(getResources().getDrawable(R.drawable.chosen));
		tick.setVisibility(View.GONE);
		tick.setBackgroundColor(Color.argb(0, 49, 54, 68));
		tick.setLayoutParams(lpl);
		ll.addView(tick);
		
		//the edit text
		ChooseText ct = new ChooseText(this,this.cts.size());
		
		ct.setBackgroundColor(Color.argb(0,49, 54, 68));
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
		
		
		
		ct.setText(content.toCharArray(),0,content.length());
		ct.setFocusableInTouchMode(false);
		
		//hide the cursor(I can't hide it by setFocusable()!)
		ct.setCursorVisible(false);
		//ct.setEnabled(false);
		
		//now the image
		DeleteView iv = new DeleteView(this,this.cts.size());
		iv.setImageDrawable(this.getResources().getDrawable(R.drawable.delete1));
		iv.setBackgroundColor(Color.argb(0, 49, 54, 68));
		iv.setTag("dv");
		iv.setLayoutParams(lpr);
		if(!edit)	iv.setVisibility(View.GONE);
		else iv.setVisibility(View.VISIBLE);
		ll.addView(iv);
		
		
		//set the background. If it is the first one, use the one with upper shadow
		if(this.cts.size()==0){
			ll.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadowup3));
		}
		else ll.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadowlr3));
		
		
		ll4.addView(ll);
		
		//now one more thing: an empty view as a border
		/*TextView tv = new TextView(this);
		tv.setBackgroundColor(Color.rgb(55, 61, 81));
		LinearLayout.LayoutParams lptmp =new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,2);
		lptmp.setMargins(10, 0, 10, 0);
		
		tv.setLayoutParams(lptmp);
		ll4.addView(tv);*/
		
		this.cts.add(ct);
		this.dvs.add(iv);
		//this.borders.add(tv);
		this.ticks.add(tick);
		
		
	}
	
	/*
	 * name:deleteOne
	 * usage:deleteOne is opposite from addOne. it will delete some view and cause the whole to redraw
	 * 		 note that we need to delete the corresponding file
	 */
	public void deleteOne(int id){
		
		LinearLayout ll4 =(LinearLayout)this.findViewById(R.id.ll7);
		
		//delete the file first
		String target = (this.cts.get(id).getText().toString())+".txt";
		
		File file = new File(this.getFilesDir(),target);
		
		if(file.exists()){
			Log.d("a",file.getAbsolutePath());
			deleteFile(file.getName());
		}
		
		//if there are only 3,just clean the data
		if(this.cts.size()==3){
			this.cts.get(id).setText("".toCharArray(),0,0);
			
			//for there're some pre-defined questions, we have to make it editable now
			this.cts.get(id).setFocusableInTouchMode(true);
			this.cts.get(id).setCursorVisible(true);
			return;
		}
		
		//remove someone
		ll4.removeView((View) this.cts.get(id).getParent());
		//ll4.removeView(this.borders.get(id));
		
		//remove chooseView from the arraylist
		this.cts.remove(id);
		this.dvs.remove(id);
		//this.borders.remove(id);
		this.ticks.remove(id);
		
		//now update id(and reset all the background)
		for(int i=0;i<this.cts.size();i++){
			dvs.get(i).setId(i);
			this.cts.get(i).setId(i);
			LinearLayout parent  = (LinearLayout) this.cts.get(i).getParent();
			if(i==0) parent.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadowup3));
			else parent.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadowlr3));
		}
		
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.target);
		
		//initialize the layout
		this.initlayout();
		

		
		//now set the font for TARGET
		TextView tv = (TextView)this.findViewById(R.id.textView2);
		Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/segoe_script.ttf");
		tv.setTypeface(typeFace);
		
		LinearLayout ll4 =(LinearLayout)this.findViewById(R.id.ll7);
		//ll4.setLayoutParams(this.lp);
		
		
		ScrollView sv = (ScrollView)this.findViewById(R.id.sv1);
		
		//sv.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadow1));
		
		
		//initialize the scrollview
		this.lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1));
		sv.getLayoutParams().width = getWindowManager().getDefaultDisplay().getWidth()*3/4;
		this.lp.setMargins(0, 0, 0, 0);
		
		//ll4.setLayoutParams(lp);
		
		//3 basic targets
		this.addEntry("今天吃什么");
		this.addEntry("今天谁请客");
		this.addEntry("今天玩什么");
		
		//scan the file system if there's some user-defined target
		File dir = this.getFilesDir();
		File[] fs = dir.listFiles();
		for(int i=0;i<fs.length;i++){
			String name = fs[i].getName();
			
			//we want to filter the log
			String tail = name.substring(name.length()-8,name.length()-4);
			if(tail.equals("_log"))		continue;
			
			Log.d("a",name);
			if((!name.equals("今天吃什么.txt"))&&(!name.equals("今天谁请客.txt"))&&(!name.equals("今天玩什么.txt"))){
				name = name.substring(0, name.length()-4);
				this.addEntry(name);
			}
		}

		//now we add the plusView
		/*this.pv = new PlusView(this);
		//this.pv.setLayoutParams(this.lp2);
		pv.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadowdown2));
		//pv.setBackgroundColor(Color.rgb(49, 54, 68));
		pv.setTextColor(Color.rgb(255, 255, 255));
		pv.setGravity(Gravity.CENTER);
		//pv.getLayoutParams().width = getWindowManager().getDefaultDisplay().getWidth()*3/4;
		pv.setLayoutParams(this.lp2);
		
		ll4.addView(pv);*/
		
		//now we add the plus(a plus in a linearlayout)
		this.pv = new LinearLayout(this);
		this.pv.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,Convertor.dip2px(this,60));
		this.pv.setLayoutParams(lpp);
		this.pv.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadowdown2));
		
		//the plus picture
		ImageView plus = new ImageView(this);
		plus.setBackgroundDrawable(getResources().getDrawable(R.drawable.plus));
		this.pv.addView(plus);
		
		//add touchListener here
		this.pv.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Target.this.addone();
			}
			
		});

		ll4.addView(pv);
		
		//add it into the stack
		ActivityController.push(this);
		
		//I must be the first one! kill all activities before me!
		ActivityController.popall();
		

	}
	

	
}
