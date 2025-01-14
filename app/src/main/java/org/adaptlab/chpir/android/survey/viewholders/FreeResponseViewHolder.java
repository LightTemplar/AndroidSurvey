package org.adaptlab.chpir.android.survey.viewholders;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.adaptlab.chpir.android.survey.R;
import org.adaptlab.chpir.android.survey.utils.FormatUtils;
import org.adaptlab.chpir.android.survey.verhoeff.ParticipantIdValidator;

import java.util.Timer;
import java.util.TimerTask;

import static org.adaptlab.chpir.android.survey.utils.ConstantUtils.BLANK;
import static org.adaptlab.chpir.android.survey.utils.ConstantUtils.EDIT_TEXT_DELAY;

public class FreeResponseViewHolder extends QuestionViewHolder {
    private EditText mFreeText;
    private String mText = "";
    private final TextWatcher mTextWatcher = new TextWatcher() {
        private boolean backspacing = false;
        private Timer timer;

        // Required by interface
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (timer != null) timer.cancel();
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            getSpecialResponses().clearCheck();
            if (!FormatUtils.isEmpty(s.toString())) {
                backspacing = before > count;
                mText = s.toString();
                if (getQuestion().getQuestionIdentifier().equals("ParticipantID")) {
                    mText = mText.toUpperCase();
                }
            }
        }

        public void afterTextChanged(Editable s) {
            if (!backspacing) {
                mFreeText.removeTextChangedListener(this);
                if (getQuestion().getQuestionIdentifier().equals("ParticipantID")) {
                    mFreeText.setText(ParticipantIdValidator.formatText(s.toString()));
                }
                mFreeText.setSelection(mFreeText.getText().length());
                mFreeText.addTextChangedListener(this);
            }
            timer = new Timer();
            if (!isDeserializing()) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Run on UI Thread
                        if (getContext() != null) {
                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    saveResponse();
                                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(mFreeText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                }
                            });
                        }
                    }
                }, EDIT_TEXT_DELAY); // delay before saving to db
            }
        }
    };

    FreeResponseViewHolder(View itemView, Context context, OnResponseSelectedListener listener) {
        super(itemView, context, listener);
    }

    @Override
    public void createQuestionComponent(ViewGroup questionComponent) {
        questionComponent.removeAllViews();
        mFreeText = new EditText(getContext());
        mFreeText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        beforeAddViewHook(mFreeText);
        mFreeText.setHint(R.string.free_response_edit_text);
        mFreeText.addTextChangedListener(mTextWatcher);
        questionComponent.addView(mFreeText);
    }

    protected void beforeAddViewHook(EditText editText) {
    }

    @Override
    protected void deserialize(String responseText) {
        mFreeText.setText(responseText);
    }

    @Override
    protected String serialize() {
        return mText;
    }

    @Override
    protected void unSetResponse() {
        mFreeText.removeTextChangedListener(mTextWatcher);
        mFreeText.setText(BLANK);
        mFreeText.addTextChangedListener(mTextWatcher);
        mFreeText.clearFocus();
    }

    @Override
    protected void showOtherText(int position) {
    }
}