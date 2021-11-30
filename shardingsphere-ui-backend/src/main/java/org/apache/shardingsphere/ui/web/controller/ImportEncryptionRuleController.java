package org.apache.shardingsphere.ui.web.controller;


import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.apache.shardingsphere.ui.web.response.ResponseResultUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@RestController
@RequestMapping("/api/config-center")
public class ImportEncryptionRuleController {

    @PostMapping(value = "download")
    public ResponseResult<Collection<String>> downloadRuleMould(@RequestBody MultipartFile file) {

        return null;
    }

    @PostMapping(value = "import")
    public ResponseResult<Collection<String>> importRule(@RequestBody MultipartFile file) {

        return null;
    }
}
