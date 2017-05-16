package com.example.wuzp.cursorloaderdemo.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wuzp.cursorloaderdemo.R;
import com.example.wuzp.cursorloaderdemo.db.LoadDbHelper;
import com.example.wuzp.cursorloaderdemo.provider.LoadContentProvider;

/**
 * Created by wuzp on 2017/5/16.
 */
public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtName;
    private EditText edtDesc;
    private Button   btnSubmit;

    private Uri readerUri;

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
            readerUri = bundle.getParcelable("reader");
        }catch (Exception e){
            readerUri = null;
        }
    }

    private void initView(){
        edtName = (EditText)findViewById(R.id.edt_name);
        edtDesc = (EditText)findViewById(R.id.edt_desc);
        btnSubmit = (Button)findViewById(R.id.btn_submit);

        initData();
    }

    private void initData(){
        btnSubmit.setOnClickListener(this);

        if(readerUri != null){
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
        }
    }

    private void saveData(){
        String summary = edtName.getText().toString();
        String description = edtDesc.getText().toString();
        if (description.length() == 0 && summary.length() == 0) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(LoadDbHelper.READER_COLUMN_SUMMARY, summary);
        values.put(LoadDbHelper.READER_COLUMN_DESCRIPTION, description);

        if (readerUri == null) {
            readerUri = getContentResolver().insert(
                    LoadContentProvider.CONTENT_URI, values);
        } else {
            getContentResolver().update(readerUri, values, null, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                saveData();
                finish();
                break;
        }
    }
}
