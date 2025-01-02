package com.zgsy.bj.Ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;


import com.zgsy.bj.R;
import com.zgsy.bj.Ui.Activity.LoginActivity;

public class start extends AppCompatActivity implements View.OnClickListener {
    private Button button;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String path=null;
        super.onCreate(savedInstanceState);
        //PushAgent.getInstance(context).onAppStart();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.initiate);
        button=(Button)findViewById(R.id.start_main);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(start.this);

        //获得保存在SharedPredPreferences中的用户名和密码
        String user_name = sp.getString("username", "");
        if (TextUtils.isEmpty(user_name)) {
            intent.setClass(this,LoginActivity.class);
        }else {
            intent.setClass(this, ProgressActivity.class);
        }

        startActivity(intent);
        this.finish();
    }
}
