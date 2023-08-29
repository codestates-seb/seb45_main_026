// package com.server.module.s3.service;
//
// import com.server.module.ModuleServiceTest;
// import com.server.module.s3.service.dto.ImageType;
// import org.apache.tomcat.util.http.fileupload.IOUtils;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.DynamicTest;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.web.client.RestTemplate;
//
// import java.io.OutputStream;
// import java.io.UnsupportedEncodingException;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import java.net.URLDecoder;
// import java.util.Collection;
// import java.util.List;
//
// import static java.nio.charset.StandardCharsets.*;
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.DynamicTest.*;
//
// class AwsServiceTest extends ModuleServiceTest {
//
//     @Autowired AwsService awsService;
//     RestTemplate restTemplate = new RestTemplate();
//
//     private final Long mockMemberId = 9999L;
//
//     @Test
//     @DisplayName("path 를 통해 image 의 url 을 가져온다.")
//     void getImageUrl() throws Exception {
//         //given
//         String fileName = "test";
//
//         //when
//         String imageUrl = awsService.getImageUrl(fileName);
//
//         //then
//         System.out.println("imageUrl = " + imageUrl);
//         ResponseEntity<byte[]> response = getResponseEntity(imageUrl);
//         assertThat(response.getStatusCodeValue()).isEqualTo(200);
//     }
//
//     @TestFactory
//     @DisplayName("image 업로드 및 삭제 테스트")
//     Collection<DynamicTest> getUploadUrlAndDelete() {
//         //given
//         String fileName = "test2";
//         ImageType imageType = ImageType.PNG;
//
//         MockMultipartFile multipartFile =
//                 new MockMultipartFile(
//                         "test2",
//                         "test2",
//                         imageType.getDescription(),
//                         "test2".getBytes());
//
//         return List.of(
//                 dynamicTest("presignedUrl 을 가져와서 이미지를 업로드 한 후 200 OK 를 확인한다.", ()-> {
//                     //when
//                     String uploadUrl = awsService.getUploadImageUrl(fileName, imageType);
//                     URL url = new URL(uploadUrl);
//
//                     //then
//                     HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                     connection.setDoOutput(true);
//                     connection.setRequestProperty("Content-Type", multipartFile.getContentType());
//                     connection.setRequestMethod("PUT");
//
//                     try (OutputStream os = connection.getOutputStream()) {
//                         IOUtils.copy(multipartFile.getInputStream(), os);
//                     }
//
//                     // 연결의 응답 코드 확인
//                     int responseCode = connection.getResponseCode();
//                     assertThat(responseCode).isEqualTo(200);
//                 }),
//                 dynamicTest("filename 으로 해당 이미지를 삭제한다.", ()-> {
//                     //when & then
//                     awsService.deleteImage(fileName);
//
//                 })
//         );
//     }
//
//     @Test
//     @DisplayName("path 를 통해 thumbnail 의 Url 을 가져온다.")
//     void getThumbnailUrl() throws Exception {
//         //given
//         String fileName = "testthumbnail";
//
//         //when
//         String thumbnailUrl = awsService.getThumbnailUrl(mockMemberId, fileName);
//
//         //then
//         System.out.println("thumbnailUrl = " + thumbnailUrl);
//         ResponseEntity<byte[]> response = getResponseEntity(thumbnailUrl);
//         assertThat(response.getStatusCodeValue()).isEqualTo(200);
//     }
//
//     @TestFactory
//     @DisplayName("thumbnail 업로드 및 삭제 테스트")
//     Collection<DynamicTest> getThumbnailUploadUrlAndDelete() {
//         //given
//         String fileName = "test2";
//         ImageType imageType = ImageType.PNG;
//
//         MockMultipartFile multipartFile =
//                 new MockMultipartFile(
//                         "test2",
//                         "test2",
//                         imageType.getDescription(),
//                         "test2".getBytes());
//
//         return List.of(
//                 dynamicTest("presignedUrl 을 가져와서 썸네일을 업로드 한 후 200 OK 를 확인한다.", ()-> {
//                     //when
//                     String uploadUrl = awsService.getUploadThumbnailUrl(mockMemberId, fileName, imageType);
//                     URL url = new URL(uploadUrl);
//
//                     //then
//                     HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                     connection.setDoOutput(true);
//                     connection.setRequestProperty("Content-Type", multipartFile.getContentType());
//                     connection.setRequestMethod("PUT");
//
//                     try (OutputStream os = connection.getOutputStream()) {
//                         IOUtils.copy(multipartFile.getInputStream(), os);
//                     }
//
//                     // 연결의 응답 코드 확인
//                     int responseCode = connection.getResponseCode();
//                     assertThat(responseCode).isEqualTo(200);
//                 }),
//                 dynamicTest("filename 으로 해당 이미지를 삭제한다.", ()-> {
//                     //when & then
//                     awsService.deleteThumbnail(mockMemberId, fileName);
//
//                 })
//         );
//     }
//
//     @Test
//     @DisplayName("path 를 통해 비디오 url 을 가져온다.")
//     void getVideoUrl() throws Exception {
//         //given
//         String fileName = "test";
//
//         //when
//         String videoUrl = awsService.getVideoUrl(mockMemberId, fileName);
//
//         //then
//         System.out.println("videoUrl = " + videoUrl);
//         ResponseEntity<byte[]> response = getResponseEntity(videoUrl);
//         assertThat(response.getStatusCodeValue()).isEqualTo(200);
//     }
//
//     @TestFactory
//     @DisplayName("video 를 업로드할 수 있는 presignedUrl 을 가져와서 업로드 한 후 200 OK 를 확인한다. 그리고 비디오를 삭제한다.")
//     Collection<DynamicTest> getUploadUrl() {
//         //given
//         String fileName = "test2";
//         MockMultipartFile multipartFile =
//                 new MockMultipartFile(
//                         "test2",
//                         "test2",
//                         "video/mp4",
//                         "test".getBytes());
//
//         return List.of(
//                dynamicTest("presignedUrl 을 가져와서 업로드 한 후 200 OK 를 확인한다.", ()-> {
//                    //when
//                    String uploadUrl = awsService.getUploadVideoUrl(mockMemberId, fileName);
//                    URL url = new URL(uploadUrl);
//
//                    //then
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setDoOutput(true);
//                    connection.setRequestProperty("Content-Type", multipartFile.getContentType());
//                    connection.setRequestMethod("PUT");
//
//                    try (OutputStream os = connection.getOutputStream()) {
//                        IOUtils.copy(multipartFile.getInputStream(), os);
//                    }
//
//                    // 연결의 응답 코드 확인
//                    int responseCode = connection.getResponseCode();
//                    assertThat(responseCode).isEqualTo(200);
//                }),
//                 dynamicTest("memberId 와 filename 으로 해당 파일을 삭제한다.", ()-> {
//                         //when & then
//                         awsService.deleteVideo(mockMemberId, fileName);
//
//                 })
//         );
//     }
//
//     private ResponseEntity<byte[]> getResponseEntity(String url) throws UnsupportedEncodingException {
//         return restTemplate.getForEntity(
//                 URLDecoder.decode(url, UTF_8),
//                 byte[].class);
//     }
// }