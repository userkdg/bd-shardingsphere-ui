package org.apache.shardingsphere.ui.servcie.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.shardingsphere.ui.common.domain.DsSysSensitiveInfo;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.mapper.DsSySensitiveInfoMapper;
import org.apache.shardingsphere.ui.servcie.DsSySensitiveInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DsSySensitiveInfoServiceImpl extends ServiceImpl<DsSySensitiveInfoMapper, DsSysSensitiveInfo> implements DsSySensitiveInfoService {

    @Override
    public void insertRuleConfig(List<SensitiveInformation> list, String schemaName) {

        List<DsSysSensitiveInfo> infos = Lists.newArrayList();
        for (SensitiveInformation info : list) {
            DsSysSensitiveInfo information = new DsSysSensitiveInfo();
            BeanUtils.copyProperties(info, information);
            information.setDataType(info.getDatatype());
            information.setSchemaName(schemaName);
            information.setCreateTime(LocalDateTime.now());
            log.info("info:{}", information);
            infos.add(information);
        }
        this.saveBatch(infos);
    }

    @Override
    public Map<String, String> getTableNameAndIncFieldMap(String schema) {
        Objects.requireNonNull(schema, "不可为空");
        LambdaQueryWrapper<DsSysSensitiveInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DsSysSensitiveInfo::getSchemaName, schema)
        .eq(DsSysSensitiveInfo::getIncrField, Boolean.TRUE);
        List<DsSysSensitiveInfo> res = list(queryWrapper);
        return res.stream().collect(Collectors.toMap(DsSysSensitiveInfo::getTableName, DsSysSensitiveInfo::getFieldName, (a, b) -> b));
    }
}
