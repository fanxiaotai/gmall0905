package com.atguigu.gmall.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.netflix.client.ClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @Author: fanyitai
 * @Date: 2019/12/19 16:01
 * @Version 1.0
 */
public class GmallUtil {

    private static String endpoint="http://oss-cn-shenzhen.aliyuncs.com";
    private static String accessKeyId="LTAI4FnX7jfHNX7R2HEmXKCX";
    private static String accessKeySecret="VGTUe05I8rQkqMKQWdOVfjGFy5mYNW";
    private static String bucketName = "crowd191120";
    private static String ossProjectParentFolder = "project";
    private static String bucketDomain="http://crowd191120.oss-cn-shenzhen.aliyuncs.com";

    /**
     * 生成文件名
     * @param originalFileName 原始文件名
     * @return
     */
    public static String generateFileName(String originalFileName) {

        // 截取扩展名部分
        String extensibleName = "";

        if(originalFileName.contains(".")) {
            extensibleName = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        return UUID.randomUUID().toString().replaceAll("-", "")+extensibleName;
    }

    /**
     * 根据日期生成目录名称
     * @param ossProjectParentFolder
     * @return
     */
    public static String generateFolderNameByDate(String ossProjectParentFolder) {

        return ossProjectParentFolder + "/" + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    /**
     * 上传单个文件到OSS
     *
     * @param endpoint
     * @param accessKeyId
     * @param accessKeySecret
     * @param fileName
     * @param folderName
     * @param bucketName
     * @param inputStream
     */
    public static void uploadSingleFile(String endpoint, String accessKeyId, String accessKeySecret, String fileName,
                                        String folderName, String bucketName, InputStream inputStream) {
        try {

            // 创建OSSClient实例。
            OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

            // 存入对象的名称=目录名称+"/"+文件名
            String objectName = folderName + "/" + fileName;

            ossClient.putObject(bucketName, objectName, inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();
        } catch (OSSException e) {
            e.printStackTrace();

            throw new RuntimeException(e.getMessage());
        }
    }

    public static String uploadImage(MultipartFile multipartFile)throws Exception{
        String name = multipartFile.getOriginalFilename();
        String fileName = GmallUtil.generateFileName(name);
        String folderNameByDate = GmallUtil.generateFolderNameByDate(ossProjectParentFolder);
        InputStream inputStream = multipartFile.getInputStream();
        GmallUtil.uploadSingleFile(endpoint,accessKeyId,accessKeySecret,fileName,folderNameByDate,bucketName,inputStream);
        return bucketDomain+ "/" + folderNameByDate + "/" + fileName;
    }
}
