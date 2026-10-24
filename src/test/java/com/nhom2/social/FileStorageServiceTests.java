package com.nhom2.social;

import com.nhom2.social.entity.MediaType;
import com.nhom2.social.exception.StorageException;
import com.nhom2.social.storage.FileStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FileStorageServiceTests {

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    @DisplayName("Upload file an toàn: Từ chối file thực thi .exe")
    void testRejectExecutableFile() {
        MockMultipartFile file = new MockMultipartFile(
                "media",
                "malicious.exe",
                "application/x-msdownload",
                "executable-binary-content".getBytes()
        );

        assertThrows(StorageException.class, () -> {
            fileStorageService.storeMedia(file, "posts");
        });
    }

    @Test
    @DisplayName("Upload file an toàn: Từ chối file .exe giả mạo đuôi .jpg")
    void testRejectFakeExtensionFile() {
        MockMultipartFile file = new MockMultipartFile(
                "media",
                "malicious.jpg",
                "application/x-msdownload",
                "executable-binary-content".getBytes()
        );

        assertThrows(StorageException.class, () -> {
            fileStorageService.storeMedia(file, "posts");
        });
    }

    @Test
    @DisplayName("Upload file an toàn: Chấp nhận file ảnh hợp lệ và đổi tên UUID")
    void testStoreValidImage() {
        MockMultipartFile file = new MockMultipartFile(
                "media",
                "avatar-test.png",
                "image/png",
                new byte[]{1, 2, 3, 4}
        );

        FileStorageService.UploadResult result = fileStorageService.storeMedia(file, "posts");
        assertThat(result).isNotNull();
        assertThat(result.mediaType()).isEqualTo(MediaType.IMAGE);
        assertThat(result.webPath()).startsWith("/uploads/posts/");
        assertThat(result.webPath()).endsWith(".png");

        // Xoa sau khi test
        fileStorageService.deleteFileByWebPath(result.webPath());
    }
}
