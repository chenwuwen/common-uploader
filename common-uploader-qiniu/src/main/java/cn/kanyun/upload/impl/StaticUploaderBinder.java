package cn.kanyun.upload.impl;

import cn.kanyun.upload.IUploaderFactory;
import cn.kanyun.upload.qiniu.QiniuUploaderContext;
import cn.kanyun.upload.spi.UploaderFactoryBinder;
import com.google.common.io.Resources;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
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
     * 初始化状态
     */
    private boolean initialized = false;

    /**
     * Binder实例
     */
    private static final StaticUploaderBinder SINGLETON = new StaticUploaderBinder();

    private QiniuUploaderContext defaultUploaderContext = new QiniuUploaderContext();

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
        return defaultUploaderContext.getClass().getName();
    }


    public static StaticUploaderBinder getSingleton() {
        log.info("七牛云StaticUploaderBinder.getSingleton()方法 被执行");
        return SINGLETON;
    }

    private void init() {
        log.info("common-uploader-qiniu 七牛云桥接器开始进行初始化");
//      todo 这里可以读取classpath下特殊的storage.properties文件
        String fileName = CONFIG_FILE_NAME;
        String accessKey = "ChZnwyjHOLonZxL1AMHdv-SGaFE8C9dpjUiEq1Pn";
        String secretKey = "9JuzUfV1RNAtEhDZ3vEHFR2x2ClJT36iKAp5XO0x";
        String bucket = "hanlang";
        try {
            URL url = Resources.getResource(fileName);
            Properties prop = new Properties();
            prop.load(url.openStream());
            accessKey = prop.getProperty("qiniu.accessKey");
            secretKey = prop.getProperty("qiniu.secretKey");
            bucket = prop.getProperty("qiniu.bucket");
        } catch (Exception e) {
            log.error("[{}]类, Classpath下[{}] 文件,使用出错", this.getClass().getName(), fileName);
            e.printStackTrace();
        }
//        构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huabei());
//        其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
//       生成上传凭证，然后准备上传
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        defaultUploaderContext.setUploadManager(uploadManager);
        defaultUploaderContext.setUpToken(upToken);
    }
}


