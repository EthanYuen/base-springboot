package org.ethanyuen.springboot.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.ethanyuen.springboot.module.LogModule;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class QiNiuFileUpUtil {

    /**
     * 基本配置-从七牛管理后台拿到
     */
    //设置好账号的ACCESS_KEY和SECRET_KEY
    @Value("${qiniu-accessKey}")
    public static String ACCESS_KEY;
    @Value("${qiniu-secretKey}")
    public static String SECRET_KEY;
    //要上传的空间名--
    @Value("${qiniu-bucketName}")
    String bucketname ;

    /**指定保存到七牛的文件名--同名上传会报错  {"error":"file exists"}*/
    /**
     * {"hash":"FrQF5eX_kNsNKwgGNeJ4TbBA0Xzr","key":"aa1.jpg"} 正常返回 key为七牛空间地址 http:/xxxx.com/aa1.jpg
     */

    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    //创建上传对象
    UploadManager uploadManager = new UploadManager(new Configuration(Zone.zone2()));

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken(String fileName) {
        return auth.uploadToken(bucketname,fileName);
    }

    public void upload(String filePath,String fileName) throws IOException {
        try {
            Response res = uploadManager.put(filePath+fileName, fileName, getUpToken(fileName));
        } catch (QiniuException e) {
            LogModule.info(e.response.bodyString());
        }
    }
}
