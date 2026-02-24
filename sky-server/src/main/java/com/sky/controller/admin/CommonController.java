package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestBody MultipartFile file){
        log.info("文件上传：{}", file);

        try {
            //获取原始文件名
            String originalfilename = file.getOriginalFilename();
            //截取原始文件名后缀
            if (originalfilename != null) {
                String suffix = originalfilename.substring(originalfilename.lastIndexOf("."));
                //生成新文件名
                String fileName = UUID.randomUUID().toString() + suffix;

                String filePath = aliOssUtil.upload(file.getBytes(), fileName);
                log.info("文件上传成功：{}", fileName);

                return Result.success(filePath);

            }else {
                return Result.error("文件名为空");
            }

        } catch (IOException e) {
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }

}
