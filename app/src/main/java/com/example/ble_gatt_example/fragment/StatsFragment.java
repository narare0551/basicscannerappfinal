package com.example.ble_gatt_example.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.example.ble_gatt_example.Common.FACNROLL_Top_Actionbar;
import com.example.ble_gatt_example.FragActivity;
import com.example.ble_gatt_example.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class StatsFragment extends Fragment implements FACNROLL_Top_Actionbar.IViewListener,View.OnClickListener{
    private FACNROLL_Top_Actionbar F_actionbar;
    private ActionBar actionBar;
    private Activity activity;

    private WebView mWebView2;
    private WebSettings mWebSettings2;
    private WebView mWebView3;
    private WebSettings mWebSettings3;

//    0322 knr - 사용자선택 버튼      -------------------
    protected Button btn_select_users;
    protected CharSequence[] receivers = {
            "Receiver1", "Receiver2", "Receivers3"};
    protected ArrayList<CharSequence> selectedReceivers = new ArrayList<>();
//밑에 결과 나타내려면
    protected TextView userlist;
//    0322 knr - 사용자선택 버튼 추가 끝 --------------



    public StatsFragment(ActionBar supportActionBar) {
        this.actionBar = supportActionBar;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //화면이 시작 되었을때 보이게할 Layout 을 inflater 로 연결해주는 부분
        return inflater.inflate(R.layout.stats_fragment,container,false);
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

        mWebView2 = getActivity().findViewById(R.id.webview2); //레이어와 연결
        mWebView3 = getActivity().findViewById(R.id.webview3); //레이어와 연결

        mWebSettings2 = mWebView2.getSettings();
        mWebSettings3 = mWebView3.getSettings();
        mWebSettings2.setJavaScriptEnabled(true);
        mWebSettings3.setJavaScriptEnabled(true);
        mWebSettings2.setSupportMultipleWindows(false);                  //새창 띄우기 허용 여부
        mWebSettings3.setSupportMultipleWindows(false);                  //새창 띄우기 허용 여부
        mWebSettings2.setJavaScriptCanOpenWindowsAutomatically(false);   //자바스크립트 새창(멀티뷰) 띄우기 허용 여부
        mWebSettings3.setJavaScriptCanOpenWindowsAutomatically(false);   //자바스크립트 새창(멀티뷰) 띄우기 허용 여부
        mWebSettings2.setUseWideViewPort(true);                          //화면 사이즈 맞추기 허용 여부
        mWebSettings3.setUseWideViewPort(true);                          //화면 사이즈 맞추기 허용 여부
        mWebSettings2.setSupportZoom(false);                             //화면 줌 허용 여부
        mWebSettings3.setSupportZoom(false);                             //화면 줌 허용 여부
        mWebSettings2.setBuiltInZoomControls(false);                     //화면 확대 축소 허용 여부
        mWebSettings3.setBuiltInZoomControls(false);                     //화면 확대 축소 허용 여부
        mWebSettings2.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);     //컨텐츠 사이즈 맞추기
        mWebSettings3.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);     //컨텐츠 사이즈 맞추기
        mWebSettings2.setCacheMode(WebSettings.LOAD_NO_CACHE);           //브라우저 캐시 허용 여부
        mWebSettings3.setCacheMode(WebSettings.LOAD_NO_CACHE);           //브라우저 캐시 허용 여부
        mWebSettings2.setDomStorageEnabled(true);                        //로컬저장소 허용 여부
        mWebSettings3.setDomStorageEnabled(true);                        //로컬저장소 허용 여부
        mWebSettings2.setSaveFormData(true);                             //입력된 데이터 저장 허용 여부
        mWebSettings3.setSaveFormData(true);                             //입력된 데이터 저장 허용 여부

        mWebView2.loadUrl("http://192.168.0.34/main");                      //웹뷰 실행
        mWebView3.loadUrl("http://192.168.0.34/main");                      //웹뷰 실행
        mWebView2.setWebChromeClient(new WebChromeClient());             //웹뷰에 크롬 사용 허용//이 부분이 없으면 크롬에서 alert가 뜨지 않음
        mWebView3.setWebChromeClient(new WebChromeClient());             //웹뷰에 크롬 사용 허용//이 부분이 없으면 크롬에서 alert가 뜨지 않음
        mWebView2.setWebViewClient(new WebViewClient());            //새창열기 없이 웹뷰 내에서 다시 열기//페이지 이동 원활히 하기위해 사용
        mWebView3.setWebViewClient(new WebViewClient());            //새창열기 없이 웹뷰 내에서 다시 열기//페이지 이동 원활히 하기위해 사용


        btn_select_users=activity.findViewById(R.id.btn_select_users);        //사용자 선택하는 버튼
        btn_select_users.setOnClickListener(this);                            //클릭시 checkbox alert dialog 띄우기 위해 사용
        //밑에 결과 나타내려면
//        userlist=activity.findViewById(R.id.userlist);

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
      title.setText("FACNROLL");

      bluetooth.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
              startActivity(intent);
          }
      });
  }

  @Override
  public void onClick(View v) {
              switch(v.getId()) {
            case R.id.btn_select_users:
                showSelectReceiversDialog();
                break;
            default:
                break;
        }
  }



/*    protected void onChangeSelectedReceivers() {
        StringBuilder stringBuilder = new StringBuilder();

        for(CharSequence receivers : selectedReceivers)
            stringBuilder.append(receivers + ",");
        btn_select_users.setText(stringBuilder.toString());
    }*/


    protected void showSelectReceiversDialog() {
        boolean[] checkedReceivers = new boolean[receivers.length];
        int count = receivers.length;

        for (int i = 0; i < count; i++)
            checkedReceivers[i] = selectedReceivers.contains(receivers[i]);

        DialogInterface.OnMultiChoiceClickListener receiversDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked)
                    selectedReceivers.add(receivers[which]);
                else
                    selectedReceivers.remove(receivers[which]);

//                onChangeSelectedReceivers();     //결과 값 띄우기 위해서 사용
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
                .setTitle("Select Receivers")
                .setMultiChoiceItems(receivers, checkedReceivers, receiversDialogListener)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }



}
