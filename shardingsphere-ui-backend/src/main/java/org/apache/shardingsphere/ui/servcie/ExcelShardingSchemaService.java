package org.apache.shardingsphere.ui.servcie;

import cn.com.bluemoon.daps.system.entity.DapSystemDatasourceEnvironment;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.web.response.ResponseResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ExcelShardingSchemaService {

    ResponseResult<Boolean> CheckShardingSchemaRule(Map<String, List<DapSystemDatasourceEnvironment>> map);

    Collection<String> loadAllSchemaName();
}
