package cn.kanyun.upload.impl;

import cn.kanyun.upload.IUploaderFactory;
import cn.kanyun.upload.spi.UploaderFactoryBinder;
import cn.kanyun.upload.upyun.UpYunUploaderContext;
import com.UpYun;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Properties;

/**
 * 桥接器的原理其实就是在其jar包中存在StaticUploaderBinder.class,这样就可在运行时动态绑定底层的日志实现框架
 * 当项目中有多个桥接器时,其会找到多个StaticUploaderBinder.class
 *
 * @author Kanyun
 * @date on 2019/12/6  17:05
 */
@Slf4j
public class StaticUploaderBinder implements UploaderFactoryBinder {

    /**
     * 版本号,每个桥接器都会设定一个版本号,跟common-uploader-api中的版本号对应,
     * 该字段的值通常随着common-uploader-api版本号变化
     */
    public static final String REQUESTED_API_VERSION = "0.0.1";


    /**
     * Binder实例
     */
    private static final StaticUploaderBinder SINGLETON = new StaticUploaderBinder();

    /**
     * 初始化状态
     */
    private boolean initialized = false;



    private UpYunUploaderContext defaultUploaderContext = new UpYunUploaderContext();


    private StaticUploaderBinder() {
    }

    public static StaticUploaderBinder getSingleton() {
        log.info("又拍云StaticUploaderBinder.getSingleton()方法 被执行");
        return SINGLETON;
    }

    static {
        SINGLETON.init();
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
        return defaultUploaderContext.getClass().getName();
    }

    public static void h1() {

    }

    public static void h2() {

    }

    private void init() {
        log.info("common-uploader-upyun 又拍云桥接器开始进行初始化");
        String bucket = "";
        String userName = "";
        String password = "";
        try {
            URL url = Resources.getResource(CONFIG_FILE_NAME);
            Properties prop = new Properties();
            prop.load(url.openStream());
            userName = prop.getProperty("upyun.userName");
            password = prop.getProperty("upyun.password");
            bucket = prop.getProperty("upyun.bucketName");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        初始化 UpYun
        UpYun upyun = new UpYun("空间名称", "操作员名称", "操作员密码");
//        是否开启 debug 模式：默认不开启
        upyun.setDebug(true);
//        手动设置超时时间：默认为30秒
        upyun.setTimeout(60);
//        选择最优的接入点
        upyun.setApiDomain(UpYun.ED_AUTO);
        defaultUploaderContext.setUpyun(upyun);

    }
}


