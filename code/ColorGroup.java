package com.example.test;

import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Color;

/*
 * this class is used to store a variety of color
 */

public class ColorGroup {
	ArrayList<Integer> left =new ArrayList<Integer>(Arrays.asList(new Integer(Color.rgb(255, 205, 70)),
																		 new Integer(Color.rgb(52, 131, 213)), 
																		 new Integer(Color.rgb(240, 66, 109)),
																		 new Integer(Color.rgb(56, 169, 48)),
																		 new Integer(Color.rgb(249, 149, 56)),
																		 new Integer(Color.rgb(118, 81, 176)),
																		 new Integer(Color.rgb(0, 175, 189)),
																		 new Integer(Color.rgb(180, 91, 62))));
	ArrayList<Integer> used = new ArrayList();
	
	/*
	 * name:claim
	 * usage:when a color is deleted, restore it into left array
	 */
	public void claim(int rgb){
		int place = -1;
		for(int i=0;i<used.size();i++){
			if(used.get(i).intValue() == rgb){
				place = i;
			}
		}
		
		if(place!=-1){
			left.add(used.get(place));
			used.remove(place);
		}
	}
	
	/*
	 * name:get
	 * usage:get a color and mark it as used
	 */
	public int get(){
		
		//if we don't have any free color,give black
		if(left.size()==0) return Color.rgb(0, 0, 0);
		
		int rgb = left.get(0);
		used.add(left.get(0));
		left.remove(0);
		return rgb;
	}
	


}
