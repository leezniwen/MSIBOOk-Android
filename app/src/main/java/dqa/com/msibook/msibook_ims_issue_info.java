package dqa.com.msibook;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class msibook_ims_issue_info extends AppCompatActivity {
        static final int REQUEST_VIDEO_CAPTURE = 1;

        static final int REQUEST_IMAGE_CAPTURE = 2;
        private static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
        private static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
        private static final int REQUEST_EXTERNAL_STORAGE = 1;
        private SwipeRefreshLayout mSwipeRefreshLayout;
        private ListView lsv_main;
        private msibook_ims_issue_worknote_adapter mListAdapter;
        List<msibook_ims_issue_worknote_item> WorkNote_List = new ArrayList<msibook_ims_issue_worknote_item>();
        List<msibook_ims_issue_file_item> IssueFile_List = new ArrayList<msibook_ims_issue_file_item>();
        private RecyclerView mRecyclerView;
        private msibook_ims_issue_file_adapter mAdapter;
        public ArrayList<Image> ImageViewList = new ArrayList<Image>();
        private String IssueID;
        private String ModelID;
        private ProgressDialog pDialog;
        private File ImageFile;

        private File VideoFile;

        private Animator mCurrentAnimator;

        private Menu mMenu;

        private String Status_Display = "";

        private String Author = "";

        private String AuthorNameEN = "";

        private String AuthorNameCN = "";

        private String Owner = "";

        private String OwnerNameEN = "";

        private String OwnerNameCN = "";

        private String Issue_Priotity = "";

        // The system "short" animation time duration, in milliseconds. This
        // duration is ideal for subtle animations or animations that occur
        // very frequently.
        private int mShortAnimationDuration;

        private RequestQueue mQueue;

        private Context mContext;

        private ArrayList<String> mMultiPhotoPath = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            setContentView(R.layout.activity_msibook_ims_issue_info);

            mContext = msibook_ims_issue_info.this;


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            toolbar.setTitle("My Title");
            //getActionBar().hide();

            pDialog = new ProgressDialog(mContext);
            //View v = inflater.inflate(R.layout.fragment_my_issue, container, false);
            //宣告 ListView 元件
            lsv_main = (ListView) findViewById(R.id.listView);


            //lsv_main.setOnItemClickListener(listViewOnItemClickListener);

            Bundle Bundle = getIntent().getExtras();

            IssueID = Bundle.getString("IssueID");

            if (TextUtils.isEmpty(IssueID)) {
                String idOffer = "";
                Intent startingIntent = getIntent();
                if (startingIntent != null) {

                    if (startingIntent.getStringExtra("key") != null) {
                        if (startingIntent.getStringExtra("key").contains("IssueInfo")) {

                            idOffer = startingIntent.getStringExtra("value"); // Retrieve the id


                            showRecordingNotification(idOffer);
                        }
                    }


                }

                IssueID = idOffer;
            }

//        Issue_Get(IssueID);
//
//        Find_Issue_Comment(IssueID);
//
//        Issue_File_List(IssueID);

            GetIssue_Info(IssueID);

            String WorkID = UserData.WorkID;

            if (!TextUtils.isEmpty(WorkID)) {
                Insert_Issue_Read(IssueID, WorkID);
            }


            mRecyclerView = (RecyclerView) findViewById(R.id.Rcy_IssueFile);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(layoutManager);

            ImageView Img_IssueAuthor = (ImageView) findViewById(R.id.Img_IssueAuthor);

            ImageView Img_IssueInfo_Send = (ImageView) findViewById(R.id.Img_IssueInfo_Send);

            Img_IssueInfo_Send.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    EditText txt_Comment = (EditText) findViewById(R.id.txt_Comment);

                    String Comment = txt_Comment.getText().toString();

                    C_Comment_Insert(Comment);
                }
            });

            ImageView Img_IssueInfo_AddPhoto = (ImageView) findViewById(R.id.Img_IssueInfo_AddPhoto);

            Img_IssueInfo_AddPhoto.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    GoCamera();

                }
            });


            TextView txt_IssueInfo_No = (TextView) findViewById(R.id.txt_IssueInfo_No);

            txt_IssueInfo_No.setText("#" + IssueID);


            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);

