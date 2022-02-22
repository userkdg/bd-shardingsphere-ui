package org.apache.shardingsphere.ui.servcie;

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
     * @param tableNames                  加密表名 为空则对schema下所有加密表进行洗数，否则，对指定表洗数
     * @param tableNameAndIncrFieldPreVal 附加，定制表的增量值
     */
    void submitJob(String schema,
                   @Nullable Set<String> ignoreTableNames,
                   @Nullable Set<String> tableNames,
                   @Nullable Map<String, String> tableNameAndIncrFieldPreVal);
}
