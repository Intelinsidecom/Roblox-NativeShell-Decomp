package com.roblox.roblox_client;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/* loaded from: classes.dex */
public class FragmentAbout extends Fragment {
    private View mBackgroundView = null;
    private View mDialogueView = null;
    private TextView mTermsLicenscingPrivacy = null;
    private TextView mUserAgent = null;

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        this.mBackgroundView = view.findViewById(R.id.fragment_about_background);
        this.mDialogueView = view.findViewById(R.id.fragment_about_dialog_bg);
        this.mTermsLicenscingPrivacy = (TextView) view.findViewById(R.id.fragment_about_terms_licenscing_privacy);
        this.mUserAgent = (TextView) view.findViewById(R.id.fragment_about_user_agent);
        this.mBackgroundView.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.FragmentAbout.1
            @Override // android.view.View.OnClickListener
            public void onClick(View x) {
                FragmentAbout.this.getActivity().getFragmentManager().beginTransaction().remove(FragmentAbout.this).commit();
            }
        });
        this.mDialogueView.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.FragmentAbout.2
            @Override // android.view.View.OnClickListener
            public void onClick(View x) {
            }
        });
        String res = getString(R.string.TermsLicensingPrivacy);
        Spanned html = Html.fromHtml(res);
        this.mTermsLicenscingPrivacy.setText(html);
        this.mTermsLicenscingPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        this.mUserAgent.setText(RobloxSettings.userAgent());
        return view;
    }
}
