package com.roblox.roblox_client;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class InternalBuildLayout extends LinearLayout {
    InternalBuildLayout(Context context) {
        super(context);
        setGravity(80);
        Spinner spinner = new Spinner(context);
        ArrayList<String> spinnerArray = getServerURLs();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter((SpinnerAdapter) spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.roblox.roblox_client.InternalBuildLayout.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long i) {
                RobloxSettings.setBaseUrl(parent.getItemAtPosition(position).toString());
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        addView(spinner);
    }

    private ArrayList<String> getServerURLs() {
        ArrayList<String> array = new ArrayList<>();
        boolean isTablet = RobloxSettings.isTablet();
        String m = isTablet ? "" : "m.";
        String www = isTablet ? "www." : "m.";
        array.add(Utils.format("%sroblox.com/", www));
        array.add(String.format("%ssitetest1.robloxlabs.com/", www));
        array.add(String.format("%ssitetest2.robloxlabs.com/", www));
        array.add(String.format("%ssitetest3.robloxlabs.com/", www));
        array.add(String.format("%ssitetest4.robloxlabs.com/", www));
        array.add(String.format("%sallen.sitetest3.robloxlabs.com/", m));
        array.add(String.format("%santhony.sitetest3.robloxlabs.com/", m));
        array.add(String.format("%sguru.sitetest3.robloxlabs.com/", m));
        array.add(String.format("%smedhora.sitetest3.robloxlabs.com/", m));
        array.add(String.format("%srosemary.sitetest3.robloxlabs.com/", m));
        array.add(String.format("%ssairam.sitetest3.robloxlabs.com/", m));
        array.add(String.format("%sshannon.sitetest3.robloxlabs.com/", m));
        array.add(String.format("%svlad.sitetest3.robloxlabs.com/", m));
        array.add(String.format("%sgametest5.robloxlabs.com/", www));
        array.add(String.format("%sgametest4.robloxlabs.com/", www));
        array.add(String.format("%sgametest3.robloxlabs.com/", www));
        array.add(String.format("%sgametest2.robloxlabs.com/", www));
        array.add(String.format("%sgametest1.robloxlabs.com/", www));
        return array;
    }
}
