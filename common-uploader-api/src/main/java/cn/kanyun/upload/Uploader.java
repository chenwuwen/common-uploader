package cn.kanyun.upload;

import cn.kanyun.upload.helpers.PushCallBack;

import java.util.Map;

/**
 * Uploader 接口,定义Uploader存在哪些方法
 *
 * @author Kanyun
 * @date on 2019/12/6  15:40
 */
public interface Uploader {


    /**
     * 返回实现此Uploader的实例
     *
     * @return
     */
    String getName();

    /**
     * 上传操作
     *
     * @return
     */
    Map push(String sourcePath, String targetPath);

    /**
     * 上传完成的回调
     *
     * @param sourcePath
     * @param targetPath
     * @param callBack
     * @return
     */
    Map push(String sourcePath, String targetPath, PushCallBack callBack);
}
