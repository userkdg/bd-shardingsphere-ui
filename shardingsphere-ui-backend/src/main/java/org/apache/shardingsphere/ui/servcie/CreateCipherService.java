package org.apache.shardingsphere.ui.servcie;

import org.apache.shardingsphere.ui.web.response.ResponseResult;

public interface CreateCipherService {

    ResponseResult<String> createCipherPlainField(String schemaName, Boolean isCipher);

}
