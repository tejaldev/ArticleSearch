package com.ny.search.article.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author tejalpar
 */
public class Filter {
    public static final String ARTS_KEY = "Arts";
    public static final String FASHION_KEY = "Fashion";
    public static final String SPORTS_KEY = "Sports";

    private long dateInMillis;
    private String sortOrder;
    private HashMap<String, Boolean> newsDeskMap;

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public HashMap<String, Boolean> getNewsDeskMap() {
        return newsDeskMap;
    }

    public void setNewsDeskMap(HashMap<String, Boolean> newsDeskMap) {
        this.newsDeskMap = newsDeskMap;
    }

    public boolean isFilterSet() {
        return (dateInMillis != 0);
    }

    public String getFormattedDate() {
        Date date = new Date(dateInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(date);
    }
}
