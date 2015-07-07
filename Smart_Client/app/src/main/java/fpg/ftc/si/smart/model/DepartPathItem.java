/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.model;

/**
 * Created by MarlinJoe on 2014/10/7.
 */
public class DepartPathItem {

    public String FULL_PATH_ID;
    public String FULL_PATH_DISPLAY;

    public DepartPathItem(String FULL_PATH_ID, String FULL_PATH_DISPLAY) {
        this.FULL_PATH_ID = FULL_PATH_ID;
        this.FULL_PATH_DISPLAY = FULL_PATH_DISPLAY;
    }

    public String getFULL_PATH_ID() {
        return FULL_PATH_ID;
    }

    public String getFULL_PATH_DISPLAY() {
        return FULL_PATH_DISPLAY;
    }
}
