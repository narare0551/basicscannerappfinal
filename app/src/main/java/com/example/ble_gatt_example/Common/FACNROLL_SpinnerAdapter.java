package com.example.ble_gatt_example.Common;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class FACNROLL_SpinnerAdapter extends BaseAdapter {

    private ArrayList<SpinnerItem> data_list;
    public void setData_list(ArrayList<SpinnerItem> data_list){
        this.data_list = data_list;
    }


    private Context context;
    public FACNROLL_SpinnerAdapter(Context context, ArrayList<SpinnerItem> data_list){
        this.context = context;
        setData_list(data_list);
    }

    public void addItem(String key,String value){
        SpinnerItem spinnerItem = new SpinnerItem(key,value);
        if(data_list == null){
            data_list = new ArrayList<>();
        }
        data_list.add(spinnerItem);
        notifyDataSetChanged();
    }
    public void addItem(SpinnerItem spinnerItem){
        if(data_list == null){
            data_list = new ArrayList<>();
        }
        data_list.add(spinnerItem);
        notifyDataSetChanged();
    }

    public void clearData(){
        if(data_list != null){
            this.data_list.clear();
        }
    }

    private int font_size = 30;
    public void setFontSize(int font_size){
        this.font_size = font_size;
    }

    private int margin_size = 5;
    public void setMargin(int margin_size){
        this.margin_size = margin_size;
    }

    @Override
    public int getCount() {
        return data_list.size();
    }
    @Override
    public Object getItem(int i) {
        return data_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView text_view = new TextView(context);
        text_view.setTextSize(font_size);
        text_view.setPadding(margin_size,margin_size,margin_size,margin_size);
        text_view.setTextColor(Color.rgb(0,0,0));
        SpinnerItem row = data_list.get(i);
        text_view.setText(row.value);
        return text_view;
    }

    public static class SpinnerItem{
        public SpinnerItem(String key,String value){
            this.key = key;
            this.value = value;
        }
        public SpinnerItem(){}
        public String key;
        public String value;
    }


}
