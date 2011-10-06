/* Copyright (c) 2006-2011 Regents of the University of Minnesota.
   For licensing terms, see the file LICENSE.
 */

package com.citizensense.android;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main Activity
 * @author Phil Brown
 */
public class CitizenSense extends TabActivity implements OnClickListener {
    
	/** Reference to the tab controller*/
	TabHost tabHost;
	/** Reference to the view inside the tabHost*/
	View tabView;
	
	/** Inflate the view and set the tab functions.*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ((ImageView) findViewById(R.id.updates_menu_image)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.updates_menu_menu)).setOnClickListener(this);
        ((TextView) findViewById(R.id.updates_menu_text)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.quick_profile_image)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.quick_profile_menu)).setOnClickListener(this);
        ((TextView) findViewById(R.id.quick_profile_text)).setOnClickListener(this);
        ((TextView) findViewById(R.id.quick_profile_pts)).setOnClickListener(this);
        
        tabHost = getTabHost();
        Intent intent;

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, Home.class);//CampaignBrowser.class);
        includeTab(intent, "home", "Campaign Browser", 0);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, MyCampaignMap.class);
        includeTab(intent, "map", "Map", 0);
        
        intent = new Intent().setClass(this, Profile.class);
        includeTab(intent, "profile", "Me", 0);

        
        //TODO Save the last tab they were on and store it in sharedPreferences
        //or in the onSavedInstanceState bundle
        tabHost.setCurrentTab(0);
    }//onCreate
    
    /**
     * Include tab in the layout
     * @param intent Intent to call when the tab is selected
     * @param tag Tag to reference this tab
     * @param text Text displayed on this tab
     * @param resid Image resource of the drawable for the tab
     */
    public void includeTab(Intent intent, String tag, String text, int resid) {
    	tabView = LayoutInflater.from(this).inflate(R.layout.tab, null);
    	if (text != null) {
    		((TextView) tabView.findViewById(R.id.tab_text)).setText(text);
    	}
    	else {
    		((TextView) tabView.findViewById(R.id.tab_text)).setVisibility(View.GONE);
    	}
    	if (resid != 0) {
    		((ImageView) tabView.findViewById(R.id.tab_image)).setBackgroundResource(resid);
    	}
    	else {
    		((ImageView) tabView.findViewById(R.id.tab_image)).setVisibility(View.GONE);
    	}
   		tabHost.addTab(tabHost.newTabSpec(tag).setIndicator(tabView).setContent(intent));
    }//includeTab

    /** Handle clicks*/
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.updates_menu_image:
		case R.id.updates_menu_menu:
		case R.id.updates_menu_text:
			Toast.makeText(CitizenSense.this, "updates", Toast.LENGTH_SHORT).show();
			break;
		case R.id.quick_profile_image:
		case R.id.quick_profile_menu:
		case R.id.quick_profile_pts:
		case R.id.quick_profile_text:
			Toast.makeText(CitizenSense.this, "profile", Toast.LENGTH_SHORT).show();
			break;
			
		}
		
	}//onClick
	
}//CitizenSense