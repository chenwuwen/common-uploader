package cn.kanyun.upload;

/**
 * @author Kanyun
 * @date on 2019/12/6  15:43
 */
public interface IUploaderFactory {

    /**
     * 返回Uploader
     * @return
     */
    Uploader getUploader();
}
