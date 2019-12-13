package cn.kanyun.upload.upyun;

import cn.kanyun.upload.IUploaderFactory;
import cn.kanyun.upload.Uploader;
import com.UpYun;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kanyun
 * @date on 2019/12/10  14:49
 */
@Data
@Slf4j
public class UpYunUploaderContext implements IUploaderFactory {

    private UpYun upyun;

    @Override
    public Uploader getUploader() {
        Uploader uploader = new UpYunUploader(this.getUpyun());
        return uploader;
    }
}
