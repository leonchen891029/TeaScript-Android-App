package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 开源软件分类列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-28
 */
@XStreamAlias("teascript")
public class SoftwareCatalogList extends Entity implements EntityList{

    @XStreamAlias("softwarecount")
    private int softwareCount;
    @XStreamAlias("softwareTypes")
    private List<SoftwareType> softwareCatalogList = new ArrayList<SoftwareType>();

    public int getSoftwareCount() {
        return softwareCount;
    }

    public void setSoftwareCount(int softwareCount) {
        this.softwareCount = softwareCount;
    }

    public List<SoftwareType> getSoftwareCatalogList() {
        return softwareCatalogList;
    }

    public void setSoftwareCatalogList(List<SoftwareType> softwareCatalogList) {
        this.softwareCatalogList = softwareCatalogList;
    }

    @Override
    public List<?> getList() {
        return softwareCatalogList;
    }

    @XStreamAlias("softwareType")
    public class SoftwareType extends Entity {

        @XStreamAlias("name")
        private String name;
        @XStreamAlias("tag")
        private int tag;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getTag() {
            return tag;
        }
        public void setTag(int tag) {
            this.tag = tag;
        }

    }

}
