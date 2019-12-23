package com.atguigu.gmall.search.service;

import com.atguigu.gmall.bean.PmsSearchParam;
import com.atguigu.gmall.bean.PmsSearchSkuInfo;

import java.io.IOException;
import java.util.List;

/**
 * @Author: fanyitai
 * @Date: 2019/12/22 9:31
 * @Version 1.0
 */
public interface GmSearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) throws IOException;
}
