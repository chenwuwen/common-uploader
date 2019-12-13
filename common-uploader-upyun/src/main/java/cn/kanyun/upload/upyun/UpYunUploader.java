package cn.kanyun.upload.upyun;

import cn.kanyun.upload.helpers.PushCallBack;
import cn.kanyun.upload.Uploader;
import com.UpYun;
import com.upyun.UpException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kanyun
 * @date on 2019/12/10  10:18
 */
@Slf4j
public class UpYunUploader implements Uploader {

    private UpYun upyun;

    public UpYunUploader(UpYun upyun) {
        this.upyun = upyun;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public Map push(String sourcePath, String targetPath) {
        Map map = new HashMap();
        try {
            boolean flag = upyun.writeFile(sourcePath, targetPath);
            if (flag) {
                map.put("key", targetPath);
            } else {
                map.put("key", null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            map.put("key", null);
        } catch (UpException e) {
            e.printStackTrace();
            map.put("key", null);
        }
        return map;
    }

    @Override
    public Map push(String sourcePath, String targetPath, PushCallBack callBack) {
        Map map = new HashMap();
        try {
            boolean flag = upyun.writeFile(sourcePath, targetPath);
            if (flag) {
                map.put("key", targetPath);
            } else {
                map.put("key", null);
            }
            callBack.onSuccess(sourcePath, targetPath);
        } catch (IOException e) {
            e.printStackTrace();
            callBack.onError(sourcePath, targetPath, e);
            map.put("key", null);
        } catch (UpException e) {
            callBack.onError(sourcePath, targetPath, e);
            map.put("key", null);
        }
        return map;
    }
}
