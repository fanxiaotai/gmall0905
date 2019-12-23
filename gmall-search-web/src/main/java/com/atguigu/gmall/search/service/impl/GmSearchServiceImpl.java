package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.bean.PmsSearchParam;
import com.atguigu.gmall.bean.PmsSearchSkuInfo;
import com.atguigu.gmall.bean.PmsSkuAttrValue;
import com.atguigu.gmall.search.service.GmSearchService;
import com.atguigu.gmall.search.util.SearchUtil;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: fanyitai
 * @Date: 2019/12/22 9:31
 * @Version 1.0
 */
@Service
public class GmSearchServiceImpl implements GmSearchService {

    @Autowired
    JestClient jestClient;

    @Autowired
    SearchUtil searchUtil;


    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) throws IOException {

        //根据条件从es中搜索
        return searchUtil.searchBool(pmsSearchParam);
    }
}
