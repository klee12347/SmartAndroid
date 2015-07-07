/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;



/**
 * 派工
 * Created by MarlinJoe on 2014/9/9.
 */
public class Job {

    private String JOBID;

    private String WAYID;

    private String ENABLE;

    private String WeekDay;

    private String URID;

    private String Name;

    private String BEGTM;

    private String ENDTM;

    public Job(String JOBID, String WAYID, String ENABLE, String weekDay, String URID, String name, String BEGTM, String ENDTM) {
        this.JOBID = JOBID;
        this.WAYID = WAYID;
        this.ENABLE = ENABLE;
        WeekDay = weekDay;
        this.URID = URID;
        Name = name;
        this.BEGTM = BEGTM;
        this.ENDTM = ENDTM;
    }
}
