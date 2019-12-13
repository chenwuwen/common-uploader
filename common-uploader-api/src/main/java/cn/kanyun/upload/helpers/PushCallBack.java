package cn.kanyun.upload.helpers;

/**
 * 上传结束回调接口
 *
 * @author Kanyun
 * @date on 2019/12/13  9:19
 */
public interface PushCallBack {

    /**
     * 上传成功结束处理函数
     * @param sourcePath
     * @param targetPath
     */
    void onSuccess(String sourcePath, String targetPath);


    /**
     * 上传失败结束处理函数
     *
     * @param sourcePath
     * @param targetPath
     * @param throwable
     */
    void onError(String sourcePath, String targetPath, Throwable throwable);
}
