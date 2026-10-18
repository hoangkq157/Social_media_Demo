-- ============================================================
-- DU LIEU MAU cho demo mang xa hoi Nhom 2 (Spring Boot + H2)
-- Spring Boot tu chay file nay ngay sau khi Hibernate tao bang
-- (nho spring.jpa.defer-datasource-initialization=true).
-- CSDL in-memory => moi lan khoi dong app la bo du lieu sach nhu moi.
-- ============================================================

-- Bang users (3 ban)
INSERT INTO users (id, username, email, password, full_name, created_at) VALUES
(1, 'an',   'an@social.vn',   '123456', 'Người dùng A', '2026-10-10 08:00:00'),
(2, 'binh', 'binh@social.vn', '123456', 'Người dùng B', '2026-10-10 08:05:00'),
(3, 'chi',  'chi@social.vn',  '123456', 'Người dùng C', '2026-10-10 08:10:00');

-- Bang posts (6 bai)
INSERT INTO posts (id, user_id, title, content, created_at) VALUES
(1, 1, 'Chào cả nhà! Dự án Spring Boot của nhóm mình đã chạy được rồi 🎉',
 'Hôm nay mình vừa khởi tạo xong dự án Spring Boot từ start.spring.io, app lên Tomcat chỉ sau vài giây. Cảm giác tuyệt vời khi không phải cấu hình XML như Spring truyền thống!',
 '2026-10-15 09:00:00'),
(2, 2, 'H2 Console — xem dữ liệu ngay trong trình duyệt',
 'Mình vừa phát hiện trang /h2-console: đăng nhập bằng user "sa", dán đúng JDBC URL là SELECT được thẳng trong trình duyệt. Demo ngày báo cáo khỏi cần cài thêm phần mềm gì!',
 '2026-10-15 10:30:00'),
(3, 3, 'Thymeleaf + Bootstrap làm giao diện nhanh bất ngờ',
 'th:each để vòng lặp danh sách bài viết, th:if để hiện thông báo lỗi, ghép card của Bootstrap là có ngay bảng tin đẹp. Server-side rendering dễ hơn mình tưởng nhiều.',
 '2026-10-16 14:00:00'),
(4, 1, 'Kiến trúc phân tầng Controller – Service – Repository',
 'Tìm hiểu xong luồng xử lý: trình duyệt → DispatcherServlet → Controller → Service → Repository → H2 rồi Thymeleaf render HTML trả về. Mỗi tầng một trách nhiệm, code sạch hẳn.',
 '2026-10-16 20:00:00'),
(5, 2, 'Spring Data JPA: chỉ cần khai báo interface là có sẵn CRUD',
 'UserRepository extends JpaRepository là đủ save, findById, findAll... thậm chí tự sinh câu truy vấn từ tên phương thức findByUsername. Thật sự tiện!',
 '2026-10-17 11:00:00'),
(6, 3, 'Non-postback là gì? Mình ở đây mới hiểu',
 'Spring MVC theo mô hình non-postback: mỗi request độc lập, server không giữ trạng thái trang như ASP.NET WebForms thời xưa. Bình luận bằng fetch/AJAX có thể cập nhật một vùng nhỏ mà không tải lại cả trang.',
 '2026-10-17 16:30:00');

-- Bang comments (10 binh luan)
INSERT INTO comments (id, post_id, user_id, content, created_at) VALUES
(1,  1, 2, 'Chúc mừng cả nhóm! Mình cũng vừa chạy được trên máy.', '2026-10-15 09:15:00'),
(2,  1, 3, 'Auto-configuration đúng là ma thuật, hãy tìm hiểu cách nó hoạt động nhé.', '2026-10-15 09:40:00'),
(3,  2, 1, 'Đúng rồi, hôm qua mình còn dùng nó kiểm tra data.sql có nạp đủ không đấy.', '2026-10-15 11:00:00'),
(4,  2, 3, 'Nhớ dán đúng chuỗi JDBC có cả ;MODE=MySQL;DB_CLOSE_DELAY=-1 nhé mọi người.', '2026-10-15 11:20:00'),
(5,  3, 1, 'Trang feed với card của Bootstrap nhìn ổn thật, chuẩn để demo.', '2026-10-16 14:30:00'),
(6,  3, 2, 'Fragment dùng chung navbar cũng tiện, khỏi lặp code.', '2026-10-16 15:00:00'),
(7,  4, 2, 'Sơ đồ 7 bước này nên vẽ vào slide, phần này dễ được hỏi.', '2026-10-16 20:30:00'),
(8,  4, 3, 'Controller không chứa SQL, Repository không chứa nghiệp vụ — ghi nhớ!', '2026-10-16 21:00:00'),
(9,  5, 3, 'findByUsername tự sinh truy vấn, khỏi viết SQL thủ công.', '2026-10-17 11:30:00'),
(10, 6, 1, 'Phần này dễ bị hỏi khi bảo vệ đó, chuẩn bị kỹ 2-3 câu nói nhé.', '2026-10-17 17:00:00');

-- Don bo dem tu tang (de app them bai/binh luan moi khong trung id)
ALTER TABLE users    ALTER COLUMN id RESTART WITH 4;
ALTER TABLE posts    ALTER COLUMN id RESTART WITH 7;
ALTER TABLE comments ALTER COLUMN id RESTART WITH 11;
