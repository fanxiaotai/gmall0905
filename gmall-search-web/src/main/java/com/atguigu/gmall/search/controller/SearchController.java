package com.atguigu.gmall.search.controller;

import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.client.ManageClient;
import com.atguigu.gmall.search.service.GmSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;

/**
 * @Author: fanyitai
 * @Date: 2019/12/21 21:27
 * @Version 1.0
 */
@Controller
@CrossOrigin
public class SearchController {

    @Autowired
    GmSearchService gmSearchService;

    @Autowired
    ManageClient manageClient;

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap){

        //面包屑
        List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();

        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = null;

        try {
            //调用搜索服务，返回搜锁结果
            pmsSearchSkuInfoList = gmSearchService.list(pmsSearchParam);

            //抽去检索结果所包含的平台属性
            Set<String> valueIdSet = new HashSet<>();
            if (pmsSearchSkuInfoList!=null){
                for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfoList) {
                    List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
                    for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                        String valueId = pmsSkuAttrValue.getValueId();
                        valueIdSet.add(valueId);
                    }
                }
            }

            //平台属性筛选后的地址集合
            String urlParam = getUrlParam(pmsSearchParam);
            modelMap.put("urlParam",urlParam);

            //根据valueId将属性列表查询出来
            List<PmsBaseAttrInfo> pmsBaseAttrInfos = manageClient.getAttrValueListByValueId(valueIdSet);

            //对平台属性集合进一步处理，去掉当前条件中valueId所在的属性组
            String[] delValueIds = pmsSearchParam.getValueId();
            if (delValueIds!=null&&delValueIds.length!=0){
                for (String delValueId : delValueIds) {
                    PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                    pmsSearchCrumb.setValueId(delValueId);
                    Iterator<PmsBaseAttrInfo> attrInfoIterator = pmsBaseAttrInfos.iterator();
                    while (attrInfoIterator.hasNext()) {
                        PmsBaseAttrInfo pmsBaseAttrInfo = attrInfoIterator.next();
                        List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                        for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                            String valueId = pmsBaseAttrValue.getId();

                            if (delValueId.equals(valueId)) {
                                pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());

                                String crumbUrlParam = urlParam;
                                String[] split = crumbUrlParam.split("&valueId=" + delValueId);
                                StringBuilder stringBuilder = new StringBuilder();
                                for (String s : split) {
                                    stringBuilder.append(s);
                                }
                                pmsSearchCrumb.setUrlParam(stringBuilder.toString());

                                pmsSearchCrumbs.add(pmsSearchCrumb);
                                attrInfoIterator.remove();
                            }
                        }
                    }
                }
            }

            //商品属性列表
            modelMap.put("attrList",pmsBaseAttrInfos);

            //商品
            modelMap.put("skuLsInfoList",pmsSearchSkuInfoList);

            //显示搜索条件
            String keyword = pmsSearchParam.getKeyword();
            if (StringUtils.isNotBlank(keyword)){
                modelMap.put("keyword",keyword);
            }

            //添加面包屑
            modelMap.put("attrValueSelectedList",pmsSearchCrumbs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "list";
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam) {
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] valueId1 = pmsSearchParam.getValueId();

        StringBuilder stringBuilder = new StringBuilder();

        if (StringUtils.isNotBlank(keyword)){
            if (stringBuilder.length()!=0){
                stringBuilder.append("&");
            }
            stringBuilder.append("keyword=");
            stringBuilder.append(keyword);
        }

        if (StringUtils.isNotBlank(catalog3Id)){
            if (stringBuilder.length()!=0){
                stringBuilder.append("&");
            }
            stringBuilder.append("catalog3Id=");
            stringBuilder.append(catalog3Id);
        }

        if (valueId1!=null){
            for (String valueId : valueId1) {
                stringBuilder.append("&valueId=");
                stringBuilder.append(valueId);
            }
        }

        return stringBuilder.toString();
    }

    @RequestMapping("index")
    public String index(){
        return "index";
    }
}
