package cn.kanyun.upload.impl;

import cn.kanyun.upload.IUploaderFactory;
import cn.kanyun.upload.Uploader;

/**
 * @author Kanyun
 * @date on 2019/12/10  11:07
 */
class UploaderContext implements IUploaderFactory {

    @Override
    public Uploader getUploader() {
        Uploader uploader = new FacadeUploader();
        return uploader;
    }
}
