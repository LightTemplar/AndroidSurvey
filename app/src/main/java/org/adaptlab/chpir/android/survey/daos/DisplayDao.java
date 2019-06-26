package org.adaptlab.chpir.android.survey.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import org.adaptlab.chpir.android.survey.entities.Display;

import java.util.List;

@Dao
public abstract class DisplayDao extends BaseDao<Display> {
    @Query("SELECT * FROM Displays WHERE InstrumentRemoteId=:instrumentId AND Deleted=0 ORDER BY Position ASC")
    public abstract List<Display> instrumentDisplaysSync(Long instrumentId);

    @Query("SELECT * FROM Displays WHERE InstrumentRemoteId=:instrumentId AND Deleted=0 ORDER BY Position ASC")
    public abstract LiveData<List<Display>> instrumentDisplays(Long instrumentId);

    @Query("SELECT * FROM Displays WHERE RemoteId=:id")
    public abstract Display findByIdSync(Long id);

    @Query("SELECT * FROM Displays WHERE Title=:title AND InstrumentRemoteId=:instrumentId")
    public abstract Display findByTitleAndInstrumentIdSync(String title, Long instrumentId);
}