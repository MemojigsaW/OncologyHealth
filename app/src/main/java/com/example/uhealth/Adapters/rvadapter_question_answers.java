package com.example.uhealth.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.utils.CachedThreadPool;
import com.example.uhealth.Fragments.Frag_question;
import com.example.uhealth.R;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class rvadapter_question_answers extends RecyclerView.Adapter<rvadapter_question_answers.answers> {
    private LinkedHashMap<String, Map<String, String>> lmap;
    private int pos;
    private String[] keyarray;
    private Frag_question.questionFragmentCallback fragmentCallback;
    private CachedThreadPool threadPool;
    private int lastpos;

//    Constructor
    public rvadapter_question_answers(LinkedHashMap<String,
            Map<String, String>> input, int position, Frag_question.questionFragmentCallback callback
                                      ) {
        lmap = input;
        pos = position;
        Set<String> keyset = input.keySet();
        keyarray = keyset.toArray(new String[keyset.size()]);
        fragmentCallback = callback;
        threadPool = CachedThreadPool.getInstance();

        lastpos = 0;
    }

    @NonNull
    @Override
    public answers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_question_answers, parent, false);
        answers ans = new answers(view);

        return ans;
    }

    @Override
    public void onBindViewHolder(@NonNull answers holder, int position) {
        String key = keyarray[position];
        String text = lmap.get(key).get("text");
        int weight = Integer.parseInt(lmap.get(key).get("weight"));
        holder.mBT.setText(text);

        if (weight == fragmentCallback.getValue(pos)){
            holder.mCard.setBackgroundResource(R.color.orangeLight);
            lastpos = position;
        }else{
            holder.mCard.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return lmap.size();
    }

    public class answers extends RecyclerView.ViewHolder implements View.OnClickListener{
        Button mBT;
        CardView mCard;

        public answers(@NonNull View itemView) {
            super(itemView);
            mBT = itemView.findViewById(R.id.rv_question_answer);
            mCard = itemView.findViewById(R.id.anscard);
            mBT.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == mBT.getId()){
                int ansposition = getAdapterPosition();
                String key = keyarray[ansposition];
                int weightz = Integer.parseInt(lmap.get(key).get("weight"));
                fragmentCallback.setValue(weightz, pos);
                notifyItemChanged(lastpos);
                notifyItemChanged(ansposition);
            }
        }
    }
}
