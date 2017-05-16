package com.example.wuzp.cursorloaderdemo.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wuzp.cursorloaderdemo.R;
import com.example.wuzp.cursorloaderdemo.bean.LoadBean;
import com.example.wuzp.cursorloaderdemo.db.DataService;

/**
 * Created by wuzp on 2017/5/16.
 */
public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUid;
    private EditText edtName;
    private EditText edtDesc;
    private Button   btnSubmit;

    private Uri readerUri;
    private String uid;
    private String summary;
    private String desc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtra(getIntent());
        setContentView(R.layout.activity_edit);
        initView();
    }

    private void getExtra(Intent intent){
        Bundle bundle = intent.getExtras();
        try{
            /*readerUri = bundle.getParcelable("reader");*/
            uid = bundle.getString("uid");
            summary = bundle.getString("summary");
            desc = bundle.getString("desc");
        }catch (Exception e){
            readerUri = null;
        }
    }

    private void initView(){
        edtUid = (EditText)findViewById(R.id.edt_uid);
        edtName = (EditText)findViewById(R.id.edt_name);
        edtDesc = (EditText)findViewById(R.id.edt_desc);
        btnSubmit = (Button)findViewById(R.id.btn_submit);

        initData();
    }

    private void initData(){
        btnSubmit.setOnClickListener(this);

        /*if(readerUri != null){
            String[] projection = { LoadDbHelper.READER_COLUMN_SUMMARY,
                    LoadDbHelper.READER_COLUMN_DESCRIPTION };
            Cursor cursor = getContentResolver().query(readerUri, projection, null, null,
                    null);
            if (cursor != null) {
                cursor.moveToFirst();
                edtName.setText(cursor.getString(cursor
                        .getColumnIndexOrThrow(LoadDbHelper.READER_COLUMN_SUMMARY)));
                edtDesc.setText(cursor.getString(cursor
                        .getColumnIndexOrThrow(LoadDbHelper.READER_COLUMN_DESCRIPTION)));
                cursor.close();
            }
        }*/
        if(!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(summary)){
            edtUid.setText(uid);
            edtName.setText(summary);
            edtDesc.setText(desc);
        }
    }

    private void saveData(){
        final String uid = edtUid.getText().toString();
        final String summary = edtName.getText().toString();
        final String description = edtDesc.getText().toString();
        if (uid.length() == 0 && description.length() == 0 && summary.length() == 0) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                LoadBean bean = new LoadBean();
                bean.setUid(uid);
                bean.setSummary(summary);
                bean.setDescription(description);

                DataService.getInstance().insert(bean);
                Log.e("123","save Data");
            }
        }).start();

        /*ContentValues values = new ContentValues();
        values.put(LoadDbHelper.READER_COLUMN_SUMMARY, summary);
        values.put(LoadDbHelper.READER_COLUMN_DESCRIPTION, description);

        if (readerUri == null) {
            readerUri = getContentResolver().insert(
                    LoadContentProvider.CONTENT_URI, values);
        } else {
            getContentResolver().update(readerUri, values, null, null);
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                saveData();
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
