package cn.kanyun.upload.helpers;

import cn.kanyun.upload.IUploaderFactory;
import cn.kanyun.upload.Uploader;

/**
 * NOPUploaderFactory是一个简单的实现的 {@link IUploaderFactory}
 * 它总是返回NOPUploader的唯一实例。
 *
 * @author Kanyun
 * @date on 2019/12/6  15:43
 */
public class NOPUploaderFactory implements IUploaderFactory {

    @Override
    public Uploader getUploader() {
        return NOPUploader.NOP_UPLOADER;
    }
}
