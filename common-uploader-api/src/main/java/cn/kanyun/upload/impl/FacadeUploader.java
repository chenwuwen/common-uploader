package cn.kanyun.upload.impl;

import cn.kanyun.upload.helpers.PushCallBack;
import cn.kanyun.upload.Uploader;

import java.util.Collections;
import java.util.Map;

/**
 * 内部测试使用的Uploader
 *
 * @author Kanyun
 * @date on 2019/12/10  11:08
 */
public class FacadeUploader implements Uploader {
    @Override
    public String getName() {
        return "common-uploader门面模式内部的Uploader,正常使用不会被看到,只是做测试使用";
    }

    @Override
    public Map push(String sourcePath, String targetPath) {
        return Collections.singletonMap(sourcePath, targetPath);
    }

    @Override
    public Map push(String sourcePath, String targetPath, PushCallBack callBack) {
        double random = Math.random();
        if (random < 0.5) {
            callBack.onSuccess(sourcePath, targetPath);
        } else {
            callBack.onError(sourcePath, targetPath, new Exception());
        }
        return Collections.singletonMap(sourcePath, targetPath);
    }
}
