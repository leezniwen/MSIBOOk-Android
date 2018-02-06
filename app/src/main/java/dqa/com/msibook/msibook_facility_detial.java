package dqa.com.msibook;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class msibook_facility_detial extends AppCompatActivity {

    private ProgressDialog progressBar;
    private Context mContext;


    private TextView txt_AssetSN;
    private TextView txt_F_Type;
    private TextView txt_F_Location;
    private TextView txt_F_Dept;
    private TextView txt_F_Owner;
    private TextView txt_F_Cost;
    private TextView txt_F_Buy_Date;
    private TextView txt_F_Storage_Date;
    private TextView txt_F_Use_Year;
    private TextView txt_F_Facility_en;
    private TextView txt_F_Facility_cn;
    private TextView txt_F_Facility;
    private TextView txt_F_Model;
    private TextView txt_F_Factory;
    private TextView txt_F_Spec;
    private TextView txt_F_Standard;


    private String F_SeqNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msibook_facility_detial);

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setMessage("資料載入中");
        mContext = msibook_facility_detial.this;

        F_SeqNo = getIntent().getStringExtra("F_SeqNo");

        txt_AssetSN = (TextView) findViewById(R.id.txt_AssetSN);
        txt_F_Type = (TextView) findViewById(R.id.txt_F_Type);
        txt_F_Location = (TextView) findViewById(R.id.txt_F_Location);
        txt_F_Dept = (TextView) findViewById(R.id.txt_F_Dept);
        txt_F_Owner = (TextView) findViewById(R.id.txt_F_Owner);
        txt_F_Cost = (TextView) findViewById(R.id.txt_F_Cost);
        txt_F_Buy_Date = (TextView) findViewById(R.id.txt_F_Buy_Date);
        txt_F_Storage_Date = (TextView) findViewById(R.id.txt_F_Storage_Date);
        txt_F_Use_Year = (TextView) findViewById(R.id.txt_F_Use_Year);
        txt_F_Facility_en = (TextView) findViewById(R.id.txt_F_Facility_en);
        txt_F_Facility_cn = (TextView) findViewById(R.id.txt_F_Facility_cn);
        txt_F_Facility = (TextView) findViewById(R.id.txt_F_Facility);
        txt_F_Model = (TextView) findViewById(R.id.txt_F_Model);
        txt_F_Factory = (TextView) findViewById(R.id.txt_F_Factory);
        txt_F_Spec = (TextView) findViewById(R.id.txt_F_Spec);
        txt_F_Standard = (TextView) findViewById(R.id.txt_F_Standard);

        Find_Fac_Detail(F_SeqNo);
    }

    private void Find_Fac_Detail(String F_SeqNo) {

        //顯示  讀取等待時間Bar
        progressBar.show();

        RequestQueue mQueue = Volley.newRequestQueue(this);

        String Path = GetServiceData.ServicePath + "/Find_Fac_Detail?F_SeqNo=" + F_SeqNo;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    JSONArray UserArray = new JSONArray(result.getString("Key"));

                    if (UserArray.length() > 0) {
                        JSONObject IssueData = UserArray.getJSONObject(0);

                        String F_SeqNo = String.valueOf(IssueData.getInt("F_SeqNo"));
                        String F_AssetNo = String.valueOf(IssueData.getString("F_AssetNo"));
                        String F_Type = String.valueOf(IssueData.getString("F_Type"));
                        String F_Location = String.valueOf(IssueData.getString("F_Location"));
                        String F_Dept = String.valueOf(IssueData.getString("F_Dept"));
                        String F_LocationID = String.valueOf(IssueData.getString("F_LocationID"));
                        String F_Facility = String.valueOf(IssueData.getString("F_Facility"));
                        String F_Facility_cn = String.valueOf(IssueData.getString("F_Facility_cn"));
                        String F_Facility_en = String.valueOf(IssueData.getString("F_Facility_en"));
                        String F_Model = String.valueOf(IssueData.getString("F_Model"));
                        String F_Factory = String.valueOf(IssueData.getString("F_Factory"));
                        String F_Keeper = String.valueOf(IssueData.getString("F_Keeper"));
                        String TEL = String.valueOf(IssueData.getString("TEL"));
                        String F_Keyin = String.valueOf(IssueData.getString("F_Keyin"));
                        String F_Owner = String.valueOf(IssueData.getString("F_Owner"));
                        String F_Purpose = String.valueOf(IssueData.getString("F_Purpose"));
                        String F_Introduction = String.valueOf(IssueData.getString("F_Introduction"));
                        String F_Spec = String.valueOf(IssueData.getString("F_Spec"));
                        String F_Remark = String.valueOf(IssueData.getString("F_Remark"));
                        String F_Cost = String.valueOf(IssueData.getInt("F_Cost"));
                        String F_Storage_Date = String.valueOf(IssueData.getString("F_Storage_Date"));
                        String F_Buy_Date = String.valueOf(IssueData.getString("F_Buy_Date"));
                        String F_Use_Year = String.valueOf(IssueData.getInt("F_Use_Year"));
                        String F_Standard = String.valueOf(IssueData.getString("F_Standard"));
                        String F_Status = String.valueOf(IssueData.getString("F_Status"));
                        String F_Is_Restrict = String.valueOf(IssueData.getString("F_Is_Restrict"));


                        txt_AssetSN.setText(F_AssetNo);
                        txt_F_Type.setText(F_Type);
                        txt_F_Location.setText(F_Location);
                        txt_F_Dept.setText(F_Dept);
                        txt_F_Owner.setText(F_Owner);
                        txt_F_Cost.setText(F_Cost);
                        txt_F_Buy_Date.setText(F_Buy_Date);
                        txt_F_Storage_Date.setText(F_Storage_Date);
                        txt_F_Use_Year.setText(F_Use_Year);
                        txt_F_Facility_en.setText(F_Facility_en);
                        txt_F_Facility_cn.setText(F_Facility_cn);
                        txt_F_Facility.setText(F_Facility);
                        txt_F_Model.setText(F_Model);
                        txt_F_Factory.setText(F_Factory);
                        txt_F_Spec.setText(F_Spec);
                        txt_F_Standard.setText(F_Standard);

                    }

                    progressBar.dismiss();
                } catch (JSONException ex) {

                    Log.w("Json", ex.toString());

                }

            }
        });


    }
}
