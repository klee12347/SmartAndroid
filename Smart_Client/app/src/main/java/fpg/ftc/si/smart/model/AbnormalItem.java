/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.model;

import java.io.Serializable;
import java.util.List;

/**
 * 異常原因
 * Created by MarlinJoe on 2014/9/17.
 */
public class AbnormalItem implements Serializable {
    private String ABID;
    private String ABName;
    private List<DealMethodItem> DealMethodItems;

    public AbnormalItem(String ABID, String ABName) {
        this.ABID = ABID;
        this.ABName = ABName;
    }

    public List<DealMethodItem> getDealMethodItems() {
        return DealMethodItems;
    }

    public void setDealMethodItems(List<DealMethodItem> dealMethodItems) {
        DealMethodItems = dealMethodItems;
    }

    public String getABID() {
        return ABID;
    }

    public String getABName() {
        return ABName;
    }
}
