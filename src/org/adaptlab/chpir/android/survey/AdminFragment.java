package org.adaptlab.chpir.android.survey;

import org.adaptlab.chpir.android.survey.Models.AdminSettings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AdminFragment extends Fragment {

    private EditText mDeviceIdentifierEditText;
    private EditText mSyncIntervalEditText;
    private Button mSaveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_settings, parent,
                false);
        mDeviceIdentifierEditText = (EditText) v
                .findViewById(R.id.device_identifier_edit_text);
        mDeviceIdentifierEditText.setText(AdminSettings.getDeviceIdentifier());

        mSyncIntervalEditText = (EditText) v
                .findViewById(R.id.sync_interval_edit_text);
        mSyncIntervalEditText.setText(AdminSettings.getSyncInterval() + "");

        mSaveButton = (Button) v.findViewById(R.id.save_admin_settings_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AdminSettings.setDeviceIdentifier(mDeviceIdentifierEditText
                        .getText().toString());
                AdminSettings.setSyncInterval(Integer
                        .parseInt(mSyncIntervalEditText.getText().toString()));
                getActivity().finish();
            }
        });

        return v;
    }
}