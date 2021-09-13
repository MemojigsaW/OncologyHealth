package com.example.uhealth.DataModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Diag_obj {
    private List<String> all_Diseases;
    private Map<String, String> diseases_others;


    public Diag_obj() {
        all_Diseases = new ArrayList<>();
        diseases_others = new HashMap<>();
        String[] loadlist = {
                "Neurological",
                "Sensory",
                "Endocrine",
                "Cardiovascular",
                "Diseases",
                "Gastrointestinal",
                "Genitourinary",
                "Vascular",
                "Rheumatoid",
                "Cancers",
                "Miscellaneous"
        };
        for (String item : loadlist){
            diseases_others.put(item, "");
        }
    }

    public String getOthers(String key){
        return diseases_others.get(key);
    }
    public void setOthers(String key, String content){
        diseases_others.put(key, content);
    }
    public boolean containsDisease(String obj){
        return all_Diseases.contains(obj);
    }
    public void addDisease(String obj){
        if (!containsDisease(obj)){
            all_Diseases.add(obj);
        }
    }
    public void rmDisease(String obj){
        if (containsDisease(obj)){
            all_Diseases.remove(obj);
        }
    }
    public List<String> getAll_Diseases(){
        return all_Diseases;
    }
    public void setAll_Diseases(List<String> i_list){
        all_Diseases.addAll(i_list);
    }
    public Map<String, String> getDiseases_others(){
        return diseases_others;
    }
}
