package cn.kanyun.upload.helpers;

import cn.kanyun.upload.IUploaderFactory;
import cn.kanyun.upload.Uploader;

import java.util.HashMap;
import java.util.Map;

/**
 * SubstituteUploader 工厂
 *
 * @author Kanyun
 * @date on 2019/12/6  17:52
 */
public class SubstituteUploaderFactory implements IUploaderFactory {

    final Map<String, SubstituteUploader> uploaderMap = new HashMap();


    @Override
    public Uploader getUploader() {
        SubstituteUploader uploader = uploaderMap.get("test");
        if (uploader == null) {
            uploader = new SubstituteUploader();
            uploaderMap.put("test", uploader);
        }
        return uploader;
    }


    public void clear() {
        uploaderMap.clear();
    }
}
