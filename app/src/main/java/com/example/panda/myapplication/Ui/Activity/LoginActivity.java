package com.zgsy.bj.Ui.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.zgsy.bj.Constants;
import com.zgsy.bj.Data.DatabaseHelper;
import com.zgsy.bj.R;
import com.zgsy.bj.Data.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * A login screen that offers login via email/password.
 */
/*

TODO:- 我们在这里可以使用sqlite简单的数据库，进行用户查询，只要用户在这台手机上登录过
TODO:- 那么我们就可以把用户名密码存到本地的数据库，在登录时，我们首先在本地进行查询，如果
TODO:- 发现没有相应的用户，再从bmob端数据库获取相关信息。


 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public static boolean record = false; //true 注册 false 登录
    private UserLoginTask mAuthTask = null;
    private int i;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    final Lock lock = new ReentrantLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //PushAgent.getInstance(context).onAppStart();

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        //populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        DatabaseHelper database = new DatabaseHelper(LoginActivity.this);//这段代码放到Activity类中才用this
        SQLiteDatabase db1 = null;
        db1 = database.getReadableDatabase();
        if (getIntent().getAction() == "ExitRelogin") {
            ;
        } else {

            try {
                //创建SharedPreferences对象
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

                //获得保存在SharedPredPreferences中的用户名和密码
                String user_name = sp.getString("username", "");
                String pass_word = sp.getString("password", "");

                if (user_name.length() > 0) {
                    mEmailView.setText(user_name);
                    Constants.userName = user_name;
                    mPasswordView.setText(pass_word);
                }
                //在用户名和密码的输入框中显示用户名和密码

            } catch (Exception excepton) {

            }
        }
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        assert mEmailSignInButton != null;
        Button mEmailLoginButton = (Button) findViewById(R.id.email_register_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                record = false;
                Log.i("record", "" + record);
                attemptLogin();

            }
        });
        mEmailLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                record = true;
                Log.i("record", "" + record);
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);

            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute();


        }
    }


    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */


    private boolean isEmailValid(String email) {
        if (email == null || email.length() < 5) {
            // #如果帐号小于5位，则肯定不可能为邮箱帐号eg: x@x.x
            return false;
        }
        if (!email.contains("@")) {// 判断是否含有@符号
            return false;// 没有@则肯定不是邮箱
        }
        String[] sAcc = email.split("@");
        if (sAcc.length != 2) {// # 数组长度不为2则包含2个以上的@符号，不为邮箱帐号
            return false;
        }
        if (sAcc[0].length() <= 0) {// #@前段为邮箱用户名，自定义的话至少长度为1，其他暂不验证
            return false;
        }
        if (sAcc[1].length() < 3 || !sAcc[1].contains(".")) {
            // # @后面为域名，位数小于3位则不为有效的域名信息
            // #如果后端不包含.则肯定不是邮箱的域名信息
            return false;
        } else {
            if (sAcc[1].substring(sAcc[1].length() - 1).equals(".")) {
                // # 最后一位不能为.结束
                return false;
            }
            String[] sDomain = sAcc[1].split("\\.");
            // #将域名拆分 tm-sp.com 或者 .com.cn.xxx
            for (String s : sDomain) {
                if (s.length() <= 0) {
                    System.err.println(s);
                    return false;
                }
            }

        }
        return true;
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private String s;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;

        }


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("record>>>>>", "" + record);

            return true;
        }

        //if successfully post then finish ,else setError "this password is incorrect"
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);

            if (success) {

                Intent intent = new Intent();

                intent.setClass(LoginActivity.this, ProgressActivity.class);
                intent.setAction("com.example.panda");
                intent.putExtra("name", s);
                LoginActivity.this.sendBroadcast(intent);

                //true 注册 false 登录
                if (record) {
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                    record();
                } else {
                    String username = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("username", null);
                    String password = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("password", null);

                    if (username == mEmailView.getText().toString() && password == mPasswordView.getText().toString()){
                        Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                        record();
                    }else {
                        Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                        return;
                    }
                }

                startActivity(intent);
                finish();

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }


        public void record() {
            s = mEmail;

            // 获取默认的SharedPreferences对象（使用应用包名加上_preferences作为文件名）
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            // 获取SharedPreferences.Editor对象用于编辑数据
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // 存入一个字符串类型的键值对，键为"username"，值为"John Doe"
            editor.putString("username", "John Doe");
            //以键值对的显示将用户名和密码保存到sp中
            editor.putString("username", mEmail);
            editor.putString("password", mPassword);
            // 提交更改，将数据真正写入到存储文件中
            editor.commit();


            DatabaseHelper database = new DatabaseHelper(LoginActivity.this);//这段代码放到Activity类中才用this
            SQLiteDatabase db = null;
            db = database.getReadableDatabase();
            ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据cv.put("username","Jack Johnson");//添加用户名
            cv.put("username", mEmail);
            cv.put("password", mPassword); //添加密码
            db.insert("user", null, cv);//执行插入操作
        }


    }
}

