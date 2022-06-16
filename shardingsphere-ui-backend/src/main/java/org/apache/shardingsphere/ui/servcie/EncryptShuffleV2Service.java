package org.apache.shardingsphere.ui.servcie;

import cn.com.bluemoon.shardingsphere.custom.shuffle.base.GlobalConfig;
import org.apache.shardingsphere.ui.common.dto.TableExtractDefine;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Jarod.Kong
 */
public interface EncryptShuffleV2Service {
    /**
     * 提交洗数作业
     *
     * @param schema              中间件schema名称
     * @param dbType              数据库名称 eg: mysql、postgresql
     * @param tableNames          加密表名 为空则对schema下所有加密表进行洗数，否则，对指定表洗数
     * @param tableExtractDefines 自定义抽取方式，可空
     */
    void submitJob(String schema,
                   String dbType,
                   @Nullable Set<String> tableNames,
                   @Nullable List<TableExtractDefine> tableExtractDefines);

    /**
     * 全外部自定义
     */
    void submitJob(GlobalConfig globalConfig);

    default void submitJob(String schema,
                           @Nullable Set<String> tableNames) {
        submitJob(schema, GlobalConfig.MYSQL, tableNames, Collections.emptyList());
    }

}
