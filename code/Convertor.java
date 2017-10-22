/*
 * Convertor:just used to convert dp to px
 */

package com.example.test;

import android.content.Context;

public class Convertor {
	//convert from dp to px
	public static int dip2px(Context context, float dpValue) {  
	    final float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (dpValue * scale + 0.5f);  
	}
}
