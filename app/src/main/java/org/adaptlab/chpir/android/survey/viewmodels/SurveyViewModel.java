package org.adaptlab.chpir.android.survey.viewmodels;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.LongSparseArray;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.adaptlab.chpir.android.survey.BuildConfig;
import org.adaptlab.chpir.android.survey.R;
import org.adaptlab.chpir.android.survey.SurveyApp;
import org.adaptlab.chpir.android.survey.adapters.NavigationDrawerAdapter;
import org.adaptlab.chpir.android.survey.adapters.QuestionRelationAdapter;
import org.adaptlab.chpir.android.survey.entities.Display;
import org.adaptlab.chpir.android.survey.entities.Question;
import org.adaptlab.chpir.android.survey.entities.Response;
import org.adaptlab.chpir.android.survey.entities.Section;
import org.adaptlab.chpir.android.survey.entities.Survey;
import org.adaptlab.chpir.android.survey.relations.DisplayRelation;
import org.adaptlab.chpir.android.survey.relations.QuestionRelation;
import org.adaptlab.chpir.android.survey.relations.SectionRelation;
import org.adaptlab.chpir.android.survey.repositories.SurveyRepository;
import org.adaptlab.chpir.android.survey.utils.AppUtil;
import org.adaptlab.chpir.android.survey.utils.TranslationUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.adaptlab.chpir.android.survey.utils.ConstantUtils.COMMA;
import static org.adaptlab.chpir.android.survey.utils.FormatUtils.styleTextWithHtmlWhitelist;

public class SurveyViewModel extends AndroidViewModel {
    public static final String GENDER = "Gender";
    public static final String PARTICIPANT = "ParticipantID";
    public final String TAG = this.getClass().getName();
    private final SurveyRepository mSurveyRepository;
    private LiveData<Survey> mLiveDataSurvey;
    private HashSet<String> mQuestionsToSkipSet;
    private HashMap<String, List<String>> mQuestionsToSkipMap;
    private HashMap<String, Question> mQuestionsMap;
    private HashMap<String, Response> mResponses;
    private HashMap<String, Question> mQuestionsWithoutResponses;
    private LongSparseArray<Section> mSections;
    private LinkedHashMap<String, List<String>> mExpandableListData;
    private LongSparseArray<String> mDisplayTitles;
    private List<Question> mQuestions;
    private List<String> mExpandableListTitle;
    private List<Display> mDisplays;
    private List<Integer> mPreviousDisplays;
    private List<String> mLocations;
    private List<SectionRelation> mSectionRelations;
    private HashMap<Long, DisplayViewModel> mDisplayViewModels;
    private HashMap<Long, List<Question>> mVisibleDisplayQuestions;
    private HashMap<Long, List<Question>> mDisplayQuestions;
    private HashMap<Long, List<DisplayRelation>> mSectionDisplays;
    private HashMap<String, QuestionRelationAdapter> mQuestionRelationAdapters;
    private NavigationDrawerAdapter mNavigationDrawerAdapter;
    private Survey mSurvey;
    private int mDisplayPosition;
    private String mDeviceLanguage;
    private String mInstrumentLanguage;
    private String mGender = "";
    private long mParticipantID = -1;
    private boolean firstTime;

    public SurveyViewModel(@NonNull Application application, String uuid) {
        super(application);
        mSurveyRepository = new SurveyRepository(application);
        if (uuid == null) return;
        mLiveDataSurvey = mSurveyRepository.getSurveyDao().findByUUID(uuid);
        mDisplayTitles = new LongSparseArray<>();
        mPreviousDisplays = new ArrayList<>();
        mSectionRelations = new ArrayList<>();
        mDisplayViewModels = new HashMap<>();
        mVisibleDisplayQuestions = new HashMap<>();
        mDisplayQuestions = new HashMap<>();
        mSectionDisplays = new HashMap<>();
        mQuestionRelationAdapters = new HashMap<>();
        firstTime = true;
    }

    public void setNavigationDrawerAdapter(NavigationDrawerAdapter adapter) {
        mNavigationDrawerAdapter = adapter;
    }