//                    Issue_Get(IssueID);
//
//                    Find_Issue_Comment(IssueID);
                    }


                    //Issue_File_List(IssueID);
                }
            });

            setupUI(findViewById(android.R.id.content));





        }

        @Override
        public void onPause() {
            super.onPause();

            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.destroyDrawingCache();
                mSwipeRefreshLayout.clearAnimation();
            }
        }

        @Override
        public void onResume() {
            Issue_Get(IssueID);

            Find_Issue_Comment(IssueID);

            Issue_File_List(IssueID);

            super.onResume();
        }

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

                mMultiPhotoPath = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS);

                assert mMultiPhotoPath != null;

                ImageView image = new ImageView(mContext);

                if (mMultiPhotoPath.size() > 0) {

                    final String ImagePath = mMultiPhotoPath.get(0);

                    final Bitmap photo = FileUtil.FilePathGetBitMap(ImagePath);

                    Bitmap minibm = ThumbnailUtils.extractThumbnail(photo, 1024, 800);

                    image.setImageBitmap(minibm);

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(mContext).
                                    setMessage("Use this photo?").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String WorkID = UserData.WorkID;

                                            UpdateIssueFile(WorkID, IssueID, ImagePath, ImagePath.substring(ImagePath.lastIndexOf("/") + 1));

                                            Find_Issue_Comment(IssueID);

                                            dialog.dismiss();
                                        }
                                    }).
                                    setView(image);
                    builder.create().show();
                }


            }

        }

        private void showRecordingNotification(String IssueID) {


        }


        private void Insert_Issue_Read(String F_Master_ID, String F_Keyin) {


            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }


            String Path = GetServiceData.IMS_ServicePath + "/Insert_Issue_Read_Advantage?F_Master_ID=" + F_Master_ID + "&F_Master_Table=" + "C_Issue" + "&F_Read=" + "1" + "&F_Keyin=" + F_Keyin;

            //System.out.println(File);

            GetServiceData.SendRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {

                    //System.out.println("Test");
                }

                @Override
                public void onSendRequestError(String result) {
                    //Log.w("NotificationSuccess",result);
                }
            });
        }

        private void GoCamera() {

            int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);


            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 無權限，向使用者請求
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE
                );

                System.out.println("Storage");
            } else {

                permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);

                if (permission != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE
                    );

                } else {
//                System.out.println("CAMERA");
//
//                Intent intentCamera =
//                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                // 照片檔案名稱
//                File pictureFile = configFileName("P", ".jpg");
//
//                ImageFile = pictureFile;
//
//                Uri uri = Uri.fromFile(pictureFile);
//                // 設定檔案名稱
//                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                // 啟動相機元件
//                startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);


// start multiple photos selector
                    Intent intent = new Intent(msibook_ims_issue_info.this, ImagesSelectorActivity.class);
// max number of images to be selected
                    intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 1);
// min size of image which will be shown; to filter tiny images (mainly icons)
                    intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
// show camera or not
                    intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
