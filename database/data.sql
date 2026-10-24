-- ============================================================
-- DU LIEU MAU — CSDL mang xa hoi SpringBook (Nhom 2)
-- Cu phap chuan MySQL (mat khau '123456' ma hoa BCrypt)
-- ============================================================

USE social_db;

-- Bang users (5 nguoi dung)
INSERT INTO users (id, username, email, password, full_name, bio, avatar_path, cover_path, created_at) VALUES
(1, 'an',   'an@social.vn',   '$2a$10$fqhU8s.aV3.6NFp..MIjhOhlOMvY5TA5GQrJwEt9WKbCgjGgS3ld2', 'Nguyễn Văn An',  'Đam mê Spring Boot & kiến trúc phần mềm hướng đối tượng.', NULL, NULL, '2026-10-10 08:00:00'),
(2, 'binh', 'binh@social.vn', '$2a$10$fqhU8s.aV3.6NFp..MIjhOhlOMvY5TA5GQrJwEt9WKbCgjGgS3ld2', 'Trần Thị Bình',  'Thích thiết kế UI/UX tối giản và Thymeleaf Server-Side Rendering.', NULL, NULL, '2026-10-10 08:05:00'),
(3, 'chi',  'chi@social.vn',  '$2a$10$fqhU8s.aV3.6NFp..MIjhOhlOMvY5TA5GQrJwEt9WKbCgjGgS3ld2', 'Lê Hoàng Chi',   'Backend Developer | Clean Architecture & JPA Optimizer.', NULL, NULL, '2026-10-10 08:10:00'),
(4, 'dung', 'dung@social.vn', '$2a$10$fqhU8s.aV3.6NFp..MIjhOhlOMvY5TA5GQrJwEt9WKbCgjGgS3ld2', 'Phạm Tiến Dũng', 'Sinh viên IT chuyên ngành CNPM - Nhóm 2.', NULL, NULL, '2026-10-11 09:00:00'),
(5, 'hoa',  'hoa@social.vn',  '$2a$10$fqhU8s.aV3.6NFp..MIjhOhlOMvY5TA5GQrJwEt9WKbCgjGgS3ld2', 'Vũ Quỳnh Hoa',   'Yêu thích kiểm thử tự động và bảo mật Spring Security.', NULL, NULL, '2026-10-11 09:30:00');

-- Bang posts (11 bai viet)
INSERT INTO posts (id, user_id, content, media_path, media_type, created_at) VALUES
(1, 1, 'Chào cả nhà! Dự án SpringBook của Nhóm 2 đã hoàn thành nâng cấp giao diện phong cách Facebook. Kiến trúc phân tầng rõ ràng, code sạch sẽ!', '/seed/seed1.jpg', 'IMAGE', '2026-10-15 09:00:00'),
(2, 2, 'Giao diện mới với Bootstrap 5 nội bộ, không phụ thuộc CDN hay mạng bên ngoài, sẵn sàng chạy mượt mà khi bảo vệ đồ án.', '/seed/seed2.jpg', 'IMAGE', '2026-10-15 10:30:00'),
(3, 3, 'Spring Data JPA tối ưu hóa với @EntityGraph giải quyết triệt để vấn đề N+1 query khi hiển thị bảng tin và trang cá nhân.', '/seed/seed3.jpg', 'IMAGE', '2026-10-16 14:00:00'),
(4, 5, 'Toàn bộ hệ thống bảo mật đã được nâng cấp lên Spring Security với chuẩn BCrypt và CSRF bảo vệ toàn diện các form và API AJAX.', '/seed/seed4.jpg', 'IMAGE', '2026-10-16 16:00:00'),
(5, 1, 'Kiến trúc phân tầng Controller – Service – Repository: Controller chỉ nhận request và trả view/JSON, Service xử lý nghiệp vụ với @Transactional, Repository chỉ truy vấn dữ liệu.', NULL, 'NONE', '2026-10-16 20:00:00'),
(6, 2, 'Trang cá nhân hiện đã hỗ trợ lọc bài viết theo 3 tab: Tất cả bài viết, Ảnh và Video, hiển thị dạng lưới tiện lợi.', NULL, 'NONE', '2026-10-17 11:00:00'),
(7, 3, 'Chức năng Like và Comment hiện đã hoạt động hoàn toàn bằng AJAX non-postback, cập nhật tức thì vào giao diện mà không cần reload trang.', NULL, 'NONE', '2026-10-17 16:30:00'),
(8, 4, 'H2 Console vẫn được duy trì tại /h2-console để thầy cô và các bạn kiểm tra trực tiếp cấu trúc bảng và dữ liệu trong quá trình vấn đáp.', NULL, 'NONE', '2026-10-18 08:30:00'),
(9, 5, 'Bộ lọc tìm kiếm đã hỗ trợ tìm cả người dùng theo username/họ tên lẫn tìm bài viết theo nội dung rất nhanh chóng.', NULL, 'NONE', '2026-10-18 10:00:00'),
(10, 1, 'Các quy tắc an toàn upload file: kiểm tra MIME type, đuôi mở rộng, giới hạn 5MB cho ảnh và 50MB cho video, đổi tên UUID chống xung đột.', NULL, 'NONE', '2026-10-18 14:20:00'),
(11, 2, 'Mọi người nhớ kiểm tra kỹ kịch bản demo và các câu hỏi lý thuyết về Spring Boot trước ngày bảo vệ nhé!', NULL, 'NONE', '2026-10-18 18:00:00');

