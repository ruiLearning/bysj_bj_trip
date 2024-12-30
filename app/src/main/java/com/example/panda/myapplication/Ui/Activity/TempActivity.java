package com.zgsy.bj.Ui.Activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.zgsy.bj.Ui.Fragment.ContentFragment;
import com.zgsy.bj.Ui.Fragment.FirstFragment;
import com.zgsy.bj.Ui.Fragment.FourFragment;
import com.zgsy.bj.R;
import com.zgsy.bj.Ui.Fragment.SecondFragment;
import com.zgsy.bj.Ui.Fragment.ThirdFragment;

import java.util.ArrayList;

public class TempActivity extends AppCompatActivity {
    FragmentTransaction transaction = getSupportFragmentManager()
            .beginTransaction();
    private int count = 0;
    private ArrayList<Object> list = new ArrayList<Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp);
        FirstFragment firstFragment = FirstFragment.newInstance();
        SecondFragment secondFragment = new SecondFragment();
        ThirdFragment thirdFragment = new ThirdFragment();
        FourFragment fourFragment = new FourFragment();
        list.add(firstFragment);
        list.add(secondFragment);
        list.add(thirdFragment);
        list.add(fourFragment);
        if (getIntent().hasExtra("num")) {
            if (getIntent().getStringExtra("num").equals("1")) {
                count = 1;
                transaction.replace(R.id.main_container, firstFragment);
                //transaction.addToBackStack(null);
                transaction.commit();
                transaction.show(firstFragment);
            }
            if (getIntent().getStringExtra("num").equals("2")) {
                count = 2;

                transaction.replace(R.id.main_container, secondFragment);
                //transaction.addToBackStack(null);
                transaction.commit();
                transaction.show(secondFragment);
            }
            if (getIntent().getStringExtra("num").equals("3")) {
                count = 3;
                transaction.replace(R.id.main_container, thirdFragment);
                //transaction.addToBackStack(null);
                transaction.commit();
                transaction.show(thirdFragment);
            }
            if (getIntent().getStringExtra("num").equals("4")) {
                count = 4;
                transaction.replace(R.id.main_container, fourFragment);
                //transaction.addToBackStack(null);
                transaction.commit();
                transaction.show(fourFragment);
            }
        }
        if (getIntent().hasExtra("contentPosition")) {
            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            int s=getIntent().getExtras().getInt("contentPosition");
            Log.i(">>I",""+s);
            bundle.putString("position", s+"");
            ContentFragment contentFragment = ContentFragment.newInstance(bundle);
            fragmentTransaction.replace(R.id.main_container, contentFragment);
            fragmentTransaction.commit();
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.show(contentFragment);
        }
    }


}
