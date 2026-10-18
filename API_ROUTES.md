# Danh sách route/endpoint — SocialNhóm2 (demo Nhóm 2)

| Method | Đường dẫn | Tham số nhận | Kết quả trả về |
|---|---|---|---|
| GET | `/login` | — | Trang đăng nhập (form username/password) |
| POST | `/login` | `username`, `password` | Đúng → tạo session, chuyển về bảng tin `/`; sai → `/login?error` (hiện báo lỗi đỏ) |
| GET | `/logout` | — | Xóa session → về `/login` |
| GET | `/` | — (cần session) | Bảng tin: danh sách bài viết mới nhất + bình luận từng bài |
| GET | `/posts/new` | — (cần session) | Form đăng bài viết |
| POST | `/posts/create` | `title`, `content` | Lưu bài vào bảng `posts` → về bảng tin |
| POST | `/comments/create` | `postId`, `content` | Lưu bình luận vào bảng `comments` → về bài vừa bình luận |
| GET | `/h2-console` | đăng nhập console (JDBC URL, user `sa`) | Trang quản trị CSDL — chạy SELECT xem 3 bảng khi demo |

**Truy cập chưa đăng nhập**: mọi route cần session đều chuyển về `/login` (kiểm tra ở đầu controller).

**Tài khoản mẫu** (nạp tự động từ `data.sql`): `an` / `binh` / `chi` — mật khẩu `123456`.
