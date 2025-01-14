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

@Entity(tableName = "DisplayInstructions")
public class DisplayInstruction implements SurveyEntity {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @ColumnInfo(name = "RemoteId", index = true)
    private Long mRemoteId;
    @SerializedName("position")
    @ColumnInfo(name = "Position")
    private int mPosition;
    @SerializedName("display_id")
    @ColumnInfo(name = "DisplayRemoteId", index = true)
    private Long mDisplayRemoteId;
    @SerializedName("instruction_id")
    @ColumnInfo(name = "InstructionRemoteId", index = true)
    private Long mInstructionRemoteId;
    @SerializedName("deleted_at")
    @ColumnInfo(name = "Deleted")
    private boolean mDeleted;

    @NonNull
    public Long getRemoteId() {
        return mRemoteId;
    }

    public void setRemoteId(@NonNull Long mRemoteId) {
        this.mRemoteId = mRemoteId;
    }

    public boolean isDeleted() {
        return mDeleted;
    }

    public void setDeleted(boolean mDeleted) {
        this.mDeleted = mDeleted;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public Long getDisplayRemoteId() {
        return mDisplayRemoteId;
    }

    public void setDisplayRemoteId(Long mDisplayRemoteId) {
        this.mDisplayRemoteId = mDisplayRemoteId;
    }

    public Long getInstructionRemoteId() {
        return mInstructionRemoteId;
    }

    public void setInstructionRemoteId(Long mInstructionRemoteId) {
        this.mInstructionRemoteId = mInstructionRemoteId;
    }

    @Override
    public Type getType() {
        return new TypeToken<ArrayList<DisplayInstruction>>() {
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
