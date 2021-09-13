package com.example.uhealth.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.uhealth.utils.CachedThreadPool;
import com.example.uhealth.utils.FireBaseInfo;
import com.example.uhealth.R;
import com.example.uhealth.Adapters.rvadapter_initreg_dialog1;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoHistoryPage extends AppCompatActivity {

    private static final String TAG = InfoHistoryPage.class.getSimpleName();
//    collection
    private final String PERSONALINFO = "personalInfo";
    private FireBaseInfo mFireBaseInfo;
    private CachedThreadPool threadPool;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private Handler handler;

    private boolean fromProfilePage;


    private EditText mName, mPhone, mEContact, mDietary, mAllergy;
    private TextView mBirth;
    private Spinner spGender;
    private DatePickerDialog.OnDateSetListener mBirthdayListener;
    private RadioGroup rgAllergy;
    private RadioButton rbAllergy;



    private EditText mDrug, mSmokeinputET, mAlcoholinputET;
    private CheckBox mSmokeCurrent, mAlcoholCurrent;
    private DatePickerDialog.OnDateSetListener mSmokeStartListener, mSmokeEndListener, mAlcoholStartListener, mAlcoholEndListener;
    private TextView mSmokeStart, mSmokeEnd, mAlcoholStart, mAlcoholEnd, mSmokeDes, mAlcoholDes;
    private RadioGroup rgDrug;
    private RadioButton rbSurgery, rbTransfusion, rbSmoke, rbAlcohol, rbDrug;

    private RadioGroup rgHeart, rgCancer, rgHereditary;
    private RadioButton rbHeart, rbCancer, rbHereditary;

    private enum dialogOption {
        SURGERY,
        TRANSFUSION
    };

    private RadioGroup rgSurgery, rgTransfusion, rgSmoke, rgAlcohol;
    private Button btAddSurgery, btAddTransfusion;
    private RecyclerView rvSurgery, rvTransfusion;
    private RecyclerView.LayoutManager layoutManagerdialog1, layoutManagerdialog1tf;
    private RelativeLayout rhiddenSurgery, rhiddenTransfusion;
    private rvadapter_initreg_dialog1 mRVAdapterdialog1, mRVAdapterdialog1tf;

    private RelativeLayout layoutSmoke, layoutAlcohol;

    private Button mAddSurgery, mAddTransfusion;

    private Button msubmit;



//  init information from loading
    private String mBirthHolder, mGenderHolder;
    private String mSmokeStartHolder, mSmokeEndHolder, mAlcoholStartHolder, mAlcoholEndHolder;
    private int mSmokeinputHolder, mAlcoholinputHolder;
    private ArrayList<String> mSurgeryDates, mSurgeryNames, mTransfusionDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infohistory_page);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        mFireBaseInfo = new FireBaseInfo();
        threadPool = CachedThreadPool.getInstance();

        mName = findViewById(R.id.initreg_ET_name);
        mPhone = findViewById(R.id.initreg_ET_phone);
        mEContact = findViewById(R.id.initreg_ET_EContact);
        mDietary = findViewById(R.id.initreg_ET_dietary);
        mAllergy = findViewById(R.id.initreg_ET_allergy);
        mBirth = findViewById(R.id.initreg_TV_showbirth);

        mBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        InfoHistoryPage.this,
                        R.style.MySpinnerDatePickerStyle,
                        mBirthdayListener,
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mBirthdayListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                mBirthHolder = year +"/" + month + "/"+dayOfMonth;
                mBirth.setText(mBirthHolder);
            }
        };
        spGender = findViewById(R.id.initreg_SP_gender);
        ArrayAdapter<CharSequence> sp_gender_adapter = ArrayAdapter.createFromResource(this, R.array.gender_option, android.R.layout.simple_spinner_item);
        sp_gender_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(sp_gender_adapter);
        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String gender = parent.getItemAtPosition(position).toString();
                mGenderHolder = gender;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rgAllergy = findViewById(R.id.initreg_RG_allergy);

        rgHeart = findViewById(R.id.initreg_RG_heart);
        rgCancer = findViewById(R.id.initreg_RG_cancer);
        rgHereditary = findViewById(R.id.initreg_RG_hereditary);

        rhiddenSurgery = findViewById(R.id.initreg_surgeryhidden);
        rhiddenTransfusion = findViewById(R.id.initreg_transfusionhidden);

        layoutSmoke = findViewById(R.id.smoke_subview);
        mSmokeEnd = layoutSmoke.findViewById(R.id.subview1_enddate);
        mSmokeStart = layoutSmoke.findViewById(R.id.subview1_startdate);

        mDrug = findViewById(R.id.initreg_ET_drug);
        rgDrug = findViewById(R.id.initreg_RG_drug);

        layoutAlcohol = findViewById(R.id.alcohol_subview);
        mAlcoholEnd = layoutAlcohol.findViewById(R.id.subview1_enddate);
        mAlcoholStart = layoutAlcohol.findViewById(R.id.subview1_startdate);

        msubmit = findViewById(R.id.initreg_submit);
        msubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFirebaseWrapper();
            }
        });

        mBirthHolder = "";
        mGenderHolder = "";

        mSmokeStartHolder = "";
        mSmokeEndHolder = "";
        mSmokeinputHolder = 0;

        mAlcoholStartHolder = "";
        mAlcoholEndHolder = "";
        mAlcoholinputHolder = 0;

        mSurgeryNames = new ArrayList<>();
        mSurgeryDates = new ArrayList<>();
        mTransfusionDates = new ArrayList<>();

        initViewSurgery();
        initViewTransfusion();
        initViewSmoke();
        initViewAlcohol();

        Intent intent = getIntent();
        fromProfilePage = intent.getBooleanExtra("FromProfilePage", true);
        if (fromProfilePage){
            getSupportActionBar().setTitle("Profile Page");
        }else{
            String other = intent.getStringExtra("NAME");
            getSupportActionBar().setTitle(other+"'s Profile Page");
            msubmit.setVisibility(View.GONE);
        }


        if (savedInstanceState!=null){
            mBirthHolder = savedInstanceState.getString("BIRTHDAY", "");
            mGenderHolder = savedInstanceState.getString("GENDER", "Male");

            mAllergy.setVisibility(savedInstanceState.getInt("ALLERGY", View.GONE));
            mDrug.setVisibility(savedInstanceState.getInt("DRUG", View.GONE));

            rhiddenSurgery.setVisibility(savedInstanceState.getInt("SURGERY", View.GONE));
            rhiddenTransfusion.setVisibility(savedInstanceState.getInt("TRANSFUSION", View.GONE));

            mSurgeryNames = savedInstanceState.getStringArrayList("SURGERYNAMES");
            mSurgeryDates = savedInstanceState.getStringArrayList("SURGERYDATES");
            mTransfusionDates = savedInstanceState.getStringArrayList("TRANSFUSIONDATES");

            layoutSmoke.setVisibility(savedInstanceState.getInt("SMOKE", View.GONE));
            mSmokeEnd.setVisibility(savedInstanceState.getInt("SMOKECURRENT", View.GONE));
            mSmokeStartHolder = savedInstanceState.getString("SMOKESTART", "");
            mSmokeEndHolder = savedInstanceState.getString("SMOKEEND", "");
            mSmokeinputHolder = savedInstanceState.getInt("SMOKEINPUT", 0);

            layoutAlcohol.setVisibility(savedInstanceState.getInt("ALCOHOL", View.GONE));
            mAlcoholEnd.setVisibility(savedInstanceState.getInt("ALCOHOLCURRENT", View.GONE));
            mAlcoholStartHolder = savedInstanceState.getString("ALCOHOLSTART", "");
            mAlcoholEndHolder = savedInstanceState.getString("ALCOHOLEND", "");
            mAlcoholinputHolder = savedInstanceState.getInt("ALCOHOLINPUT", 0);


            if (!mBirthHolder.equals("")){
                mBirth.setText(mBirthHolder);
            }
            if (!mGenderHolder.equals("")){
                int spinnerpos = sp_gender_adapter.getPosition(mGenderHolder);
                spGender.setSelection(spinnerpos);
            }
            if (!mSmokeStartHolder.equals("")){
                mSmokeStart.setText(mSmokeStartHolder);
            }
            if (!mSmokeEndHolder.equals("")){
                mSmokeEnd.setText(mSmokeEndHolder);
            }
            if (!mAlcoholStartHolder.equals("")){
                mAlcoholStart.setText(mAlcoholStartHolder);
            }
            if (!mAlcoholEndHolder.equals("")){
                mAlcoholEnd.setText(mAlcoholEndHolder);
            }
        }else{
            if (fromProfilePage){
                String selfid = mFireBaseInfo.mUser.getUid();
                loadInformation(selfid);
            }else{
                String uid = intent.getStringExtra("UID");
                loadInformation(uid);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (fromProfilePage){
            MenuInflater inflater = getMenuInflater();
//        adds to action bar if present
            inflater.inflate(R.menu.infohistory_toolbar_menu, menu);
            return true;
        }else{
            //        todo inflate this one depending on intent
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        int v_id = item.getItemId();
        if (v_id==R.id.infohistory_next){
            Intent intent = new Intent(this, PastDiagnosis.class);
            intent.putExtra("FromInfoHistory", true);
            startActivity(intent);
            finish();
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    private Intent getParentActivityIntentImpl() {
        Intent i = null;

        // Here you need to do some logic to determine from which Activity you came.
        // example: you could pass a variable through your Intent extras and check that.
        if (fromProfilePage) {
            i = new Intent(this, ProfilePage.class);
            // set any flags or extras that you need.
            // If you are reusing the previous Activity (i.e. bringing it to the top
            // without re-creating a new instance) set these flags:
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            i.putExtra("someExtra", "whateverYouNeed");
        } else {
            i = new Intent(this, ShareFeature.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            i.putExtra("someExtra", "whateverYouNeed");
        }
        return i;
    }

    private void submitFirebaseWrapper() {
        progressDialog.show();
//        section 1
        String uid = mFireBaseInfo.mUser.getUid();
        String fullname = mName.getText().toString();
        String birthdate = mBirthHolder;
        String gender = mGenderHolder;
        String phone = mPhone.getText().toString();
        String econtact = mEContact.getText().toString();
        String dietary = mDietary.getText().toString();
        String allergy = mAllergy.getText().toString();
        boolean select_allergy;
        if (allergy.equals("")){
            select_allergy = false;
        }else{
            select_allergy = true;
        }
//        section 2
        boolean heartdiseasesFH;
        int radioidheart = rgHeart.getCheckedRadioButtonId();
        rbHeart = findViewById(radioidheart);
        if (rbHeart.getText().toString().equals("Yes")){
            heartdiseasesFH = true;
        }else{
            heartdiseasesFH = false;
        }
        boolean cancerdiseasesFH;
        int radioidcancer = rgCancer.getCheckedRadioButtonId();
        rbCancer = findViewById(radioidcancer);
        if (rbCancer.getText().toString().equals("Yes")){
            cancerdiseasesFH = true;
        }else{
            cancerdiseasesFH = false;
        }
        boolean hereditarydiseasesFH;
        int radioidhereditary = rgHereditary.getCheckedRadioButtonId();
        rbHereditary = findViewById(radioidhereditary);
        if (rbHereditary.getText().toString().equals("Yes")){
            hereditarydiseasesFH = true;
        }else{
            hereditarydiseasesFH = false;
        }
//        section 3
        ArrayList<String> pastsurgery = mSurgeryNames;
        ArrayList<String> pastsurgerydates = mSurgeryDates;
        boolean ispastsurgery;
        if (pastsurgery.isEmpty()){
            ispastsurgery = false;
        }else{
            ispastsurgery=true;
        }
        ArrayList<String> pasttransfusion = mTransfusionDates;
        boolean ispasttransfusion;
        if (pasttransfusion.isEmpty()){
            ispasttransfusion = false;
        }else{
            ispasttransfusion=true;
        }
        String smokestart = mSmokeStartHolder;
        String smokeend = mSmokeEndHolder;
        String smokeinput = mSmokeinputET.getText().toString();
        if (smokeinput.equals("")){
            mSmokeinputHolder = 0;
        }else{
            mSmokeinputHolder = Integer.parseInt(smokeinput);
        }
        int smokeper = mSmokeinputHolder;
        boolean issmoke;
        if (smokestart.equals("")){
            smokeend = "";
            smokeper = 0;
            issmoke = false;
        }else{
            issmoke = true;
        }

        String alcolholstart = mAlcoholStartHolder;
        String alcolholend = mAlcoholEndHolder;
        String alcoholinput = mAlcoholinputET.getText().toString();
        if (alcoholinput.equals("")){
            mAlcoholinputHolder = 0;
        }else{
            mAlcoholinputHolder = Integer.parseInt(alcoholinput);
        }
        int alcolholper = mAlcoholinputHolder;
        boolean isalcolhol;
        if (alcolholstart.equals("")){
            alcolholend="";
            alcolholper = 0;
            isalcolhol = false;
        }else{
            isalcolhol = true;
        }

        String drug = mDrug.getText().toString();
        boolean isdrug;
        if (drug.equals("")){
            isdrug=false;
        }else{
            isdrug=true;
        }

        Map<String, Object> mp = new HashMap<>();
        mp.put("Fullname", fullname);
        mp.put("Birthdate", birthdate);
        mp.put("Gender", gender);
        mp.put("Phone", phone);
        mp.put("Econtact", econtact);
        mp.put("Dietary", dietary);
        mp.put("Allergy", allergy);
        mp.put("IsAllergy", select_allergy);

        mp.put("IsHeartDisease", heartdiseasesFH);
        mp.put("IsCancer", cancerdiseasesFH);
        mp.put("IsHereditary", hereditarydiseasesFH);

        mp.put("IsPastSurgery", ispastsurgery);
        mp.put("PastSurgery", pastsurgery);
        mp.put("PastSurgeryDate", pastsurgerydates);
        mp.put("IsPastTransfusion", ispasttransfusion);
        mp.put("PastTransfusion", pasttransfusion);
        mp.put("IsSmoke", issmoke);
        mp.put("SmokeStart", smokestart);
        mp.put("SmokeEnd", smokeend);
        mp.put("SmokeNumber", smokeper);
        mp.put("IsAlcohol", isalcolhol);
        mp.put("AlcoholStart", alcolholstart);
        mp.put("AlcoholEnd", alcolholend);
        mp.put("AlcoholNumber", alcolholper);
        mp.put("IsDrug", isdrug);
        mp.put("Drug", drug);

        uploadFirebase(uid, mp);
    }

    private void uploadFirebase(String uid, Map<String, Object> mp) {
        mFireBaseInfo.mFirestore.collection(PERSONALINFO).document(uid).set(mp)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "upload submit success");
                        progressDialog.dismiss();
                        Toast.makeText(InfoHistoryPage.this, "Information Updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "upload submit failed");
                        progressDialog.dismiss();
                        Toast.makeText(InfoHistoryPage.this, "Submit Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadInformation(String uid) {
        progressDialog.show();

        mFireBaseInfo.mFirestore.collection(PERSONALINFO).document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "Load info Success");
                        Map<String, Object> loadmp = documentSnapshot.getData();
                        if (loadmp==null){
                            Log.d(TAG, "No previous information");
                            progressDialog.dismiss();
                            return;
                        }
                        mName.setText(loadmp.get("Fullname").toString());
                        mBirthHolder = loadmp.get("Birthdate").toString();
                        if (!mBirthHolder.equals("")){
                            mBirth.setText(mBirthHolder);
                        }
                        mGenderHolder = loadmp.get("Gender").toString();
                        if (mGenderHolder.equals("Female")){
                            spGender.setSelection(1);
                        }else if (mGenderHolder.equals("Other")){
                            spGender.setSelection(2);
                        }
                        mPhone.setText(loadmp.get("Phone").toString());
                        mEContact.setText(loadmp.get("Econtact").toString());
                        mDietary.setText(loadmp.get("Dietary").toString());
//                        nonnull expected from submit function
                        boolean isAllergy = (boolean)loadmp.get("IsAllergy");
                        if (isAllergy){
                            rbAllergy = findViewById(R.id.initreg_RB_allergy_yes);
                            rbAllergy.performClick();
                            mAllergy.setText(loadmp.get("Allergy").toString());
                        }

//                        nonnull expected from submit function
                        boolean isHeart = (boolean)loadmp.get("IsHeartDisease");
                        if (isHeart){
                            rbHeart = findViewById(R.id.initreg_RB_heart_yes);
                            rbHeart.performClick();
                        }
                        boolean isCancer = (boolean)loadmp.get("IsCancer");
                        if (isCancer){
                            rbCancer = findViewById(R.id.initreg_RB_cancer_yes);
                            rbCancer.performClick();
                        }
                        boolean isHereditary = (boolean)loadmp.get("IsHereditary");
                        if (isHereditary){
                            rbHereditary = findViewById(R.id.initreg_RB_hereditary_yes);
                            rbHereditary.performClick();
                        }

                        boolean isSurgery = (boolean)loadmp.get("IsPastSurgery");
                        if (isSurgery){
                            rbSurgery = findViewById(R.id.initreg_RB_surgery_yes);
                            rbSurgery.performClick();
                            mSurgeryNames = (ArrayList<String>)loadmp.get("PastSurgery");
                            mSurgeryDates = (ArrayList<String>)loadmp.get("PastSurgeryDate");
                            initViewSurgery();
                        }

                        boolean isTransfusion = (boolean)loadmp.get("IsPastTransfusion");
                        if (isTransfusion){
                            rbTransfusion = findViewById(R.id.initreg_RB_transfusion_yes);
                            rbTransfusion.performClick();
                            mTransfusionDates = (ArrayList<String>)loadmp.get("PastTransfusion");
                            initViewTransfusion();
                        }

                        boolean issmoke = (boolean)loadmp.get("IsSmoke");
                        if (issmoke){
                            rbSmoke = findViewById(R.id.initreg_RB_smoke_yes);
                            rbSmoke.performClick();
                            mSmokeStartHolder = loadmp.get("SmokeStart").toString();
                            mSmokeEndHolder = loadmp.get("SmokeEnd").toString();
                            mSmokeinputHolder = ((Long)(loadmp.get("SmokeNumber"))).intValue();
                            if (mSmokeEndHolder.equals("")){
                                mSmokeCurrent.setChecked(true);
                            }else{
                                mSmokeCurrent.setChecked(false);
                                mSmokeEnd.setText(mSmokeEndHolder);
                            }
                            mSmokeStart.setText(mSmokeStartHolder);
                            mSmokeinputET.setText(String.valueOf(mSmokeinputHolder));
                        }

                        boolean isdrug = (boolean)loadmp.get("IsDrug");
                        if (isdrug){
                            mDrug.setText(loadmp.get("Drug").toString());
                        }

                        boolean isalcohol = (boolean)loadmp.get("IsAlcohol");
                        if (isalcohol){
                            rbAlcohol = findViewById(R.id.initreg_RB_alcohol_yes);
                            rbAlcohol.performClick();
                            mAlcoholStartHolder = loadmp.get("AlcoholStart").toString();
                            mAlcoholEndHolder = loadmp.get("AlcoholEnd").toString();
                            mAlcoholinputHolder = ((Long)(loadmp.get("AlcoholNumber"))).intValue();
                            if (mAlcoholEndHolder.equals("")){
                                mAlcoholCurrent.setChecked(true);
                            }else{
                                mAlcoholCurrent.setChecked(false);
                                mAlcoholEnd.setText(mAlcoholEndHolder);
                            }
                            mAlcoholStart.setText(mAlcoholStartHolder);
                            mAlcoholinputET.setText(String.valueOf(mAlcoholinputHolder));

                            progressDialog.dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Load info Failed");
                        progressDialog.dismiss();
                        Toast.makeText(InfoHistoryPage.this, "Load Info from Firebase Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("ALLERGY", mAllergy.getVisibility());
        outState.putInt("DRUG", mDrug.getVisibility());
        outState.putInt("SURGERY", rhiddenSurgery.getVisibility());
        outState.putInt("TRANSFUSION", rhiddenTransfusion.getVisibility());

        outState.putInt("SMOKE", layoutSmoke.getVisibility());
        outState.putInt("SMOKECURRENT", mSmokeEnd.getVisibility());
        outState.putString("SMOKESTART", mSmokeStartHolder);
        outState.putString("SMOKEEND", mSmokeEndHolder);
        outState.putInt("SMOKEINPUT", mSmokeinputHolder);

        outState.putInt("ALCOHOL", layoutAlcohol.getVisibility());
        outState.putInt("ALCOHOLCURRENT", mAlcoholEnd.getVisibility());
        outState.putString("ALCOHOLSTART", mAlcoholStartHolder);
        outState.putString("ALCOHOLEND", mAlcoholEndHolder);
        outState.putInt("ALCOHOLINPUT", mAlcoholinputHolder);

        outState.putStringArrayList("SURGERYNAMES", mSurgeryNames);
        outState.putStringArrayList("SURGERYDATES", mSurgeryDates);
        outState.putStringArrayList("TRANSFUSIONDATES", mTransfusionDates);

        if (!mBirthHolder.equals("")){
            outState.putString("BIRTHDAY", mBirthHolder);
        }
        if (!mGenderHolder.equals("")){
            outState.putString("GENDER", mGenderHolder);
        }

        super.onSaveInstanceState(outState);

    }

    public void selectAllergy(View view) {
        int radioid = rgAllergy.getCheckedRadioButtonId();
        rbAllergy = findViewById(radioid);
        if (rbAllergy.getText().toString().equals("Yes")){
            mAllergy.setVisibility(View.VISIBLE);
        }else{
            mAllergy.setVisibility(View.GONE);
            mAllergy.setText("");
        }
    }

    public void selectDrug(View view) {
        int radioid = rgDrug.getCheckedRadioButtonId();
        rbDrug = findViewById(radioid);
        if (rbDrug.getText().toString().equals("Yes")){
            mDrug.setVisibility(View.VISIBLE);
        }else{
            mDrug.setVisibility(View.GONE);
            mDrug.setText("");
        }
    }

    public void selectSmoke(View view) {
        int smokeid = rgSmoke.getCheckedRadioButtonId();
        rbSmoke = findViewById(smokeid);
        if (rbSmoke.getText().toString().equals("Yes")){
            layoutSmoke.setVisibility(View.VISIBLE);
        }else{
            layoutSmoke.setVisibility(View.GONE);
            mSmokeCurrent.setChecked(true);
            mSmokeinputHolder = 0;
            mSmokeStartHolder = "";
            mSmokeEndHolder = "";

            mSmokeStart.setText("Start");
            mSmokeEnd.setText("End");
            mSmokeinputET.setText("");
        }
    }

    public void selectAlcohol(View view){
        int radioid = rgAlcohol.getCheckedRadioButtonId();
        rbAlcohol = findViewById(radioid);
        if (rbAlcohol.getText().toString().equals("Yes")){
            layoutAlcohol.setVisibility(View.VISIBLE);
        }else{
            layoutAlcohol.setVisibility(View.GONE);
            mAlcoholCurrent.setChecked(true);
            mAlcoholinputHolder = 0;
            mAlcoholStartHolder = "";
            mAlcoholEndHolder = "";

            mAlcoholStart.setText("Start");
            mAlcoholEnd.setText("End");
            mAlcoholinputET.setText("");
        }

    }

    private void initViewAlcohol() {
        rgAlcohol = findViewById(R.id.initreg_RG_alcohol);
        mAlcoholCurrent = layoutAlcohol.findViewById(R.id.subview1_check);
        mAlcoholDes = layoutAlcohol.findViewById(R.id.subview1_des);
        mAlcoholDes.setText("Number of drinks per week");
        mAlcoholinputET = layoutAlcohol.findViewById(R.id.subview1_input);
        mAlcoholCurrent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mAlcoholCurrent.isChecked()){
                    mAlcoholEnd.setVisibility(View.GONE);
                    mAlcoholEnd.setText("End");
                    mAlcoholEndHolder = "";
                }else{
                    mAlcoholEnd.setVisibility(View.VISIBLE);
                }
            }
        });

        mAlcoholStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        InfoHistoryPage.this,
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mAlcoholStartListener,
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mAlcoholStartListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                mAlcoholStartHolder = year +"/" + month + "/"+dayOfMonth;
                mAlcoholStart.setText(mAlcoholStartHolder);
            }
        };

        mAlcoholEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        InfoHistoryPage.this,
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mAlcoholEndListener,
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mAlcoholEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                mAlcoholEndHolder = year +"/" + month + "/"+dayOfMonth;
                mAlcoholEnd.setText(mAlcoholEndHolder);
            }
        };
    }

    private void initViewSmoke() {
        rgSmoke = findViewById(R.id.initreg_RG_smoke);
        mSmokeCurrent = layoutSmoke.findViewById(R.id.subview1_check);
        mSmokeDes = layoutSmoke.findViewById(R.id.subview1_des);
        mSmokeDes.setText("Number of cigarretes per day");
        mSmokeinputET = layoutSmoke.findViewById(R.id.subview1_input);
        mSmokeCurrent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mSmokeCurrent.isChecked()){
                    mSmokeEnd.setVisibility(View.GONE);
                    mSmokeEnd.setText("End");
                    mSmokeEndHolder = "";
                }else{
                    mSmokeEnd.setVisibility(View.VISIBLE);
                }
            }
        });

        mSmokeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        InfoHistoryPage.this,
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mSmokeStartListener,
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mSmokeStartListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                mSmokeStartHolder = year +"/" + month + "/"+dayOfMonth;
                mSmokeStart.setText(mSmokeStartHolder);
            }
        };

        mSmokeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        InfoHistoryPage.this,
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        mSmokeEndListener,
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mSmokeEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                mSmokeEndHolder = year +"/" + month + "/"+dayOfMonth;
                mSmokeEnd.setText(mSmokeEndHolder);
            }
        };
    }

    private void initViewTransfusion() {
        rgTransfusion = findViewById(R.id.initreg_RG_transfusion);
        rvTransfusion = findViewById(R.id.initreg_RV_transfusion);
        btAddTransfusion = findViewById(R.id.initreg_BT_addtransfusion);
        layoutManagerdialog1tf = new GridLayoutManager(this, 1);

        mRVAdapterdialog1tf = new rvadapter_initreg_dialog1(new removeRV() {
            @Override
            public void rfSurgery(int position) {
                mSurgeryNames.remove(position);
                mSurgeryDates.remove(position);
            }

            @Override
            public void rfTransfusion(int position) {
                mTransfusionDates.remove(position);
            }
        }, mTransfusionDates);

        rvTransfusion.setLayoutManager(layoutManagerdialog1tf);
        rvTransfusion.setAdapter(mRVAdapterdialog1tf);
        rvTransfusion.setHasFixedSize(true);

        btAddTransfusion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builddialog(dialogOption.TRANSFUSION);
            }
        });
    }

    private void initViewSurgery() {
        rgSurgery = findViewById(R.id.initreg_RG_surgery);
        rvSurgery = findViewById(R.id.initreg_RV_surgery);
        btAddSurgery = findViewById(R.id.initreg_BT_addsurgery);
        layoutManagerdialog1 = new GridLayoutManager(this, 1);

        mRVAdapterdialog1 = new rvadapter_initreg_dialog1(new removeRV() {
            @Override
            public void rfSurgery(int position) {
                mSurgeryNames.remove(position);
                mSurgeryDates.remove(position);
            }

            @Override
            public void rfTransfusion(int position) {
                mTransfusionDates.remove(position);
            }
        }, mSurgeryNames, mSurgeryDates);
        rvSurgery.setLayoutManager(layoutManagerdialog1);
        rvSurgery.setAdapter(mRVAdapterdialog1);
        rvSurgery.setHasFixedSize(true);

        btAddSurgery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builddialog(dialogOption.SURGERY);
            }
        });
    }

    private void builddialog(dialogOption val) {
        dialog = new Dialog(this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ImageButton mDateAdd;
        Button mCancel, mAdd;
        EditText mDialogName;
        RelativeLayout nameSection;
        TextView mDate;
        dialog.setContentView(R.layout.dialog_initreg_type1);
        nameSection = dialog.findViewById(R.id.dialog1_nameSection);
        mDialogName = dialog.findViewById(R.id.initreg_dialog1_name);
        mDate = dialog.findViewById(R.id.initreg_dialog1_date);
        mDateAdd = dialog.findViewById(R.id.dialog1_dateBT);
        mDateAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        InfoHistoryPage.this,
                        R.style.MySpinnerDatePickerStyle,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month = month+1;
                                String temp_date = year +"/" + month + "/"+dayOfMonth;
                                mDate.setText(temp_date);
                            }
                        },
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mCancel = dialog.findViewById(R.id.initreg_dialog1_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mAdd = dialog.findViewById(R.id.initreg_dialog1_add);
        switch (val){
            case SURGERY:
                mAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = mDialogName.getText().toString();
                        String date = mDate.getText().toString();
                        if (name.equals("")){
                            mDialogName.setError("Cant be empty");
                            return;
                        }
                        if (date.equals("")){
                            mDate.setError("Cant be empty");
                            return;
                        }
                        mSurgeryNames.add(name);
                        mSurgeryDates.add(date);
//                        mRVAdapterdialog1.notifyItemInserted(mSurgeryNames.size());
                        mRVAdapterdialog1.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                break;
            case TRANSFUSION:
                nameSection.setVisibility(View.GONE);
                mAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String date = mDate.getText().toString();
                        if (date.equals("")){
                            mDate.setError("Cant be empty");
                        }
                        mTransfusionDates.add(date);
//                        mRVAdapterdialog1tf.notifyItemInserted(mTransfusionDates.size());
                        mRVAdapterdialog1tf.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                break;
            default:
                Log.e(TAG, "Build Dialog Option Incorrect");
        }
        dialog.show();
    }

    public void selectTransfusion(View view) {
        int tfid = rgTransfusion.getCheckedRadioButtonId();
        rbTransfusion = findViewById(tfid);
        if (rbTransfusion.getText().toString().equals("Yes")){
            rhiddenTransfusion.setVisibility(View.VISIBLE);
        }else{
            mTransfusionDates.clear();
            mRVAdapterdialog1tf.notifyDataSetChanged();
            rhiddenTransfusion.setVisibility(View.GONE);
        }
    }

    public void selectSurgery(View view) {
        int surgeryid = rgSurgery.getCheckedRadioButtonId();
        rbSurgery = findViewById(surgeryid);
        if (rbSurgery.getText().toString().equals("Yes")){
            rhiddenSurgery.setVisibility(View.VISIBLE);
        }else{
            mSurgeryNames.clear();
            mSurgeryDates.clear();
            mRVAdapterdialog1.notifyDataSetChanged();
            rhiddenSurgery.setVisibility(View.GONE);
        }
    }

    public interface removeRV{
        void rfSurgery(int position);
        void rfTransfusion(int position);
    }
}