    public long getParticipantID() {
        return mParticipantID;
    }

    public void setParticipantID(String participantID) {
        if (participantID.length() != 8) return;
        String[] splitString = participantID.split("-");
        mParticipantID = Long.parseLong(splitString[0] + splitString[1] + ((int) splitString[2].charAt(0) - 65));
        Response gender = mResponses.get(GENDER);
        if (gender != null && !gender.getText().isEmpty()) {
            mGender = gender.getText();
        }
        setParticipantBlocks();
    }

    public QuestionRelationAdapter getQuestionRelationAdapter(String identifier) {
        return mQuestionRelationAdapters.get(identifier);
    }

    public void updateQuestionRelationAdapters(String identifier, QuestionRelationAdapter adapter) {
        mQuestionRelationAdapters.put(identifier, adapter);
    }

    public List<SectionRelation> getSectionRelations() {
        return mSectionRelations;
    }

    public void setSectionRelations(List<SectionRelation> sectionRelations) {
        mSectionRelations = sectionRelations;
    }

    public void updateSectionDisplays(Long id, List<DisplayRelation> displayRelations) {
        mSectionDisplays.put(id, displayRelations);
    }

    public List<DisplayRelation> getSectionDisplayRelations(Long id) {
        return mSectionDisplays.get(id);
    }

    public void setDisplayViewModel(Long displayId, DisplayViewModel displayViewModel) {
        mDisplayViewModels.put(displayId, displayViewModel);
    }

    public void updateVisibleQuestions(Long displayId, List<Question> displayQuestions) {
        mVisibleDisplayQuestions.put(displayId, displayQuestions);
    }

    public void setDisplayQuestions(Long displayId, List<Question> displayQuestions) {
        mDisplayQuestions.put(displayId, displayQuestions);
    }

    public void addDisplayTitle(Long displayId, String translation) {
        mDisplayTitles.put(displayId, translation);
    }

    public String getDisplayTitle(Long displayId) {
        return mDisplayTitles.get(displayId);
    }

    public LiveData<Survey> getLiveDataSurvey() {
        return mLiveDataSurvey;
    }

    public Survey getSurvey() {
        return mSurvey;
    }

    public void setSurvey(Survey survey) {
        mSurvey = survey;
    }

    public HashSet<String> getQuestionsToSkipSet() {
        if (mQuestionsToSkipSet == null) return new HashSet<>();
        return mQuestionsToSkipSet;
    }

    public Display currentDisplay() {
        return mDisplays.get(mDisplayPosition);
    }

    public int getDisplayPosition() {
        return mDisplayPosition;
    }

    public void setDisplayPosition(int position) {
        mDisplayPosition = position;
    }

    public void decrementDisplayPosition() {
        mDisplayPosition -= 1;
    }

    public void incrementDisplayPosition() {
        mDisplayPosition += 1;
        if (mDisplayPosition >= mDisplays.size()) return;
        Display display = mDisplays.get(mDisplayPosition);
        List<Question> questions = mVisibleDisplayQuestions.get(display.getRemoteId());
        if (questions == null || questions.isEmpty()) {
            incrementDisplayPosition();
        }
    }

    public HashMap<String, Question> getQuestionsMap() {
        return mQuestionsMap;
    }

    public List<Question> getQuestions() {
        return mQuestions;
    }

    public void setQuestions(List<Question> questions) {
        mQuestions = questions;
        mQuestionsMap = new HashMap<>();
        for (Question question : questions) {
            mQuestionsMap.put(question.getQuestionIdentifier(), question);
        }
    }

    public List<Display> getDisplays() {
        return mDisplays;
    }

    public void setDisplays(List<Display> displays) {
        this.mDisplays = displays;
    }

    public List<Integer> getPreviousDisplays() {
        return mPreviousDisplays;
    }

    public void setPreviousDisplays(List<Integer> previousDisplays) {
        mPreviousDisplays = previousDisplays;
    }

    public LongSparseArray<Section> getSections() {
        return mSections;
    }

    public void setSections(LongSparseArray<Section> sections) {
        mSections = sections;
    }

