package io.github.elongdeo.mybatis.util;

import io.github.elongdeo.mybatis.core.BaseDO;

/**
 * @author dingyinlong
 * @date 2020年05月28日17:17:56
 **/
public class BaseDoUtils {
    public static <P> void clearBaseDo(BaseDO<P> baseDO, boolean clearId, boolean clearGmtCreate, boolean clearCreator,
                                       boolean clearGmtModified, boolean clearModifier) {
        if (baseDO != null) {
            if (clearId) {
                baseDO.setId(null);
            }
            if (clearGmtCreate) {
                baseDO.setGmtCreate(null);
            }
            if (clearCreator) {
                baseDO.setCreator(null);
            }
            if (clearGmtModified) {
                baseDO.setGmtModified(null);
            }
            if (clearModifier) {
                baseDO.setModifier(null);
            }
        }
    }

    public static void main(String[] args) {
    }
}