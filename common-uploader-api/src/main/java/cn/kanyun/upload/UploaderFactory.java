package cn.kanyun.upload;

import cn.kanyun.upload.helpers.*;
import cn.kanyun.upload.impl.StaticUploaderBinder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author Kanyun
 * @date on 2019/12/6  15:03
 */
@Slf4j
public final class UploaderFactory {

    /**
     * 地址前缀【官网文档】
     */
    static final String CODES_PREFIX = "http://www.kanyun.common-uploader/codes.html";

    /**
     * 找不到Binder的说明地址
     */
    static final String NO_STATICUPLOADERBINDER_URL = CODES_PREFIX + "#StaticUploaderBinder";

    /**
     * 存在多个Binder的说明地址
     */
    static final String MULTIPLE_BINDINGS_URL = CODES_PREFIX + "#multiple_bindings";

    /**
     * 未初始化成功的URL
     */
    static final String UNSUCCESSFUL_INIT_URL = CODES_PREFIX + "#unsuccessfulInit";

    /**
     * 版本不匹配URL
     */
    static final String VERSION_MISMATCH = CODES_PREFIX + "#version_mismatch";

    /**
     * 类加载器会寻找这个类并进行加载,这也是实现类需要些的类
     */
    private static final String STATIC_UPLOADER_BINDER_PATH = "cn/kanyun/upload/impl/StaticUploaderBinder.class";

    /**
     * 未初始化成功消息提示
     */
    static final String UNSUCCESSFUL_INIT_MSG = "cn.kanyun.common-cn.kanyun.upload.UploaderFactory in failed state. Original exception was thrown EARLIER. See also " + UNSUCCESSFUL_INIT_URL;


    /**
     * 未初始化
     */
    static final int UNINITIALIZED = 0;
    /**
     * 正在进行初始化
     */
    static final int ONGOING_INITIALIZATION = 1;
    /**
     * 初始化失败
     */
    static final int FAILED_INITIALIZATION = 2;
    /**
     * 初始化成功
     */
    static final int SUCCESSFUL_INITIALIZATION = 3;

    /**
     * 未加载到STATIC_UPLOADER_BINDER_PATH常量所定义的资源,使用NOP_FALLBACK_FACTORY常量所定义的对象
     * 当做初始化状态,相当于初始化成功,但是实现是默认的实现(即：不做任何操作)
     */
    private static final int NOP_FALLBACK_INITIALIZATION = 4;

    /**
     * 初始化状态
     */
    static volatile int INITIALIZATION_STATE = UNINITIALIZED;

    static final SubstituteUploaderFactory SUBST_FACTORY = new SubstituteUploaderFactory();
    static final NOPUploaderFactory NOP_FALLBACK_FACTORY = new NOPUploaderFactory();

    /**
     * 版本兼容列表
     */
    static private final String[] API_COMPATIBILITY_LIST = new String[]{"0.0.1", "0.0.1"};

    private UploaderFactory() {
    }

    public static Uploader getUploader() {
        IUploaderFactory iUploaderFactory = getIUploaderFactory();
        return iUploaderFactory.getUploader();
    }


    /**
     * 执行初始化
     */
    private final static void performInitialization() {
        bind();
        if (INITIALIZATION_STATE == SUCCESSFUL_INITIALIZATION) {
            log.info("common-uploader执行初始化成功");
//            验证版本
            versionSanityCheck();
            log.info("common-uploader验证版本成功");
        }
    }

    /**
     * 验证桥接器binder的版本,需要与本类中定义的API_COMPATIBILITY_LIST常量对应
     */
    private static void versionSanityCheck() {

        try {
            String requested = StaticUploaderBinder.REQUESTED_API_VERSION;
            boolean match = false;
            for (String versionNumber : API_COMPATIBILITY_LIST) {
                if (requested.startsWith(versionNumber)) {
                    match = true;
                }
            }
            if (!match) {
                Util.report("The requested version " + requested + " by your common-cn.kanyun.upload binding is not compatible with "
                        + Arrays.asList(API_COMPATIBILITY_LIST).toString());
                Util.report("See " + VERSION_MISMATCH + " for further details.");
            }
        } catch (java.lang.NoSuchFieldError e) {
            log.error("匹配的桥接器未设置版本号");
        } catch (Throwable e) {
            Util.report("Unexpected problem occured during version sanity check", e);
        }

    }

    /**
     * 寻找并绑定,STATIC_UPLOADER_BINDER_PATH常量所定义的资源
     * 同时修改common-uploader初始化状态值
     */
    private final static void bind() {
        try {
//         通过类加载器加载 cn/kanyun/cn.kanyun.upload/impl/StaticUploaderBinder.class(该类需要在桥接器中去实现)
            Set<URL> staticUploaderBinderPathSet = findPossibleStaticUploaderBinderPathSet();
//        报告是否存在多个绑定
            reportMultipleBindingAmbiguity(staticUploaderBinderPathSet);
//        开始绑定(默认绑定的是staticUploaderBinderPathSet中的第一个)
            StaticUploaderBinder.getSingleton();

//        设置初始化状态
            INITIALIZATION_STATE = SUCCESSFUL_INITIALIZATION;
//        报告真实的绑定情况
            reportActualBinding(staticUploaderBinderPathSet);
            SUBST_FACTORY.clear();
        } catch (NoClassDefFoundError e) {
            String msg = e.getMessage();
            if (messageContainsCnKanyunUploaderImplStaticUploaderBinder(msg)) {
//            当在Classpath中找不到STATIC_UPLOADER_BINDER_PATH常量所定义的资源时(并且错误信息中包含common-uploader字样),将初始化状态置为NOP_FALLBACK_INITIALIZATION常量所定义的值
                INITIALIZATION_STATE = NOP_FALLBACK_INITIALIZATION;
                Util.report("Failed to load class \"StaticUploaderBinder\".");
                Util.report("Defaulting to no-operation (NOP) uploader implementation");
                Util.report("See " + NO_STATICUPLOADERBINDER_URL + " for further details.");
            } else {
                failedBinding(e);
                throw e;
            }
        }
    }

