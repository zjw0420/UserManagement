package com.usermgr.admin.service.impl;

import com.usermgr.admin.config.MinioProperties;
import com.usermgr.admin.service.FileService;
import com.usermgr.common.exception.BusinessException;
import com.usermgr.common.result.ResultCodeEnum;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnExpression("not '${minio.endpoint:}'.equals('')")
public class FileServiceImpl implements FileService {

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/png", "image/jpeg", "image/webp"
    );

    @Autowired
    private MinioClient client;

    @Autowired
    private MinioProperties properties;

    @Override
    public String upload(MultipartFile file) {
        // 第一层: Content-Type 快速过滤
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException(ResultCodeEnum.FILE_TYPE_NOT_ALLOWED,
                    "仅支持 PNG / JPEG / WEBP 格式");
        }

        String format = contentType.substring("image/".length());

        try {
            // 第二层: Magic Number 文件头校验
            if (!isValidImageHeader(file)) {
                throw new BusinessException(ResultCodeEnum.FILE_TYPE_NOT_ALLOWED,
                        "文件内容与类型不匹配");
            }

            // 第三层: ImageIO 重解析，丢弃非图片数据
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new BusinessException(ResultCodeEnum.FILE_TYPE_NOT_ALLOWED,
                        "不是有效的图片文件");
            }
            ByteArrayOutputStream cleaned = new ByteArrayOutputStream();
            ImageIO.write(image, format, cleaned);
            byte[] cleanedBytes = cleaned.toByteArray();

            // 上传清洗后的文件到 MinIO
            String bucket = properties.getBucketName();
            boolean bucketExists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build()
            );
            if (!bucketExists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }

            String fileName = UUID.randomUUID() + "." + format;
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(new ByteArrayInputStream(cleanedBytes), cleanedBytes.length, -1)
                            .contentType(contentType)
                            .build()
            );

            String url = properties.getEndpoint() + "/" + bucket + "/" + fileName;
            log.info("文件上传成功: {}", url);
            return url;

        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ResultCodeEnum.FILE_UPLOAD_ERROR,
                    "文件写入失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("MinIO 上传失败", e);
            throw new BusinessException(ResultCodeEnum.FILE_UPLOAD_ERROR,
                    "存储服务异常: " + e.getMessage());
        }
    }

    /**
     * 第二层校验: Magic Number 文件头
     */
    private boolean isValidImageHeader(MultipartFile file) throws IOException {
        byte[] header = new byte[12];
        try (InputStream is = file.getInputStream()) {
            int read = is.read(header, 0, 12);
            if (read < 12) return false;
        }

        // PNG:  89 50 4E 47 0D 0A 1A 0A
        if (header[0] == (byte) 0x89 && header[1] == 'P'
                && header[2] == 'N' && header[3] == 'G') {
            return true;
        }
        // JPEG: FF D8 FF
        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8
                && header[2] == (byte) 0xFF) {
            return true;
        }
        // WEBP: 52 49 46 46 ... 57 45 42 50
        if (header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
            return true;
        }

        return false;
    }

}
