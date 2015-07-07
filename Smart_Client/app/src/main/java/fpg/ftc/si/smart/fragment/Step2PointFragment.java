package fpg.ftc.si.smart.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpg.ftc.si.smart.ExecuteActivity;
import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.adapter.PointAdapter;
import fpg.ftc.si.smart.dao.ControlPoint;
import fpg.ftc.si.smart.dao.Equipment;
import fpg.ftc.si.smart.dao.RecordArrive;
import fpg.ftc.si.smart.dao.RecordRFID;
import fpg.ftc.si.smart.model.EquipmentItem;
import fpg.ftc.si.smart.model.PointItem;
import fpg.ftc.si.smart.model.RouteItem;
import fpg.ftc.si.smart.util.DateUtils;
import fpg.ftc.si.smart.util.UserSession;

import static fpg.ftc.si.smart.util.LogUtils.LOGI;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IOnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 */
public class Step2PointFragment extends Fragment {

    private static final String TAG = makeLogTag(Step1RouteItemFragment.class);

    private View mRootView;
    private ListView mPointListView;
    private ExecuteActivity mActivity;
    private PointAdapter mPointAdapter;
    private IOnFragmentInteractionListener mListener;

    private Button mBtnNote;
    private TextView mWayName;


    private RouteItem mRouteItem;


    public Step2PointFragment()
    {
        // Required empty public constructor
    }

