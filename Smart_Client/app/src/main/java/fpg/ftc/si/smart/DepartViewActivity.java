package fpg.ftc.si.smart;

import android.app.ActionBar;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.Arrays;

import fpg.ftc.si.smart.adapter.DepartTabsPagerAdapter;
import fpg.ftc.si.smart.dao.Depart;
import fpg.ftc.si.smart.fragment.DepartUserFragment;
import fpg.ftc.si.smart.fragment.LoadingFragment;
import fpg.ftc.si.smart.provider.BasicDBHelper;
import fpg.ftc.si.smart.provider.SmartDBHelper;
import fpg.ftc.si.smart.util.consts.RequestTagConstants;
import fpg.ftc.si.smart.util.consts.SystemConstants;
import fpg.ftc.si.smart.util.PreferenceUtils;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.LOGE;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

public class DepartViewActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String TAG = makeLogTag(DepartViewActivity.class);
    private String mHash = "";//Server 端回傳的hash

    public BasicDBHelper BasicDBHelper = null;//資料庫
    public SmartDBHelper SmartDBHelper  = null;//資料庫
    public PreferenceUtils Preferences;//TODO 和Fragment共用不知有無副作用



    private FragmentManager mFragmentManager;
    private Fragment mFragment;//loading fragment

    //Tab
    private ViewPager mViewPager;
    private DepartTabsPagerAdapter mAdapter;
    private ActionBar mActionBar;
    // Tab 標題
    private String[] mTabs ;
    private int[] mTabsIcons = { R.drawable.ic_action_group_white, R.drawable.ic_action_person_white};

    //目前要看的部門的DepID
    public String CurrentSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depart_explore);
        BasicDBHelper = new BasicDBHelper(this);
        SmartDBHelper = new SmartDBHelper(this);
        mTabs = getResources().getStringArray(R.array.depart_view_items);
        if (savedInstanceState == null) {
            // Get the preferences
            Preferences = PreferenceUtils.getInstance(getApplicationContext());
            mFragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            mFragment = new LoadingFragment();
            ft.add(R.id.container,mFragment);
            ft.commit();

            mViewPager = (ViewPager) findViewById(R.id.pager);

            mActionBar = getActionBar();

        }

        // 1.先取得遠端主機hash
        getDepartHashRequest();

    }

    public void goToDepartUserView()
    {
        mActionBar.setSelectedNavigationItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        BasicDBHelper.close();

    }


    /**
     * 取得Hash 取到後則新增到資料庫中
     * @return
     */
    private void getDepartHashRequest()
    {
        String url = Preferences.getDepartHashUrl();
        LOGD(TAG,"url:" + url);
        StringRequest stringRequest = new StringRequest(url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                mHash = response.replace("\"","");

                String current_hash = BasicDBHelper.getAppSetting(SystemConstants.KEY_DEPART_HASH);
                LOGD(TAG,"抓取hash完成");
                if(!current_hash.equals(mHash))
                {
                    //2.抓取組織樹
                    getDepartRequest();
                }
                else
                {
                    //組織樹不需要更新
                    redirectToDepart();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                LOGE(TAG,"VolleyError:" + error.getMessage());
                Toast.makeText(getApplicationContext(), getString(R.string.system_message_connection_error), Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest, RequestTagConstants.DEPARTHASH_REQUEST);
    }


    /**
     * 取得主機資料
     */
    private void getDepartRequest()
    {
        String url = Preferences.getDepartUrl();
        LOGD(TAG,"url:" + url);
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        LOGD(TAG,"抓取hash完成");
                        Gson gson = new Gson();
                        Depart[] tempResult = gson.fromJson(response.toString(), Depart[].class);
                        //清除掉Depart重新Insert
                        boolean result_falg = BasicDBHelper.deleteInsertDepartList(Arrays.asList(tempResult));
                        if(result_falg)
                        {
                            BasicDBHelper.setAppSetting(SystemConstants.KEY_DEPART_HASH,mHash);
                            //導頁
                            redirectToDepart();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.system_message_update_group_data_fail), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(getApplicationContext(), getString(R.string.system_message_connection_error), Toast.LENGTH_LONG).show();
                error.printStackTrace();
                LOGE(TAG,"onErrorResponse,"+error.toString());

            }
        });

        AppController.getInstance().addToRequestQueue(jsArrayRequest, RequestTagConstants.DEPART_REQUEST);
    }

    /**
     * 當組織樹的資料已經是最新時,直接到群組畫面中
     */
    private void redirectToDepart()
    {
        if(mFragment!=null)
        {
            //隱藏Loadging
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(mFragment);
            ft.commit();
            mAdapter = new DepartTabsPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mAdapter);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            // Adding Tabs
            for (int i=0; i < mTabs.length; i++)
            {
                mActionBar.addTab(mActionBar.newTab()
                        .setText(mTabs[i])
                        .setIcon(getResources().getDrawable(mTabsIcons[i]))
                        .setTabListener(this));
            }


            /**
             * on swiping the viewpager make respective tab selected
             * */
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    // on changing the page
                    // make respected tab selected
                    mActionBar.setSelectedNavigationItem(position);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
        }

    }



    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

        int position = tab.getPosition();
        mViewPager.setCurrentItem(position);
        switch (position)
        {

            case 1:
                DepartUserFragment departUserFragment = (DepartUserFragment) mAdapter.getItem(position);
                departUserFragment.RefreshData();
                break;

        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

}