    public static IUploaderFactory getIUploaderFactory() {
//      如果是未初始化状态则进行初始化,否则判断状态值,并返回对应IUploaderFactory实例
        if (INITIALIZATION_STATE == UNINITIALIZED) {
            synchronized (UploaderFactory.class) {
                if (INITIALIZATION_STATE == UNINITIALIZED) {
                    log.info("common-uploader初始化,执行getIUploaderFactory()方法,由于是未初始化状态,因此需要修改初始化状态(同步代码块),同时开始寻找Classpath中存在的[{}]的资源", STATIC_UPLOADER_BINDER_PATH);
                    INITIALIZATION_STATE = ONGOING_INITIALIZATION;
                    performInitialization();
                }
            }
        }
        switch (INITIALIZATION_STATE) {
            case SUCCESSFUL_INITIALIZATION:
//                初始化成功
                return StaticUploaderBinder.getSingleton().getUploaderFactory();
            case NOP_FALLBACK_INITIALIZATION:
                log.warn("common-uploader未找到对应Binder,将返回空的Binder实现");
//                空的实现
                return NOP_FALLBACK_FACTORY;
            case FAILED_INITIALIZATION:
                log.error("common-uploader执行初始化失败");
//                初始化失败
                throw new IllegalStateException(UNSUCCESSFUL_INIT_MSG);
            case ONGOING_INITIALIZATION:
                // 支持重入行为,正在进行初始化,一般不会进入此步
                return SUBST_FACTORY;
            default:
                log.info("初始化状态,未找到对应操作,将抛出异常.....");
        }
        throw new IllegalStateException("Unreachable code");
    }

    /**
     * 打印真实绑定情况
     *
     * @param binderPathSet
     */
    private static void reportActualBinding(Set<URL> binderPathSet) {
        if (binderPathSet != null && isAmbiguousStaticUploaderBinderPathSet(binderPathSet)) {
            Util.report("实际绑定类型是： [" + StaticUploaderBinder.getSingleton().getUploaderFactoryClassStr() + "]");
        }
    }


    /**
     * 在classpath中寻找存在的binder
     *
     * @return
     */
    private static Set<URL> findPossibleStaticUploaderBinderPathSet() {
        log.info("开始查找资源：[{}]", STATIC_UPLOADER_BINDER_PATH);
//        使用LinkedHashSet，因为它保留插入顺序
        Set<URL> staticUploaderBinderPathSet = new LinkedHashSet();
        try {
//            获得当前类的ClassLoader
            ClassLoader uploaderFactoryClassLoader = UploaderFactory.class.getClassLoader();
            Enumeration<URL> paths;
//            加载资源详见 https://blog.csdn.net/qq_14957991/article/details/80673324
            if (uploaderFactoryClassLoader == null) {
                log.info("获取的ClassLoader为null,将使用ClassLoader.getSystemResources()查找资源");
                paths = ClassLoader.getSystemResources(STATIC_UPLOADER_BINDER_PATH);
            } else {
                log.info("获取的ClassLoader不为null,将使用ClassLoader.getResources()查找资源");
                paths = uploaderFactoryClassLoader.getResources(STATIC_UPLOADER_BINDER_PATH);
            }
            while (paths.hasMoreElements()) {
                URL path = paths.nextElement();
                staticUploaderBinderPathSet.add(path);
            }
        } catch (IOException ioe) {
            Util.report("Error getting resources from path", ioe);
        }
        return staticUploaderBinderPathSet;
    }

    /**
     * 报告存在多个绑定,如果存在
     *
     * @param binderPathSet
     */
    private static void reportMultipleBindingAmbiguity(Set<URL> binderPathSet) {
        if (isAmbiguousStaticUploaderBinderPathSet(binderPathSet)) {
            Util.report("Classpath中包含多个common-uploader绑定.");
            for (URL path : binderPathSet) {
//                URL是磁盘上的绝对路径,如果包含在jar文件中,则会显示jar文件的绝对路径,和对应类的全限定名
                Util.report("Found binding in [" + path + "]");
            }
            Util.report("See " + MULTIPLE_BINDINGS_URL + " for an explanation.");
        }
        log.info("common-cn.kanyun.upload binderPathSet size ：[{}]", binderPathSet.size());
    }


    /**
     * 是否存在多个绑定关系
     *
     * @param binderPathSet
     * @return
     */
    private static boolean isAmbiguousStaticUploaderBinderPathSet(Set<URL> binderPathSet) {
        return binderPathSet.size() > 1;
    }


    /**
     * 判断错误信息是否包含 common-uploader
     *
     * @param msg
     * @return
     */
    private static boolean messageContainsCnKanyunUploaderImplStaticUploaderBinder(String msg) {
        if (msg == null) {
            return false;
        }
        if (msg.contains("cn/kanyun/upload/impl/StaticUploaderBinder") || msg.contains("cn.kanyun.cn.kanyun.upload.impl.StaticUploaderBinder")) {
            return true;
        }

        return false;
    }


    /**
     * 绑定失败操作
     *
     * @param t
     */
    static void failedBinding(Throwable t) {
        INITIALIZATION_STATE = FAILED_INITIALIZATION;
        Util.report("Failed to instantiate common-uploader LoggerFactory", t);
    }
}
