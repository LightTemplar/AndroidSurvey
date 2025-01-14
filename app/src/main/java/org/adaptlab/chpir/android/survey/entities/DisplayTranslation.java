package org.adaptlab.chpir.android.survey.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.adaptlab.chpir.android.survey.daos.BaseDao;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "DisplayTranslations")
public class DisplayTranslation implements SurveyEntity, Translation {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @ColumnInfo(name = "RemoteId", index = true)
    private Long mRemoteId;
    @SerializedName("text")
    @ColumnInfo(name = "Text")
    private String mText;
    @SerializedName("language")
    @ColumnInfo(name = "Language")
    private String mLanguage;
    @SerializedName("display_id")
    @ColumnInfo(name = "DisplayRemoteId", index = true)
    private Long mDisplayRemoteId;

    @NonNull
    public Long getRemoteId() {
        return mRemoteId;
    }

    public void setRemoteId(@NonNull Long id) {
        this.mRemoteId = id;
    }

    public String getText() {
        return mText;
    }

    public void setText(String title) {
        this.mText = title;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        this.mLanguage = language;
    }

    public Long getDisplayRemoteId() {
        return mDisplayRemoteId;
    }

    public void setDisplayRemoteId(Long remoteId) {
        this.mDisplayRemoteId = remoteId;
    }

    @Override
    public Type getType() {
        return new TypeToken<ArrayList<DisplayTranslation>>() {
        }.getType();
    }

    @Override
    public List<? extends SurveyEntity> getTranslations() {
        return null;
    }

    @Override
    public void save(BaseDao dao, List list) {
        dao.updateAll(list);
        dao.insertAll(list);
    }

    @Override
    public void setDeleted(boolean deleted) {

    }
}
