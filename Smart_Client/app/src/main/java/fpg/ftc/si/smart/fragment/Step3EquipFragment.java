package fpg.ftc.si.smart.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fpg.ftc.si.smart.ExecuteActivity;
import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.adapter.EquipAdapter;
import fpg.ftc.si.smart.dao.Equipment;
import fpg.ftc.si.smart.dao.RecordArrive;
import fpg.ftc.si.smart.model.EquipmentItem;
import fpg.ftc.si.smart.model.PointItem;
import fpg.ftc.si.smart.util.DateUtils;
import fpg.ftc.si.smart.util.UserSession;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.LOGI;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Step3EquipFragment} interface
 * to handle interaction events.
 *
 */
public class Step3EquipFragment extends Fragment {

    private static final String TAG = makeLogTag(Step1RouteItemFragment.class);

    private View mRootView;
    private ListView mPointListView;
    private ExecuteActivity mActivity;

    private IOnFragmentInteractionListener mListener;

    private PointItem mPointItem;
    private TextView mPointName;
    private TextView mArriveTime;
    private ProgressBar mProgressBar;
    private EquipAdapter mEquipAdapter;
    private Button mBtnRest;
    public Step3EquipFragment() {
        // Required empty public constructor
    }

    public Step3EquipFragment(PointItem pointItem) {
        this.mPointItem = pointItem;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        mActivity = (ExecuteActivity) getActivity();
        mActivity.setTitle(mActivity.getString(R.string.actionbar_title_equip));
        mActivity.getActionBar().setIcon(R.drawable.ic_action_labels);
        // Inflate the layout for this fragment
        mRootView =inflater.inflate(R.layout.fragment_step3_equip, container, false);
        mPointListView = (ListView) mRootView.findViewById(R.id.equip_list);
        mPointName = (TextView) mRootView.findViewById(R.id.lb_point_name);
        mArriveTime = (TextView) mRootView.findViewById(R.id.lb_arrive_time);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progress_bar);
        mPointName.setText(mPointItem.getNAME());
        mBtnRest = (Button) mRootView.findViewById(R.id.btn_rest);



        //set event
        mPointListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mListener) {

                    EquipmentItem item = mEquipAdapter.getItem(position);
                    mListener.onEquipFragmentInteraction(item);

                }
            }
        });

        mBtnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "管制點:" + mPointItem.getNAME() + " 設備皆為重要設備無法備機", Toast.LENGTH_SHORT).show();
            }
        });
        return mRootView;
    }

    /**
     * 更新進度條
     */
    public void updateProgress()
    {
        mProgressBar.setProgress(mEquipAdapter.getProgress());
    }

    /**
     * 取得目前進度
     * @return
     */
    public int getCurrentProgress()
    {
        return mProgressBar.getProgress();
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

    @Override
    public void onResume() {
        super.onResume();
        LOGI(TAG,"Step3EquipFragment onResume");
        refresh();
    }

    /**
     * 重整
     */
    private void refresh() {
        LOGI(TAG, "Step3EquipFragment refresh");

        //取得到位資料
        final UserSession userSession =mActivity.mSession.getUserSession();
        Date currentDate = new Date();
        String currentDateStr = DateUtils.format(currentDate,DateUtils.FORMAT_YYYYMMDD);
        String clsid = userSession.getmCLSID();
        String wayid = mPointItem.getWAYID();
        String ctlptid = mPointItem.getCTLPTID();
        String urid = userSession.getmUserId();
        String checkKey = clsid + "_" + wayid + "_" + ctlptid + "_" + currentDateStr + "_" + urid;
        //帶入設備資料

        //取得設備
        List<Equipment> equipmentList = mActivity.mSmartDBHelper.getEquipments(mPointItem.getCTLPTID());

        //取得設備下 的基準數
        HashMap<String, Integer> equipMentTotalCountMap = mActivity.mSmartDBHelper.getEquipmentTotalMap(mPointItem.getCTLPTID());

        HashMap<String, Integer> getEquipRecordDoneMap = mActivity.mSmartDBHelper.getEquipRecordDoneMap(ctlptid,clsid,wayid,ctlptid,currentDateStr,urid);

        //將DB資料轉成畫面的物件 EquipmentItem
        ArrayList<EquipmentItem> equipmentItemArrayList = new ArrayList<EquipmentItem>();
        for (Equipment item : equipmentList)
        {
            int totalCount = 0;
            if(equipMentTotalCountMap.containsKey(item.getEQID()))
            {
                totalCount =equipMentTotalCountMap.get(item.getEQID());
            }

            EquipmentItem equipmentItem = new EquipmentItem(item.getEQID(),item.getEQNM(),item.getCTLPTID(),wayid,totalCount);


            if(getEquipRecordDoneMap.containsKey(item.getEQID()))
            {
                int doneCount = getEquipRecordDoneMap.get(item.getEQID());
                equipmentItem.setFinish_Count(doneCount);
            }

            equipmentItemArrayList.add(equipmentItem);
        }

        //主要顯示目前到位時間
        RecordArrive recordArrive = mActivity.mSmartDBHelper.getRecordArrive(checkKey);
        mArriveTime.setText(recordArrive.getARRIVE_DATE() + " " + recordArrive.getARRIVE_TIME());

        mEquipAdapter = new EquipAdapter(mActivity,equipmentItemArrayList);
        mPointListView.setAdapter(mEquipAdapter);

        updateProgress();
    }

}
