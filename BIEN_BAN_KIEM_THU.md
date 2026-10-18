# BIÊN BẢN KIỂM THỬ — DEMO MẠNG XÃ HỘI NHÓM 2 (Spring Boot + H2)

- **Ngày kiểm thử:** 18/10/2026
- **Môi trường:** Windows 10, JDK 21 (Temurin 21.0.12.1), Spring Boot 4.1.1, H2 in-memory, Maven
- **Cách chạy test:** gửi HTTP request thật đến app đang chạy (curl) + kiểm chứng dữ liệu qua /h2-console
- **Commit được kiểm thử:** `b7e72c5`

## Kết quả tổng hợp: **14/14 PASS — 0 lỗi nghiêm trọng**

| # | Test case | Kỳ vọng | Kết quả |
|---|---|---|---|
| 1 | Vào `/` khi chưa đăng nhập | Chuyển hướng về `/login` | ✅ PASS (302 → /login) |
| 2 | Mở trang `/login` | Hiện form username/password | ✅ PASS |
| 3 | Đăng nhập SAI mật khẩu | Báo lỗi đỏ, không vào được | ✅ PASS (302 → /login?error) |
| 4 | Trang `/login?error` | Hiện dòng "Sai tên đăng nhập hoặc mật khẩu!" | ✅ PASS |
| 5 | Đăng nhập ĐÚNG (an/123456) | Tạo phiên, vào bảng tin | ✅ PASS (302 → /) |
| 6 | Bảng tin hiển thị dữ liệu mẫu | Đủ 6 bài mẫu + tên tác giả | ✅ PASS |
| 7 | Đăng bài viết mới (POST /posts/create) | Lưu bài, quay lại bảng tin | ✅ PASS (302 → /) |
| 8 | Bài vừa đăng xuất hiện trên bảng tin | Có tiêu đề bài mới ngay đầu trang | ✅ PASS |
| 9 | Bình luận bài viết (POST /comments/create) | Lưu bình luận, về đúng bài | ✅ PASS (302 → /#post-2) |
| 10 | Bình luận hiển thị dưới bài | Có nội dung + tên người bình luận | ✅ PASS |
| 11 | Bình luận toàn khoảng trắng | Không lưu vào CSDL | ✅ PASS (không ghi) |
| 12 | Đếm bản ghi qua /h2-console sau test | users=3, posts=7 (6 mẫu + 1 test), comments=11 (10 mẫu + 1 test) | ✅ PASS |
| 13 | Đăng xuất rồi vào `/` | Bị đẩy về `/login` | ✅ PASS (302) |
| 14 | Khởi động lại app | data.sql tự nạp lại bộ dữ liệu chuẩn (sạch như mới) | ✅ PASS |

## Ghi chú kỹ thuật
- Test case 7–10 lần đầu gửi từ terminal Windows bị mã hóa tiếng Việt hỏng (lỗi của **công cụ test**, không phải app): server nhận chuỗi byte không hợp lệ UTF-8 nên từ chối tham số — đúng hành vi an toàn. Khi gửi đúng UTF-8 (như trình duyệt thật vẫn làm) thì PASS toàn bộ. Kết luận: **từ trình duyệt (Chrome/Firefox/Edge) app hoạt động bình thường với tiếng Việt**.
- Mọi thao tác ghi đều kiểm chứng thấy bản ghi thật trong CSDL qua /h2-console (SELECT COUNT + SELECT dữ liệu).

## Checklist đối chiếu đề bài
| Yêu cầu đề | Đạt | Bằng chứng |
|---|---|---|
| Dùng CSDL mạng xã hội 3 bảng có sẵn (users + cột password, posts, comments) | ✅ | entity + schema.sql + /h2-console |
| Demo ≥ 2 tính năng dựa trên 3 bảng | ✅ | 3 tính năng (vượt yêu cầu) |
| Có giao diện, thao tác ≥ 2 bảng | ✅ | Thymeleaf + Bootstrap; demo chạm cả 3 bảng |
| CSDL mã nguồn mở / miễn phí | ✅ | H2 (EPL/MPL), JDK, VS Code, Maven, Git đều miễn phí |
| Code lên Git kèm script CSDL | ✅ | github.com/hoangkq157/Social_media_Demo + database/schema.sql + data.sql |

*Biên bản lập bởi quá trình kiểm thử tự động có bằng chứng log kèm theo.*
