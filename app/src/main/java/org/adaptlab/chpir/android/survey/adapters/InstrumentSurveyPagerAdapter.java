package org.adaptlab.chpir.android.survey.adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.adaptlab.chpir.android.survey.InstrumentActivity;
import org.adaptlab.chpir.android.survey.R;
import org.adaptlab.chpir.android.survey.entities.Settings;
import org.adaptlab.chpir.android.survey.viewmodels.SettingsViewModel;
import org.adaptlab.chpir.android.survey.viewpagerfragments.InstrumentPagerFragment;
import org.adaptlab.chpir.android.survey.viewpagerfragments.SubmittedSurveyPagerFragment;
import org.adaptlab.chpir.android.survey.viewpagerfragments.SurveyPagerFragment;

import java.util.ArrayList;

public class InstrumentSurveyPagerAdapter extends FragmentPagerAdapter {
    private final Context mContext;
    private ArrayList<String> mTabs;

    public InstrumentSurveyPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        setTabs();
    }

    private void setTabs() {
        SettingsViewModel settingsViewModel = ViewModelProviders.of((InstrumentActivity) mContext).get(SettingsViewModel.class);
        settingsViewModel.getSettings().observe((InstrumentActivity) mContext, new Observer<Settings>() {
            @Override
            public void onChanged(@Nullable Settings settings) {
                mTabs = new ArrayList<>();
                mTabs.add(mContext.getString(R.string.instruments));
                mTabs.add(mContext.getString(R.string.ongoing_surveys));
                mTabs.add(mContext.getString(R.string.submitted_surveys));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public Fragment getItem(int position) {
        return createPagerFragment(mTabs.get(position));
    }

    private Fragment createPagerFragment(String name) {
        if (name.equals(mContext.getString(R.string.instruments))) {
            return new InstrumentPagerFragment();
        } else if (name.equals(mContext.getString(R.string.ongoing_surveys))) {
            return new SurveyPagerFragment();
        } else if (name.equals(mContext.getString(R.string.submitted_surveys))) {
            return new SubmittedSurveyPagerFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mTabs == null ? 0 : mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position);
    }

}