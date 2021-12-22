package org.apache.shardingsphere.ui.servcie;

import cn.com.bluemoon.daps.system.entity.DapSystemDatasourceEnvironment;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.web.response.ResponseResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ExcelShardingSchemaService {

    void ruleImport(String schemaName, List<SensitiveInformation> list, Map<String, DataSourceConfiguration> maps);

    ResponseResult<Boolean> CheckShardingSchemaRule(Map<String, List<DapSystemDatasourceEnvironment>> map);

    Collection<String> loadAllSchemaName();

    void addRuleConfig(String rule);
}
