package com.teacore.teascript.module.general.base.basebean;

import java.io.Serializable;
import java.util.List;

/**
 * 页面bean类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-22
 */

public class PageBean<T> implements Serializable {

    private List<T> itemsList;
    private String nextPageToken;
    private String prevPageToken;
    private PageInfo pageInfo;

    public List<T> getItems() {
        return itemsList;
    }

    public void setItems(List<T> items) {
        this.itemsList = items;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public String getPrevPageToken() {
        return prevPageToken;
    }

    public void setPrevPageToken(String prevPageToken) {
        this.prevPageToken = prevPageToken;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public static class PageInfo implements Serializable {

        private int totalResults;
        private int resultsPerPage;

        public int getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(int totalResults) {
            this.totalResults = totalResults;
        }

        public int getResultsPerPage() {
            return resultsPerPage;
        }

        public void setResultsPerPage(int resultsPerPage) {
            this.resultsPerPage = resultsPerPage;
        }

    }
}