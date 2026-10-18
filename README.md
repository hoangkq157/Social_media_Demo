# nhom2-springboot-social

Demo **mạng xã hội đơn giản** bằng **Spring Boot** — bài giữa kì môn PTPM hướng đối tượng, **Nhóm 2** (chủ đề: Spring Boot).

## Tính năng demo (theo kế hoạch)
1. **Đăng nhập** — bảng `users` (cột `password` bổ sung theo yêu cầu đề)
2. **Đăng bài viết** — bảng `posts`
3. **Bình luận bài viết** — bảng `comments`

## Công nghệ
| Thành phần | Lựa chọn |
|---|---|
| Ngôn ngữ | Java 21 (LTS) |
| Framework | Spring Boot 4.x (Spring MVC + Thymeleaf) |
| Tầng dữ liệu | Spring Data JPA (Hibernate) |
| CSDL | **H2 Database** (in-memory, nhúng trong app — **không cần cài CSDL**) |
| Giao diện | Thymeleaf + Bootstrap 5 |
| Build | Maven |

## Yêu cầu môi trường (chỉ 3 thứ, KHÔNG cần cài CSDL)
1. **JDK 21** (bản Eclipse Temurin — adoptium.net; nhớ tick *Add to PATH* + *Set JAVA_HOME*)
2. **VS Code** + 2 extension: *Extension Pack for Java* (Microsoft), *Spring Boot Extension Pack* (VMware)
3. **Git for Windows**

Kiểm tra: mở terminal gõ `java -version` (phải ra 21.x) và `git --version`.

## Cách chạy (3 bước)
```bash
git clone <link-repo-này>
cd nhom2-springboot-social
.\mvnw.cmd spring-boot:run
```
App chạy tại **http://localhost:8080** (lần đầu chạy Maven tự tải dependency vài phút).

### Xem CSDL khi demo
Mở **http://localhost:8080/h2-console** — dán đúng JDBC URL:
`jdbc:h2:mem:socialdb;MODE=MySQL;DB_CLOSE_DELAY=-1`, User Name `sa`, Password để trống → **Connect** → chạy `SELECT * FROM users;`

## Cơ chế CSDL H2
- CSDL chạy **trong RAM**: mỗi lần khởi động app là bộ mới sạch, bảng do Hibernate tạo từ entity.
- Dữ liệu mẫu trong `src/main/resources/data.sql` được **Spring Boot tự nạp** ngay sau khi tạo bảng (nhờ `spring.jpa.defer-datasource-initialization=true`).
- Script CSDL nộp kèm repo: `database/schema.sql` (tạo bảng) + `database/data.sql` (dữ liệu mẫu) — xem mục *Cấu trúc thư mục*.
- Muốn dữ liệu tồn tại sau khi tắt app: đổi URL trong `application.properties` thành `jdbc:h2:file:./data/socialdb` (file `data/socialdb.mv.db` chính là bản backup).

## Cấu trúc thư mục
```
src/main/java/com/nhom2/social/
  ├── SocialApplication.java      # lớp main
  ├── entity/                     # các entity ánh xạ bảng CSDL
  └── repository/                 # repository Spring Data JPA
src/main/resources/
  ├── application.properties      # cấu hình H2 + JPA
  ├── data.sql                    # dữ liệu mẫu tự nạp mỗi lần chạy
  └── templates/                  # trang Thymeleaf (Gói 6 sẽ làm)
database/                         # script CSDL nộp kèm (Gói 4 sẽ hoàn thiện)
```

## Quy ước commit
Message bắt đầu bằng `feat:` (thêm tính năng) · `fix:` (sửa lỗi) · `docs:` (tài liệu). Làm nhánh `main`, **pull trước khi push**.