    public LinkedHashMap<String, List<String>> getExpandableListData() {
        return mExpandableListData;
    }

    public void setExpandableListData(LinkedHashMap<String, List<String>> listData) {
        mExpandableListData = listData;
    }

    public List<String> getExpandableListTitle() {
        return mExpandableListTitle;
    }

    public void setExpandableListTitle(List<String> listTitle) {
        mExpandableListTitle = listTitle;
    }

    public void setNavigationDrawerData() {
        LongSparseArray<Section> longSparseArray = new LongSparseArray<>();
        LinkedHashMap<String, List<String>> listData = new LinkedHashMap<>();
        List<SectionRelation> sectionRelations = getSectionRelations();
        for (SectionRelation relation : sectionRelations) {
            longSparseArray.put(relation.section.getRemoteId(), relation.section);
            String sectionTitle = TranslationUtil.getText(relation.section, relation.translations, this);
            List<String> displayTitles = new ArrayList<>();
            List<DisplayRelation> displayRelations = getSectionDisplayRelations(relation.section.getRemoteId());
            for (DisplayRelation displayRelation : displayRelations) {
                String displayTitle = TranslationUtil.getText(displayRelation.display, displayRelation.translations, this);
                displayTitles.add(styleTextWithHtmlWhitelist(displayTitle).toString());
                addDisplayTitle(displayRelation.display.getRemoteId(), styleTextWithHtmlWhitelist(displayTitle).toString());
            }
            listData.put(styleTextWithHtmlWhitelist(sectionTitle).toString(), displayTitles);
        }
        setExtraItemLinks(listData);
        setSections(longSparseArray);
        setExpandableListData(listData);
        setExpandableListTitle(new ArrayList<>(listData.keySet()));
    }

    private void setExtraItemLinks(LinkedHashMap<String, List<String>> listData) {
        Context context = SurveyApp.getInstance();
        List<String> notes = Collections.singletonList(context.getString(R.string.survey_notes));
        List<String> review = Collections.singletonList(context.getString(R.string.survey_review));
        listData.put(context.getString(R.string.survey_notes), notes);
        listData.put(context.getString(R.string.survey_review), review);
    }

    private void checkIdGender() {
        Response gender = mResponses.get(GENDER);
        if (gender != null && !gender.getText().isEmpty()) {
            mGender = gender.getText();
        }
        Response id = mResponses.get(PARTICIPANT);
        if (id != null && id.getText().length() == 8) {
            String[] splitString = id.getText().split("-");
            mParticipantID = Long.parseLong(splitString[0] + splitString[1] + ((int) splitString[2].charAt(0) - 65));
        }
        setParticipantBlocks();
        firstTime = false;
    }

    private void recordCrashKeys() {
        if (AppUtil.PRODUCTION) {
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            Context context = SurveyApp.getInstance();
            crashlytics.setCustomKey(context.getString(R.string.gender), mGender);
            crashlytics.setCustomKey(context.getString(R.string.participant), mParticipantID);
        }
    }

    public void setParticipantGender(String response) {
        mGender = response;
        Response id = mResponses.get(PARTICIPANT);
        if (id != null && id.getText().length() == 8) {
            String[] splitString = id.getText().split("-");
            if (splitString.length >= 3) {
                mParticipantID = Long.parseLong(splitString[0] + splitString[1] + ((int) splitString[2].charAt(0) - 65));
            }
        }
        setParticipantBlocks();
    }

