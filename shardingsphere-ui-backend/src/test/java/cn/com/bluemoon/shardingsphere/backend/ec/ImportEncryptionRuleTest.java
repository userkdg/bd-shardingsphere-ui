package cn.com.bluemoon.shardingsphere.backend.ec;

import cn.com.bluemoon.shardingsphere.backend.util.BaseTest;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class ImportEncryptionRuleTest extends BaseTest {

    @Autowired
    private ConfigCenterService configCenterService;


    @Test
    public void createField() {
        File file = new File("D:\\jarodkong\\bluemoonCode\\bd-shardingsphere-ui\\shardingsphere-ui-backend\\系统敏感信息采集表-公共.xlsx");
        List<SensitiveInformation> list = EasyExcel.read(file).head(SensitiveInformation.class).sheet().doReadSync();
        List<String> sqls = list.stream().map(s -> {
            return String.format("alter table %s add column %s_cipher varchar(256) after %s", s.getTableName(), s.getFieldName(), s.getFieldName());
        }).collect(Collectors.toList());

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.243.34:23308/ec_order_db?user=root&password=root&serverTimezone=UTC&useSSL=false")) {
            for (String sql : sqls) {
                PreparedStatement ps = conn.prepareStatement(sql);
                boolean execute = ps.execute();
                log.info("创建{},status:{}", sql, execute);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @Test
    public void main1() {
        File file = new File("D:\\jarodkong\\bluemoonCode\\bd-shardingsphere-ui\\shardingsphere-ui-backend\\系统敏感信息采集表-公共.xlsx");
        List<SensitiveInformation> list = EasyExcel.read(file).head(SensitiveInformation.class).sheet().doReadSync();
        for (SensitiveInformation data : list) {
            System.out.println(data);
        }
        // 创建字段

        // 规则
        Map<String, List<SensitiveInformation>> collect = list.stream().collect(Collectors.groupingBy(SensitiveInformation::getTableName));
        List<EncryptTableRuleConfiguration> tableRuleConfigurations = new ArrayList<>();
        Map<String, ShardingSphereAlgorithmConfiguration> map = new HashMap<>();
        for (Map.Entry<String, List<SensitiveInformation>> entry : collect.entrySet()) {
            List<SensitiveInformation> value = entry.getValue();
            List<EncryptColumnRuleConfiguration> configurations = new ArrayList<>();
            for (SensitiveInformation information : value) {
                Properties properties = new Properties();
                properties.setProperty("aes-key-value", RandomUtil.randomString(16));
                ShardingSphereAlgorithmConfiguration shardingSphereAlgorithmConfiguration = new ShardingSphereAlgorithmConfiguration(information.getAlgorithmType(), properties);
                EncryptColumnRuleConfiguration encrypt = new EncryptColumnRuleConfiguration
                        (information.getFieldName(), information.getFieldName() + "_cipher",
                                null, information.getFieldName(), entry.getKey() + "_" + information.getFieldName());
                configurations.add(encrypt);
                map.put(entry.getKey() + "_" + information.getFieldName(), shardingSphereAlgorithmConfiguration);
            }

            EncryptTableRuleConfiguration encryptTableRuleConfiguration = new EncryptTableRuleConfiguration(entry.getKey(), configurations, true);
            tableRuleConfigurations.add(encryptTableRuleConfiguration);
        }
        ;
        EncryptRuleConfiguration encryptRuleConfiguration = new EncryptRuleConfiguration(tableRuleConfigurations, map, true);
        List<RuleConfiguration> encryptRuleConfigurations = Arrays.asList(encryptRuleConfiguration);
        // System.out.println(JSON.toJSONString(encryptRuleConfigurations));
//        ShardingSchemaServiceImpl shardingSchemaService = new ShardingSchemaServiceImpl();
        configCenterService.getActivatedMetadataService().getSchemaRuleService()
                .persist("ec_order_db", encryptRuleConfigurations);

    }


}
