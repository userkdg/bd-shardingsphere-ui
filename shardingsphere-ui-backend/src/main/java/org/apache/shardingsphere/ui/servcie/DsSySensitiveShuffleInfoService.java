package org.apache.shardingsphere.ui.servcie;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.shardingsphere.ui.common.domain.DsSysSensitiveShuffleInfo;
import org.apache.shardingsphere.ui.common.domain.SensitiveShuffleInfo;

import java.util.List;


public interface DsSySensitiveShuffleInfoService extends IService<DsSysSensitiveShuffleInfo> {

    void insertRuleShuffleInfo(List<SensitiveShuffleInfo> model, String schemaName);
}
