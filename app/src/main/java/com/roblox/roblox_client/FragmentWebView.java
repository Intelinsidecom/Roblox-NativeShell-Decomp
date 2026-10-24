package com.roblox.roblox_client;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/* loaded from: classes.dex */
public class FragmentWebView extends Fragment {
    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        return view;
    }

    public void setText(String item) {
        View view = getView().findViewById(R.id.page_web_view);
        TextView textView = (TextView) view;
        textView.setText(item);
    }
}
