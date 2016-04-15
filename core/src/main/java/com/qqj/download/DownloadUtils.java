package com.qqj.download;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangguodong on 16/4/14.
 */
public class DownloadUtils {
    public static HttpEntity<byte[]> downloadApk(HttpServletRequest request, HttpServletResponse response) throws Exception {
            String fileName = "com.mirror.cgwy_2.1.3_338.apk";

            InputStream inputStream = null;
            ServletOutputStream out = null;
            try {
                File file = new File("/root/app/" + fileName);
                long fileSize = file.length();

                response.setCharacterEncoding("utf-8");
                response.setContentType("application/x-download");
                response.setHeader("Content-Length", String.valueOf(fileSize));
                response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);

                inputStream=new FileInputStream(file);

                byte[] buffer = new byte[1024*10];
                int length = 0;
                out = response.getOutputStream();
                while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, length);
                }
            } finally {
                try {
                    if(out != null) out.flush();
                    if(out != null) out.close();
                    if(inputStream != null) inputStream.close();
                } catch (IOException e) {
                }
            }
            return new ResponseEntity(null, HttpStatus.OK);
    }
}
