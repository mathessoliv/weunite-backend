package com.example.weuniteauth.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.Url;
import com.cloudinary.Transformation;
import com.example.weuniteauth.config.CloudinaryImageProperties;
import com.example.weuniteauth.config.CloudinaryImageProperties.Banner;
import com.example.weuniteauth.config.CloudinaryImageProperties.Post;
import com.example.weuniteauth.config.CloudinaryImageProperties.Profile;
import com.example.weuniteauth.config.CloudinaryImageProperties.VideoProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private Url url;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private CloudinaryImageProperties imageProperties;

    @Mock
    private CloudinaryImageProperties.VideoProperties videoProps;

    @Mock
    private Post postProps;

    @Mock
    private Profile profileProps;

    @Mock
    private Banner bannerProps;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    private void configureCommonImagePropsForVideo() {
        when(imageProperties.getVideo()).thenReturn(videoProps);
        when(videoProps.getQualityLevel()).thenReturn(90);
        when(videoProps.getBitrate()).thenReturn("8000k");
        when(videoProps.getVideoCodec()).thenReturn("h264");
    }

    private void configureCommonImagePropsForImage() {
        when(imageProperties.getPost()).thenReturn(postProps);
        when(postProps.getWidth()).thenReturn(1280);
        when(postProps.getHeight()).thenReturn(720);
    }

    private void configureCommonImagePropsForProfile() {
        when(imageProperties.getProfile()).thenReturn(profileProps);
        when(profileProps.getHeight()).thenReturn(200);
    }

    private void configureCommonImagePropsForBanner() {
        when(imageProperties.getBanner()).thenReturn(bannerProps);
        when(bannerProps.getHeight()).thenReturn(300);
    }

    @Test
    @DisplayName("uploadPost deve delegar para uploadVideoPost quando contentType for video")
    void uploadPost_video_deveChamarUploadVideoPost() throws Exception {
        configureCommonImagePropsForVideo();

        when(multipartFile.getContentType()).thenReturn("video/mp4");
        when(multipartFile.getBytes()).thenReturn("video-data".getBytes());
        when(cloudinary.uploader()).thenReturn(uploader);

        Map<String, Object> result = new HashMap<>();
        result.put("secure_url", "https://cloudinary.com/video.mp4");
        when(uploader.upload(any(), any())).thenReturn(result);

        String url = cloudinaryService.uploadPost(multipartFile, 1L);

        assertEquals("https://cloudinary.com/video.mp4", url);
        verify(cloudinary).uploader();
        verify(uploader).upload(any(), any(Map.class));
    }

    @Test
    @DisplayName("uploadPost deve delegar para uploadImagePost quando contentType nao for video")
    void uploadPost_imagem_deveChamarUploadImagePost() throws Exception {
        configureCommonImagePropsForImage();

        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getBytes()).thenReturn("image-data".getBytes());
        when(cloudinary.uploader()).thenReturn(uploader);

        Map<String, Object> tempUpload = new HashMap<>();
        tempUpload.put("width", 1920);
        tempUpload.put("height", 1080);
        tempUpload.put("public_id", "temp_id");

        Map<String, Object> finalUpload = new HashMap<>();
        finalUpload.put("secure_url", "https://cloudinary.com/image.png");

        when(uploader.upload(any(), any())).thenReturn(tempUpload, finalUpload);
        when(uploader.destroy(eq("temp_id"), any())).thenReturn(new HashMap<>());

        String url = cloudinaryService.uploadPost(multipartFile, 1L);

        assertEquals("https://cloudinary.com/image.png", url);
        verify(uploader, times(2)).upload(any(), any(Map.class));
        verify(uploader).destroy(eq("temp_id"), any(Map.class));
    }

    @Test
    @DisplayName("uploadPost deve lancar RuntimeException quando ocorrer IOException")
    void uploadPost_deveLancarRuntimeExceptionQuandoIOException() throws Exception {
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getBytes()).thenThrow(new IOException("erro IO"));

        assertThrows(RuntimeException.class, () -> cloudinaryService.uploadPost(multipartFile, 1L));
    }

    @Test
    @DisplayName("uploadProfileImg deve fazer upload com transformacao para perfil")
    void uploadProfileImg_sucesso() throws Exception {
        configureCommonImagePropsForProfile();

        when(cloudinary.uploader()).thenReturn(uploader);
        when(multipartFile.getBytes()).thenReturn("profile".getBytes());

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/profile.jpg");
        when(uploader.upload(any(), any())).thenReturn(uploadResult);

        String url = cloudinaryService.uploadProfileImg(multipartFile, "user1");

        assertEquals("https://cloudinary.com/profile.jpg", url);
        verify(uploader).upload(any(), any(Map.class));
    }

    @Test
    @DisplayName("uploadProfileImg deve lancar RuntimeException quando ocorrer IOException")
    void uploadProfileImg_deveLancarRuntimeExceptionQuandoIOException() throws Exception {
        configureCommonImagePropsForProfile();

        when(cloudinary.uploader()).thenReturn(uploader);
        when(multipartFile.getBytes()).thenThrow(new IOException("erro IO"));

        assertThrows(RuntimeException.class, () -> cloudinaryService.uploadProfileImg(multipartFile, "user1"));
    }

    @Test
    @DisplayName("uploadBannerImg deve fazer upload com transformacao para banner")
    void uploadBannerImg_sucesso() throws Exception {
        configureCommonImagePropsForBanner();

        when(cloudinary.uploader()).thenReturn(uploader);
        when(multipartFile.getBytes()).thenReturn("banner".getBytes());

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://cloudinary.com/banner.jpg");
        when(uploader.upload(any(), any())).thenReturn(uploadResult);

        String url = cloudinaryService.uploadBannerImg(multipartFile, "user1");

        assertEquals("https://cloudinary.com/banner.jpg", url);
        verify(uploader).upload(any(), any(Map.class));
    }

    @Test
    @DisplayName("uploadBannerImg deve lancar RuntimeException quando ocorrer IOException")
    void uploadBannerImg_deveLancarRuntimeExceptionQuandoIOException() throws Exception {
        configureCommonImagePropsForBanner();

        when(cloudinary.uploader()).thenReturn(uploader);
        when(multipartFile.getBytes()).thenThrow(new IOException("erro IO"));

        assertThrows(RuntimeException.class, () -> cloudinaryService.uploadBannerImg(multipartFile, "user1"));
    }

    @Test
    @DisplayName("getImageWithHeight deve gerar URL com transformacao de altura")
    void getImageWithHeight_sucesso() {
        when(cloudinary.url()).thenReturn(url);
        when(url.transformation(any(Transformation.class))).thenReturn(url);
        when(url.generate("public/id")).thenReturn("https://cloudinary.com/img_transformed.jpg");

        String result = cloudinaryService.getImageWithHeight("public/id", 400);

        assertEquals("https://cloudinary.com/img_transformed.jpg", result);
        verify(url).transformation(any(Transformation.class));
        verify(url).generate("public/id");
    }

    @Test
    @DisplayName("getImageProperties deve retornar configuracoes")
    void getImageProperties_retornaObjetoConfig() {
        CloudinaryImageProperties props = cloudinaryService.getImageProperties();
        assertSame(imageProperties, props);
    }
}
