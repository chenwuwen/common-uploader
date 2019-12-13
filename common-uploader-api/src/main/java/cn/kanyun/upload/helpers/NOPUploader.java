package cn.kanyun.upload.helpers;

import cn.kanyun.upload.Uploader;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

/**
 * 一个无操作的Uploader 实现自 {@link Uploader}
 * @author Kanyun
 * @date on 2019/12/9  11:16
 */
@Slf4j
public class NOPUploader implements Uploader {

    /**
     * 唯一的实例
     */
    protected NOPUploader() {
    }

    public static final NOPUploader NOP_UPLOADER = new NOPUploader();

    /**
     * 总是返回NOP
     *
     * @return
     */
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public Map push(String sourcePath, String targetPath) {
        log.warn("[{}]没有默认的Uploader实现,将使用NOPUploader实例,不进行任何操作,将返回空集合",LocalDateTime.now());
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
