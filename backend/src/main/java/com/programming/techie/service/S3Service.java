package com.programming.techie.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Service implements FileService{

    public static final String YOUTUBE_CLONED = "youtube-cloned";
    private final AmazonS3Client awsS3Client;
    @Override
    public String uploadFile(MultipartFile file){
    //Upload file to AWS S3
        var filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        var key = UUID.randomUUID().toString() + filenameExtension;

        var metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            awsS3Client.putObject(YOUTUBE_CLONED, key, file.getInputStream(), metadata);
        }
        catch (IOException ioException){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An Exception occured while uploading the file");
        }
        awsS3Client.setObjectAcl(YOUTUBE_CLONED,key, CannedAccessControlList.PublicRead);

        return awsS3Client.getResourceUrl(YOUTUBE_CLONED, key);
    }
}