-- Bang comments (10 binh luan)
INSERT INTO comments (id, post_id, user_id, content, created_at) VALUES
(1,  1, 2, 'Chúc mừng nhóm! Giao diện mới nhìn hiện đại và chuyên nghiệp hơn hẳn.', '2026-10-15 09:15:00'),
(2,  1, 3, 'Phân tầng Controller - Service - DTO rất chuẩn mẫu hướng đối tượng.', '2026-10-15 09:40:00'),
(3,  2, 1, 'Việc nhúng sẵn CSS/JS offline giúp demo an toàn tuyệt đối, không sợ mất mạng.', '2026-10-15 11:00:00'),
(4,  3, 4, 'EntityGraph giúp giảm đáng kể số lượng câu lệnh SQL gửi xuống CSDL.', '2026-10-16 14:30:00'),
(5,  4, 2, 'Mật khẩu đã được mã hóa BCrypt hoàn toàn, rất an toàn.', '2026-10-16 16:30:00'),
(6,  5, 3, 'Service trả về DTO record thay vì entity ra ngoài view, đúng chuẩn best practice.', '2026-10-16 20:30:00'),
(7,  6, 5, 'Tab Ảnh dạng lưới hiển thị rất trực quan.', '2026-10-17 11:30:00'),
(8,  7, 1, 'Like và Comment phản hồi ngay lập tức, trải nghiệm người dùng rất mượt.', '2026-10-17 17:00:00'),
(9,  8, 2, 'Nhớ chuẩn bị sẵn câu trả lời về lý do chọn H2 in-memory nhé.', '2026-10-18 09:00:00'),
(10, 9, 4, 'Tìm kiếm không phân biệt hoa thường hoạt động rất chuẩn.', '2026-10-18 10:15:00');

-- Bang post_likes (12 luot thich)
INSERT INTO post_likes (id, post_id, user_id, created_at) VALUES
(1,  1, 2, '2026-10-15 09:10:00'),
(2,  1, 3, '2026-10-15 09:12:00'),
(3,  1, 4, '2026-10-15 09:20:00'),
(4,  2, 1, '2026-10-15 10:45:00'),
(5,  2, 3, '2026-10-15 11:00:00'),
(6,  3, 1, '2026-10-16 14:15:00'),
(7,  3, 2, '2026-10-16 14:20:00'),
(8,  4, 1, '2026-10-16 16:10:00'),
(9,  4, 3, '2026-10-16 16:15:00'),
(10, 5, 2, '2026-10-16 20:15:00'),
(11, 6, 1, '2026-10-17 11:15:00'),
(12, 7, 2, '2026-10-17 16:45:00');

-- Bang friendships (quan he ban be va loi moi ket ban)
INSERT INTO friendships (id, sender_id, receiver_id, status, created_at, updated_at) VALUES
(1, 1, 2, 'ACCEPTED', '2026-10-12 10:00:00', '2026-10-12 10:30:00'),
(2, 1, 3, 'ACCEPTED', '2026-10-12 11:00:00', '2026-10-12 11:15:00'),
(3, 2, 3, 'ACCEPTED', '2026-10-13 09:00:00', '2026-10-13 09:30:00'),
(4, 4, 1, 'PENDING',  '2026-10-18 08:00:00', '2026-10-18 08:00:00'),
(5, 5, 1, 'PENDING',  '2026-10-18 09:30:00', '2026-10-18 09:30:00');
