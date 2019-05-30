package org.adaptlab.chpir.android.survey.entities.relations;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import org.adaptlab.chpir.android.survey.entities.Display;
import org.adaptlab.chpir.android.survey.entities.Instrument;

import java.util.List;

public class InstrumentRelation {
    @Embedded
    public Instrument instrument;
    @Relation(parentColumn = "RemoteId", entityColumn = "InstrumentRemoteId", entity = Display.class)
    public List<Display> displays;
}
