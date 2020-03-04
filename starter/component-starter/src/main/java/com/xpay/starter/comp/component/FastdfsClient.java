package com.xpay.starter.comp.component;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.csource.common.NameValuePair;
import com.xpay.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Author: Cmf
 * Date: 2019/11/13
 * Time: 19:27
 * Description: fastdfs文件客户端，请使用该类对fastdfs进行操作
 */
public class FastdfsClient {
    private static final String ORIGINAL_FILE_NAME = "ORIGINAL_FILE_NAME";
    private Logger logger = LoggerFactory.getLogger(FastdfsClient.class);

    public String uploadFile(String file, String originalFileName) {
        StorageClient1 storageClient1 = null;
        try {
            storageClient1 = getStorageClient();
            return storageClient1.upload_file1(file, getFileExtension(originalFileName), new NameValuePair[]{new NameValuePair(ORIGINAL_FILE_NAME, originalFileName)});
        } catch (Exception e) {
            logger.info("上传fastdfs失败,file={},originalFileName={}", file, originalFileName, e);
            throw new BizException("上传FASTDFS 失败");
        } finally {
            try {
                if (storageClient1 != null) {
                    storageClient1.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public String uploadFile(byte[] fileBytes, String originalFileName) {
        StorageClient1 storageClient1 = null;
        try {
            storageClient1 = getStorageClient();
            return storageClient1.upload_file1(fileBytes, getFileExtension(originalFileName), new NameValuePair[]{new NameValuePair(ORIGINAL_FILE_NAME, originalFileName)});
        } catch (Exception e) {
            logger.error("上传fastdfs失败, originalFileName={}", originalFileName, e);
            throw new BizException("上传FASTDFS 失败");
        } finally {
            try {
                if (storageClient1 != null) {
                    storageClient1.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 获取accesssString,附加到url后面,此方面的返回结果为token=*****&ts=*****
     *
     * @param fastdfsFile fastdfs文件id
     * @return
     */
    public String genAccessString(String fastdfsFile) {
        try {
            long epochSecond = Instant.now().getEpochSecond();
            String token = ProtoCommon.getToken(fastdfsFile.substring(fastdfsFile.indexOf("/") + 1), (int) epochSecond, ClientGlobal.getG_secret_key());
            return "token=" + token + "&ts=" + epochSecond;
        } catch (Exception ex) {
            logger.error("生成token失败,fastdfsfile={}", fastdfsFile, ex);
            throw new BizException("生成token失败");
        }
    }

    /**
     * 获取源文件名
     *
     * @param fastdfsFile fastdfs文件id
     * @return 源文件名
     */
    public String getOriginalFileName(String fastdfsFile) {
        StorageClient1 storageClient1 = null;
        try {
            storageClient1 = getStorageClient();
            NameValuePair[] metadata1 = storageClient1.get_metadata1(fastdfsFile);
            if (metadata1 == null) {
                return null;
            }
            Optional<NameValuePair> first = Stream.of(metadata1).filter(p -> Objects.equals(p.getName(), ORIGINAL_FILE_NAME)).findFirst();
            return first.map(NameValuePair::getValue).orElse(null);
        } catch (Exception e) {
            logger.error("获取源文件名失败,fastdfsFile={}", fastdfsFile, e);
            throw new BizException("获取源文件名失败");
        } finally {
            try {
                if (storageClient1 != null) {
                    storageClient1.close();
                }
            } catch (Exception ignored) {
            }
        }
    }


    private StorageClient1 getStorageClient() throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            return new StorageClient1(trackerServer, storageServer);
        } finally {
            try {
                if (trackerServer != null) {
                    trackerServer.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.trim().equals("")) {
            return "";
        }
        int i = fileName.lastIndexOf(".");
        if (i > 0) {
            return fileName.substring(i + 1);
        } else {
            return "";
        }
    }
}
