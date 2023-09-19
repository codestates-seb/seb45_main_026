 package com.server.module.s3.service;

 import com.server.module.ModuleServiceTest;
 import com.server.module.s3.service.dto.FileType;
 import com.server.module.s3.service.dto.ImageType;
 import org.apache.tomcat.util.http.fileupload.IOUtils;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.DynamicTest;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.TestFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.http.ResponseEntity;
 import org.springframework.mock.web.MockMultipartFile;
 import org.springframework.web.client.RestTemplate;

 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.util.Collection;
 import java.util.List;

 import static java.nio.charset.StandardCharsets.*;
 import static org.assertj.core.api.Assertions.assertThat;
 import static org.junit.jupiter.api.DynamicTest.*;

 class AwsModuleTest extends ModuleServiceTest {

     @Autowired AwsService awsService;
     RestTemplate restTemplate = new RestTemplate();

     //s3 버킷의 9999번 id 에 test 파일 넣어둠
     private final Long mockMemberId = 9999L;
     private final Long mockVideoId = 9999L;

     @Test
     @DisplayName("path 를 통해 image 의 url 을 가져온다.")
     void getImageUrl() throws Exception {
         //given
         String fileName = mockVideoId + "/profile/test";

         //when
         String imageUrl = awsService.getFileUrl(fileName, FileType.PROFILE_IMAGE);

         //then
         ResponseEntity<byte[]> response = getResponseEntity(imageUrl);
         assertThat(response.getStatusCodeValue()).isEqualTo(200);
     }

     @TestFactory
     @DisplayName("image 업로드 및 삭제 테스트")
     Collection<DynamicTest> getUploadUrlAndDelete() {
         //given
         String fileName = "test2";
         ImageType imageType = ImageType.PNG;

         MockMultipartFile multipartFile =
                 new MockMultipartFile(
                         "test2",
                         "test2",
                         imageType.getDescription(),
                         "test2".getBytes());

         return List.of(
                 dynamicTest("presignedUrl 을 가져와서 이미지를 업로드 한 후 200 OK 를 확인한다.", ()-> {
                     //when
                     String uploadUrl = awsService.getImageUploadUrl(
                             mockMemberId,
                             fileName,
                             FileType.PROFILE_IMAGE,
                             imageType);
                     URL url = new URL(uploadUrl);

                     //then
                     HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                     connection.setDoOutput(true);
                     connection.setRequestProperty("Content-Type", multipartFile.getContentType());
                     connection.setRequestMethod("PUT");

                     try (OutputStream os = connection.getOutputStream()) {
                         IOUtils.copy(multipartFile.getInputStream(), os);
                     }

                     // 연결의 응답 코드 확인
                     int responseCode = connection.getResponseCode();
                     assertThat(responseCode).isEqualTo(200);
                 }),
                 dynamicTest("filename 으로 해당 이미지를 삭제한다.", ()-> {
                     //when & then
                     awsService.deleteFile(mockMemberId + "/profile/" + fileName, FileType.PROFILE_IMAGE);

                 })
         );
     }

     @Test
     @DisplayName("path 를 통해 thumbnail 의 Url 을 가져온다.")
     void getThumbnailUrl() throws Exception {
         //given
         String fileName = mockMemberId + "/videos/" + mockVideoId + "/testthumbnail";

         //when
         String thumbnailUrl = awsService.getFileUrl(fileName, FileType.THUMBNAIL);

         //then
         ResponseEntity<byte[]> response = getResponseEntity(thumbnailUrl);
         assertThat(response.getStatusCodeValue()).isEqualTo(200);
     }

     @TestFactory
     @DisplayName("thumbnail 업로드 및 삭제 테스트")
     Collection<DynamicTest> getThumbnailUploadUrlAndDelete() {
         //given
         String fileName = mockVideoId + "/test2";
         ImageType imageType = ImageType.PNG;

         MockMultipartFile multipartFile =
                 new MockMultipartFile(
                         "test2",
                         "test2",
                         imageType.getDescription(),
                         "test2".getBytes());

         return List.of(
                 dynamicTest("presignedUrl 을 가져와서 썸네일을 업로드 한 후 200 OK 를 확인한다.", ()-> {
                     //when
                     String uploadUrl = awsService.getImageUploadUrl(mockMemberId, fileName, FileType.THUMBNAIL, imageType);
                     URL url = new URL(uploadUrl);

                     //then
                     HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                     connection.setDoOutput(true);
                     connection.setRequestProperty("Content-Type", multipartFile.getContentType());
                     connection.setRequestMethod("PUT");

                     try (OutputStream os = connection.getOutputStream()) {
                         IOUtils.copy(multipartFile.getInputStream(), os);
                     }

                     // 연결의 응답 코드 확인
                     int responseCode = connection.getResponseCode();
                     assertThat(responseCode).isEqualTo(200);
                 }),
                 dynamicTest("filename 으로 해당 이미지를 삭제한다.", ()-> {
                     //when & then
                     awsService.deleteFile(mockMemberId + "/videos/" + fileName, FileType.THUMBNAIL);

                 })
         );
     }

     @Test
     @DisplayName("path 를 통해 비디오 url 을 가져온다.")
     void getVideoUrl() throws Exception {
         //given
         String fileName = mockMemberId + "/videos/" + mockVideoId + "/test";

         //when
         String videoUrl = awsService.getFileUrl(fileName, FileType.VIDEO);

         //then
         ResponseEntity<byte[]> response = getResponseEntity(videoUrl);
         assertThat(response.getStatusCodeValue()).isEqualTo(200);
     }

     @TestFactory
     @DisplayName("video 를 업로드할 수 있는 presignedUrl 을 가져와서 업로드 한 후 200 OK 를 확인한다. 그리고 비디오를 삭제한다.")
     Collection<DynamicTest> getUploadUrl() {
         //given
         String fileName = mockVideoId + "/test2";
         MockMultipartFile multipartFile =
                 new MockMultipartFile(
                         "test2",
                         "test2",
                         "video/mp4",
                         "test".getBytes());

         return List.of(
                dynamicTest("presignedUrl 을 가져와서 업로드 한 후 200 OK 를 확인한다.", ()-> {
                    //when
                    String uploadUrl = awsService.getUploadVideoUrl(mockMemberId, fileName);
                    URL url = new URL(uploadUrl);

                    //then
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", multipartFile.getContentType());
                    connection.setRequestMethod("PUT");

                    try (OutputStream os = connection.getOutputStream()) {
                        IOUtils.copy(multipartFile.getInputStream(), os);
                    }

                    // 연결의 응답 코드 확인
                    int responseCode = connection.getResponseCode();
                    assertThat(responseCode).isEqualTo(200);
                }),
                 dynamicTest("memberId 와 filename 으로 해당 파일을 삭제한다.", ()-> {
                         //when & then
                         awsService.deleteFile(mockMemberId + "/videos/" + fileName, FileType.VIDEO);

                 })
         );
     }

     @Test
     @DisplayName("path 를 통해 해당 이미지 파일이 존재하는지 확인한다. 존재하면 true 를 반환한다.")
     void isExistFileImageTrue() {
         //given
         String imageName = mockMemberId + "/profile/test";

         //when
         boolean isExist = awsService.isExistFile(imageName, FileType.PROFILE_IMAGE);

         //then
         assertThat(isExist).isTrue();
     }

     @Test
     @DisplayName("path 를 통해 해당 이미지 파일이 존재하는지 확인한다. 존재하지 않으면 false 를 반환한다.")
     void isExistFileImageFalse() {
         //given
         String imageName = mockMemberId + "/profile/testNotExist";

         //when
         boolean isExist = awsService.isExistFile(imageName, FileType.PROFILE_IMAGE);

         //then
         assertThat(isExist).isFalse();
     }

     @Test
     @DisplayName("path 를 통해 해당 썸네일 파일이 존재하는지 확인한다. 존재하면 true 를 반환한다.")
     void isExistFileThumbnailTrue() {
         //given
         String thumbnailName = mockMemberId + "/videos/" + mockVideoId + "/testthumbnail";

         //when
         boolean isExist = awsService.isExistFile(thumbnailName, FileType.THUMBNAIL);

         //then
         assertThat(isExist).isTrue();
     }

     @Test
     @DisplayName("path 를 통해 해당 썸네일 파일이 존재하는지 확인한다. 존재하지 않으면 false 를 반환한다.")
     void isExistFileThumbnailFalse() {
         //given
         String thumbnailName = mockMemberId + "/videos/" + mockVideoId + "/testthumbnailNotExist";

         //when
         boolean isExist = awsService.isExistFile(thumbnailName, FileType.THUMBNAIL);

         //then
         assertThat(isExist).isFalse();
     }

     @Test
     @DisplayName("path 를 통해 해당 비디오 파일이 존재하는지 확인한다. 존재하면 true 를 반환한다.")
     void isExistFileVideoTrue() {
         //given
         String videoName = mockMemberId + "/videos/" + mockVideoId + "/test";

         //when
         boolean isExist = awsService.isExistFile(videoName, FileType.VIDEO);

         //then
         assertThat(isExist).isTrue();
     }

     @Test
     @DisplayName("path 를 통해 해당 비디오 파일이 존재하는지 확인한다. 존재하지 않으면 false 를 반환한다.")
     void isExistFileVideoFalse() {
         //given
         String videoName = mockMemberId + "/videos/" + mockVideoId + "/testNotExist";

         //when
         boolean isExist = awsService.isExistFile(videoName, FileType.VIDEO);

         //then
         assertThat(isExist).isFalse();
     }

     private ResponseEntity<byte[]> getResponseEntity(String url) throws UnsupportedEncodingException {
         return restTemplate.getForEntity(
                 URLDecoder.decode(url, UTF_8),
                 byte[].class);
     }
 }