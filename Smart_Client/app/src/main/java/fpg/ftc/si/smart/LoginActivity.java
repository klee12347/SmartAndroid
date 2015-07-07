package fpg.ftc.si.smart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import fpg.ftc.si.smart.adapter.ShiftAdapter;
import fpg.ftc.si.smart.dao.Shift;
import fpg.ftc.si.smart.dao.User;
import fpg.ftc.si.smart.model.enumtype.LoginStatus;
import fpg.ftc.si.smart.provider.SmartDBHelper;
import fpg.ftc.si.smart.request.CheckAppUpdateRequest;
import fpg.ftc.si.smart.util.DecodeUtils;
import fpg.ftc.si.smart.util.SessionManager;

import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

public class LoginActivity extends Activity {

    private static final String TAG = makeLogTag(LoginActivity.class);
    private SmartDBHelper mSmartDBHelper = null;
    private SessionManager mSession;

    private Spinner mSpinnerShift;
    private Button mBtnLogin;
    private Button mBtnDownload;
    private Button mBtnSetting;
    private ShiftAdapter mShiftAdapter;
    private UserLoginTask mAuthTask = null;//登入的非同步的task
    private String mAccount;
    private String mPassword;

    // UI references
    private EditText mAccountView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();
        mSmartDBHelper = new SmartDBHelper(this);
        mSession = new SessionManager(this);
        mSpinnerShift = (Spinner) findViewById(R.id.sp_shift);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnDownload = (Button) findViewById(R.id.btn_download);
        mBtnSetting = (Button) findViewById(R.id.btn_setting);

        // Importing all assets
        mAccountView = (EditText) findViewById(R.id.txtAccount);
        mPasswordView = (EditText) findViewById(R.id.txtPassword);
        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        //Seting list
        mShiftAdapter = new ShiftAdapter(this);
        mSpinnerShift.setAdapter(mShiftAdapter);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        mBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        DepartViewActivity.class);
                startActivity(intent);
            }
        });

        mBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        SettingsActivity.class);
                startActivity(intent);
            }
        });



        //檢查更新版本
        // TODO 應該用service去做
        CheckAppUpdateRequest checkAppUpdateRequest = new CheckAppUpdateRequest(this);
        checkAppUpdateRequest.getRequest();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新班別
        List<Shift> shiftList = mSmartDBHelper.getShifts();
        mShiftAdapter.setItemList(shiftList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }




    /**
     * 登入
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mAccount = mAccountView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 驗證帳號有沒有填
        if (TextUtils.isEmpty(mAccount)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        }

        // 驗證密碼有沒有填
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }


        if (cancel) {
            // 表示有錯誤,不能進行登入,將焦點聚在第一個 form 欄位
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
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
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 使用者登入 非同步
     *
     */
    public class UserLoginTask extends AsyncTask<Void, Void, LoginStatus> {

        @Override
        protected LoginStatus doInBackground(Void... params) {
            User user = null;
            user = mSmartDBHelper.getUser(mAccount);
            String md5_pwd = DecodeUtils.getMD5EncryptedString(mPassword);
            // 帳號不存在
            if (user == null)
            {
                return  LoginStatus.NotExist;
            }
            else if (!user.getPWD().equals(md5_pwd))
            {
                return  LoginStatus.PWD_Invalid;
            }
            else
            {
                Shift selectedShift  = (Shift)mSpinnerShift.getSelectedItem();
                mSession.createLoginSession(user.getURID(),user.getACCOUNT(),user.getNAME(),selectedShift.getCLSID(),selectedShift.getCLSNM(),selectedShift.getFIRST_TIME());
                return LoginStatus.Success;
            }
        }

        @Override
        protected void onPostExecute(final LoginStatus status) {
            mAuthTask = null;
            showProgress(false);

            //登入成功
            if (status == LoginStatus.Success) {

                Intent intent = new Intent(getApplicationContext(),ExecuteActivity.class);
                startActivity(intent);


            } else if (status == LoginStatus.NotExist) {
                mAccountView.setError(getString(R.string.error_incorrect_user));
                mAccountView.requestFocus();
            } else if (status == LoginStatus.PWD_Invalid) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}
