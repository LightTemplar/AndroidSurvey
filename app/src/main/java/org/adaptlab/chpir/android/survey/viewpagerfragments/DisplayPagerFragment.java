package org.adaptlab.chpir.android.survey.viewpagerfragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.adaptlab.chpir.android.survey.R;
import org.adaptlab.chpir.android.survey.adapters.DisplayAdapter;
import org.adaptlab.chpir.android.survey.adapters.QuestionRelationAdapter;
import org.adaptlab.chpir.android.survey.adapters.QuestionRelationTableAdapter;
import org.adaptlab.chpir.android.survey.entities.LoopQuestion;
import org.adaptlab.chpir.android.survey.entities.MultipleSkip;
import org.adaptlab.chpir.android.survey.entities.Option;
import org.adaptlab.chpir.android.survey.entities.Question;
import org.adaptlab.chpir.android.survey.entities.Response;
import org.adaptlab.chpir.android.survey.entities.Survey;
import org.adaptlab.chpir.android.survey.relations.QuestionRelation;
import org.adaptlab.chpir.android.survey.repositories.ResponseRepository;
import org.adaptlab.chpir.android.survey.viewholders.QuestionViewHolder;
import org.adaptlab.chpir.android.survey.viewmodelfactories.DisplayViewModelFactory;
import org.adaptlab.chpir.android.survey.viewmodelfactories.QuestionRelationViewModelFactory;
import org.adaptlab.chpir.android.survey.viewmodelfactories.SurveyViewModelFactory;
import org.adaptlab.chpir.android.survey.viewmodels.DisplayViewModel;
import org.adaptlab.chpir.android.survey.viewmodels.QuestionRelationViewModel;
import org.adaptlab.chpir.android.survey.viewmodels.SurveyViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.adaptlab.chpir.android.survey.utils.ConstantUtils.COMMA;
import static org.adaptlab.chpir.android.survey.utils.ConstantUtils.COMPLETE_SURVEY;
import static org.adaptlab.chpir.android.survey.utils.ConstantUtils.LOOP_MAX;

public class DisplayPagerFragment extends Fragment {
    public final static String EXTRA_INSTRUMENT_ID = "org.adaptlab.chpir.android.survey.EXTRA_INSTRUMENT_ID";
    public final static String EXTRA_DISPLAY_ID = "org.adaptlab.chpir.android.survey.EXTRA_DISPLAY_ID";
    public final static String EXTRA_SURVEY_UUID = "org.adaptlab.chpir.android.survey.EXTRA_SURVEY_UUID";

    private static final String TAG = DisplayPagerFragment.class.getName();

    private SurveyViewModel mSurveyViewModel;
    private DisplayViewModel mDisplayViewModel;
    private Long mInstrumentId;
    private Long mDisplayId;
    private String mSurveyUUID;