// pass current selected images as the initial value
//                intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, mMultiPhotoPath);
// start the selector
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

                }
            }


        }

        private void Upload_Comment_File(String F_Keyin, String F_Master_ID, String File) {

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }

            String Path = GetServiceData.IMS_ServicePath + "/Issue_Comment_File_Insert?F_Keyin=" + F_Keyin + "&F_Master_ID=" + F_Master_ID + "&File=" + File;

            //System.out.println(File);

            GetServiceData.SendRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {

                    //System.out.println("Test");
                }

                @Override
                public void onSendRequestError(String result) {
                    //Log.w("NotificationSuccess",result);
                }
            });
        }

        private File configFileName(String prefix, String extension) {

            String fileName = FileUtil.getUniqueFileName();

            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

            return new File(dir, prefix + fileName + extension);

        }

        private void UpdateIssueFile(String F_Keyin, String F_Master_ID, String FilePath, String FileName) {

//        pDialog.setMessage("Uploading...");
//
//        pDialog.show();

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }

            Upload_Comment_File(F_Keyin, F_Master_ID, FileName);

            File ImageFileUpload = new File(FilePath);

            GetServiceData.uploadImage(GetServiceData.IMS_ServicePath + "/Upload_Issue_File_MultiPart", mQueue, ImageFileUpload, "");

        }

        private void C_Comment_Insert(String Comment) {


            String WorkID = UserData.WorkID;

            if (!TextUtils.isEmpty(Comment)) {

                final ImageView Img_IssueInfo_Send = (ImageView) findViewById(R.id.Img_IssueInfo_Send);

                Img_IssueInfo_Send.setVisibility(View.GONE);

                Map<String, String> map = new HashMap<String, String>();
                map.put("F_Keyin", WorkID);
                map.put("F_Master_Table", "C_Issue");
                map.put("F_Master_ID", IssueID);
                map.put("F_Comment", Comment);

                if (mQueue == null) {
                    mQueue = Volley.newRequestQueue(mContext);
                }


                String Path = GetServiceData.IMS_ServicePath + "/C_Comment_Insert";


                GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                    @Override
                    public void onSendRequestSuccess(String result) {


                        Find_Issue_Comment(IssueID);

                        EditText txt_Comment = (EditText) findViewById(R.id.txt_Comment);

                        txt_Comment.setText("");

                        Img_IssueInfo_Send.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSendRequestError(String result) {


                        Find_Issue_Comment(IssueID);

                        EditText txt_Comment = (EditText) findViewById(R.id.txt_Comment);

                        txt_Comment.setText("");

                        Img_IssueInfo_Send.setVisibility(View.VISIBLE);
                    }

                }, map);

            }


        }


        private AdapterView.OnItemClickListener listViewOnItemClickListener
                = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        };

        public void setupUI(View view) {

            // Set up touch listener for non-text box views to hide keyboard.
            if (!(view instanceof EditText)) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        hideSoftKeyboard(msibook_ims_issue_info.this);
                        return false;
                    }
                });
            }

            //If a layout container, iterate over children and seed recursion.
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View innerView = ((ViewGroup) view).getChildAt(i);
                    setupUI(innerView);
                }
            }
        }

        public static void hideSoftKeyboard(Activity activity) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }

        private void Issue_File_List(String Issue_ID) {

//        pDialog.setMessage("Loading...");
//        pDialog.show();

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }

            String Path = GetServiceData.IMS_ServicePath + "/Issue_File_List?F_SeqNo=" + Issue_ID;

            GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {

//                pDialog.hide();

                    IssueInfoFile_ListMapping(result);
                }
            });

        }

        private void IssueInfoFile_ListMapping(JSONObject result) {
            try {
                IssueFile_List.clear();

                JSONArray IssueInfoFileArray = new JSONArray(result.getString("Key"));

                if (IssueInfoFileArray.length() > 0) {


                    for (int i = 0; i < IssueInfoFileArray.length(); i++) {

                        JSONObject IssueData = IssueInfoFileArray.getJSONObject(i);

                        String F_DownloadFilePath = GetServiceData.IMS_ServicePath + "/Get_File?FileName=" + IssueData.getString("F_DownloadFilePath");

                        //if (F_DownloadFilePath.contains())

                        IssueFile_List.add(i, new msibook_ims_issue_file_item(F_DownloadFilePath, "", "", ""));


                    }


                    mAdapter = new msibook_ims_issue_file_adapter(mContext, IssueFile_List);


                    mRecyclerView.setAdapter(mAdapter);

                } else {
                    LinearLayout RecycleView = (LinearLayout) findViewById(R.id.Lie_IssueFile);

                    RecycleView.setVisibility(View.GONE);

                    //TextView txt_IssueInfo_Gallery = (TextView) findViewById(R.id.txt_IssueInfo_Gallery);

                    //txt_IssueInfo_Gallery.setVisibility(View.GONE);
                }


            } catch (JSONException ex) {

            }


        }


        private void Issue_Get(String Issue_ID) {

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }

            String Path = GetServiceData.IMS_ServicePath + "/Issue_Get?F_SeqNo=" + Issue_ID;

            GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {

                    IssueInfoMapping(result);
                }
            });

        }

        private void GetIssue_Info(String Issue_ID) {

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }

            String Path = GetServiceData.IMS_ServicePath + "/GetIssue_Info?IssueID=" + Issue_ID;

            GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {


                    try {
                        JSONArray UserArray = new JSONArray(result.getString("Key"));

                        if (UserArray.length() > 0) {
                            IssueInfoMapping(UserArray.getJSONObject(0));

                            IssueCommentMapping(UserArray.getJSONObject(1));

                            IssueInfoFile_ListMapping(UserArray.getJSONObject(2));
                        }

                    } catch (Exception ex) {

                    }


                }
            });

        }


        private void IssueInfoMapping(JSONObject result) {
            try {
                WorkNote_List.clear();

                JSONArray UserArray = new JSONArray(result.getString("Key"));

                if (UserArray.length() > 0) {

                    JSONObject IssueData = UserArray.getJSONObject(0);

                    AuthorNameEN = IssueData.getString("F_Owner_en");

                    AuthorNameCN = IssueData.getString("F_Owner_cn");

                    Owner = IssueData.getString("F_RespGroup");

                    OwnerNameEN = IssueData.getString("Issue_OwnerEN");

                    OwnerNameCN = IssueData.getString("Issue_Owner");

                    String F_SeqNo = String.valueOf(IssueData.getInt("F_SeqNo"));

                    String F_Owner_en = IssueData.getString("F_Owner_en");

                    String Issue_OwnerEN = IssueData.getString("Issue_OwnerEN");

                    String F_ModelName = IssueData.getString("F_ModelName");

                    String F_Priority = IssueData.getString("F_Priority");

                    Issue_Priotity = F_Priority;

                    Status_Display = IssueData.getString("F_Status_Display");

                    String F_CreateDate = AppClass.ConvertDateString(IssueData.getString("F_CreateDate"));

                    String F_Subject = IssueData.getString("F_Subject");

                    Author = IssueData.getString("F_Keyin");

                    ModelID = IssueData.getString("F_PM_ID");

                    F_Subject = AppClass.stripHtml(F_Subject);

                    TextView txt_IssueInfo_Author = (TextView) findViewById(R.id.txt_IssueInfo_Author);

                    TextView txt_IssueInfo_Owner = (TextView) findViewById(R.id.txt_IssueInfo_Owner);

                    //TextView txt_IssueInfo_ProjectName = (TextView) findViewById(R.id.txt_IssueInfo_ProjectName);

                    ImageView Img_IssuePriority = (ImageView) findViewById(R.id.Img_IssuePriority);

                    TextView txt_IssueInfo_Date = (TextView) findViewById(R.id.txt_IssueInfo_Date);

                    TextView txt_Issue_Subject = (TextView) findViewById(R.id.txt_IssueInfo_Subject);

                    ImageView Img_Issue_Status = (ImageView) findViewById(R.id.Img_Issue_Status);

                    txt_IssueInfo_Author.setText(F_Owner_en);

                    if (Issue_OwnerEN == null || Issue_OwnerEN == "null") {
                        txt_IssueInfo_Owner.setText("");
                    } else {
                        txt_IssueInfo_Owner.setText(Issue_OwnerEN);
                    }

                    if (Status_Display.equals("2")) {
                        Img_Issue_Status.setImageResource(R.mipmap.ic_verify_arrow);
                    } else {
                        Img_Issue_Status.setImageResource(R.mipmap.newissue_ic_assign_arrow);
                    }

                    txt_IssueInfo_Date.setText(F_CreateDate);

                    //txt_IssueInfo_ProjectName.setText("MS-" + F_ModelName);

                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

                    toolbar.setTitle("MS-" + F_ModelName);

                    txt_Issue_Subject.setText(F_Subject);

                    Img_IssuePriority.setImageResource(AppClass.PriorityImage(F_Priority));

                    final ImageView Img_IssueAuthor = (ImageView) findViewById(R.id.Img_IssueAuthor);


                    supportInvalidateOptionsMenu();

                }
            } catch (JSONException ex) {

            }


        }

        private String PriorityConvert(String Priority) {
            String PriorityDisplayText = "";

            switch (Priority) {
                case "1":
                    PriorityDisplayText = "Critical (P1)";
                    break;
                case "2":
                    PriorityDisplayText = "Major (P2)";
                    break;
                case "3":
                    PriorityDisplayText = "Minor (P3)";
                    break;

            }

            return PriorityDisplayText;
        }

        public void IssuePriorityChange() {

            Go_Issue_Change_Priority("Priority", IssueID);


        }

        public void IssueOwnerChange(String PM_ID) {


        }


        private void Find_Issue_Comment(String Issue_ID) {

//        pDialog.setMessage("Loading...");
//
//        pDialog.show();

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }

            String Path = GetServiceData.IMS_ServicePath + "/Find_Issue_Comment?Issue_ID=" + Issue_ID;

            GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {

                    IssueCommentMapping(result);

                    //pDialog.hide();
                }
            });

        }

        private void IssueCommentMapping(JSONObject result) {
            try {
                WorkNote_List.clear();

                JSONArray UserArray = new JSONArray(result.getString("Key"));

                for (int i = 0; i < UserArray.length(); i++) {
                    JSONObject IssueData = UserArray.getJSONObject(i);

                    String ID = String.valueOf(IssueData.getInt("ID"));

                    String F_Keyin = IssueData.getString("F_Keyin");

                    String F_Owner = IssueData.getString("F_Owner");

                    String F_Owner_cn = IssueData.getString("F_Owner_cn");

                    String F_Owner_en = IssueData.getString("F_Owner_en");

                    String F_CreateDate = AppClass.ConvertLongDateString(IssueData.getString("F_CreateDate"));

                    String F_SeqNo = String.valueOf(IssueData.getInt("F_SeqNo"));

                    String F_Comment = IssueData.getString("F_Comment");

                    F_Comment = AppClass.stripHtml(F_Comment);

                    String CommentFile = IssueData.getString("Comment_File");

                    WorkNote_List.add(i, new msibook_ims_issue_worknote_item(F_Owner_en, F_Keyin, F_CreateDate, F_Comment, "", CommentFile));
                }

                // ListView 中所需之資料參數可透過修改 Adapter 的建構子傳入
                mListAdapter = new msibook_ims_issue_worknote_adapter(mContext, WorkNote_List);

                //mListAdapter.notifyDataSetChanged();
                //設定 ListView 的 Adapter
                lsv_main.setAdapter(mListAdapter);


            } catch (JSONException ex) {

            }


        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {

            mMenu = menu;

            if (!Status_Display.equals("3") && Author.equals(UserData.WorkID)) {
                getMenuInflater().inflate(R.menu.menu_issue, menu);
            }

            if (Status_Display.equals("1") && !Author.equals(UserData.WorkID)) {
                getMenuInflater().inflate(R.menu.menu_issue_owner, menu);
            }

            return true;
        }

        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.CloseIssue) {
                Close_Issue_Fun();
            } else if (id == R.id.IssueOwner) {


                Go_Issue_Change_Owner(ModelID, IssueID);
            } else if (id == R.id.IssuePriotiry) {
                IssuePriorityChange();
            } else if (id == R.id.Verify_Issue) {
                Verify_Issue_Fun();
            } else if (id == R.id.Reject_Verify_Issue) {
                Reject_Issue_Fun();
            }

            return super.onOptionsItemSelected(item);
        }

        private void Close_Issue_Fun() {

            List<AppClass_List_Item> CloseList = new ArrayList<AppClass_List_Item>();

            CloseList.add(0, new AppClass_List_Item("Limitation", "1"));

            CloseList.add(1, new AppClass_List_Item("Fixed", "2"));

            CloseList.add(2, new AppClass_List_Item("Waive", "5"));

            msibook_ims_issue_close._DataList = CloseList;

            Go_Issue_Close("Close", IssueID);

//        final Alert_Search_Dialog DataDialog = new Alert_Search_Dialog(mContext, "Select Close Type", CloseList);
//
//        DataDialog.SetOnDialog_Finish_Listener(new Alert_Search_Dialog.Dialog_Finish_Listener() {
//            @Override
//            public void Finished() {
//
//                if (!TextUtils.isEmpty(DataDialog.SelectValue)) {
//
//                    String Reason = DataDialog.GetReason();
//
//                    Close_Issue(IssueID, UserData.WorkID, DataDialog.SelectValue);
//
//                    C_Comment_Insert("Issue Close  " + Reason);
//
//                }
//            }
//
//            @Override
//            public void Cancel() {
//
//            }
//
//        });
//
//        DataDialog.setCancelable(false);
//
//        DataDialog.show();

        }

        private void Go_Issue_Close(String Type, String IssueID) {
            Bundle bundle = new Bundle();

            bundle.putString("IssueID", IssueID);

            bundle.putString("StatusType", Type);

            bundle.putString("Priority", Issue_Priotity);

            bundle.putString("Author", Author);

            //bundle.putString("Owner", );
            // 建立啟動另一個Activity元件需要的Intent物件
            // 建構式的第一個參數：「this」
            // 建構式的第二個參數：「Activity元件類別名稱.class」
            Intent intent = new Intent(this, msibook_ims_issue_close.class);

            intent.putExtras(bundle);
            // 呼叫「startActivity」，參數為一個建立好的Intent物件
            // 這行敘述執行以後，如果沒有任何錯誤，就會啟動指定的元件
            startActivity(intent);
        }

        private void Go_Issue_Change_Priority(String Type, String IssueID) {
            Bundle bundle = new Bundle();

            bundle.putString("IssueID", IssueID);

            bundle.putString("StatusType", Type);

            bundle.putString("Priority", Issue_Priotity);

            //bundle.putString("Owner", );
            // 建立啟動另一個Activity元件需要的Intent物件
            // 建構式的第一個參數：「this」
            // 建構式的第二個參數：「Activity元件類別名稱.class」
            Intent intent = new Intent(this, msibook_ims_Issue_change_priority.class);

            intent.putExtras(bundle);
            // 呼叫「startActivity」，參數為一個建立好的Intent物件
            // 這行敘述執行以後，如果沒有任何錯誤，就會啟動指定的元件
            startActivity(intent);
        }

        private void Go_Issue_Change_Owner(String ModelID, String IssueID) {
            Bundle bundle = new Bundle();

            bundle.putString("IssueID", IssueID);

            bundle.putString("ModelID", ModelID);

            bundle.putString("AuthorNameCN", AuthorNameCN);

            bundle.putString("AuthorNameEN", AuthorNameEN);

            bundle.putString("Author", Author);

            //bundle.putString("Owner", );
            // 建立啟動另一個Activity元件需要的Intent物件
            // 建構式的第一個參數：「this」
            // 建構式的第二個參數：「Activity元件類別名稱.class」
            Intent intent = new Intent(this, msibook_ims_Issue_change_owner.class);

            intent.putExtras(bundle);
            // 呼叫「startActivity」，參數為一個建立好的Intent物件
            // 這行敘述執行以後，如果沒有任何錯誤，就會啟動指定的元件
            startActivity(intent);
        }

        private void Verify_Issue_Fun() {
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    mContext);
            alert.setTitle("Verify Issue!!");
            alert.setMessage("Are you sure to verify issue");
            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {


                    Verify_Issue(IssueID, UserData.WorkID);

                }
            });
            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            alert.show();
        }

        private void Reject_Issue_Fun() {
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    mContext);
            alert.setTitle("Issue Fail!!");
            alert.setMessage("Are you sure to update issue");
            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {


                    Reject_Verify_Issue(IssueID, UserData.WorkID);

                }
            });
            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            alert.show();
        }

        private void Change_Issue_Owner(final String IssueID, final String WorkID) {


            if (!TextUtils.isEmpty(IssueID) && !TextUtils.isEmpty(WorkID)) {

                if (mQueue == null) {
                    mQueue = Volley.newRequestQueue(mContext);
                }

                Map<String, String> map = new HashMap<String, String>();
                map.put("IssueID", IssueID);
                map.put("WorkID", WorkID);

                String Path = GetServiceData.IMS_ServicePath + "/Change_Issue_Owner";

                GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                    @Override
                    public void onSendRequestSuccess(String result) {

                        Issue_Get(IssueID);

                        Find_Issue_Comment(IssueID);

                        AppClass.AlertMessage("Change Owner Success", mContext);
                    }

                    @Override
                    public void onSendRequestError(String result) {
                        Log.w("NotificationSuccess", result);
                    }

                }, map);


            } else {
                AppClass.AlertMessage("Change Owner Error", mContext);
            }


        }

        private void Change_Issue_Priority(final String IssueID, final String Priority) {


            if (!TextUtils.isEmpty(IssueID) && !TextUtils.isEmpty(Priority)) {

                if (mQueue == null) {
                    mQueue = Volley.newRequestQueue(mContext);
                }

                Map<String, String> map = new HashMap<String, String>();
                map.put("IssueID", IssueID);
                map.put("Priority", Priority);

                String Path = GetServiceData.IMS_ServicePath + "/Change_Issue_Priority";

                GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                    @Override
                    public void onSendRequestSuccess(String result) {

                        Issue_Get(IssueID);

                        AppClass.AlertMessage("Change Priority Success", mContext);
                    }

                    @Override
                    public void onSendRequestError(String result) {
                        //Log.w("NotificationSuccess",result);
                    }

                }, map);


            } else {
                AppClass.AlertMessage("Change Priority Error", mContext);
            }


        }

        private void Verify_Issue(final String IssueID, final String WorkID) {


            if (!TextUtils.isEmpty(IssueID)) {


                if (mQueue == null) {
                    mQueue = Volley.newRequestQueue(mContext);
                }

                Map<String, String> map = new HashMap<String, String>();
                map.put("IssueNo", IssueID);
                map.put("WorkID", WorkID);

                String Path = GetServiceData.IMS_ServicePath + "/Verify_Issue";

                GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                    @Override
                    public void onSendRequestSuccess(String result) {

                        AppClass.AlertMessage("Verify Issue Success", mContext);

                        Issue_Get(IssueID);
                        Issue_Get(IssueID);
                    }

                    @Override
                    public void onSendRequestError(String result) {
                        Log.w("NotificationSuccess", result);
                    }

                }, map);


            } else {
                AppClass.AlertMessage("Close Issue Error", mContext);
            }


        }

        private void Reject_Verify_Issue(final String IssueID, final String WorkID) {


            if (!TextUtils.isEmpty(IssueID)) {


                if (mQueue == null) {
                    mQueue = Volley.newRequestQueue(mContext);
                }

                Map<String, String> map = new HashMap<String, String>();
                map.put("IssueNo", IssueID);
                map.put("WorkID", WorkID);
                String Path = GetServiceData.IMS_ServicePath + "/Reject_Verify_Issue";

                GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                    @Override
                    public void onSendRequestSuccess(String result) {

                        AppClass.AlertMessage("Update Issue Success", mContext);
                    }

                    @Override
                    public void onSendRequestError(String result) {
                        Log.w("NotificationSuccess", result);
                    }

                }, map);


            } else {
                AppClass.AlertMessage("Close Issue Error", mContext);
            }


        }

        private void Close_Issue(final String IssueID, final String WorkID, final String CloseType) {


            if (!TextUtils.isEmpty(IssueID)) {


                if (mQueue == null) {
                    mQueue = Volley.newRequestQueue(mContext);
                }

                Map<String, String> map = new HashMap<String, String>();
                map.put("IssueNo", IssueID);
                map.put("WorkID", WorkID);
                map.put("CloseType", CloseType);

                String Path = GetServiceData.IMS_ServicePath + "/Close_Issue";

                GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                    @Override
                    public void onSendRequestSuccess(String result) {

                        AppClass.AlertMessage("Close Issue Success", mContext);

                        Issue_Get(IssueID);
                    }

                    @Override
                    public void onSendRequestError(String result) {
                        Log.w("NotificationSuccess", result);
                    }

                }, map);


            } else {
                AppClass.AlertMessage("Close Issue Error", mContext);
            }


        }

        @Override
        public boolean onPrepareOptionsMenu(Menu menu) {

            MenuItem RejectMenu = menu.findItem(R.id.Reject_Verify_Issue);

            MenuItem CloseMenu = menu.findItem(R.id.CloseIssue);

            if (!Status_Display.equals("3") && Owner.equals(Author)) {

                if (RejectMenu != null) {
                    RejectMenu.setVisible(false);
                }

            }
            if (Status_Display.equals("1") && !Owner.equals(Author)) {

                if (RejectMenu != null) {
                    RejectMenu.setVisible(false);
                }

                if (CloseMenu != null) {
                    CloseMenu.setVisible(false);
                }
            }

            return true;
        }

    }
