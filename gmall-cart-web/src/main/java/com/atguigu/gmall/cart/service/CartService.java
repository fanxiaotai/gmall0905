package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.bean.OmsCartItem;

import java.util.List;

/**
 * @Author: fanyitai
 * @Date: 2019/12/22 22:03
 * @Version 1.0
 */
public interface CartService {
    OmsCartItem ifCartBxistByUser(String memberId, String skuId);

    void addCart(OmsCartItem omsCartItem);

    void updateCart(OmsCartItem omsCartItemDB);

    void flushCartCache(String memberId);

    List<OmsCartItem> cartList(String userId,String skuId);

    void checkCart(OmsCartItem omsCartItem);
}
