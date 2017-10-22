/*
 * ActivityController
 * act as a stack to store all the activity.Used to manage the life cycle of activities mutually
 */

package com.example.test;

import java.util.ArrayList;

import android.app.Activity;

public class ActivityController {
	static ArrayList<Activity> acs = new ArrayList<Activity>();
	
	/*
	 * name:push
	 * usage:add a new activity
	 */
	static void push(Activity act){
		acs.add(act);
	}
	
	
	/*
	 * name:popall
	 * usage:pop all the activities instead of the one at the bottom
	 */
	static void popall(){
		for(int i = 0; i < acs.size()-1; i++){
			acs.get(i).finish();
		}
	}
}
