package com.example.uhealth.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.utils.CachedThreadPool;
import com.example.uhealth.Fragments.Frag_question;
import com.example.uhealth.R;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class rvadp_qs_ans_multistr extends RecyclerView.Adapter<rvadp_qs_ans_multistr.multselect> {
    private LinkedHashMap<String, Map<String, String>> lmap;
    private int pos;
    private String[] keyarray;
    private Frag_question.questionFragmentCallback fragmentCallback;
    private CachedThreadPool threadPool;

//    constructor
    public rvadp_qs_ans_multistr(LinkedHashMap<String,
                Map<String, String>> input, int position, Frag_question.questionFragmentCallback callback){
        lmap = input;
        pos = position;
        Set<String> keyset = input.keySet();
        keyarray = keyset.toArray(new String[keyset.size()]);
        fragmentCallback = callback;
        threadPool = CachedThreadPool.getInstance();
    }

    @NonNull
    @Override
    public multselect onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_question_multiselect, parent, false);
        multselect ms = new multselect(view);
        return ms;
    }

    @Override
    public void onBindViewHolder(@NonNull multselect holder, int position) {
        String text = lmap.get(keyarray[position]).get("text");
        holder.tv.setText(text);
        if (fragmentCallback.contains(text)) {
            holder.cb.setChecked(true);
        }else{
            holder.cb.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return lmap.size();
    }

    public class multselect extends RecyclerView.ViewHolder {
        TextView tv;
        CheckBox cb;

        public multselect(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.ms_text);
            cb = itemView.findViewById(R.id.ms_check);
            fragmentCallback.setValue(0, pos);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int posi = getAdapterPosition();
                    String key = keyarray[posi];
                    String text = lmap.get(key).get("text");
//                    is checked is new state
                    if (isChecked){
                        if (!fragmentCallback.contains(text)){
                            fragmentCallback.addString(text);
                        }
                    }else{
                        fragmentCallback.rmString(text);
                    }
                }
            });
        }
    }
}