    public Step2PointFragment(RouteItem routeItem) {

        this.mRouteItem = routeItem;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        LOGI(TAG,"Step2EquipFragment onCreateView");
        mActivity = (ExecuteActivity) getActivity();
        mActivity.setTitle(mActivity.getString(R.string.actionbar_title_point));
        mActivity.getActionBar().setIcon(R.drawable.ic_action_labels);
        // Inflate the layout for this fragment
        mRootView =inflater.inflate(R.layout.fragment_step2_point, container, false);
        mPointListView = (ListView) mRootView.findViewById(R.id.point_list);
        mWayName = (TextView) mRootView.findViewById(R.id.lb_way_name);
        mWayName.setText(mRouteItem.getNAME());
        mBtnNote = (Button) mRootView.findViewById(R.id.btn_note);
        //取得管制點資料
        //TODO 條件還不確定
        List<ControlPoint> controlPointList = mActivity.mSmartDBHelper.getControlPoints(mRouteItem.getID());
        ArrayList<PointItem> pointItemArrayList = new ArrayList<PointItem>();
        //TODO 基準開始時間
        Integer baseArrTime = 1300;
        // 將時間字串 轉成時間物件 方便後面的操作 yyyyMMdd hhmm
        String baseArrDateTimeStr = mRouteItem.getCURRENTDATE() +" "+ mRouteItem.getARRTIME();
        SimpleDateFormat baseArrFormatter = new SimpleDateFormat("yyyyMMdd hhmm");
        Date baseArrDate = null;
        try {
            baseArrDate = baseArrFormatter.parse(baseArrDateTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<String> ctlpList = new ArrayList<String>();//管制點集合,為了要去問有那些卡片


        for (ControlPoint item : controlPointList)
        {
            Date timeStart = DateUtils.addMinsToDate(baseArrDate,item.getMIN1());
            Date timeEnd = DateUtils.addMinsToDate(baseArrDate,item.getMIN2());
            PointItem new_item = new PointItem(item.getCTLPTID(),"XXX",item.getCTLPTNM(),item.getWAYID(),timeStart,timeEnd);

            //將設備的總數先取得
            HashMap<String,Integer> equipmentTotalMap = mActivity.mSmartDBHelper.getEquipmentTotalMap(item.getCTLPTID());
            int total_count = equipmentTotalMap.size();


            //NOTE:排掉管制點沒有建立設備
            if(total_count==0)
                continue;

            new_item.setTotal_Count(total_count);

            pointItemArrayList.add(new_item);
            ctlpList.add(item.getCTLPTID());

        }

        //取得管制點下的所有卡片
        HashMap<String,String> nfcTagsMap = mActivity.mSmartDBHelper.getNFCTags(ctlpList);
        mPointAdapter = new PointAdapter(mActivity,pointItemArrayList,nfcTagsMap);
        mPointListView.setAdapter(mPointAdapter);

        //set event
        mPointListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mListener) {

                    final PointItem item = mPointAdapter.getItem(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("管制點:"+ item.getNAME() +" 無法靠卡原因 ");
                    builder.setItems(R.array.control_point_error_values, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //前往設備
                            goToNextEquip(item,which);

                        }
                    });
                    builder.show();

                }
            }
        });



        mBtnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity,"今日無任何交代事項",Toast.LENGTH_SHORT).show();
            }
        });

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGI(TAG,"Step2EquipFragment onResume");
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();

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


    /**
     * 供外層叫用 找目前掃描到的TAG是哪一個管制點
     * @param uid
     */
    public void getRFIDItem(String uid)
    {
        //
        PointItem pointItem = mPointAdapter.getPointItemByNFCTag(uid);

        if(pointItem!=null)
        {
            goToNextEquip(pointItem,-1);
        }
        else
        {
            Toast.makeText(mActivity, "找不到 TAG: " + uid + " 的管制點 ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * @param pointItem 目前所在的管制點
     * @param error_flag -1 表示正常 0 1 2 因為異常才進來
     */
    private void goToNextEquip(PointItem pointItem,int error_flag)
    {
        final UserSession userSession =mActivity.mSession.getUserSession();
        //NOTE 如果該管制點只有一個設備 則直接跳到抄表基準頁面
        List<Equipment> equipmentList = mActivity.mSmartDBHelper.getEquipments(pointItem.getCTLPTID());
        if(equipmentList.size()==1)
        {
            //從DB取得的設備
            Equipment equipment = equipmentList.get(0);
            //此物件主要是要給Step3EquipFragment,但此例子要跳過該頁面,固有些欄位不用帶進去
            EquipmentItem equipmentItem = new EquipmentItem(equipment.getEQID(),equipment.getEQNM(),equipment.getCTLPTID(),pointItem.getWAYID(),0);
            mListener.onEquipFragmentInteraction(equipmentItem);
        }
        else
        {
            //原本流程
            mListener.onPointFragmentInteraction(pointItem);
        }

        //區域變數
        Date currentDate = new Date();
        String clsid = userSession.getmCLSID();
        String wayid = mRouteItem.getID();
        String ctlptid = pointItem.getCTLPTID();
        String urid = userSession.getmUserId();
        String arrive_date = DateUtils.format(currentDate,DateUtils.FORMAT_YYYYMMDD);
        String arrive_time = DateUtils.format(currentDate,DateUtils.FORMAT_HHMMSS);
        String currentDateTime = DateUtils.format(currentDate,DateUtils.FORMAT_YYYYMMDD_HHMMSS);

        //DELETE INSERT
        //抄表
        RecordRFID rfid_new_item = new RecordRFID(
                clsid,
                wayid,
                ctlptid,
                arrive_date,
                urid,
                String.valueOf(error_flag),
                currentDateTime
        );

        boolean isError = error_flag != -1 ? true : false;
        mActivity.mSmartDBHelper.deleteInsertRecordRFID(rfid_new_item,isError);

        //到位
        RecordArrive arrive_new_item = new RecordArrive(
                clsid,
                wayid,
                ctlptid,
                arrive_date,
                arrive_time,
                urid,
                currentDateTime
        );

        mActivity.mSmartDBHelper.deleteInsertRecordArrive(arrive_new_item);
    }

    /**
     * 重整
     */
    private void refresh() {
        LOGI(TAG, "Step2PointFragment refresh");
        final UserSession userSession =mActivity.mSession.getUserSession();
        Date currentDate = new Date();
        String currentDateStr = DateUtils.format(currentDate,DateUtils.FORMAT_YYYYMMDD);
        String clsid = userSession.getmCLSID();
        String urid = userSession.getmUserId();
        // 刷實際檢查設備數
        for (PointItem item : mPointAdapter.getmDataSource())
        {


            //TODO 取得管制點的完成度 有效能上的問題
            HashMap<String,Integer> equipmentTotalMap = mActivity.mSmartDBHelper.getEquipmentTotalMap(item.getCTLPTID());
            HashMap<String,Integer> equipmentDoneMap = mActivity.mSmartDBHelper.getEquipRecordDoneMap(item.getCTLPTID(),clsid, item.getWAYID(), item.getCTLPTID(), currentDateStr, urid);

            int finish_count = 0;



            for (Map.Entry<String, Integer> entry : equipmentTotalMap.entrySet())
            {
                String eqid = entry.getKey();//設備識別
                int total_stdid_count =  entry.getValue();//該設備的基準數量
                if(equipmentDoneMap.containsKey(eqid))
                {
                    int finish_stdid_count = equipmentDoneMap.get(eqid);
                    if(total_stdid_count == finish_stdid_count)
                    {
                        finish_count +=1;
                    }
                }
            }


            item.setFinish_Count(finish_count);

        }

        mPointAdapter.notifyDataSetChanged();
        //updateProgress();
    }
}
