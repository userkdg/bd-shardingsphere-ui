package org.apache.shardingsphere.ui.servcie;

import cn.com.bluemoon.shardingsphere.custom.shuffle.base.GlobalConfig;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * @author Jarod.Kong
 */
public interface EncryptShuffleService {
    /**
     * 提交洗数作业
     *
     * @param schema                      中间件schema名称
     * @param dbType                      数据库名称 eg: mysql、postgresql
     * @param tableNames                  加密表名 为空则对schema下所有加密表进行洗数，否则，对指定表洗数
     * @param tableNameAndIncrFieldPreVal 附加，定制表的增量值
     * @param withIncrFieldExtractOnce    针对增量抽取，是否只抽取一次
     */
    void submitJob(String schema,
                   String dbType,
                   @Nullable Set<String> ignoreTableNames,
                   @Nullable Set<String> tableNames,
                   @Nullable Map<String, String> tableNameAndIncrFieldPreVal,
                   boolean withIncrFieldExtractOnce);

    default void submitJob(String schema,
                           @Nullable Set<String> ignoreTableNames,
                           @Nullable Set<String> tableNames,
                           @Nullable Map<String, String> tableNameAndIncrFieldPreVal) {
        submitJob(schema, GlobalConfig.MYSQL, ignoreTableNames, tableNames,
                tableNameAndIncrFieldPreVal, false);
    }
}
