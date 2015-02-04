package org.adaptlab.chpir.android.survey;


import java.util.Date;

import org.adaptlab.chpir.android.survey.Models.ConsentForm;
import org.adaptlab.chpir.android.survey.Models.ConsentText;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class ConsentFragment extends Fragment {
    private TextView mConsentTextView;
    private EditText mNameEditText;
    private EditText mEmailEditText;
    private CheckBox mEmailConsentFormCheckBox;
    private Button mConsentButton;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        
        View v = inflater.inflate(R.layout.fragment_consent, parent, false);
        v.findViewById(R.id.consent_title);
        mConsentTextView = (TextView) v.findViewById(R.id.consent_text);
        mConsentTextView.setText(getConsentText());
        mConsentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        v.findViewById(R.id.consent_name);
        mNameEditText = (EditText) v.findViewById(R.id.consent_name_edit_text);
        v.findViewById(R.id.consent_email);
        mEmailEditText = (EditText) v.findViewById(R.id.consent_email_edit_text);
        mEmailConsentFormCheckBox = (CheckBox) v.findViewById(R.id.email_copy_of_consent_checkbox);
        
        mConsentButton = (Button) v.findViewById(R.id.consent_button);
        mConsentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ConsentForm consentForm = new ConsentForm();
                consentForm.setName(mNameEditText.getText().toString());
                consentForm.setEmail(mEmailEditText.getText().toString());
                consentForm.setDate(new Date());
                consentForm.setSendEmailCopy(mEmailConsentFormCheckBox.isChecked());
                consentForm.save();
                
                getActivity().finish();
            }            
        });
        
        return v;       
    }

	private String getConsentText() {
		String projectId = AppUtil.getAdminSettingsInstance().getProjectId();
		ConsentText consent = ConsentText.findByProjectId(Long.valueOf(projectId));
		return consent.getText();
	}
}