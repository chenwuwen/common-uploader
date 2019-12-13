package cn.kanyun.upload.impl;


import cn.kanyun.upload.IUploaderFactory;
import cn.kanyun.upload.spi.UploaderFactoryBinder;

import java.net.URI;


/**
 * 这个类需要在桥接器中编写,而不是写在这个依赖包中,之所以写在了这里,是为了该依赖可以通过编译,实际打包
 * 实惠排除这个类的class文件的
 * 桥接器的原理其实就是在其jar包中存在StaticUploaderBinder.class,这样就可在运行时动态绑定底层的日志实现框架
 * 当项目中有多个桥接器时,其会找到多个StaticUploaderBinder.class
 *
 * @author Kanyun
 * @date on 2019/12/6  17:05
 */
public class StaticUploaderBinder implements UploaderFactoryBinder {

    /**
     * 版本号,每个桥接器都会设定一个版本号,跟common-uploader中的版本号对应,
     * 该字段的值通常随着common-uploader版本号变化
     */
    public static final String REQUESTED_API_VERSION = "0.0.1";

    /**
     * 初始化状态
     */
    private boolean initialized = false;

    /**
     * Binder实例
     */
    private static final StaticUploaderBinder SINGLETON = new StaticUploaderBinder();

    private UploaderContext defaultUploaderContext = new UploaderContext();

    static {
        SINGLETON.init();
    }


    private StaticUploaderBinder() {
    }

    @Override
    public IUploaderFactory getUploaderFactory() {
        if (!initialized) {
            return defaultUploaderContext;
        }
        return null;
    }

    @Override
    public String getUploaderFactoryClassStr() {
        return "common-uploader门面模式内部的UploaderFactory->UploaderContext,正常使用不会被看到,只是做测试使用";
    }


    public static StaticUploaderBinder getSingleton() {
        return SINGLETON;
    }

    private void init() {
        System.out.println("StaticUploaderBinder初始化进行一些操作");
    }
}



