package com.example.uhealth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.Activity.Questionnaire;
import com.example.uhealth.Activity.QuestionnaireResults;
import com.example.uhealth.utils.FireBaseInfo;
import com.example.uhealth.R;
import com.example.uhealth.utils.GraphXAxisValueFormater;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class rvadapter_questionnaire_questions extends RecyclerView.Adapter<rvadapter_questionnaire_questions.questionset> {
    private LinkedHashMap<String, Map<String, String>> listmap;
    private onQSClickListener listener;
    private String[] keyarray;
    private FireBaseInfo mFirebaseInfo;
    private int lastpos = -1;
    private Context mContext;

    public rvadapter_questionnaire_questions(Context context, LinkedHashMap<String, Map<String, String>> input, onQSClickListener clickListener){
        this.listener = clickListener;
        this.listmap = input;
        Set<String> keySet = input.keySet();
        this.keyarray = keySet.toArray(new String[keySet.size()]);
        mFirebaseInfo = new FireBaseInfo();
        mContext = context;
    }


    @NonNull
    @Override
    public questionset onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_questionnaires_questionnaires,
                parent, false);
        questionset qs = new questionset(view, listener);

        return qs;
    }

    @Override
    public void onBindViewHolder(@NonNull questionset holder, int position) {
        String key = this.keyarray[position];
        Map<String, String> v_mp = this.listmap.get(key);
        String text = v_mp.get("name");
        String des = v_mp.get("des");
        holder.mQuestiontext.setText(text);
        holder.mQuestiondes.setText(des);
        if (lastpos == position){
            holder.chartLayout.setVisibility(View.VISIBLE);
            holder.mBT.setVisibility(View.VISIBLE);
        }else{
            holder.chartLayout.setVisibility(View.GONE);
            holder.mBT.setVisibility(View.GONE);
        }

        XAxis x_axis= holder.lineChart.getXAxis();
        holder.lineChart.getDescription().setText("");
        holder.lineChart.setExtraLeftOffset(5);
        holder.lineChart.setExtraRightOffset(5);
        x_axis.setGranularity(1f);
        x_axis.setLabelRotationAngle(335f);
        x_axis.setValueFormatter(new GraphXAxisValueFormater());
    }

    @Override
    public int getItemCount() {
        return listmap.size();
    }

    public class questionset extends RecyclerView.ViewHolder implements View.OnClickListener{
        RelativeLayout mRLclick;
        TextView mQuestiontext, mQuestiondes;
        onQSClickListener mlistener;
        FrameLayout chartLayout;
        LineChart lineChart;
        Button mBT;

        public questionset(@NonNull View itemView, onQSClickListener listener) {
            super(itemView);
            mRLclick = itemView.findViewById(R.id.rv_questionnaire_clickable);
            mQuestiontext = itemView.findViewById(R.id.rv_questionnaires_questiontext);
            mQuestiondes = itemView.findViewById(R.id.rv_questionnaires_questiondescrip);
            mBT = itemView.findViewById(R.id.see_more_BT);
            mlistener = listener;
            lineChart = itemView.findViewById(R.id.linechart);
            chartLayout = itemView.findViewById(R.id.graphlayout);
            lineChart.setTouchEnabled(true);
            lineChart.setPinchZoom(true);
            mRLclick.setOnClickListener(this);
            mBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int posi = getAdapterPosition();
                    String key = rvadapter_questionnaire_questions.this.keyarray[posi];
                    Intent intent = new Intent(mContext, QuestionnaireResults.class);
                    intent.putExtra("HASSCORE", false);
                    intent.putExtra("SCORE", 0);
                    intent.putExtra("QID", key);
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == mRLclick.getId()){
                int posi = getAdapterPosition();
                String key = rvadapter_questionnaire_questions.this.keyarray[posi];
                Map<String, String> v_map = listmap.get(key);
                String text = v_map.get("name");
                String des = v_map.get("des");
                mlistener.onQSClick(key, text, des);
                chartLayout.setVisibility(View.VISIBLE);
                mBT.setVisibility(View.VISIBLE);
                int temp_last = lastpos;
                lastpos = posi;
                if (posi != temp_last){
                    loadgraph(key, lineChart);
                    notifyItemChanged(temp_last);
                }

            }
        }
    }

    private void loadgraph(String key, LineChart lchart) {
        ArrayList<Entry> values = new ArrayList<>();

        Query query = mFirebaseInfo.mFirestore.collection("user_score")
                .whereEqualTo("qs_id", key)
                .whereEqualTo("user", mFirebaseInfo.mUser.getUid())
                .orderBy("timestamp", Query.Direction.ASCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null || value.isEmpty() || value==null){
                    Log.d(Questionnaire.TAG, "Load graph data Failed");
                }else{
                    values.clear();
                    for (DocumentSnapshot documentSnapshot: value.getDocuments()){
                        try{
                            long timestamp = (long)documentSnapshot.getData().get("timestamp");
                            long score = (long)documentSnapshot.getData().get("score");
                            values.add(new Entry(timestamp,score));
                        }catch (Exception e){
                            Log.d(Questionnaire.TAG, "Exception in avaliable data");
                            return;
                        }
                    }

                    LineDataSet lineDataSet = new LineDataSet(values, "Past Results");
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(lineDataSet);

                    LineData oData = new LineData(dataSets);
                    lchart.notifyDataSetChanged();
                    lchart.setData(oData);
                    lchart.invalidate();
                }
            }
        });
    }




    public interface onQSClickListener{
        void onQSClick(String qs_id, String qs_text, String qs_des);
    }

}
