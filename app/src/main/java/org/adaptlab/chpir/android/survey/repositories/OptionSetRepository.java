package org.adaptlab.chpir.android.survey.repositories;

import android.app.Application;

import org.adaptlab.chpir.android.survey.SurveyRoomDatabase;
import org.adaptlab.chpir.android.survey.daos.BaseDao;
import org.adaptlab.chpir.android.survey.daos.OptionSetDao;
import org.adaptlab.chpir.android.survey.daos.OptionSetTranslationDao;
import org.adaptlab.chpir.android.survey.entities.Entity;
import org.adaptlab.chpir.android.survey.entities.OptionSet;
import org.adaptlab.chpir.android.survey.entities.OptionSetTranslation;
import org.adaptlab.chpir.android.survey.tasks.EntityDownloadTask;

public class OptionSetRepository extends Repository {
    private OptionSetDao mOptionSetDao;
    private OptionSetTranslationDao mOptionSetTranslationDao;

    public OptionSetRepository(Application application) {
        SurveyRoomDatabase db = SurveyRoomDatabase.getDatabase(application);
        mOptionSetDao = db.optionSetDao();
        mOptionSetTranslationDao = db.optionSetTranslationDao();
    }

    @Override
    public void download() {
        new EntityDownloadTask(this).execute();
    }

    @Override
    public String getRemoteTableName() {
        return "option_sets";
    }

    @Override
    public BaseDao getDao() {
        return mOptionSetDao;
    }

    @Override
    public BaseDao getTranslationDao() {
        return mOptionSetTranslationDao;
    }

    @Override
    public Entity getEntity() {
        return new OptionSet();
    }

    @Override
    public Entity getTranslationEntity() {
        return new OptionSetTranslation();
    }
}
