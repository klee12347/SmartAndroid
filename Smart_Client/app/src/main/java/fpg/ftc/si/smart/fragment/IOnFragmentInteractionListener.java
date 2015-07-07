package fpg.ftc.si.smart.fragment;

import fpg.ftc.si.smart.model.EquipmentItem;
import fpg.ftc.si.smart.model.PointItem;
import fpg.ftc.si.smart.model.RouteItem;

/**
 * 提供各個流程項目與主Activity溝通的方法
 * Created by MarlinJoe on 2014/8/13.
 */
public interface IOnFragmentInteractionListener {

    public void onRouteFragmentInteraction(RouteItem routeItem);
    public void onPointFragmentInteraction(PointItem pointItem);
    public void onEquipFragmentInteraction(EquipmentItem equipmentItem);
}
