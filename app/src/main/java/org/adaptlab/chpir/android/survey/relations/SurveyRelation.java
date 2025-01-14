package org.adaptlab.chpir.android.survey.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import org.adaptlab.chpir.android.survey.entities.Response;
import org.adaptlab.chpir.android.survey.entities.Survey;

import java.util.List;

public class SurveyRelation {
    @Embedded
    public Survey survey;
    @Relation(parentColumn = "UUID", entityColumn = "SurveyUUID", entity = Response.class)
    public List<Response> responses;

}
