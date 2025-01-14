package org.adaptlab.chpir.android.survey.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.adaptlab.chpir.android.survey.daos.BaseDao;
import org.adaptlab.chpir.android.survey.vendor.BCrypt;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "DeviceUsers")
public class DeviceUser implements SurveyEntity {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @ColumnInfo(name = "RemoteId", index = true)
    private Long mRemoteId;
    @SerializedName("active")
    @ColumnInfo(name = "Active")
    private boolean mActive;
    @SerializedName("name")
    @ColumnInfo(name = "Name")
    private String mName;
    @SerializedName("username")
    @ColumnInfo(name = "UserName", index = true)
    private String mUserName;
    @SerializedName("password_digest")
    @ColumnInfo(name = "PasswordDigest")
    private String mPasswordDigest;

    @NonNull
    public Long getRemoteId() {
        return mRemoteId;
    }

    public void setRemoteId(@NonNull Long mRemoteId) {
        this.mRemoteId = mRemoteId;
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean mActive) {
        this.mActive = mActive;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getPasswordDigest() {
        return mPasswordDigest;
    }

    public void setPasswordDigest(String mPasswordDigest) {
        this.mPasswordDigest = mPasswordDigest;
    }

    public boolean checkPassword(String password) {
        if (!mActive) {
            return false;
        }
        return BCrypt.checkpw(password, mPasswordDigest);
    }

    @Override
    public Type getType() {
        return new TypeToken<ArrayList<DeviceUser>>() {
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
