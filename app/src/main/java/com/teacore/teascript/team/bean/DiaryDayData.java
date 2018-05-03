package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.List;

/**
 * 周报数据实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-22
 */
@XStreamAlias("diaryday")
public class DiaryDayData extends Entity{

    @XStreamAlias("sun")
    private DayData sun;
    @XStreamAlias("thu")
    private DayData thu;
    @XStreamAlias("wed")
    private DayData wed;
    @XStreamAlias("tue")
    private DayData tue;
    @XStreamAlias("mon")
    private DayData mon;
    @XStreamAlias("fri")
    private DayData fri;
    @XStreamAlias("sat")
    private DayData sat;

    public class DayData implements Serializable{

        @XStreamImplicit(itemFieldName = "list")
        private List<String> list;

        public List<String> getList(){
            return list;
        }

        public void setList(List<String> list){
            this.list=list;
        }

    }

    public DayData getSun() {
        return sun;
    }

    public void setSun(DayData sun) {
        this.sun = sun;
    }

    public DayData getThu() {
        return thu;
    }

    public void setThu(DayData thu) {
        this.thu = thu;
    }

    public DayData getWed() {
        return wed;
    }

    public void setWed(DayData wed) {
        this.wed = wed;
    }

    public DayData getTue() {
        return tue;
    }

    public void setTue(DayData tue) {
        this.tue = tue;
    }

    public DayData getMon() {
        return mon;
    }

    public void setMon(DayData mon) {
        this.mon = mon;
    }

    public DayData getFri() {
        return fri;
    }

    public void setFri(DayData fri) {
        this.fri = fri;
    }

    public DayData getSat() {
        return sat;
    }

    public void setSat(DayData sat) {
        this.sat = sat;
    }

}
