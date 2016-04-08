package com.mishu.cgwy.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.common.repository.MediaFileRepository;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * User: xudong
 * Date: 3/20/15
 * Time: 11:50 AM
 */
@Service
public class MediaFileService {
    @Autowired
    private MediaFileRepository mediaFileRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String uploadMedia(InputStream inputStream) {

        // TODO
        HttpClient httpClient = new DefaultHttpClient();

        try {
            String ACCESS_KEY = "7nF667eDFCl7NrYMphI0mqlbourI_UStB5UNm0Li";
            String SECRET_KEY = "XRtlQCdlh2gktbp6Wr5AK-2Vhl2An3MzyenZ_Uz_";
            Mac mac = new Mac(ACCESS_KEY, SECRET_KEY);

            String bucketName = "canguanwuyou";

            PutPolicy putPolicy = new PutPolicy(bucketName);
            String upToken = putPolicy.token(mac);

            HttpPost post = new HttpPost("http://upload.qiniu.com/");
            MultipartEntity mpEntity = new MultipartEntity();
            mpEntity.addPart("file", new InputStreamBody(inputStream, "media"));
            mpEntity.addPart("token", new StringBody(upToken));

            post.setEntity(mpEntity);

            HttpResponse execute = httpClient.execute(post);

            if (execute.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
                JsonNode jsonNode = objectMapper.readTree(EntityUtils.toString(execute.getEntity(), "utf-8"));
                String hash = jsonNode.get("hash").asText();

                EntityUtils.consume(execute.getEntity());
                return hash;
            } else {
                throw new RuntimeException("fail to upload media to 7niu");
            }
        } catch (Exception e) {
            throw new RuntimeException("fail to upload media to 7niu", e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
    
    
    private String uploadMedia(MultipartFile file) {
    	try {
			return uploadMedia(file.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("fail to upload media to 7niu", e);
		}
    }

    public MediaFile saveMediaFile(InputStream inputStream) {
        String hash = uploadMedia(inputStream);
        return saveMediaFile(hash);
    }

    
    public MediaFile saveMediaFile(MultipartFile file) {
    	String hash = uploadMedia(file);
    	return saveMediaFile(hash);
    }

    public MediaFile saveMediaFile(String qiNiuHash) {
    	
    	List<MediaFile> mediaList = mediaFileRepository.findByQiNiuHash(qiNiuHash);
    	
    	MediaFile mediaFile = null;
    	if (mediaList.isEmpty()) {
    		mediaFile = new MediaFile();
    		mediaFile.setQiNiuHash(qiNiuHash);
    		
    		return mediaFileRepository.save(mediaFile);
    	} else {
    		return mediaList.get(0);
    	}
    }

    public MediaFile saveMediaFile(MediaFile mediaFile) {
        return mediaFileRepository.save(mediaFile);
    }
    
    public MediaFile getMediaFile(Long mediaFileId) {
        return mediaFileRepository.findOne(mediaFileId);
    }
    
    public MediaFile getMediaFile(String hash) {
        List<MediaFile> list = mediaFileRepository.findByQiNiuHash(hash);
        if(!list.isEmpty()) {
        	return list.get(0);
        }
        return null;
    }

    //excel导入过程中使用截取图片
    public MediaFile getMediaFileByUrl(String url) {
        if (StringUtils.isNotBlank(url)) {
            if (url.startsWith(MediaFile.default7NiuDomain)) {
                url = StringUtils.removeStart(url, MediaFile.default7NiuDomain);
            } else if (url.startsWith(MediaFile.defaultLocalDomain)) {
                url = StringUtils.removeStart(url, MediaFile.defaultLocalDomain);
            } else {
                url = MediaFile.DEFAULT_IMAGE;
            }
        } else {
            url = MediaFile.DEFAULT_IMAGE;
        }
        return getMediaFile(url);
    }
}
