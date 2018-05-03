package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * Teatime bean 列表类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-16
 */
@XStreamAlias("teascript")
public class TeatimesList extends Entity implements EntityList<Teatime>{

    public final static int CATALOG_LATEST=0;
    public final static int CATALOG_HOT=-1;
    public final static int CATALOG_ME=1;

    private int teatimeCount;

    private int pageSize;

    @XStreamAlias("teatimeList")
    private List<Teatime> teatimeList=new ArrayList<Teatime>();

    public int getTeatimeCount(){
        return teatimeCount;
    }

    public void setTeatimeCount(int teatimeCount){
        this.teatimeCount=teatimeCount;
    }

    public int getPageSize(){
        return pageSize;
    }

    public void setPageSize(int pageSize){
        this.pageSize=pageSize;
    }

    public List<Teatime> getTeatimesList(){
        return teatimeList;
    }

    public void setTeatimesList(List<Teatime> teatimesList){
        this.teatimeList=teatimesList;
    }

    @Override
    public List<Teatime> getList() {
        return teatimeList;
    }

}
