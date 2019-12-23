package com.atguigu.gmall.cart.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.client.ManageClient;
import com.atguigu.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: fanyitai
 * @Date: 2019/12/22 17:14
 * @Version 1.0
 */
@Controller
@CrossOrigin
public class CartController {

    @Autowired
    ManageClient manageClient;

    @Autowired
    CartService cartService;

    @RequestMapping("checkCart")
    public String checkCart(String isChecked,String skuId,HttpServletRequest request, HttpServletResponse response,ModelMap modelMap){

        String memberId = "1";
        //调用服务，修改状态
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setIsChecked(isChecked);
        cartService.checkCart(omsCartItem);

        //将最新的数据从缓存中查出，渲染给页面
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId,null);
        modelMap.put("cartList",omsCartItems);
        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        modelMap.put("totalAmount",totalAmount);
        return "cartListInner";
    }

    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, HttpServletResponse response,ModelMap modelMap){

        List<OmsCartItem> omsCartItems = new ArrayList<>();

        String memberId = "1";

        if (StringUtils.isNotBlank(memberId)){
            //登陆，查询db
            omsCartItems = cartService.cartList(memberId,null);
        }else {
            //没有登陆，查询cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)){
                omsCartItems = JSON.parseArray(cartListCookie,OmsCartItem.class);
            }


        }

        modelMap.put("cartList",omsCartItems);

        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        modelMap.put("totalAmount",totalAmount);

        return "cartList";
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");

        for (OmsCartItem omsCartItem : omsCartItems) {
            String isChecked = omsCartItem.getIsChecked();
            if (isChecked.equals("1")){
                totalAmount = totalAmount.add(omsCartItem.getTotalPrice());
            }
        }
        return totalAmount;
    }

    @RequestMapping("addToCart")
    public String addToCart(String skuId, int quantity, HttpServletRequest request, HttpServletResponse response){

        //调用商品服务查询商品信息
        PmsSkuInfo skuInfo = manageClient.getSkuById(skuId);

        //将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("1111111");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(new BigDecimal(quantity));
        omsCartItem.setMemberNickname("小孩");
        if (skuInfo.getSkuSaleAttrValueList().size()>=2){
            omsCartItem.setSp1(skuInfo.getSkuSaleAttrValueList().get(0).getSaleAttrValueName());
            omsCartItem.setSp2(skuInfo.getSkuSaleAttrValueList().get(1).getSaleAttrValueName());
            omsCartItem.setSp3("");
        }

        //判断用户是否登陆
        String memberId = "1";
        List<OmsCartItem> omsCartItems = new ArrayList<>();

        if (StringUtils.isBlank(memberId)){
            //用户没有登陆


            //从cookie中获取原有购物车的数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isBlank(cartListCookie)){
                omsCartItems.add(omsCartItem);
            }else {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                //判断添加商品在购物车是否已经存在，存在：在原商品的数量上加quantity，不存在，往购物车中添加新的商品
                if_cart_exist(omsCartItems,omsCartItem,quantity);
            }

            //更新cookie
            CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(omsCartItems),60*60*72,true);
        }else {
            //用户有登陆
            OmsCartItem omsCartItemDB = cartService.ifCartBxistByUser(memberId,skuId);

            if (omsCartItemDB==null){
                //该用户没有添加过当前商品
                omsCartItem.setMemberId(memberId);
                cartService.addCart(omsCartItem);
            }else {
                //该用户添加过当前商品
                omsCartItemDB.setQuantity(omsCartItemDB.getQuantity().add(omsCartItem.getQuantity()));
                cartService.updateCart(omsCartItemDB);
            }
        }

        return "redirect:/success.html";
    }

    private void if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem,int quantity) {

        for (OmsCartItem cartItem : omsCartItems) {
            String productSkuId = cartItem.getProductSkuId();
            if (productSkuId.equals(omsCartItem.getProductSkuId())){
                //之前添加过，更新数量
                cartItem.setQuantity(cartItem.getQuantity().add(new BigDecimal(quantity)));
            }else {
                //之前没有添加过，新增当前的购物车
                omsCartItems.add(omsCartItem);
            }
        }
        //return omsCartItems;
    }
}