    private void setParticipantBlocks() {
        if (!mGender.isEmpty() && mParticipantID != -1) {
            Log.i(TAG, "GENDER = " + mGender + " ID = " + mParticipantID);
            recordCrashKeys();
            Random rBlock = new Random(mParticipantID);
            int block = -1;
            String sectionTitle = "";
            List<String> questionsToSkip = new ArrayList<>();
            if (mGender.equals("0")) { // female
                block = rBlock.nextInt(13);
                if (block == 0) block = 13;
                sectionTitle = "F" + block;
                for (int k = 1; k <= 13; k++) {
                    if (k == block) continue;
                    for (String identifier : mQuestionsMap.keySet()) {
                        if (identifier.contains("-")) {
                            String[] parts = identifier.split("-");
                            if (parts[1].charAt(0) == 'F') {
                                String blockStr = parts[1].substring(1);
                                int blockNum = Integer.parseInt(blockStr);
                                if (blockNum != 0 && block != blockNum) { // do not skip block 0 and assigned block
                                    questionsToSkip.add(identifier);
                                }
                            }
                        }
                    }
                }
            } else if (mGender.equals("1")) { // male
                block = rBlock.nextInt(11);
                if (block == 0) block = 11;
                sectionTitle = "M" + block;
                for (int k = 1; k <= 11; k++) {
                    if (k == block) continue;
                    for (String identifier : mQuestionsMap.keySet()) {
                        if (identifier.contains("-")) {
                            String[] parts = identifier.split("-");
                            if (parts[1].charAt(0) == 'M') {
                                String blockStr = parts[1].substring(1);
                                int blockNum = Integer.parseInt(blockStr);
                                if (blockNum != 0 && block != blockNum) { // do not skip block 0 and assigned block
                                    questionsToSkip.add(identifier);
                                }
                            }
                        }
                    }
                }
            }
            Log.i(TAG, "BLOCK = " + block);
            updateQuestionsToSkipMap("ParticipantID", questionsToSkip);
            setQuestionOrder(mSectionRelations, sectionTitle);
            setNavigationDrawerData();
            if (mNavigationDrawerAdapter != null) {
                mNavigationDrawerAdapter.updateData(getExpandableListTitle(), getExpandableListData());
            }
        }
    }

    private void setQuestionOrder(List<SectionRelation> sectionRelations, String sectionTitle) {
        List<Display> displayList = new ArrayList<>();
        randomizeDisplays(sectionRelations, displayList, sectionTitle);
        displayList = getSortedDisplays(displayList);
        setDisplayOrder(displayList);
        update();
        setDisplays(displayList);
    }

    private void randomizeDisplays(List<SectionRelation> sectionRelations, List<Display> displayList, String sectionTitle) {
        int index = 1;
        for (SectionRelation sectionRelation : sectionRelations) {
            List<DisplayRelation> displayRelations;
            if (sectionRelation.section.getTitle().equals(sectionTitle) && sectionRelation.section.isRandomizeDisplays()) {
                List<List<DisplayRelation>> relations = new ArrayList<>();
                for (int k = 0; k < sectionRelation.displays.size(); k += 2) {
                    if (k + 2 < sectionRelation.displays.size()) {
                        relations.add(sectionRelation.displays.subList(k, k + 2));
                    } else {
                        relations.add(sectionRelation.displays.subList(k, sectionRelation.displays.size()));
                    }
                }
                Collections.shuffle(relations, new Random(mParticipantID));
                displayRelations = relations.stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
                displayRelations.stream().map(displayRelation -> displayRelation.display.getTitle()).forEach(System.out::println);
            } else {
                displayRelations = sectionRelation.displays;
            }
            for (DisplayRelation displayRelation : displayRelations) {
                displayRelation.display.setInstrumentPosition(index);
                index += 1;
                displayList.add(displayRelation.display);
            }
            updateSectionDisplays(sectionRelation.section.getRemoteId(), displayRelations);
        }
    }

    private List<Display> getSortedDisplays(List<Display> displayList) {
        List<Display> displays = new ArrayList<>();
        for (Display display : displayList) {
            if (!display.isDeleted()) {
                displays.add(display);
            }
        }
        displays.sort(this::compareDisplays);
        return displays;
    }

    private int compareDisplays(Display display1, Display display2) {
        if (display1.getInstrumentPosition() == display2.getInstrumentPosition()) {
            return display1.getTitle().compareTo(display2.getTitle());
        } else {
            return display1.getInstrumentPosition() - display2.getInstrumentPosition();
        }
    }

