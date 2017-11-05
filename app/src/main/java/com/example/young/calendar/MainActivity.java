package com.example.young.calendar;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.young.calendar.biz.ShowIsHide;
import com.example.young.calendar.log.Log;

public class MainActivity extends AppCompatActivity implements ShowIsHide{
    private TextView txtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtView=(TextView)findViewById(R.id.time);
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBirthdayFragment();
            }
        });
    }
    private SelectBirthdayDialog selectBirthdayDialog;
    private void showBirthdayFragment(){
        selectBirthdayDialog=new SelectBirthdayDialog();
        FragmentTransaction ft  = getFragmentManager().beginTransaction();
//       将当前的Fragment加入到回退堆栈，当用户按返回键，或者通过按帮助框的Close按钮dismiss帮助框是，重新显示提示框。
//       对于back stack的处理，系统具有一定的智能。例如：执行两次addToStackStack()，实际不会重复压栈。 有例如：注释掉remove()语句，即提示框不消失，而是在帮助框的下面，如右图，由于提示框存在，我们并不需要将提示框键入到back stack，但是在实验中发现是否有addToBackStack()都不会结果有影响，系统能够分析到对象存在，不需要压栈。没有去查源代码，猜测通过mBackStackId比对来进行智能处理。
        ft.addToBackStack(null);
        if("出生年月".contains(txtView.getText().toString())){

        }else {
            selectBirthdayDialog.date = txtView.getText().toString();
        }
        selectBirthdayDialog.show(ft,"selectBirthdayDialog");

    }

    @Override
    public void checkCurrentTime(String time) {
        Log.e("==="+time);
        txtView.setText(time);
    }
}
