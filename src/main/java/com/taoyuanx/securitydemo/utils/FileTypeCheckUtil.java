package com.taoyuanx.securitydemo.utils;

import cn.hutool.core.io.FileTypeUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.taoyuanx.securitydemo.config.GlobalConfig;

import java.io.InputStream;
import java.util.Set;

/**
 * @author dushitaoyuan
 * @desc 文件类型检测工具
 * @date 2019/8/30
 */
public class FileTypeCheckUtil {
    private static GlobalConfig globalConfig = SpringContextUtil.getBean(GlobalConfig.class);

    private static Set<String> ALLOWEXT = null;

    static {
        ALLOWEXT = Sets.newHashSet();
        Splitter.on(",").split(globalConfig.getAllowUploadExt()).forEach(ext -> {
            ALLOWEXT.add(ext.trim().toLowerCase());
        });
    }

    public static boolean allow(String fileExt) {
        if (ALLOWEXT == null || ALLOWEXT.isEmpty()) {
            return true;
        }
        return ALLOWEXT.contains(fileExt);
    }

    public static String getType(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".")+1);
    }

    public static String getRealType(InputStream inputStream) {
        return FileTypeUtil.getType(inputStream).toLowerCase();
    }


}
