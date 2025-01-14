package org.adaptlab.chpir.android.survey.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import org.adaptlab.chpir.android.survey.entities.Instruction;
import org.adaptlab.chpir.android.survey.entities.Option;
import org.adaptlab.chpir.android.survey.entities.OptionCollage;
import org.adaptlab.chpir.android.survey.entities.OptionSetOption;

import java.util.List;

public class OptionSetOptionRelation {
    @Embedded
    public OptionSetOption optionSetOption;
    @Relation(parentColumn = "OptionRemoteId", entityColumn = "RemoteId", entity = Option.class)
    public List<OptionRelation> options;
    @Relation(parentColumn = "InstructionId", entityColumn = "RemoteId", entity = Instruction.class)
    public List<InstructionRelation> instructions;
    @Relation(parentColumn = "RemoteId", entityColumn = "OptionInOptionSetId", entity = OptionCollage.class)
    public List<OptionCollageRelation> optionCollages;
}
