# Danh sách route/endpoint — SpringBook (Nhóm 2)

| Method | Đường dẫn | Tham số nhận | Kết quả trả về |
|---|---|---|---|
| GET | `/login` | — | Trang đăng nhập (Spring Security form login) |
| POST | `/login` | `username`, `password`, `_csrf` | Đúng → chuyển về `/`; sai → `/login?error=true` |
| POST | `/logout` | `_csrf` | Đăng xuất, hủy session → về `/login?logout=true` |
| GET | `/register` | — | Form đăng ký tài khoản mới |
| POST | `/register` | `username`, `fullName`, `email`, `password`, `_csrf` | Tạo tài khoản (BCrypt) → đăng nhập ngay |
| GET | `/` | `page` (tùy chọn) | Bảng tin (Feed): composer, bài viết, tải thêm, lời mời kết bạn |
| GET | `/feed/more` | `page` | Trả về Thymeleaf HTML fragment các bài viết tiếp theo |
| POST | `/posts` | `content`, `media`, `_csrf` | Đăng bài mới (văn bản/ảnh/video) → redirect `/` |
| POST | `/posts/{id}/delete` | `_csrf` | Xóa bài viết (chỉ chủ bài) → redirect `/` |
| POST | `/api/posts/{id}/like` | — (kèm CSRF header) | Toggle like/bỏ like → JSON `{liked, likeCount}` |
| POST | `/api/posts/{id}/comments` | JSON `{content}` (kèm CSRF header) | Thêm bình luận mới → JSON `CommentView` |
| GET | `/search` | `q`, `type` (`users` hoặc `posts`) | Kết quả tìm kiếm người dùng hoặc bài viết |
| GET | `/users/{username}` | `tab` (`all`, `photos`, `videos`) | Trang cá nhân người dùng kèm ảnh bìa, avatar, bộ lọc |
| GET | `/settings/profile` | — | Trang cập nhật họ tên, bio, email, đổi avatar/ảnh bìa |
| POST | `/settings/profile` | `fullName`, `bio`, `email`, `avatarFile`, `coverFile`, `_csrf` | Lưu thông tin cá nhân → redirect |
| POST | `/settings/password` | `currentPassword`, `newPassword`, `confirmPassword`, `_csrf` | Đổi mật khẩu |
| GET | `/friends` | `tab` (`requests`, `all`, `suggestions`) | Trang quản lý bạn bè: Lời mời kết bạn, Bạn bè, Gợi ý |
| POST | `/api/friends/request/{targetUsername}` | — (kèm CSRF header) | Gửi lời mời kết bạn → JSON `FriendStatusResponse` |
| POST | `/api/friends/accept/{senderUsername}` | — (kèm CSRF header) | Chấp nhận kết bạn → JSON `FriendStatusResponse` |
| POST | `/api/friends/reject/{senderUsername}` | — (kèm CSRF header) | Từ chối/xóa lời mời kết bạn → JSON `FriendStatusResponse` |
| POST | `/api/friends/cancel/{targetUsername}` | — (kèm CSRF header) | Hủy lời mời kết bạn đã gửi → JSON `FriendStatusResponse` |
| POST | `/api/friends/unfriend/{targetUsername}` | — (kèm CSRF header) | Hủy kết bạn → JSON `FriendStatusResponse` |
| GET | `/h2-console` | JDBC URL: `jdbc:h2:mem:socialdb`, user `sa` | Trang quản trị CSDL H2 in-memory |

**Tài khoản mẫu** (nạp tự động từ `data.sql`): `an` / `binh` / `chi` / `dung` / `hoa` — mật khẩu `123456`.
