package com.example.uhealth.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GenerateFakeData {
    private FireBaseInfo fireBaseInfo;
    private final String APP_T = "appointment_testing_alan";
    private final String MED_T = "medication_testing_alan";
    private final String[] imgpaths = {"images/App_test_1.jpg",
            "images/App_test_2.jpg",
            "images/App_test_3.jpg",
            "images/App_test_4.jpg",
            "images/App_test_5.jpg",
            "images/App_test_6.jpg",};
//    2015
    private final int START_TIME = 1420095388;
//    2021
    private final int END_TIME = 1616655388;

    public GenerateFakeData(){
        fireBaseInfo = new FireBaseInfo();
    }

    public int genRand(int start, int end){
        if (end<start){
            throw new IllegalArgumentException("end smaller than start");
        }
        Random r = new Random();
        return r.nextInt((end-start)+1)+start;
    }

    public String genNote(){
        int n = genRand(0, 100);
        // length is bounded by 256 Character
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString
                = new String(array, Charset.forName("UTF-8"));

        // Create a StringBuffer to store the result
        StringBuffer r = new StringBuffer();

        // Append first 20 alphanumeric characters
        // from the generated random String into the result
        for (int k = 0; k < randomString.length(); k++) {

            char ch = randomString.charAt(k);

            if (((ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9'))
                    && (n > 0)) {

                r.append(ch);
                n--;
            }
        }

        // return the resultant string
        return r.toString();
    }

    public String[] randassignpic(){
        List<String> strList = Arrays.asList(imgpaths);
        Collections.shuffle(strList);
        String[] shuffle_paths = strList.toArray(new String[imgpaths.length]);

        int size = genRand(0, imgpaths.length);
        String[] out = new String[size];
        for (int i=0; i<size; i++){
            out[i] = shuffle_paths[i];
        }
        return out;
    }



    public void updateAppoints(){
        fireBaseInfo.mFirestore.collection(APP_T).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                fireBaseInfo.mFirestore.collection(APP_T).document(document.getId())
                                        .update(
                                                "date", genRand(START_TIME, END_TIME),
                                                "status", "Complete",
                                                "note", genNote(),
                                                "path", Arrays.asList(randassignpic())
                                        );
                            }
                        }
                    }
                });

    }


    public void updateMeds(){
        fireBaseInfo.mFirestore.collection(MED_T).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                int start_date = genRand(START_TIME, END_TIME);
                                int end_date = genRand(start_date, END_TIME);
                                Object end_d;
                                String status;
                                if (end_date>(END_TIME-100000000/20)){
                                    end_d = null;
                                    status = "ongoing";
                                }else{
                                    end_d = end_date;
                                    status = "complete";
                                }
                                fireBaseInfo.mFirestore.collection(MED_T).document(document.getId())
                                        .update(
                                                "initdate", start_date,
                                                "enddate", end_d,
                                                "status", status
                                        );
                            }
                        }
                    }
                });

    }

    public void copyapps(){
        fireBaseInfo.mFirestore.collection("Appointment").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                fireBaseInfo.mFirestore.collection(APP_T).add(document.getData());
                            }
                        }
                    }
                });
    }
}
