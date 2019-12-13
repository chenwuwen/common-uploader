package cn.kanyun.upload.qiniu;

import cn.kanyun.upload.IUploaderFactory;
import cn.kanyun.upload.Uploader;
import com.qiniu.storage.UploadManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kanyun
 * @date on 2019/12/10  14:49
 */
@Data
@Slf4j
public class QiniuUploaderContext implements IUploaderFactory {

    private String upToken;
    private UploadManager uploadManager;

    @Override
    public Uploader getUploader() {
        log.info("QiniuUploaderContext执行getUploader()方法,upToken : [{}]  uploadManager : [{}]", this.upToken, this.uploadManager);
        Uploader uploader = new QiniuUploader(this.upToken, this.uploadManager);
        return uploader;
    }
}
