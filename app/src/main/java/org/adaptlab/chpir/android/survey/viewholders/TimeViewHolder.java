package org.adaptlab.chpir.android.survey.viewholders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import org.adaptlab.chpir.android.survey.utils.FormatUtils;

public class TimeViewHolder extends QuestionViewHolder {

    private int mHour;
    private int mMinute;
    private TimePicker mTimePicker;

    TimeViewHolder(View itemView, Context context, OnResponseSelectedListener listener) {
        super(itemView, context, listener);
    }

    @Override
    protected void createQuestionComponent(ViewGroup questionComponent) {
//        mTimePicker = new TimePicker(getActivity());
//        mHour = mTimePicker.getCurrentHour();
//        mMinute = mTimePicker.getCurrentMinute();
//        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//            @Override
//            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                mHour = hourOfDay;
//                mMinute = minute;
//                setResponse(null);
//                mSpecialResponses.clearCheck();
//            }
//        });
//        questionComponent.addView(mTimePicker);
    }

    @Override
    protected String serialize() {
        return FormatUtils.formatTime(mHour, mMinute);
    }

    @Override
    protected void deserialize(String responseText) {
        int[] timeComponents = FormatUtils.unformatTime(responseText);
        if (timeComponents != null) {
            mTimePicker.setCurrentHour(timeComponents[0]);
            mTimePicker.setCurrentMinute(timeComponents[1]);
        }
    }

    @Override
    protected void unSetResponse() {
    }

    @Override
    protected void showOtherText(int position) {
    }

}
