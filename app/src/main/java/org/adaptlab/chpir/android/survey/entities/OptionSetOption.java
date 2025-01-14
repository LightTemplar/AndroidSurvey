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

@Entity(tableName = "OptionSetOptions")
public class OptionSetOption implements SurveyEntity {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @ColumnInfo(name = "RemoteId", index = true)
    private Long mRemoteId;
    @SerializedName("number_in_question")
    @ColumnInfo(name = "Position")
    private Integer mPosition;
    @SerializedName("option_set_id")
    @ColumnInfo(name = "OptionSetRemoteId", index = true)
    private Long mOptionSetRemoteId;
    @SerializedName("option_id")
    @ColumnInfo(name = "OptionRemoteId", index = true)
    private Long mOptionRemoteId;
    @SerializedName("special")
    @ColumnInfo(name = "Special")
    private boolean mSpecial;
    @SerializedName("deleted_at")
    @ColumnInfo(name = "Deleted")
    private boolean mDeleted;
    @SerializedName("is_exclusive")
    @ColumnInfo(name = "Exclusive")
    private boolean mExclusive;
    @SerializedName("instruction_id")
    @ColumnInfo(name = "InstructionId")
    private Long mInstructionId;
    @SerializedName("allow_text_entry")
    @ColumnInfo(name = "AllowTextEntry")
    private boolean mAllowTextEntry;
    @SerializedName("exclusion_ids")
    @ColumnInfo(name = "ExclusionIds")
    private String mExclusionIds;

    @NonNull
    public Long getRemoteId() {
        return mRemoteId;
    }

    public void setRemoteId(@NonNull Long mRemoteId) {
        this.mRemoteId = mRemoteId;
    }

    public Integer getPosition() {
        return mPosition;
    }

    public void setPosition(Integer mPosition) {
        this.mPosition = mPosition;
    }

    public Long getOptionSetRemoteId() {
        return mOptionSetRemoteId;
    }

    public void setOptionSetRemoteId(Long mOptionSetRemoteId) {
        this.mOptionSetRemoteId = mOptionSetRemoteId;
    }

    public Long getOptionRemoteId() {
        return mOptionRemoteId;
    }

    public void setOptionRemoteId(Long mOptionRemoteId) {
        this.mOptionRemoteId = mOptionRemoteId;
    }

    public boolean isSpecial() {
        return mSpecial;
    }

    public void setSpecial(boolean mSpecial) {
        this.mSpecial = mSpecial;
    }

    public boolean isDeleted() {
        return mDeleted;
    }

    public void setDeleted(boolean mDeleted) {
        this.mDeleted = mDeleted;
    }

    public boolean isExclusive() {
        return mExclusive;
    }

    public void setExclusive(boolean mExclusive) {
        this.mExclusive = mExclusive;
    }

    public Long getInstructionId() {
        return mInstructionId;
    }

    public void setInstructionId(Long mInstructionId) {
        this.mInstructionId = mInstructionId;
    }

    public boolean isAllowTextEntry() {
        return mAllowTextEntry;
    }

    public void setAllowTextEntry(boolean mAllowTextEntry) {
        this.mAllowTextEntry = mAllowTextEntry;
    }

    public String getExclusionIds() {
        return mExclusionIds;
    }

    public void setExclusionIds(String mExclusionIds) {
        this.mExclusionIds = mExclusionIds;
    }

    @Override
    public Type getType() {
        return new TypeToken<ArrayList<OptionSetOption>>() {
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
}
