package com.douglasdc.cursomc.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

	private Logger LOG = LoggerFactory.getLogger(S3Service.class);

	@Autowired
	private AmazonS3 s3client;

	@Value("${s3.bucket}")
	private String bucketName;

	public URI uploadFile(MultipartFile multipartFile) {
		try {
			String fileName = multipartFile.getOriginalFilename();
			InputStream is = multipartFile.getInputStream(); // encapsula um processo de leitura
			String contenteType = multipartFile.getContentType(); // string contendo informacao do tipo de arquivo enviado
			return uploadFile(is, fileName, contenteType);
		} catch (IOException e) {
			throw new RuntimeException("Erro de IO:" + e.getMessage());
		} 
	}

	private URI uploadFile(InputStream is, String fileName, String contenteType) {
		try {
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentType(contenteType);
			LOG.info("Iniciando upload...");
			s3client.putObject(new PutObjectRequest(bucketName, fileName, is, meta));
			LOG.info("Upload finalizado!");
			return s3client.getUrl(bucketName, fileName).toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException("Erro ao converter URL para URI");
		}
	}

}