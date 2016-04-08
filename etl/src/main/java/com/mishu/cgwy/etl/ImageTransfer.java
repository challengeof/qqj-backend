package com.mishu.cgwy.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.common.domain.MediaFile;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ImageTransfer {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final String prefix = "/var/www/b2b/web";
    private HttpClient httpClient = new DefaultHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();

    private Logger logger = LoggerFactory.getLogger(ImageTransfer.class);

    @Autowired
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"/application-persist.xml",
                        "/application-context.xml"});
        ImageTransfer mediaFile = applicationContext
                .getBean(ImageTransfer.class);
        //更新库
        mediaFile.InsertImage(mediaFile.upload());
        applicationContext.close();

    }

    public List<MediaFile>  upload() {
        List<MediaFile> contents = new ArrayList<>();
        List<MediaFile> files = jdbcTemplate.query(
                "select id,local_path from media_file where local_path is not null",
                new RowMapper<MediaFile>() {
                    @Override
                    public MediaFile mapRow(ResultSet rs, int arg1)
                            throws SQLException {
                        MediaFile mf = new MediaFile();
                        mf.setId(rs.getLong("id"));
                        mf.setLocalPath(rs.getString("local_path"));
                        mf.setQiNiuHash(null);
                        return mf;
                    }
                });

        for (MediaFile mf : files) {
            try {
                File file = new File(prefix + mf.getLocalPath());
                if (file.exists()) {
                    // 上传图片到7牛
                    // 保存到集合
                    MediaFile media = new MediaFile();
                    media.setId(mf.getId());
                    media.setQiNiuHash(uploadMedia(new File(prefix + mf.getLocalPath())));
                    contents.add(media);
                }
            } catch (Exception e) {
                logger.warn("catch exception", e);
            }
        }

        return contents;
    }

    private String uploadMedia(File file) {
        try {
            String ACCESS_KEY = "7nF667eDFCl7NrYMphI0mqlbourI_UStB5UNm0Li";
            String SECRET_KEY = "XRtlQCdlh2gktbp6Wr5AK-2Vhl2An3MzyenZ_Uz_";
            Mac mac = new Mac(ACCESS_KEY, SECRET_KEY);

            String bucketName = "canguanwuyou";

            PutPolicy putPolicy = new PutPolicy(bucketName);
            String upToken = putPolicy.token(mac);

            HttpPost post = new HttpPost("http://upload.qiniu.com/");
            MultipartEntity mpEntity = new MultipartEntity();
            mpEntity.addPart("file", new InputStreamBody(new FileInputStream(
                    file), "media"));
            mpEntity.addPart("token", new StringBody(upToken));

            post.setEntity(mpEntity);

            HttpResponse execute = httpClient.execute(post);

            if (execute.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
                JsonNode jsonNode = objectMapper.readTree(EntityUtils.toString(
                        execute.getEntity(), "utf-8"));
                String hash = jsonNode.get("hash").asText();

                return hash;
            } else {
                throw new RuntimeException("fail to upload media to 7niu");
            }
        } catch (Exception e) {
            throw new RuntimeException("fail to upload media to 7niu", e);
        }
    }

    // 更新数据库
    @Transactional
    public int[][] InsertImage(final Collection<MediaFile> files) {
        int[][] updateCounts = jdbcTemplate.batchUpdate(
                "update media_file set qi_niu_hash = ? where id = ?", files,
                1000, new ParameterizedPreparedStatementSetter<MediaFile>() {
                    @Override
                    public void setValues(PreparedStatement ps,
                                          MediaFile argument) throws SQLException {
                        ps.setString(1, argument.getQiNiuHash());
                        ps.setLong(2, argument.getId());

                    }
                });
        return updateCounts;
    }

}
