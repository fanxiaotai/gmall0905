package com.atguigu.gmall.user.service;

import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

/**
 * @Author: fanyitai
 * @Date: 2019/12/18 10:49
 * @Version 1.0
 */
public interface UserService {


    List<UmsMember> getAllUser();


    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);
}
