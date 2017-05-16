package com.example.wuzp.cursorloaderdemo.view;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.wuzp.cursorloaderdemo.R;
import com.example.wuzp.cursorloaderdemo.adapter.LoadAdapter;
import com.example.wuzp.cursorloaderdemo.bean.LoadBean;
import com.example.wuzp.cursorloaderdemo.db.DataService;
import com.example.wuzp.cursorloaderdemo.db.LoadDbHelper;
import com.example.wuzp.cursorloaderdemo.provider.LoadContentProvider;

import java.util.ArrayList;

//整理的一个cursorloader demo
/**
 * 步骤：
 * 1.实现接口LoaderManager.LoaderCallbacks<Cursor>
 * 2.创建数据表 继承DBOpenHelper,提供数据源
 * 3.创建ContentProvider 封装处理DBHelper的方法，暴露统一的查插删改的方法
 * */
public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemClickListener,
        ListView.OnItemLongClickListener
{
    private final int REQUESTCODE = 0X101;

    private Button btnMenu;
    private ListView listView;

    private SimpleCursorAdapter adapter;
    private LoadAdapter loadAdapter;
    private ArrayList<LoadBean> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        btnMenu = (Button)findViewById(R.id.btn_menu);
        listView = (ListView)findViewById(R.id.list);

        initData();
    }

    private void initData(){
        /*String[] from = new String[] { LoadDbHelper.READER_COLUMN_SUMMARY, LoadDbHelper.READER_COLUMN_DESCRIPTION };
        int[] to = new int[] { R.id.text_name, R.id.text_desc };

        getLoaderManager().initLoader(0, null, this); //开始准备数据
        adapter = new SimpleCursorAdapter(this, R.layout.item_row, null, from, to, 0);

        listView.setAdapter(adapter);*/

        loadAdapter = new LoadAdapter(this,mData);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mData = DataService.getInstance().queryAll(null,null,null,null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadAdapter.setData(mData);
                        loadAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

        listView.setAdapter(loadAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        btnMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_menu:
                Intent intent = new Intent(this, EditActivity.class);
                startActivityForResult(intent,REQUESTCODE);
                break;
        }
    }

    //Item的点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,final long id) {
        /*Intent i = new Intent(this, EditActivity.class);
        Uri todoUri = Uri.parse(LoadContentProvider.CONTENT_URI + "/" + id);
        i.putExtra("reader", todoUri);
        startActivity(i);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
               final LoadBean bean = DataService.getInstance().query(null," uid = ? ",new String[]{"" + ((int)id)},null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = new Bundle();
                        bundle.putString("uid", bean.getUid());
                        bundle.putString("summary",bean.getSummary());
                        bundle.putString("desc",bean.getDescription());
                        Intent i = new Intent(MainActivity.this, EditActivity.class);
                        i.putExtras(bundle);
                        startActivityForResult(i,REQUESTCODE);
                    }
                });
            }
        }).start();

    }

    //item的长恩事件
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        /*Uri uri = Uri.parse(LoadContentProvider.CONTENT_URI + "/" + id);
        getContentResolver().delete(uri, null, null);
        Toast.makeText(this,"delete success",Toast.LENGTH_SHORT).show();
        return true;*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = DataService.getInstance().delete(position + "",null,null);
                if(count > 0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUESTCODE:
                if(resultCode == RESULT_OK){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mData = DataService.getInstance().queryAll(null,null,null,null);
                            Log.e("123","get Data");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadAdapter.setData(mData);
                                    loadAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                }
                break;
        }
    }

    /**
* --------------------------------------------------------------------------------------------------
* */
    //LoaderManager.LoaderCallbacks<Cursor>接口方法
    //在cursorLoader创建时调用
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { LoadDbHelper.READER_COLUMN_ID, LoadDbHelper.READER_COLUMN_SUMMARY, LoadDbHelper.READER_COLUMN_DESCRIPTION };
        //cursorloader 会帮你查找你提供的的contentprovider 来查询你给定的uri
        return new CursorLoader(this, LoadContentProvider.CONTENT_URI, projection, null, null, null);
    }

    //在cursorloader 处理完成之后调用
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    //在cursorloader重置时候调用
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
