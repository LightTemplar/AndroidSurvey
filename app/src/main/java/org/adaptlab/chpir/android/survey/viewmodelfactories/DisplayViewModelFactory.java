package org.adaptlab.chpir.android.survey.viewmodelfactories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.adaptlab.chpir.android.survey.viewmodels.DisplayViewModel;

public class DisplayViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final Long mRemoteId;

    public DisplayViewModelFactory(@NonNull Application application, Long id) {
        this.mApplication = application;
        this.mRemoteId = id;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DisplayViewModel(mApplication, mRemoteId);
    }

}