package com.restaurant.restapi.service;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileAlreadyExistsException;


@Service
public class ImageUploadService {

    public String save(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename().replaceAll("\\s", "") ;
            byte[] fileBytes = file.getBytes();
            String fileType = file.getContentType();
            StorageClient storageClient = StorageClient.getInstance();

           storageClient.bucket().create(fileName, fileBytes , fileType);
           return "https://firebasestorage.googleapis.com/v0/b/food-1c80d.appspot.com/o/" + fileName + "?alt=media";
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }

            throw new RuntimeException(e.getMessage());
        }
    }

}
