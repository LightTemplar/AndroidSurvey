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

@Entity(tableName = "OptionSets")
public class OptionSet implements SurveyEntity {
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
    @SerializedName("instruction_id")
    @ColumnInfo(name = "InstructionRemoteId", index = true)
    private Long mInstructionRemoteId;
    @SerializedName("special")
    @ColumnInfo(name = "Special")
    private boolean mSpecial;
    @SerializedName("has_images")
    @ColumnInfo(name = "HasImages")
    private boolean mHasImages;
    @SerializedName("align_image_vertical")
    @ColumnInfo(name = "AlignImageVertical")
    private boolean mAlignImageVertical;
    @Ignore
    @SerializedName("option_set_translations")
    private List<OptionSetTranslation> mOptionSetTranslations;

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

    public Long getInstructionRemoteId() {
        return mInstructionRemoteId;
    }

    public void setInstructionRemoteId(Long mInstructionRemoteId) {
        this.mInstructionRemoteId = mInstructionRemoteId;
    }

    public boolean isSpecial() {
        return mSpecial;
    }

    public void setSpecial(boolean mSpecial) {
        this.mSpecial = mSpecial;
    }

    public boolean hasImages() {
        return mHasImages;
    }

    public void setHasImages(boolean mHasImages) {
        this.mHasImages = mHasImages;
    }

    public boolean isAlignImageVertical() {
        return mAlignImageVertical;
    }

    public void setAlignImageVertical(boolean mAlignImageVertical) {
        this.mAlignImageVertical = mAlignImageVertical;
    }

    public List<OptionSetTranslation> getOptionSetTranslations() {
        return mOptionSetTranslations;
    }

    public void setOptionSetTranslations(List<OptionSetTranslation> mOptionSetTranslations) {
        this.mOptionSetTranslations = mOptionSetTranslations;
    }

    @Override
    public Type getType() {
        return new TypeToken<ArrayList<OptionSet>>() {
        }.getType();
    }

    @Override
    public List<OptionSetTranslation> getTranslations() {
        return mOptionSetTranslations;
    }

    @Override
    public void save(BaseDao dao, List list) {
        dao.updateAll(list);
        dao.insertAll(list);
    }
}
