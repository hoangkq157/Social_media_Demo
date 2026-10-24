package com.nhom2.social.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * Bo xu ly ngoai le tap trung toan cuc (@ControllerAdvice).
 * Giup ung dung khong bi loi 500 mac dinh cua Tomcat,
 * tra ve giao dien loi than thien hoac JSON error cho API.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNotFound(NotFoundException ex, HttpServletRequest request, Model model) {
        log.warn("404 Not Found tai {}: {}", request.getRequestURI(), ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
        model.addAttribute("status", 404);
        model.addAttribute("error", "Không tìm thấy nội dung");
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNoResourceFound(org.springframework.web.servlet.resource.NoResourceFoundException ex, HttpServletRequest request, Model model) {
        log.debug("404 Resource Not Found tai {}: {}", request.getRequestURI(), ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
        model.addAttribute("status", 404);
        model.addAttribute("error", "Không tìm thấy tài nguyên");
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object handleForbidden(Exception ex, HttpServletRequest request, Model model) {
        log.warn("403 Forbidden tai {}: {}", request.getRequestURI(), ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
        }
        model.addAttribute("status", 403);
        model.addAttribute("error", "Truy cập bị từ chối");
        model.addAttribute("message", ex.getMessage());
        return "error/403";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleMaxSizeException(MaxUploadSizeExceededException ex, HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
        log.warn("Upload vuot dung luong toi da tai {}: {}", request.getRequestURI(), ex.getMessage());
        String msg = "Dung lượng file tải lên vượt quá giới hạn cho phép (tối đa 50MB)!";
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(Map.of("error", msg));
        }
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:" + referer;
        }
        model.addAttribute("status", 413);
        model.addAttribute("error", "Dung lượng file quá lớn");
        model.addAttribute("message", msg);
        return "error/500";
    }

    @ExceptionHandler(StorageException.class)
    public Object handleStorageException(StorageException ex, HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
        log.error("Loi luu tru file tai {}: {}", request.getRequestURI(), ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:" + referer;
        }
        model.addAttribute("status", 400);
        model.addAttribute("error", "Lỗi tải file");
        model.addAttribute("message", ex.getMessage());
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleGeneralException(Exception ex, HttpServletRequest request, Model model) {
        log.error("Loi he thong tai {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi máy chủ nội bộ"));
        }
        model.addAttribute("status", 500);
        model.addAttribute("error", "Lỗi máy chủ nội bộ");
        model.addAttribute("message", "Đã có lỗi xảy ra trên hệ thống. Vui lòng thử lại sau.");
        return "error/500";
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/") || "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}
