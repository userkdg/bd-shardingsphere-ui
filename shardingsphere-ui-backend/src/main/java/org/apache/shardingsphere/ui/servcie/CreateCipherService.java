package org.apache.shardingsphere.ui.servcie;

import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.ui.web.response.ResponseResult;

import java.util.List;
import java.util.Map;

public interface CreateCipherService {

    ResponseResult<String> operateDbField(String schemaName, Boolean isCipher);

    void getMetaInfo(DataSourceConfiguration dataSourceConfiguration, String names, Map<QueryMetaDataRequest, List<TableInfoVO>> map);

}
