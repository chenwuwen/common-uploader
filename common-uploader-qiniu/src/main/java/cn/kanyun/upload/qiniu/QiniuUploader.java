package cn.kanyun.upload.qiniu;

import cn.kanyun.upload.helpers.PushCallBack;
import cn.kanyun.upload.Uploader;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kanyun
 * @date on 2019/12/10  10:18
 */
@Slf4j
public class QiniuUploader implements Uploader {

    private String upToken;
    private UploadManager uploadManager;

    public QiniuUploader(String upToken, UploadManager uploadManager) {
        this.upToken = upToken;
        this.uploadManager = uploadManager;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public Map push(String sourcePath, String targetPath) {
        Map ret = new HashMap();
        try {
            Response response = uploadManager.put(sourcePath, targetPath, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = response.jsonToObject(DefaultPutRet.class);
            ret.put("key", putRet.key);
            ret.put("hash", putRet.hash);
        } catch (QiniuException e) {
            e.printStackTrace();
            log.error("[{}] 上传出错 [{}]", this.getClass().getName(), e.getMessage());
            ret.put("key", null);
        }
        return ret;
    }

    @Override
    public Map push(String sourcePath, String targetPath, PushCallBack callBack) {
        Map ret = new HashMap();
        try {
            Response response = uploadManager.put(sourcePath, targetPath, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = response.jsonToObject(DefaultPutRet.class);
            ret.put("key", putRet.key);
            ret.put("hash", putRet.hash);
            callBack.onSuccess(sourcePath, targetPath);
        } catch (QiniuException e) {
            callBack.onError(sourcePath, targetPath, e);
            ret.put("key", null);
        }
        return ret;
    }
}
