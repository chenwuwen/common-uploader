package cn.kanyun.upload.spi;

import cn.kanyun.upload.IUploaderFactory;

/**
 * StaticUploadBinder 需要实现此接口
 * Binder产生工厂,有Binder生产的Factory,再生产Uploader
 * @author Kanyun
 * @date on 2019/12/6  16:33
 */
public interface UploaderFactoryBinder {

    /**
     * 配置文件名称
     * 启动时如果加入了桥接器,将会自动读取Classpath下的CONFIG_FILE_NAME常量定义的文件
     */
    String CONFIG_FILE_NAME = "storage.properties";

    /**
     * 得到UpLoaderFactory实例
     * 有了Uploader的工厂类,就可以阐述Uploader实例
     * @return
     */
    IUploaderFactory getUploaderFactory();

    /**
     * 得到UpLoaderFactory 类的名称
     *
     * @return
     */
    String getUploaderFactoryClassStr();
}
