package com.usermgr.admin.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传图片到 MinIO。
     * 三层校验: Content-Type → Magic Number → ImageIO 重解析
     *
     * @param file 上传的文件
     * @return 文件访问 URL
     */
    String upload(MultipartFile file);

}
