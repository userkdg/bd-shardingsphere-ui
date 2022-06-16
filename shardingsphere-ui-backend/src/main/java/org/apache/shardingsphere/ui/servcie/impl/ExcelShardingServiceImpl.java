package org.apache.shardingsphere.ui.servcie.impl;

import cn.com.bluemoon.daps.system.entity.DapSystemDatasourceEnvironment;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.infra.metadata.schema.ShardingSphereSchema;
import org.apache.shardingsphere.mode.metadata.persist.node.SchemaMetaDataNode;
import org.apache.shardingsphere.mode.persist.PersistRepository;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.apache.shardingsphere.ui.servcie.ExcelShardingSchemaService;
import org.apache.shardingsphere.ui.util.ConfigurationYamlConverter;
import org.apache.shardingsphere.ui.util.ImportEncryptionRuleUtils;
import org.apache.shardingsphere.ui.util.jdbc.ConnectionProxyUtils;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelShardingServiceImpl implements ExcelShardingSchemaService {

    @Autowired
    ConfigCenterService configCenterService;

    @Override
    public void ruleImport(String schemaName, List<SensitiveInformation> infoList, Map<String, DataSourceConfiguration> maps) {

        // 获取所有的schema名
        Collection<String> schemaList = loadAllSchemaName();
        List<SensitiveInformation> list = new ArrayList<>(infoList);
        // 封装规则结果集
        List<RuleConfiguration> ruleConfigurations = ImportEncryptionRuleUtils.transToRuleConfiguration(list);
        // 判断schema是否存在,存在的话覆盖规则
        if(schemaList.contains(schemaName)){
            configCenterService.getActivatedMetadataService().getSchemaRuleService().persist(schemaName, ruleConfigurations, true);
        }else {
            configCenterService.getActivatedMetadataService().getSchemaMetaDataService().persist(schemaName, new ShardingSphereSchema());
            configCenterService.getActivatedMetadataService().getSchemaRuleService().persist(schemaName, ruleConfigurations, true);
            configCenterService.getActivatedMetadataService().getDataSourceService().persist(schemaName, maps);
            addRuleConfig(schemaName);
        }
    }

    @Override
    public ResponseResult<Boolean> CheckShardingSchemaRule(Map<String, List<DapSystemDatasourceEnvironment>> map) {

        String schemaName = map.keySet().stream().findFirst().get();
        // 检查数据源是否能进行连接
        List<DapSystemDatasourceEnvironment> environments = map.get(schemaName);
        for (DapSystemDatasourceEnvironment environment : environments) {
            Boolean flag = ConnectionProxyUtils.connectTest(environment);
            if(!flag){
                return ResponseResult.error(String.format("%s:%s数据源连接失败", environment.getHost(),environment.getPort()));
            }
        }
        return ResponseResult.ok(true);
    }

    @Override
    public Collection<String> loadAllSchemaName() {
        return configCenterService.getActivatedMetadataService().getSchemaMetaDataService().loadAllNames();
    }

    @Override
    public void addRuleConfig(String schemaName) {

        PersistRepository repository = configCenterService.getActivatedMetadataService().getRepository();
        String configData = repository.get(SchemaMetaDataNode.getRulePath(schemaName));
        Collection<RuleConfiguration> ruleConfigurations = checkRuleConfiguration(configData);
        configCenterService.getActivatedMetadataService().getSchemaRuleService().persist(schemaName, ruleConfigurations, true);

    }

    private Collection<RuleConfiguration> checkRuleConfiguration(final String configData) {
        try {
            if (configData.contains("encryptors:\n")) {
                return ConfigurationYamlConverter.loadEncryptRuleConfiguration(configData);
            } else {
                return ConfigurationYamlConverter.loadMasterSlaveRuleConfiguration(configData);
            }
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            throw new IllegalArgumentException("rule configuration is invalid.");
        }
    }
}
