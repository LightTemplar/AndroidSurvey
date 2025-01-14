package org.adaptlab.chpir.android.survey.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import org.adaptlab.chpir.android.survey.entities.ConditionSkip;
import org.adaptlab.chpir.android.survey.entities.Instruction;
import org.adaptlab.chpir.android.survey.entities.LoopQuestion;
import org.adaptlab.chpir.android.survey.entities.MultipleSkip;
import org.adaptlab.chpir.android.survey.entities.NextQuestion;
import org.adaptlab.chpir.android.survey.entities.OptionSet;
import org.adaptlab.chpir.android.survey.entities.Question;
import org.adaptlab.chpir.android.survey.entities.QuestionCollage;
import org.adaptlab.chpir.android.survey.entities.QuestionTranslation;
import org.adaptlab.chpir.android.survey.entities.Response;

import java.util.List;

public class QuestionRelation {
    @Embedded
    public Question question;
    @Relation(parentColumn = "InstructionId", entityColumn = "RemoteId", entity = Instruction.class)
    public List<InstructionRelation> instructions;
    @Relation(parentColumn = "PopUpInstructionId", entityColumn = "RemoteId", entity = Instruction.class)
    public List<InstructionRelation> popUpInstructions;
    @Relation(parentColumn = "AfterTextInstructionId", entityColumn = "RemoteId", entity = Instruction.class)
    public List<InstructionRelation> afterTextInstructions;
    @Relation(parentColumn = "RemoteOptionSetId", entityColumn = "RemoteId", entity = OptionSet.class)
    public List<OptionSetRelation> optionSets;
    @Relation(parentColumn = "RemoteSpecialOptionSetId", entityColumn = "RemoteId", entity = OptionSet.class)
    public List<OptionSetRelation> specialOptionSets;
    @Relation(parentColumn = "CarryForwardIdentifier", entityColumn = "QuestionIdentifier", entity = Question.class)
    public List<Question> carryForwardQuestions;
    @Relation(parentColumn = "CarryForwardOptionSetId", entityColumn = "RemoteId", entity = OptionSet.class)
    public List<OptionSetRelation> carryForwardOptionSets;
    @Relation(parentColumn = "QuestionIdentifier", entityColumn = "QuestionIdentifier", entity = NextQuestion.class)
    public List<NextQuestion> nextQuestions;
    @Relation(parentColumn = "QuestionIdentifier", entityColumn = "QuestionIdentifier", entity = MultipleSkip.class)
    public List<MultipleSkip> multipleSkips;
    @Relation(parentColumn = "QuestionIdentifier", entityColumn = "QuestionIdentifier", entity = ConditionSkip.class)
    public List<ConditionSkip> conditionSkips;
    @Relation(parentColumn = "QuestionIdentifier", entityColumn = "Parent", entity = LoopQuestion.class)
    public List<LoopQuestion> loopQuestions;
    @Relation(parentColumn = "QuestionIdentifier", entityColumn = "Looped", entity = LoopQuestion.class)
    public List<LoopQuestion> loopedQuestions;
    @Relation(parentColumn = "QuestionId", entityColumn = "QuestionRemoteId", entity = QuestionTranslation.class)
    public List<QuestionTranslation> translations;
    @Relation(parentColumn = "QuestionIdentifier", entityColumn = "QuestionIdentifier", entity = Response.class)
    public List<Response> responses;
    @Relation(parentColumn = "CarryForwardIdentifier", entityColumn = "QuestionIdentifier", entity = Response.class)
    public List<Response> carryForwardResponses;
    @Relation(parentColumn = "QuestionId", entityColumn = "QuestionId", entity = QuestionCollage.class)
    public List<QuestionCollageRelation> questionCollages;
    @Relation(parentColumn = "QuestionIdentifier", entityColumn = "CarryForwardIdentifier", entity = Question.class)
    public List<Question> followUpQuestions;
}
