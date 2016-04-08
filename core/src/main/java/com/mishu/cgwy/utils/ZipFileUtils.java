package com.mishu.cgwy.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public class ZipFileUtils {

	public static byte[] zipFiles(String... fileNames) {

		List<File> list = new ArrayList<>();
		for (String fileName : fileNames) {
			File file = new File(fileName);
			list.add(file);
		}
		return zipFiles(list.toArray(new File[list.size()]));
	}

	public static byte[] zipFiles(File... files) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		byte bytes[] = new byte[2048];

		for (File file : files) {
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				zos.putNextEntry(new ZipEntry(file.getName()));
				int bytesRead;
				while ((bytesRead = bis.read(bytes)) != -1) {
					zos.write(bytes, 0, bytesRead);
				}
				zos.closeEntry();
				bis.close();
				fis.close();
			} catch (IOException e) {
				throw new RuntimeException("file is not exist");
			}
		}

		try {
			zos.flush();
			baos.flush();
			zos.close();
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

		return baos.toByteArray();
	}

	public static File zipFiles(File zipFile, File... files) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		byte bytes[] = new byte[2048];

		for (File file : files) {
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				zos.putNextEntry(new ZipEntry(file.getName()));
				int bytesRead;
				while ((bytesRead = bis.read(bytes)) != -1) {
					zos.write(bytes, 0, bytesRead);
				}
				zos.closeEntry();
				bis.close();
				fis.close();
			} catch (IOException e) {
				throw new RuntimeException("file is not exist");
			}
		}

		try {
			zos.flush();
			baos.flush();
			zos.close();
			baos.close();
			FileUtils.writeByteArrayToFile(zipFile, baos.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

		return zipFile;
	}

	
	
	public static List<File> unZipFiles(MultipartFile file, String fileDir) {
		List<File> list = new ArrayList<>();
		File dir = new File(fileDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String[] strs = new String[]{"[？]", "[?]",  "、",  "“", "”", "'", "\"", "‘", "’", "<", ">", "[*]", "[|]", "\\+"};
		try {
			File uploadFile = new File(fileDir, file.getOriginalFilename());
			file.transferTo(uploadFile);

			ZipFile zipFile = new ZipFile(uploadFile, getFileCharset(uploadFile));

			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while(entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				
				if (entry.isDirectory()) {
					new File(dir, entry.getName()).mkdirs();
					continue;
				}

				String fileName = entry.getName();
				for (String str : strs) {
					fileName = fileName.replaceAll(str, "");
				}
				File tempFile = new File(fileDir, fileName);

				if(tempFile.isHidden()) {
					continue;
				}
				list.add(tempFile);

				if (tempFile.getParentFile() != null && !tempFile.getParentFile().exists()) {
					tempFile.getParentFile().mkdirs();
				}

				IOUtils.copy(zipFile.getInputStream(entry), new FileOutputStream(tempFile));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return list;
	}


	private static Charset getFileCharset(File file) throws IOException {
		List<String> list = new ArrayList<>();
		list.add("GBK");
		list.add("utf-8");
		for(int i = 0; i < list.size(); i ++ ){
			try {
				Charset charset = Charset.forName(list.get(i));
				ZipFile zipFile = new ZipFile(file, charset);
				Enumeration<? extends ZipEntry> entries = zipFile.entries();
				while(entries.hasMoreElements()) {
					entries.nextElement();
				}
				return charset;
			} catch (Exception e) {
				continue;
			}
		}
		return Charset.forName("ISO-8859-1"); 
	}
	
	
	public static HttpEntity<byte[]> getHttpEntityZip(String excelName,
			String fileName) {
		if (StringUtils.isNotBlank(excelName)) {
			String[] strs = excelName.split(";");
			byte[] zipFiles = zipFiles(strs);
			HttpHeaders header = new HttpHeaders();
			header.setContentType(new MediaType("application", "zip"));
			fileName = fileName.substring(fileName.lastIndexOf("/") + 1)
					.replace(" ", "_");
			try {
				header.set("Content-Disposition", "attachment; filename="
						+ URLEncoder.encode(fileName, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			header.setContentLength(zipFiles.length);

			return new HttpEntity<byte[]>(zipFiles, header);
		}
		return null;
	}

	public static HttpEntity<byte[]> getHttpEntityZip(String fileName) {
		File file = new File(fileName);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "zip"));
		fileName = fileName.substring(fileName.lastIndexOf("/") + 1).replace(
				" ", "_");
		try {
			header.set("Content-Disposition", "attachment; filename="
					+ URLEncoder.encode(fileName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		header.setContentLength(file.length());
		byte[] data = null;
		try {
			data = FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new HttpEntity<byte[]>(data, header);
	}
}
