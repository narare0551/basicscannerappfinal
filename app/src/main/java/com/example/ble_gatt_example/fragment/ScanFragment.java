package com.example.ble_gatt_example.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.ble_gatt_example.Common.BleGattServer;
import com.example.ble_gatt_example.Common.CentralCallback;
import com.example.ble_gatt_example.Common.Common;
import com.example.ble_gatt_example.Common.FACNROLL_HttpSocket;
import com.example.ble_gatt_example.Common.FACNROLL_ListAdapter;
import com.example.ble_gatt_example.Common.FACNROLL_Top_Actionbar;
import com.example.ble_gatt_example.HistoryDetail;
import com.example.ble_gatt_example.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.ble_gatt_example.Common.Constants.REQUEST_ENABLE_BT;
import static com.example.ble_gatt_example.Common.Constants.REQUEST_FINE_LOCATION;

public class ScanFragment extends Fragment implements FACNROLL_Top_Actionbar.IViewListener, View.OnKeyListener, FACNROLL_ListAdapter.FACNROLL_Custom_GetViewListener {
    private FACNROLL_Top_Actionbar F_actionbar;
    private FACNROLL_ListAdapter listAdapter;
    private ActionBar actionBar;
    private Activity activity;
    private Common common = new Common();
    public ScanFragment(ActionBar supportActionBar) {
        this.actionBar = supportActionBar;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //화면이 시작 되었을때 보이게할 Layout 을 inflater 로 연결해주는 부분
        return inflater.inflate(R.layout.scan_fragment,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //화면이 시작된 후 화면동작 이벤트 설정 부분은 여기서 부터 시작
        super.onActivityCreated(savedInstanceState);
        //Fragment 에서 context or activity 사용을 위해 getActivity 받아오기
        this.activity = getActivity();
        //Top Action Bar 라이브러리 연결
        F_actionbar = new FACNROLL_Top_Actionbar(activity,this.actionBar,R.layout.actionbar);
        F_actionbar.setIViewListener(this);
        F_actionbar.init();
        init();
        getHistory();
    }

    private EditText scan_text;
    private TextView scan_barcode,history_count;
    private ListView barcode_history;
    private ImageView scan_img;
    private Handler handler;
    private ArrayList<HistoryBean> data;
    private void init() {
        scan_text = activity.findViewById(R.id.scan_text);
        scan_barcode = activity.findViewById(R.id.scan_barcode);
        scan_img = activity.findViewById(R.id.scan_img);
        barcode_history = activity.findViewById(R.id.barcode_history);
        history_count = activity.findViewById(R.id.history_count);

        listAdapter = new FACNROLL_ListAdapter(activity, R.layout.history_rows,this);
        data = new ArrayList();
        listAdapter.setDataList(data);
        barcode_history.setAdapter(listAdapter);
        scan_text.setOnKeyListener(this);
    }

    /* actionbar.xml */
    private ImageView left_menu,bluetooth;
    private TextView title;

    @Override
    public void onView(View view) {
        //FACNROLL_Top_Actionbar 뷰 세팅 부분
        left_menu = view.findViewById(R.id.left_menu);
        title = view.findViewById(R.id.title);
        bluetooth = view.findViewById(R.id.bluetooth);
        left_menu.setVisibility(View.GONE);
        title.setText("스캔");
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
            }
        });
    }

    /* 스캔시 엔터값 체크 해서 서버에 데이터 저장 */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(v == scan_text && keyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            String barcode = scan_text.getText().toString().trim();
            scan_barcode.setText(barcode);
            save_server();
            handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    scan_text.setText(null);
                }
            });
        }
        return false;
    }

    private void getHistory() {
        FACNROLL_HttpSocket socket = new FACNROLL_HttpSocket();
        socket.setURL("http://192.168.0.34/aiapi/now_history");
        socket.addText("user_id",common.user_id);
        socket.start();
        try {
            socket.join();
            String result = socket.response_data;
            if(!result.equals("[]")){
                data.clear();
                JSONArray array = new JSONArray(result);
                for(int i=0; i<array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    HistoryBean bean = new HistoryBean();
                    bean.idx = obj.getString("idx");
                    bean.barcode = obj.getString("barcode");
                    bean.device_mac = obj.getString("device_mac_addr");
                    bean.reg_dttm = obj.getString("reg_dttm");
                    bean.user_name = obj.getString("name");
                    bean.reg_user_id = obj.getString("reg_user_id");
                    this.data.add(bean);
                }
                listAdapter.listRefresh();
                history_count.setText("("+array.length()+")");
            }else{
                Common common = new Common();
                common.showToast(activity,"데이터가 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View customView(int i, View view, ViewGroup viewGroup, ArrayList data_list, FACNROLL_ListAdapter facnrollBaseAdapter, Activity activity, LayoutInflater layoutInflater) {
        TextView users, mac_addr, barcode, dttm, idx;
        LinearLayout select_layout;

        users = view.findViewById(R.id.users);
        mac_addr = view.findViewById(R.id.mac_addr);
        barcode = view.findViewById(R.id.barcode);
        dttm = view.findViewById(R.id.dttm);
        idx = view.findViewById(R.id.idx);
        select_layout = view.findViewById(R.id.select_layout);

        users.setTextSize(17f);
        mac_addr.setTextSize(17f);
        barcode.setTextSize(17f);
        dttm.setTextSize(17f);

        ArrayList<HistoryBean> array = data_list;
        HistoryBean bean = array.get(i);
        users.setText("등록자 : " + bean.user_name);
        if(bean.device_mac.isEmpty() || bean.device_mac.equals("null")) {
            mac_addr.setText("Mac : No Data");
        }else{
            mac_addr.setText("Mac : "+bean.device_mac);
        }
        barcode.setText("바코드 : " + bean.barcode);
        dttm.setText(bean.reg_dttm);
        idx.setText(bean.idx);

        select_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idx = bean.idx;
                Intent intent = new Intent(activity, HistoryDetail.class);
                intent.putExtra("idx",idx);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public String[] getColumns() {
        return new String[0];
    }

    private void save_server(){
        String barcode = scan_barcode.getText().toString().trim();
        String reg_user_id = common.user_id;
        FACNROLL_HttpSocket socket = new FACNROLL_HttpSocket();
        socket.setURL("http://192.168.0.34/aiapi/save_data");
        socket.addText("barcode",barcode);
        socket.addText("reg_user_id",reg_user_id);
        socket.start();
        try {
            socket.join();
            getHistory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class HistoryBean{
        public String idx;
        public String user_name;
        public String device_mac;
        public String barcode;
        public String reg_dttm;
        public String reg_user_id;
    }
}
