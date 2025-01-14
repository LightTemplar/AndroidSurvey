package org.adaptlab.chpir.android.survey.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.card.MaterialCardView;

import org.adaptlab.chpir.android.survey.R;
import org.adaptlab.chpir.android.survey.adapters.OnItemClickListener;
import org.adaptlab.chpir.android.survey.adapters.OptionDiagramAdapter;
import org.adaptlab.chpir.android.survey.entities.Question;
import org.adaptlab.chpir.android.survey.entities.Response;
import org.adaptlab.chpir.android.survey.relations.DiagramRelation;
import org.adaptlab.chpir.android.survey.relations.OptionRelation;
import org.adaptlab.chpir.android.survey.relations.OptionSetOptionRelation;
import org.adaptlab.chpir.android.survey.relations.OptionSetRelation;
import org.adaptlab.chpir.android.survey.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.adaptlab.chpir.android.survey.utils.ConstantUtils.COMMA;

public class SelectMultipleImagesViewHolder extends QuestionViewHolder {
    private ArrayList<Integer> mResponseIndices;
    private ArrayList<MaterialCardView> mCardViews;

    SelectMultipleImagesViewHolder(View itemView, Context context, OnResponseSelectedListener listener) {
        super(itemView, context, listener);
    }

    @Override
    protected void createQuestionComponent(ViewGroup questionComponent) {
        questionComponent.removeAllViews();
        mCardViews = new ArrayList<>();
        mResponseIndices = new ArrayList<>();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        OptionSetRelation optionSetRelation = getQuestionRelation().optionSets.get(0);
        List<OptionRelation> optionRelations = getOptionRelations();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int margin = (int) (displayMetrics.widthPixels * 0.1);

        if (optionSetRelation.optionSet.isAlignImageVertical()) {
            for (final OptionRelation optionRelation : optionRelations) {
                View view = inflater.inflate(R.layout.list_item_collage, null);
                final MaterialCardView cardView = view.findViewById(R.id.materialCardView);
                cardView.setId(optionRelations.indexOf(optionRelation));

                OptionSetOptionRelation relation = getOptionSetOptionRelation(optionRelation);
                GridView gridView = (GridView) inflater.inflate(R.layout.list_item_option_grid_view, null);
                List<DiagramRelation> diagrams = relation.optionCollages.get(0).collages.get(0).diagrams;
                gridView.setNumColumns(diagrams.size());

                OnItemClickListener listener = cardView::performClick;
                gridView.setAdapter(new OptionDiagramAdapter(getContext(), getQuestionRelation(),
                        diagrams, getSurveyViewModel(), listener));

                LinearLayout linearLayout = cardView.findViewById(R.id.gridViewLayout);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(margin / 2, 0, margin / 2, 0);
                linearLayout.addView(gridView, layoutParams);

                cardView.setOnClickListener(v -> {
                    int index = v.getId();
                    cardView.setSelected(!cardView.isSelected());
                    cardView.setChecked(!cardView.isChecked());
                    setResponseIndex(index, cardView.isChecked());
                    setForeGroundSelection();
                });

                mCardViews.add(cardView);
                questionComponent.addView(view);
            }
        } else {
            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.card_images, null);
            view.setWeightSum(optionRelations.size());
            for (final OptionRelation optionRelation : optionRelations) {
                View layout = inflater.inflate(R.layout.list_item_card, null);
                MaterialCardView cardView = layout.findViewById(R.id.material_card_view);
                cardView.setId(optionRelations.indexOf(optionRelation));
                ImageView imageView = cardView.findViewById(R.id.item_image);
                String path = getContext().getFilesDir().getAbsolutePath() + "/" +
                        getQuestionRelation().question.getInstrumentRemoteId() + "/" +
                        optionRelation.option.getIdentifier() + ".png";
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = true;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                imageView.setImageBitmap(bitmap);

                cardView.setOnClickListener(v -> {
                    int index = v.getId();
                    cardView.setSelected(!cardView.isSelected());
                    cardView.setChecked(!cardView.isChecked());
                    setResponseIndex(index, cardView.isChecked());
                });
                mCardViews.add(cardView);
                view.addView(layout);
            }
            questionComponent.addView(view);
        }
        toggleCarryForward();
    }


    void toggleCarryForward() {
        if (getQuestion().isCarryForward() &&
                !(getCarryForwardQuestion().getQuestionType().equals(Question.CHOICE_TASK))) {
            ArrayList<Integer> responseIndices = new ArrayList<>();
            Response cfr = getCarryForwardResponse();
            if (cfr == null) return;

            String[] listOfIndices = cfr.getText().split(COMMA);
            for (String index : listOfIndices) {
                if (!index.equals("")) {
                    responseIndices.add(Integer.parseInt(index));
                }
            }
            for (int k = 0; k < mCardViews.size(); k++) {
                if (responseIndices.contains(k)) {
                    MaterialCardView cardView = mCardViews.get(k);
                    cardView.setEnabled(false);
                    cardView.setCheckable(false);
                    cardView.setOnClickListener(null);
                    cardView.setCardForegroundColor(getContext().getColorStateList(R.color.fourth));
                }
            }
        }
    }

    private void setResponseIndex(int index, boolean status) {
        if (mResponseIndices.contains(index)) {
            mResponseIndices.remove((Integer) index);
        } else {
            mResponseIndices.add(index);
        }
        saveResponse();
    }

    private void setForeGroundSelection() {
        MaterialCardView materialCardView = new MaterialCardView(getContext());
        ColorStateList colorStateList = materialCardView.getCardForegroundColor();
        for (MaterialCardView cardView : mCardViews) {
            if (cardView.isChecked()) {
                cardView.setCardForegroundColor(getContext().getColorStateList(R.color.first));
            } else {
                cardView.setCardForegroundColor(colorStateList);
            }
        }
        toggleCarryForward();
    }

    @Override
    protected String serialize() {
        return FormatUtils.arrayListToString(mResponseIndices);
    }

    @Override
    protected void deserialize(String responseText) {
        mResponseIndices = new ArrayList<>();
        if (responseText.trim().isEmpty()) {
            for (MaterialCardView cardView : mCardViews) {
                if (cardView.isChecked()) {
                    cardView.setChecked(false);
                    cardView.setSelected(false);
                }
            }
        } else {
            String[] listOfIndices = responseText.split(COMMA);
            for (String index : listOfIndices) {
                if (!index.isEmpty()) {
                    int indexInteger = Integer.parseInt(index);
                    MaterialCardView cardView = mCardViews.get(indexInteger);
                    cardView.setChecked(true);
                    cardView.setSelected(true);
                    cardView.setCardForegroundColor(getContext().getColorStateList(R.color.first));
                    mResponseIndices.add(indexInteger);
                }
            }
        }
    }

    @Override
    protected void unSetResponse() {
        mResponseIndices = new ArrayList<>();
        MaterialCardView materialCardView = new MaterialCardView(getContext());
        ColorStateList colorStateList = materialCardView.getCardForegroundColor();
        for (MaterialCardView cardView : mCardViews) {
            if (cardView.isChecked()) {
                cardView.setChecked(false);
                cardView.setSelected(false);
                cardView.setCardForegroundColor(colorStateList);
            }
        }
    }

    @Override
    protected void showOtherText(int position) {
    }

}
