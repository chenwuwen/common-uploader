package cn.kanyun.upload.helpers;

import cn.kanyun.upload.Uploader;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

/**
 * @author Kanyun
 * @date on 2019/12/6  17:52
 */
@Slf4j
public class SubstituteUploader implements Uploader {

    @Override
    public String getName() {
        return "SubstituteUploader";
    }

    @Override
    public Map push(String sourcePath, String targetPath) {
        log.info("SubstituteUploader执行");
        return Collections.EMPTY_MAP;
    }

    @Override
    public Map push(String sourcePath, String targetPath, PushCallBack callBack) {
        Random random=new Random();
        if (random.nextBoolean()) {
            callBack.onSuccess(sourcePath, targetPath);
        }else {
            callBack.onError(sourcePath, targetPath,new Exception(getName()+" Exception"));
        }
        return Collections.EMPTY_MAP;
    }
}
