package com.example.ble_gatt_example.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.example.ble_gatt_example.Common.Common;
import com.example.ble_gatt_example.Common.FACNROLL_HttpSocket;
import com.example.ble_gatt_example.Common.FACNROLL_ListAdapter;
import com.example.ble_gatt_example.Common.FACNROLL_Top_Actionbar;
import com.example.ble_gatt_example.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashBoardFragment extends Fragment implements FACNROLL_Top_Actionbar.IViewListener {
    private FACNROLL_Top_Actionbar F_actionbar;
    private ActionBar actionBar;
    private Activity activity;
    private Common common = new Common();

    private WebView mWebView;
    private WebSettings mWebSettings;

    public DashBoardFragment(ActionBar supportActionBar) {
        this.actionBar = supportActionBar;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //화면이 시작 되었을때 보이게할 Layout 을 inflater 로 연결해주는 부분
        return inflater.inflate(R.layout.dashboard_fragment,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //화면이 시작된 후 화면동작 이벤트 설정 부분은 여기서 부터 시작
        super.onActivityCreated(savedInstanceState);
        //Fragment 에서 context or activity 사용을 위해 getActivity 받아오기
        this.activity = getActivity();
        //Top Action Bar 라이브러리 연결
        F_actionbar = new FACNROLL_Top_Actionbar(activity,this.actionBar, R.layout.actionbar);
        F_actionbar.setIViewListener(this);
        F_actionbar.init();
        init();

        mWebView = getActivity().findViewById(R.id.webview); //레이어와 연결

        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportMultipleWindows(false);                  //새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false);   //자바스크립트 새창(멀티뷰) 띄우기 허용 여부
        mWebSettings.setUseWideViewPort(true);                          //화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false);                             //화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false);                     //화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);     //컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);           //브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(true);                        //로컬저장소 허용 여부
        mWebSettings.setSaveFormData(true);                             //입력된 데이터 저장 허용 여부

        mWebView.loadUrl("http://192.168.0.34/main");                      //웹뷰 실행
        mWebView.setWebChromeClient(new WebChromeClient());             //웹뷰에 크롬 사용 허용//이 부분이 없으면 크롬에서 alert가 뜨지 않음
        mWebView.setWebViewClient(new WebViewClient());            //새창열기 없이 웹뷰 내에서 다시 열기//페이지 이동 원활히 하기위해 사용

    }

    private TextView today_scan, yesterday_scan, average_scan, use_day;
    private void init() {
        today_scan = activity.findViewById(R.id.today_scan);
        yesterday_scan = activity.findViewById(R.id.yesterday_scan);
        average_scan = activity.findViewById(R.id.average_scan);
        use_day = activity.findViewById(R.id.total_scan);
        get_today();
        get_yesterday();
        get_average();
        get_total();
    }

    private void get_today() {
        FACNROLL_HttpSocket socket = new FACNROLL_HttpSocket();
        socket.setURL("http://192.168.0.34/dashboard/today_scan");
        socket.addText("user_id", common.user_id);
        socket.start();
        try {
            socket.join();
            String result = socket.response_data;
            System.out.println(result);
            if(!result.equals("[]")){
                result = result.replace("\"","");
                today_scan.setText(result);
            }else{
                today_scan.setText("0");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void get_yesterday() {
        FACNROLL_HttpSocket socket = new FACNROLL_HttpSocket();
        socket.setURL("http://192.168.0.34/dashboard/yesterday_scan");
        socket.addText("user_id", common.user_id);
        socket.start();
        try {
            socket.join();
            String result = socket.response_data;
            System.out.println(result);
            if(!result.equals("[]")){
                result = result.replace("\"","");
                yesterday_scan.setText(result);
            }else{
                yesterday_scan.setText("0");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void get_average() {
        FACNROLL_HttpSocket socket = new FACNROLL_HttpSocket();
        socket.setURL("http://192.168.0.34/dashboard/average_scan");
        socket.addText("user_id", common.user_id);
        socket.start();
        try {
            socket.join();
            String result = socket.response_data;
            System.out.println(result);
            if(result.equals("error")){
                average_scan.setText("0");
            }else if(!result.equals("[]")){
                result = result.replace("\"","");
                average_scan.setText(result);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void get_total() {
        FACNROLL_HttpSocket socket = new FACNROLL_HttpSocket();
        socket.setURL("http://192.168.0.34/dashboard/total_scan");
        socket.addText("user_id", common.user_id);
        socket.start();
        try {
            socket.join();
            String result = socket.response_data;
            System.out.println(result);
            if(!result.equals("[]")){
                result = result.replace("\"","");
                use_day.setText(result);
            }else{
                use_day.setText("0");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        title.setText("대시보드");
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
            }
        });
    }


    public class DataBean{
        public String today_scan;
        public String yesterday_scan;
        public String average_scan;
        public String use_day;
    }

}
