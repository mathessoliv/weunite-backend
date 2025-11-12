package com.example.weuniteauth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cloudinary.image")
public class CloudinaryImageProperties {

    private Profile profile = new Profile();
    private Banner banner = new Banner();
    private Post post = new Post();

    @Getter
    @Setter
    public static class Profile {
        private int height = 400;
    }

    @Getter
    @Setter
    public static class Banner {
        private int height = 300;
    }

    @Getter
    @Setter
    public static class Post {
        private int height = 720;
        private int width = 1280; // 16:9 aspect ratio - HD resolution
        private int verticalMaxHeight = 500; // For vertical images
        private int verticalMaxWidth = 375; // For vertical images
    }

    @Getter
    @Setter
    public static class VideoProperties {
        private int maxDuration = 120;
        private int maxSizeInMB = 100;
        // Vertical videos
        private int verticalMaxWidth = 375;
        private int verticalMaxHeight = 500;  // ✅ ATUALIZADO: 667 → 500
        // Horizontal videos
        private int horizontalMaxWidth = 1280;
        private int horizontalMaxHeight = 720;
        // Quality settings
        private int qualityLevel = 90;  // ✅ ATUALIZADO: 85 → 90
        private String bitrate = "12000k";  // ✅ NOVO: Bitrate configurável
        private String videoCodec = "h264";
    }

    private VideoProperties video = new VideoProperties();

}
