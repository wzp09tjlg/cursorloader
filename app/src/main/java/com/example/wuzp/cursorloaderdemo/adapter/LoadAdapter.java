package com.example.wuzp.cursorloaderdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.wuzp.cursorloaderdemo.R;
import com.example.wuzp.cursorloaderdemo.bean.LoadBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzp on 2017/5/16.
 * 自己实现一个adapter 类似cursoradapter的数据处理
 */
public class LoadAdapter extends BaseAdapter {
    private List<LoadBean> mData;
    private LayoutInflater inflater;

    public LoadAdapter(Context context, ArrayList<LoadBean> mData) {
        inflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    public void setData(ArrayList<LoadBean> data){
        mData = data;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_row, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textUid.setText(mData.get(position).getUid());
        viewHolder.textName.setText(mData.get(position).getSummary());
        viewHolder.textDesc.setText(mData.get(position).getDescription());
        return convertView;
    }

    static class ViewHolder {
        private TextView textUid;
        private TextView textName;
        private TextView textDesc;

        public ViewHolder(View view) {
            textUid  = (TextView) view.findViewById(R.id.text_uid);
            textName = (TextView) view.findViewById(R.id.text_name);
            textDesc = (TextView) view.findViewById(R.id.text_desc);
        }
    }
}
