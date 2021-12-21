package org.apache.shardingsphere.ui.servcie;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.shardingsphere.ui.common.domain.DsSysSensitiveInfo;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;

import java.util.List;


public interface DsSySensitiveInfoService extends IService<DsSysSensitiveInfo> {

    void insertRuleConfig(List<SensitiveInformation> list);
}
