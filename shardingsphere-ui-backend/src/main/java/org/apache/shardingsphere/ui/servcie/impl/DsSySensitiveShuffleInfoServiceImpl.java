package org.apache.shardingsphere.ui.servcie.impl;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.ui.common.domain.DsSysSensitiveShuffleInfo;
import org.apache.shardingsphere.ui.common.domain.SensitiveShuffleInfo;
import org.apache.shardingsphere.ui.mapper.DsSySensitiveShuffleInfoMapper;
import org.apache.shardingsphere.ui.servcie.DsSySensitiveShuffleInfoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jarod.Kong
 */
@Service
@Slf4j
public class DsSySensitiveShuffleInfoServiceImpl
        extends ServiceImpl<DsSySensitiveShuffleInfoMapper, DsSysSensitiveShuffleInfo>
        implements DsSySensitiveShuffleInfoService {

    @Override
    public void insertRuleShuffleInfo(List<SensitiveShuffleInfo> model, String schemaName, String importUuid) {
        if (model != null) {
            List<DsSysSensitiveShuffleInfo> res = model.stream().map(m -> {
                DsSysSensitiveShuffleInfo each = new DsSysSensitiveShuffleInfo();
                each.setSchemaName(schemaName);
                each.setTableName(m.getTableName());
                each.setIncrFieldName(m.getIncrFieldName());
                each.setOnUpdateTimestampFields(m.getOnUpdateTimestampFields());
                each.setCreateTime(LocalDateTime.now());
                each.setSensitiveId(importUuid);
                log.info("shuffleInfo:{}", each);
                return each;
            }).collect(Collectors.toList());
            saveBatch(res);
        }

    }
}
