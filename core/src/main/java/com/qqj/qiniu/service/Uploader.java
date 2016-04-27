package com.qqj.qiniu.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Uploader {

    private Logger logger = LoggerFactory.getLogger(Uploader.class);

    //设置好账号的ACCESS_KEY和SECRET_KEY
    private String ACCESS_KEY = "mzj4AOWNnJPnubctXx6Qz0tB4MtXvfbWf1ev9Ade";
    private String SECRET_KEY = "aimUOyvm8dvUQMDYhf0cXfKHcw6zgVtLq_UAEEv5";

    //要上传的空间
    private String bucketname = "qqjgirl";

    //上传到七牛后保存的文件名
    private String fileName;

    //上传文件的路径
    private String filePath;

    public Uploader(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    //创建上传对象
    UploadManager uploadManager = new UploadManager();

    // 覆盖上传
    public String getUpToken(){
        //<bucket>:<key>，表示只允许用户上传指定key的文件。在这种格式下文件默认允许“修改”，已存在同名资源则会被本次覆盖。
        //如果希望只能上传指定key的文件，并且不允许修改，那么可以将下面的 insertOnly 属性值设为 1。
        //第三个参数是token的过期时间
        return auth.uploadToken(bucketname, fileName, 3600, new StringMap());

    }

    public void upload() throws IOException{
        try {
            //调用put方法上传，这里指定的key和上传策略中的key要一致
            Response res = uploadManager.put(filePath, fileName, getUpToken());
            //打印返回的信息
            logger.info(res.bodyString());
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            logger.error(e.getMessage(), e);
            try {
                //响应的文本信息
                logger.info(r.bodyString());
            } catch (QiniuException e1) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static void main(String args[]) throws IOException{
        new Uploader("/Users/wangguodong/Downloads/1.jpg", "1.png").upload();
    }
}


