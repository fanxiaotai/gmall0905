package com.atguigu.gmall.bean;

import java.io.Serializable;

/**
 * @Author: fanyitai
 * @Date: 2019/12/22 15:02
 * @Version 1.0
 */
public class PmsSearchCrumb implements Serializable {

    private String valueId;
    private String valueName;
    private String urlParam;

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }
}
