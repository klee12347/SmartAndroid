package fpg.ftc.si.smart.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fpg.ftc.si.smart.ExecuteActivity;
import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.adapter.RouteAdapter;
import fpg.ftc.si.smart.dao.Route;
import fpg.ftc.si.smart.model.RouteItem;
import fpg.ftc.si.smart.util.DateUtils;
import fpg.ftc.si.smart.util.SessionManager;
import fpg.ftc.si.smart.util.UserSession;

import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link}
 * interface.
 */
public class Step1RouteItemFragment extends Fragment {

    private static final String TAG = makeLogTag(Step1RouteItemFragment.class);

    private IOnFragmentInteractionListener mListener;

    private View mRootView;
    private ListView mRouteListView;
    private ExecuteActivity mActivity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Step1RouteItemFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        mActivity = (ExecuteActivity) getActivity();
        mActivity.setTitle(mActivity.getString(R.string.actionbar_title_route));
        mActivity.getActionBar().setIcon(R.drawable.ic_action_place);
        // Inflate the layout for this fragment
        mRootView =inflater.inflate(R.layout.fragment_step1_route, container, false);
        mRouteListView = (ListView) mRootView.findViewById(R.id.route_list);
        SessionManager sessionManager = new SessionManager(mActivity);
        UserSession userSession = sessionManager.getUserSession();
        //取得時間
        Date currentDate = new Date();
        String currentDateStr = DateUtils.format(currentDate,DateUtils.FORMAT_YYYYMMDD);
        //取得路線資料
        List<Route> routeList = mActivity.mSmartDBHelper.getRoutes(userSession.getmUserId(),userSession.getmCLSID(),currentDateStr);
        ArrayList<RouteItem> routeItemList = new ArrayList<RouteItem>();
        for (Route item : routeList)
        {
            routeItemList.add(new RouteItem(item.getWAYID(),item.getWAYNM(),userSession.getmFirstTime(),currentDateStr));
        }

        final RouteAdapter mRouteAdapter = new RouteAdapter(mActivity,routeItemList);
        mRouteListView.setAdapter(mRouteAdapter);


        //set event
        mRouteListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mListener) {

                    RouteItem routeItem = mRouteAdapter.getItem(position);
                    mListener.onRouteFragmentInteraction(routeItem);
                }
            }
        });

        //DISTINCT 路線 篩選日期

        return mRootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (IOnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
