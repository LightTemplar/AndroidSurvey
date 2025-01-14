package org.adaptlab.chpir.android.survey.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.adaptlab.chpir.android.survey.daos.BaseDao;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "Sections")
public class Section implements SurveyEntity, Translatable {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @ColumnInfo(name = "RemoteId", index = true)
    private Long mRemoteId;
    @SerializedName("title")
    @ColumnInfo(name = "Title")
    private String mTitle;
    @SerializedName("deleted_at")
    @ColumnInfo(name = "Deleted")
    private boolean mDeleted;
    @SerializedName("instrument_id")
    @ColumnInfo(name = "InstrumentRemoteId", index = true)
    private Long mInstrumentRemoteId;
    @SerializedName("position")
    @ColumnInfo(name = "Position", index = true)
    private int mPosition;
    @SerializedName("randomize_displays")
    @ColumnInfo(name = "RandomizeDisplays")
    private boolean mRandomizeDisplays;
    @Ignore
    @SerializedName("section_translations")
    private List<SectionTranslation> mSectionTranslations;

    @NonNull
    public Long getRemoteId() {
        return mRemoteId;
    }

    public void setRemoteId(@NonNull Long mRemoteId) {
        this.mRemoteId = mRemoteId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public boolean isDeleted() {
        return mDeleted;
    }

    public void setDeleted(boolean mDeleted) {
        this.mDeleted = mDeleted;
    }

    public Long getInstrumentRemoteId() {
        return mInstrumentRemoteId;
    }

    public void setInstrumentRemoteId(Long mInstrumentRemoteId) {
        this.mInstrumentRemoteId = mInstrumentRemoteId;
    }

    public boolean isRandomizeDisplays() {
        return mRandomizeDisplays;
    }

    public void setRandomizeDisplays(boolean randomizeDisplays) {
        this.mRandomizeDisplays = randomizeDisplays;
    }

    public List<SectionTranslation> getSectionTranslations() {
        return mSectionTranslations;
    }

    public void setSectionTranslations(List<SectionTranslation> mSectionTranslations) {
        this.mSectionTranslations = mSectionTranslations;
    }

    @Override
    public Type getType() {
        return new TypeToken<ArrayList<Section>>() {
        }.getType();
    }

    @Override
    public List<SectionTranslation> getTranslations() {
        return mSectionTranslations;
    }

    @Override
    public void save(BaseDao dao, List list) {
        dao.updateAll(list);
        dao.insertAll(list);
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    @Override
    public String getText() {
        return getTitle();
    }
}