    public void setSkipData() {
        mQuestionsToSkipSet = new HashSet<>();
        mQuestionsToSkipMap = new HashMap<>();
        if (mSurvey.getSkippedQuestions() != null) {
            mQuestionsToSkipSet = new HashSet<>(Arrays.asList(mSurvey.getSkippedQuestions().split(COMMA)));
        }
        if (mSurvey.getSkipMaps() != null) {
            try {
                JSONObject jsonObject = new JSONObject(mSurvey.getSkipMaps());
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String skipString = jsonObject.getString(key);
                    String[] skipArray = skipString.split(COMMA);
                    mQuestionsToSkipMap.put(key, Arrays.asList(skipArray));
                }
                updateQuestionsToSkipSet();
            } catch (JSONException e) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Exception: ", e);
            }
        }
    }

    public void persistSkipMaps() {
        if (mQuestionsToSkipMap == null) return;
        try {
            JSONObject jsonObject = new JSONObject();
            for (HashMap.Entry<String, List<String>> pair : mQuestionsToSkipMap.entrySet()) {
                if (pair.getValue() != null && pair.getValue().size() != 0) {
                    StringBuilder serialized = new StringBuilder();
                    int count = 0;
                    for (String question : pair.getValue()) {
                        serialized.append(question);
                        if (count < pair.getValue().size() - 1)
                            serialized.append(COMMA);
                        count += 1;
                    }
                    jsonObject.put(pair.getKey(), serialized.toString());
                }
            }
            mSurvey.setSkipMaps(jsonObject.toString());
        } catch (JSONException je) {
            if (BuildConfig.DEBUG) Log.e(TAG, "JSON exception", je);
        }
    }

    public void persistSkippedQuestions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mSurvey.setSkippedQuestions(String.join(COMMA, mQuestionsToSkipSet));
        } else {
            StringBuilder serialized = new StringBuilder();
            int count = 0;
            for (String identifier : mQuestionsToSkipSet) {
                serialized.append(identifier);
                if (count < mQuestionsToSkipSet.size() - 1)
                    serialized.append(COMMA);
                count += 1;
            }
            mSurvey.setSkippedQuestions(serialized.toString());
        }
    }

    public void persistPreviousDisplays() {
        mSurvey.setPreviousDisplays(StringUtils.join(mPreviousDisplays, COMMA));
    }

    public void update() {
        mSurveyRepository.update(mSurvey);
    }

    public void updateQuestionsToSkipSet() {
        mQuestionsToSkipSet = new HashSet<>();
        for (HashMap.Entry<String, List<String>> curPair : mQuestionsToSkipMap.entrySet()) {
            mQuestionsToSkipSet.addAll(curPair.getValue());
        }
    }

    public void updateQuestionsToSkipMap(String questionIdentifier, List<String> questionsToSkip) {
        if (questionsToSkip.size() == 0) {
            mQuestionsToSkipMap.remove(questionIdentifier);
        } else {
            mQuestionsToSkipMap.put(questionIdentifier, questionsToSkip);
        }
    }

    public void setSurveyLastDisplayPosition() {
        mSurvey.setLastDisplayPosition(mDisplayPosition);
    }

    public void setSurveyLastUpdatedTime() {
        mSurvey.setLastUpdated(new Date());
    }

    public void setSurveyComplete() {
        mSurvey.setComplete(true);
    }

    public HashMap<String, Response> getResponses() {
        return mResponses;
    }

    public void setResponses(HashMap<String, Response> map) {
        mResponses = map;
        if (firstTime) {
            checkIdGender();
        }
    }

    public void setResponse(String questionIdentifier, Response response) {
        mResponses.put(questionIdentifier, response);
    }

    public String getDeviceLanguage() {
        return mDeviceLanguage;
    }

    public void setDeviceLanguage(String language) {
        mDeviceLanguage = language;
    }

    public String getInstrumentLanguage() {
        return mInstrumentLanguage;
    }

    public void setInstrumentLanguage(String language) {
        mInstrumentLanguage = language;
    }

    public void setSurveyLanguage() {
        mSurvey.setLanguage(mDeviceLanguage);
    }

    public void setQuestionsWithoutResponses() {
        mQuestionsWithoutResponses = new HashMap<>(getQuestionsMap());
        for (String identifier : getQuestionsToSkipSet()) {
            mQuestionsWithoutResponses.remove(identifier);
        }
        for (Map.Entry<String, Response> entry : getResponses().entrySet()) {
            if (!entry.getValue().isEmptyResponse()) {
                mQuestionsWithoutResponses.remove(entry.getKey());
            }
        }
        HashMap<String, Question> map = new HashMap<>(mQuestionsWithoutResponses);
        for (Map.Entry<String, Question> entry : mQuestionsWithoutResponses.entrySet()) {
            if (entry.getValue().isDeleted() || entry.getValue().getQuestionType() == null ||
                    entry.getValue().getQuestionType().equals(Question.INSTRUCTIONS)) {
                map.remove(entry.getKey());
            }
        }
        mQuestionsWithoutResponses = map;
    }

    public HashMap<String, Question> getQuestionsWithoutResponses() {
        return mQuestionsWithoutResponses;
    }

    public List<String> getLocations() {
        return mLocations;
    }

    public void setLocations(List<String> mLocations) {
        this.mLocations = mLocations;
    }

    public String checkForEmptyResponses() {
        if (currentDisplay() == null) return "";
        StringBuilder stringBuilder = new StringBuilder();
        DisplayViewModel displayViewModel = mDisplayViewModels.get(currentDisplay().getRemoteId());
        if (displayViewModel == null) return "";
        List<QuestionRelation> displayQuestions = new ArrayList<>(displayViewModel.getQuestions().values());
        displayQuestions.sort((dq1, dq2) -> dq1.question.getPosition() - dq2.question.getPosition());
        HashMap<String, Response> displayResponses = displayViewModel.getResponses();
        Context context = SurveyApp.getInstance();
        for (QuestionRelation questionRelation : displayQuestions) {
            Question question = questionRelation.question;
            Response response = displayResponses.get(question.getQuestionIdentifier());
            boolean oneChoice = question.getQuestionType().equals(Question.CHOICE_TASK)
                    && response != null && !response.getText().contains(",");
            if ((!question.getQuestionType().equals(Question.INSTRUCTIONS) &&
                    !mQuestionsToSkipSet.contains(question.getQuestionIdentifier()) &&
                    response != null && response.isEmptyResponse()) || (oneChoice &&
                    !mQuestionsToSkipSet.contains(question.getQuestionIdentifier()))) {
                if (stringBuilder.length() > 0) stringBuilder.append(System.lineSeparator());
                stringBuilder.append(context.getResources().getString(R.string.question))
                        .append(" ").append(question.getPosition())
                        .append(") ")
                        .append(question.getQuestionIdentifier());
            }
        }
        return stringBuilder.toString();
    }


    public void hideQuestions(int position) {
        HashSet<String> hideSet = new HashSet<>();
        for (int k = position; k < mDisplays.size(); k++) {
            Display display = mDisplays.get(k);
            List<Question> displayQuestions = mDisplayQuestions.get(display.getRemoteId());
            if (displayQuestions == null) continue;

            List<String> displayQuestionIds = new ArrayList<>();
            for (Question question : displayQuestions) {
                displayQuestionIds.add(question.getQuestionIdentifier());
            }

            for (String questionToSkip : getQuestionsToSkipSet()) {
                if (displayQuestionIds.contains(questionToSkip)) {
                    hideSet.add(questionToSkip);
                }
            }

            List<Question> visibleDisplayQuestions = new ArrayList<>();
            for (Question question : displayQuestions) {
                if (!hideSet.contains(question.getQuestionIdentifier())) {
                    visibleDisplayQuestions.add(question);
                }
            }
            mVisibleDisplayQuestions.put(display.getRemoteId(), visibleDisplayQuestions);
        }
    }

    public void setDisplayOrder(List<Display> displayList) {
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        for (Display display : displayList) {
            stringBuilder.append(display.getTitle());
            if (count < displayList.size()) {
                stringBuilder.append(COMMA);
            }
            count++;
        }
        mSurvey.setDisplayOrder(stringBuilder.toString());
    }
}
