package com.atguigu.gmall.bean;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: fanyitai
 * @Date: 2019/12/21 17:11
 * @Version 1.0
 */
public class PmsSearchSkuInfo implements Serializable {

    @Id
    private long id;
    private String skuName;
    private String skuDesc;
    private String price;
    private String skuDefaultImg;
    private String hotScore;
    private String productId;
    private String catalog3Id;
    private List<PmsSkuAttrValue> skuAttrValueList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuDesc() {
        return skuDesc;
    }

    public void setSkuDesc(String skuDesc) {
        this.skuDesc = skuDesc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSkuDefaultImg() {
        return skuDefaultImg;
    }

    public void setSkuDefaultImg(String skuDefaultImg) {
        this.skuDefaultImg = skuDefaultImg;
    }

    public String getHotScore() {
        return hotScore;
    }

    public void setHotScore(String hotScore) {
        this.hotScore = hotScore;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public List<PmsSkuAttrValue> getSkuAttrValueList() {
        return skuAttrValueList;
    }

    public void setSkuAttrValueList(List<PmsSkuAttrValue> skuAttrValueList) {
        this.skuAttrValueList = skuAttrValueList;
    }
}
