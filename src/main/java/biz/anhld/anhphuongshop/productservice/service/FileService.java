package biz.anhld.anhphuongshop.productservice.service;

import biz.anhld.anhphuongshop.productservice.dto.AccessType;
import biz.anhld.anhphuongshop.productservice.dto.MultiplePreSignedUrlRequest;
import biz.anhld.anhphuongshop.productservice.dto.MultiplePreSignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.util.List;
import java.util.ArrayList;

import java.text.Normalizer;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${aws.bucket_name}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    /**
     * Generates a presigned URL for GET or PUT operations with specified access type.
     */
    public String generatePreSignedUrl(String filePath, SdkHttpMethod method, AccessType accessType) {
        if (method == SdkHttpMethod.GET) {
            return generateGetPresignedUrl(filePath);
        } else if (method == SdkHttpMethod.PUT) {
            return generatePutPresignedUrl(filePath, accessType);
        } else {
            throw new UnsupportedOperationException("Unsupported HTTP method: " + method);
        }
    }

    /**
     * Generates a presigned GET URL.
     */
    private String generateGetPresignedUrl(String filePath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    /**
     * Generates a presigned PUT URL with optional ACL based on AccessType.
     */
    private String generatePutPresignedUrl(String filePath, AccessType accessType) {
        PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath);

        if (accessType == AccessType.PUBLIC) {
            putObjectRequestBuilder.acl(ObjectCannedACL.PUBLIC_READ);
        }

        PutObjectRequest putObjectRequest = putObjectRequestBuilder.build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }

    /**
     * Generates multiple presigned URLs with specified access type.
     */
    public List<MultiplePreSignedUrlResponse> generateMultiplePreSignedUrls(
            List<MultiplePreSignedUrlRequest> requests,
            AccessType accessType) {
        List<MultiplePreSignedUrlResponse> urls = new ArrayList<>();
        for (MultiplePreSignedUrlRequest request : requests) {
            String filename = buildFilename(request.getOriginalFileName());
            String url = generatePreSignedUrl(filename, SdkHttpMethod.PUT, accessType);
            MultiplePreSignedUrlResponse response = new MultiplePreSignedUrlResponse(url, filename, request.getOriginalFileName());
            urls.add(response);
        }
        return urls;
    }

    /**
     * Builds a sanitized filename with a timestamp prefix.
     */
    public static String buildFilename(String filename) {
        return String.format("%s_%s", System.currentTimeMillis(), sanitizeFileName(filename));
    }

    /**
     * Sanitizes the filename by removing special characters and replacing spaces.
     */
    private static String sanitizeFileName(String fileName) {
        String normalizedFileName = Normalizer.normalize(fileName, Normalizer.Form.NFKD);
        return normalizedFileName.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9.\\-_]", "");
    }


}