    private QuestionViewHolder.OnResponseSelectedListener mListener;
    private DisplayAdapter mDisplayAdapter;
    private List<List<QuestionRelation>> mQuestionRelationGroups;
    private List<QuestionRelationAdapter> mQuestionRelationAdapters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInstrumentId = getArguments().getLong(EXTRA_INSTRUMENT_ID);
            mDisplayId = getArguments().getLong(EXTRA_DISPLAY_ID);
            mSurveyUUID = getArguments().getString(EXTRA_SURVEY_UUID);
        }
        setOnResponseSelectedListener();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_DISPLAY_ID, mDisplayId);
        outState.putLong(EXTRA_INSTRUMENT_ID, mInstrumentId);
        outState.putString(EXTRA_SURVEY_UUID, mSurveyUUID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mInstrumentId = savedInstanceState.getLong(EXTRA_INSTRUMENT_ID);
            mDisplayId = savedInstanceState.getLong(EXTRA_DISPLAY_ID);
            mSurveyUUID = savedInstanceState.getString(EXTRA_SURVEY_UUID);
        }
    }

    private void setOnResponseSelectedListener() {
        mListener = new QuestionViewHolder.OnResponseSelectedListener() {
            @Override
            public void onResponseSelected(QuestionRelation qr, Option selectedOption, List<Option> selectedOptions, String enteredValue, String nextQuestion, String text) {
                setNextQuestions(qr, nextQuestion);
                setMultipleSkips(qr, selectedOption, selectedOptions, enteredValue);
                setQuestionLoops(qr, text);
                mSurveyViewModel.updateQuestionsToSkipSet();
            }

            private void setQuestionLoops(QuestionRelation qr, String text) {
                if (qr.loopQuestions != null && !qr.loopQuestions.isEmpty()) {
                    if (qr.question.getQuestionType().equals(Question.INTEGER)) {
                        List<String> questionsToHide = new ArrayList<>();
                        int start = 0;
                        if (!TextUtils.isEmpty(text)) {
                            start = Integer.parseInt(text);
                        }
                        for (int k = start + 1; k <= LOOP_MAX; k++) {
                            for (LoopQuestion lq : qr.loopQuestions) {
                                String id = qr.question.getQuestionIdentifier() + "_" + lq.getLooped() + "_" + k;
                                questionsToHide.add(id);
                            }
                        }
                        mSurveyViewModel.updateQuestionsToSkipMap(qr.question.getQuestionIdentifier() + "/intLoop", questionsToHide);
                    } else if (qr.question.isMultipleResponseLoop()) {
                        List<String> responses;
                        if (qr.question.isListResponse()) {
                            responses = Arrays.asList(text.split(COMMA, -1)); // Keep empty values
                        } else {
                            responses = Arrays.asList(text.split(COMMA)); // Ignore empty values
                        }
                        List<String> questionsToHide = new ArrayList<>();
                        int optionsSize = qr.optionSets.get(0).optionSetOptions.size() - 1;
                        if (qr.question.isOtherQuestionType()) {
                            optionsSize += 1;
                        }
                        for (int k = 0; k <= optionsSize; k++) {
                            for (LoopQuestion lq : qr.loopQuestions) {
                                if (!TextUtils.isEmpty(lq.getLooped())) {
                                    String id = qr.question.getQuestionIdentifier() + "_" + lq.getLooped() + "_" + k;
                                    if (qr.question.isMultipleResponse()) {
                                        if (!responses.contains(String.valueOf(k))) {
                                            questionsToHide.add(id);
                                        }
                                    } else if (qr.question.isListResponse()) {
                                        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(responses.get(k))) {
                                            questionsToHide.add(id);
                                        }
                                    }
                                }
                            }
                        }
                        mSurveyViewModel.updateQuestionsToSkipMap(qr.question.getQuestionIdentifier() + "/multiLoop", questionsToHide);
                    }
                }
            }

            private void setMultipleSkips(QuestionRelation qr, Option selectedOption, List<Option> selectedOptions, String enteredValue) {
                if (qr.multipleSkips != null && !qr.multipleSkips.isEmpty()) {
                    List<String> skipList = new ArrayList<>();
                    if (selectedOption != null && selectedOption.getIdentifier() != null) {
                        for (MultipleSkip multipleSkip : qr.multipleSkips) {
                            if (multipleSkip.getOptionIdentifier() != null &&
                                    multipleSkip.getOptionIdentifier().equals(selectedOption.getIdentifier())) {
                                skipList.add(multipleSkip.getSkipQuestionIdentifier());
                            }
                        }
                    }
                    if (enteredValue != null) {
                        for (MultipleSkip multipleSkip : qr.multipleSkips) {
                            if (multipleSkip.getValue().equals(enteredValue)) {
                                skipList.add(multipleSkip.getSkipQuestionIdentifier());
                            }
                        }
                    }
                    mSurveyViewModel.updateQuestionsToSkipMap(qr.question.getQuestionIdentifier() + "/multi", skipList);
                    if (!selectedOptions.isEmpty()) {
                        HashSet<String> skipSet = new HashSet<>();
                        for (Option option : selectedOptions) {
                            for (MultipleSkip multipleSkip : qr.multipleSkips) {
                                if (multipleSkip.getOptionIdentifier().equals(option.getIdentifier())) {
                                    skipSet.add(multipleSkip.getSkipQuestionIdentifier());
                                }
                            }
                        }
                        mSurveyViewModel.updateQuestionsToSkipMap(qr.question.getQuestionIdentifier() + "/multi", new ArrayList<>(skipSet));
                    }
                }
            }

            private void setNextQuestions(QuestionRelation qr, String nextQuestion) {
                if (qr.nextQuestions != null && !qr.nextQuestions.isEmpty()) {
                    List<String> skipList = new ArrayList<>();
                    if (nextQuestion != null) {
                        if (nextQuestion.equals(COMPLETE_SURVEY)) {
                            List<String> questions = new ArrayList<>();
                            for (Question q : mSurveyViewModel.getQuestions()) {
                                questions.add(q.getQuestionIdentifier());
                            }
                            skipList = new ArrayList<>(questions.subList(questions.indexOf(qr.question.getQuestionIdentifier()) + 1, questions.size()));
                        } else {
                            boolean toBeSkipped = false;
                            for (Question curQuestion : mSurveyViewModel.getQuestions()) {
                                if (curQuestion.getQuestionIdentifier().equals(nextQuestion)) {
                                    break;
                                }
                                if (toBeSkipped) {
                                    skipList.add(curQuestion.getQuestionIdentifier());
                                }
                                if (curQuestion.getQuestionIdentifier().equals(qr.question.getQuestionIdentifier()))
                                    toBeSkipped = true;
                            }
                        }
                    }
                    mSurveyViewModel.updateQuestionsToSkipMap(qr.question.getQuestionIdentifier() + "/skipTo", skipList);
                }
            }
        };
    }

    private void hideQuestions() {
        if (mQuestionRelationGroups == null) return;
        HashSet<String> hideSet = new HashSet<>();
        List<String> displayQuestionIds = new ArrayList<>();
        for (List<QuestionRelation> relationList : mQuestionRelationGroups) {
            for (QuestionRelation questionRelation : relationList) {
                displayQuestionIds.add(questionRelation.question.getQuestionIdentifier());
            }
        }
        for (String questionToSkip : mSurveyViewModel.getQuestionsToSkipSet()) {
            if (displayQuestionIds.contains(questionToSkip)) {
                hideSet.add(questionToSkip);
            }
        }
        List<List<QuestionRelation>> visibleRelations = new ArrayList<>();
        for (List<QuestionRelation> relationList : mQuestionRelationGroups) {
            List<QuestionRelation> questionRelations = new ArrayList<>();
            for (QuestionRelation questionRelation : relationList) {
                if (!hideSet.contains(questionRelation.question.getQuestionIdentifier())) {
                    questionRelations.add(questionRelation);
                }
            }
            visibleRelations.add(questionRelations);
        }

        boolean responsesPresent = true;
        outerLoop:
        for (List<QuestionRelation> questionRelationList : visibleRelations) {
            for (QuestionRelation questionRelation : questionRelationList) {
                if (mDisplayViewModel.getResponse(questionRelation.question.getQuestionIdentifier()) == null) {
                    responsesPresent = false;
                    break outerLoop;
                }
            }
        }
        if (responsesPresent) {
            mDisplayAdapter.submitList(visibleRelations);
        }
    }

    private void setQuestions() {
        if (mDisplayId == null || mInstrumentId == null) return;
        QuestionRelationViewModelFactory factory = new QuestionRelationViewModelFactory(getActivity().getApplication(), mInstrumentId, mDisplayId);
        QuestionRelationViewModel questionRelationViewModel = ViewModelProviders.of(this, factory).get(QuestionRelationViewModel.class);
        questionRelationViewModel.getQuestionRelations().observe(getViewLifecycleOwner(), new Observer<List<QuestionRelation>>() {
            @Override
            public void onChanged(@Nullable List<QuestionRelation> questionRelations) {
                for (QuestionRelation questionRelation : questionRelations) {
                    for (Response response : questionRelation.responses) {
                        if (response.getSurveyUUID().equals(mSurveyUUID)) {
                            if (mDisplayViewModel.getResponse(response.getQuestionIdentifier()) == null) {
                                mDisplayViewModel.setResponse(response.getQuestionIdentifier(), response);
                            }
                            break;
                        }
                    }
                    hideLoopedQuestions(questionRelation);
                }

                Collections.sort(questionRelations, new Comparator<QuestionRelation>() {
                    @Override
                    public int compare(QuestionRelation o1, QuestionRelation o2) {
                        if (o1.question.getNumberInInstrument() < o2.question.getNumberInInstrument())
                            return -1;
                        if (o1.question.getNumberInInstrument() > o2.question.getNumberInInstrument())
                            return 1;
                        return 0;
                    }
                });

                if (questionRelations.size() > 0) {
                    if (mDisplayViewModel.getResponse(questionRelations.get(0).question.getQuestionIdentifier()) == null) {
                        initializeResponses(questionRelations);
                    } else {
                        groupQuestionRelations(questionRelations);
                    }
                }
            }
        });
    }

    private void initializeResponses(List<QuestionRelation> questionRelations) {
        if (mSurveyUUID == null) return;
        ResponseRepository responseRepository = new ResponseRepository(getActivity().getApplication());
        List<Response> responses = new ArrayList<>();
        for (QuestionRelation questionRelation : questionRelations) {
            Response response = mDisplayViewModel.getResponse(questionRelation.question.getQuestionIdentifier());
            if (response == null) {
                response = new Response();
                response.setSurveyUUID(mSurveyUUID);
                response.setQuestionIdentifier(questionRelation.question.getQuestionIdentifier());
                response.setQuestionRemoteId(questionRelation.question.getRemoteId());
                response.setQuestionVersion(questionRelation.question.getQuestionVersion());
                response.setTimeStarted(new Date());
                if (!TextUtils.isEmpty(questionRelation.question.getDefaultResponse())) {
                    response.setText(questionRelation.question.getDefaultResponse());
                }
                responses.add(response);
            }
        }
        if (responses.size() > 0) {
            responseRepository.insertAll(responses);
        }
    }

    private void groupQuestionRelations(List<QuestionRelation> questionRelations) {
        String tableName = questionRelations.get(0).question.getTableIdentifier();
        mQuestionRelationGroups = new ArrayList<>();
        List<QuestionRelation> group = new ArrayList<>();
        for (QuestionRelation qr : questionRelations) {
            if ((tableName == null && qr.question.getTableIdentifier() == null) ||
                    (tableName != null && tableName.equals(qr.question.getTableIdentifier()))) {
                group.add(qr);
            } else {
                tableName = qr.question.getTableIdentifier();
                mQuestionRelationGroups.add(new ArrayList<>(group));
                group.clear();
                group.add(qr);
            }
            if (questionRelations.indexOf(qr) == questionRelations.size() - 1) {
                mQuestionRelationGroups.add(new ArrayList<>(group));
            }
        }
        setAdapters();
        hideQuestions();
    }

    private void setAdapters() {
        if (mQuestionRelationAdapters == null || mQuestionRelationAdapters.size() != mQuestionRelationGroups.size()) {
            mQuestionRelationAdapters = new ArrayList<>();
            for (List<QuestionRelation> list : mQuestionRelationGroups) {
                if (TextUtils.isEmpty(list.get(0).question.getTableIdentifier())) {
                    mQuestionRelationAdapters.add(new QuestionRelationAdapter(mListener, mSurveyViewModel));
                } else {
                    mQuestionRelationAdapters.add(new QuestionRelationTableAdapter(mListener, mSurveyViewModel));
                }
            }
            mDisplayAdapter.setResponseRelationAdapters(mQuestionRelationAdapters);
        }
    }

    private void hideLoopedQuestions(QuestionRelation questionRelation) {
        if (questionRelation.loopedQuestions.size() > 0) {
            List<String> questionsToHide = new ArrayList<>();
            for (LoopQuestion lq : questionRelation.loopedQuestions) {
                questionsToHide.add(lq.getLooped());
            }
            mSurveyViewModel.updateQuestionsToSkipMap(questionRelation.question.getQuestionIdentifier() + "/looped", questionsToHide);
        }
    }

    private void setSurvey() {
        if (mSurveyUUID == null) return;
        SurveyViewModelFactory factory = new SurveyViewModelFactory(getActivity().getApplication(), mSurveyUUID);
        mSurveyViewModel = ViewModelProviders.of(getActivity(), factory).get(SurveyViewModel.class);
        mSurveyViewModel.getLiveDataSurvey().observe(getViewLifecycleOwner(), new Observer<Survey>() {
            @Override
            public void onChanged(@Nullable Survey survey) {
                if (survey != null && mSurveyViewModel.getSurvey() == null) {
                    mSurveyViewModel.setSurvey(survey);
                    mSurveyViewModel.setSkipData();
                }
            }
        });
    }

    private void setDisplayViewModel() {
        if (mDisplayId == null) return;
        DisplayViewModelFactory factory = new DisplayViewModelFactory(getActivity().getApplication(), mDisplayId);
        mDisplayViewModel = ViewModelProviders.of(this, factory).get(DisplayViewModel.class);
        mDisplayAdapter.setDisplayViewModel(mDisplayViewModel);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_display, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.displayRecyclerView);
        mDisplayAdapter = new DisplayAdapter(getContext());
        recyclerView.setAdapter(mDisplayAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setDisplayViewModel();
        setSurvey();
        setQuestions();
        return view;
    }

}