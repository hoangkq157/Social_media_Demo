package com.nhom2.social.storage;

import com.nhom2.social.config.AppProperties;
import com.nhom2.social.entity.MediaType;
import com.nhom2.social.exception.StorageException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service quan ly luu tru file upload:
 * - Kiem tra ca MIME Content-Type va duoi file
 * - Gioi han dung luong theo loai (anh <= 5MB, video <= 50MB)
 * - Doi ten bang UUID tranh trung lap va tranh injection
 * - Chong Path Traversal bang normalize()
 * - Ho tro xoa file cu khi cap nhat anh/bai viet
 */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private static final Map<String, String> ALLOWED_IMAGE_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/gif", ".gif",
            "image/webp", ".webp"
    );

    private static final Map<String, String> ALLOWED_VIDEO_TYPES = Map.of(
            "video/mp4", ".mp4",
            "video/webm", ".webm"
    );

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS = Set.of(".mp4", ".webm");

    private final Path rootLocation;
    private final long maxImageBytes;
    private final long maxVideoBytes;

    public FileStorageService(AppProperties appProperties) {
        this.rootLocation = Paths.get(appProperties.getUpload().getDir()).toAbsolutePath().normalize();
        this.maxImageBytes = (long) appProperties.getUpload().getMaxImageMb() * 1024 * 1024;
        this.maxVideoBytes = (long) appProperties.getUpload().getMaxVideoMb() * 1024 * 1024;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation.resolve("posts"));
            Files.createDirectories(rootLocation.resolve("avatars"));
            Files.createDirectories(rootLocation.resolve("covers"));
        } catch (IOException e) {
            throw new StorageException("Không thể khởi tạo thư mục lưu trữ file: " + rootLocation, e);
        }
    }

    public UploadResult storeMedia(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        if (contentType == null || originalFilename == null) {
            throw new StorageException("File tải lên không hợp lệ");
        }

        String ext = getFileExtension(originalFilename).toLowerCase();
        boolean isImage = ALLOWED_IMAGE_TYPES.containsKey(contentType) && ALLOWED_IMAGE_EXTENSIONS.contains(ext);
        boolean isVideo = ALLOWED_VIDEO_TYPES.containsKey(contentType) && ALLOWED_VIDEO_EXTENSIONS.contains(ext);

        if (!isImage && !isVideo) {
            throw new StorageException("Định dạng file không được hỗ trợ! Chỉ chấp nhận ảnh (JPG, PNG, GIF, WEBP) hoặc video (MP4, WEBM).");
        }

        if (isImage && file.getSize() > maxImageBytes) {
            throw new StorageException("Dung lượng ảnh vượt quá " + (maxImageBytes / (1024 * 1024)) + "MB!");
        }

        if (isVideo && file.getSize() > maxVideoBytes) {
            throw new StorageException("Dung lượng video vượt quá " + (maxVideoBytes / (1024 * 1024)) + "MB!");
        }

        String standardExt = isImage ? ALLOWED_IMAGE_TYPES.get(contentType) : ALLOWED_VIDEO_TYPES.get(contentType);
        String filename = UUID.randomUUID().toString().replace("-", "") + standardExt;

        Path destinationDir = rootLocation.resolve(subFolder).normalize();
        if (!destinationDir.startsWith(rootLocation)) {
            throw new StorageException("Đường dẫn lưu trữ không hợp lệ!");
        }

        Path destinationFile = destinationDir.resolve(filename).normalize();
        if (!destinationFile.startsWith(destinationDir)) {
            throw new StorageException("Tên file chứa ký tự không hợp lệ!");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Lỗi trong quá trình ghi file lên máy chủ", e);
        }

        MediaType mediaType = isImage ? MediaType.IMAGE : MediaType.VIDEO;
        String webPath = "/uploads/" + subFolder + "/" + filename;
        return new UploadResult(webPath, mediaType);
    }

    public void deleteFileByWebPath(String webPath) {
        if (webPath == null || !webPath.startsWith("/uploads/")) {
            return;
        }
        try {
            String relative = webPath.substring("/uploads/".length());
            Path target = rootLocation.resolve(relative).normalize();
            if (target.startsWith(rootLocation) && Files.exists(target)) {
                Files.delete(target);
            }
        } catch (Exception e) {
            log.warn("Không thể xóa file cũ tại {}: {}", webPath, e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : "";
    }

    public record UploadResult(String webPath, MediaType mediaType) {}
}
