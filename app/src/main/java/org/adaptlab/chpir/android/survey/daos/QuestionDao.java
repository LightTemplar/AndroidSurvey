package org.adaptlab.chpir.android.survey.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.adaptlab.chpir.android.survey.entities.Question;
import org.adaptlab.chpir.android.survey.entities.relations.QuestionRelation;

import java.util.List;

@Dao
public abstract class QuestionDao extends BaseDao<Question> {
    @Query("SELECT * FROM Questions WHERE InstrumentRemoteId=:instrumentId AND Deleted=0 ORDER BY NumberInInstrument ASC")
    public abstract List<Question> instrumentQuestionsSync(Long instrumentId);

    @Query("SELECT * FROM Questions WHERE QuestionIdentifier=:identifier LIMIT 1")
    public abstract Question findByQuestionIdentifierSync(String identifier);

    @Query("SELECT * FROM Questions WHERE RemoteId=:id LIMIT 1")
    public abstract Question findByIdSync(Long id);

    @Transaction
    @Query("SELECT Questions.* FROM Questions INNER JOIN Displays ON Displays.RemoteId=Questions.DisplayId " +
            "WHERE Questions.DisplayId=:displayId AND Questions.InstrumentRemoteId=:instrumentId AND Questions.Deleted=0")
    public abstract LiveData<List<QuestionRelation>> displayQuestions(Long instrumentId, Long displayId);

}
