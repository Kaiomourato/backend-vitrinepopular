package br.edu.ifpi.picos.backend_vp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@Service
public class ImagemService {

    private Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @PostConstruct
    public void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }


    public String fazerUpload(MultipartFile arquivo) {
        try {
            Map uploadResult = cloudinary.uploader().upload(arquivo.getBytes(), ObjectUtils.asMap(
                    "folder", "vitrine_popular"
            ));
            
            return uploadResult.get("secure_url").toString();
            
        } catch (IOException e) {
            throw new RuntimeException("Falha ao enviar a imagem: " + e.getMessage());
        }
    }
}