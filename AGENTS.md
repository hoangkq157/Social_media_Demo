# AGENTS.md — Nâng cấp SpringBook thành mạng xã hội kiểu Facebook (Spring Boot)

> File này dành cho coding agent (Claude Code, Codex, Cursor, Copilot...). Đặt ở **thư mục gốc project** (cạnh `pom.xml`).
> Dùng Claude Code thì copy/đổi tên thành `CLAUDE.md`. Đọc **toàn bộ** file trước khi viết dòng code đầu tiên.

---

## 1. Mục tiêu

Project hiện tại là demo môn PTPM hướng đối tượng (Nhóm 2): đăng nhập, đăng bài (tiêu đề + nội dung), bình luận. UI còn đơn giản.

Nâng cấp thành **mạng xã hội mini giống Facebook** với đúng các chức năng cần thiết, nhưng **mục đích chính là trình bày Spring Boot**:
code phải sạch, phân tầng rõ, dùng đúng các tính năng đặc trưng của Spring để sinh viên có thể mở file ra và giải thích khi bảo vệ.

**Không làm** thứ ngoài phạm vi: nhắn tin realtime, thông báo, story, nhóm, kết bạn phức tạp, React/Vue, Docker, MySQL, microservice.

---

## 2. Hiện trạng (đã đọc code — đừng đoán lại)

| Mục | Hiện tại |
|---|---|
| Stack | Java 21, **Spring Boot 4.1.1**, Maven (`mvnw`), Thymeleaf, Spring Data JPA, H2 in-memory (`MODE=MySQL`) |
| Package gốc | `com.nhom2.social` → `controller/ service/ repository/ entity/` |
| Entity | `User(username,email,password,fullName,createdAt)`, `Post(user,title,content,createdAt,comments)`, `Comment(post,user,content,createdAt)` |
| Auth | Tự làm: lưu `username` vào `HttpSession`, **mỗi controller tự lặp lại đoạn kiểm tra session** |
| Mật khẩu | **Plain text** (`123456`), so sánh bằng `equals` |
| Dữ liệu mẫu | `src/main/resources/data.sql` (3 user `an/binh/chi`, 6 post, 10 comment), nạp nhờ `spring.jpa.defer-datasource-initialization=true` |
| UI | Bootstrap 5 **nhúng sẵn trong `static/`** (chạy offline, không CDN), avatar là chữ cái đầu (`User.getInitials()`, `getAvatarColor()`) |
| Tìm kiếm | Chỉ tìm **bài viết** theo `?q=` (title/content) |
| Tài liệu | `README.md`, `API_ROUTES.md`, `HUONG_DAN_CHAY_DEMO.md`, `BIEN_BAN_KIEM_THU.md`, `database/schema.sql`, `database/data.sql` |
| Chạy | `.\mvnw.cmd spring-boot:run` hoặc `chay_demo.bat` (JDK ở `C:\lab-tools\jdk21`) → http://localhost:8080 |

Vấn đề cần sửa trong lúc nâng cấp: lặp code kiểm tra session ở mọi controller; mật khẩu plain text; không validate input; `orElseThrow()` trần → lỗi 500; `Post.title` bắt buộc (Facebook không có tiêu đề); comment redirect `/#post-x` hỏng khi có phân trang; entity bị gọi lazy ngay trong template (rủi ro N+1); logout bằng `GET`.

---

## 3. Ràng buộc cứng (KHÔNG được vi phạm)

1. **Giữ** Java 21 + Spring Boot 4.1.1 + Maven. Không đổi version.
2. **Giữ H2 in-memory** làm mặc định; `mvnw spring-boot:run` phải chạy được mà **không cài thêm gì** và **không cần internet lúc chạy** (không CDN, không Google Fonts, không icon font từ mạng — tải về đặt vào `static/` hoặc dùng SVG/emoji inline).
3. **Giữ server-side rendering** (Spring MVC + Thymeleaf). JS chỉ là vanilla, ít, đặt trong `static/js/app.js`; không framework JS, không bundler, không Node.
4. **Không dùng Lombok**; code hiện tại viết getter/setter tay — giữ nhất quán (DTO dùng `record`).
5. Tài khoản mẫu `an / binh / chi`, mật khẩu `123456` **vẫn đăng nhập được** (nay lưu dạng BCrypt).
6. Dependency được phép thêm: `spring-boot-starter-security`, `spring-boot-starter-validation`, `spring-security-test` (test), và `spring-boot-starter-actuator` (tùy chọn). Muốn thêm thứ khác → dừng và hỏi.
7. **Spring Boot 4 đã tách starter thành module** (ví dụ project đang dùng `spring-boot-starter-webmvc`, `spring-boot-h2console`). **Không đoán tên artifact/package từ trí nhớ**: thêm xong phải `./mvnw -q dependency:resolve` và build; nếu import/artifact lỗi, tra tài liệu Spring Boot 4 hiện hành. Một số API cũ đã đổi (ví dụ `@MockBean` → `@MockitoBean`).
8. Không refactor hàng loạt line ending (file hiện là CRLF; `.gitattributes` đã có). Không format lại file không liên quan.
9. UTF-8 toàn bộ; thêm `server.servlet.encoding.force=true` (biên bản kiểm thử từng dính lỗi tiếng Việt).
10. **Không `git push`**. Commit cục bộ theo quy ước của repo: `feat:` / `fix:` / `docs:` / `test:` / `refactor:`.

---

## 4. Chức năng cần có

### 4.1 Tài khoản
- Đăng ký (`username`, `fullName`, `email`, `password`) có validate; username/email không trùng.
- Đăng nhập / đăng xuất bằng **Spring Security** (form login, BCrypt, CSRF bật).
- Trang chưa đăng nhập → chuyển `/login`. Đăng nhập xong → `/`.

### 4.2 Bảng tin (`/`)
- Ô soạn bài kiểu Facebook: nội dung + đính kèm **1 ảnh hoặc 1 video** (có xem trước trước khi đăng).
- Danh sách bài mới nhất trước, **phân trang** (10 bài/trang) với nút "Tải thêm" (nạp HTML fragment, không reload).
- Mỗi bài: avatar + tên (bấm vào sang trang cá nhân), thời gian, nội dung, ảnh/video (`<img>` / `<video controls>`), số like, số bình luận.
- **Like / bỏ like** (AJAX, cập nhật không reload, không like trùng).
- **Bình luận** inline (AJAX, thêm vào DOM ngay).
- Chủ bài mới thấy nút **Xóa bài** (có `confirm`).

### 4.3 Tìm kiếm người dùng (`/search?q=`)
- Ô tìm kiếm trên navbar. Kết quả: danh sách người dùng (khớp `username` hoặc `fullName`, không phân biệt hoa thường) dạng thẻ có avatar, tên, bio ngắn, nút "Xem trang cá nhân".
- Tab thứ hai "Bài viết" tìm theo nội dung (tận dụng chức năng tìm bài đã có).

### 4.4 Trang cá nhân (`/users/{username}`)
- Ảnh bìa, avatar lớn đè lên ảnh bìa, tên, `@username`, bio, ngày tham gia, số bài.
- **Bộ lọc dạng tab**: **Tất cả bài viết | Ảnh | Video** (`?tab=all|photos|videos`), có số lượng trên mỗi tab. Tab Ảnh/Video hiển thị dạng lưới.
- Nếu là trang của chính mình: có nút **Chỉnh sửa trang cá nhân**.

### 4.5 Chỉnh sửa thông tin cá nhân (`/settings/profile`)
- Sửa `fullName`, `bio`, `email`; đổi **avatar** và **ảnh bìa** (upload).
- Đổi mật khẩu (`/settings/password`): nhập mật khẩu cũ + mới + xác nhận.
- Có thông báo thành công/lỗi (flash message).

### 4.6 Giữ lại từ bản cũ
- H2 console (`/h2-console`) vẫn dùng được khi demo (cấu hình Security cho phép **chỉ** đường dẫn này, tắt CSRF/frame-options riêng cho nó).

---

## 5. Thiết kế dữ liệu

```
users      id, username(unique), email(unique), password(BCrypt), full_name, bio(≤255), avatar_path, cover_path, created_at
posts      id, user_id→users, content(TEXT, nullable nếu có media), media_path(nullable), media_type(ENUM STRING: NONE|IMAGE|VIDEO), created_at
comments   id, post_id→posts, user_id→users, content(≤1000), created_at
post_likes id, post_id→posts, user_id→users, created_at,  UNIQUE(post_id, user_id)
```

- Bỏ cột `title` của `posts` (migrate seed data: gộp title vào đầu content nếu cần).
- Quan hệ dùng `FetchType.LAZY`. Thêm `index` cho `posts(user_id, created_at)`, `posts(media_type)`.
- `Post.createdAt` nên đặt bằng `@PrePersist` hoặc `@CreationTimestamp`, không `= LocalDateTime.now()` lúc khai báo field.
- Tạo `enum MediaType`.
- Bắt buộc: bài phải có `content` **hoặc** `media`.

### Repository (thể hiện Spring Data JPA)
- Derived query: `findByUsername`, `existsByUsername`, `existsByEmail`, `countByUserId...`
- `@Query` JPQL: tìm user theo `username`/`fullName` (`lower(...) like lower(concat('%', :q, '%'))`).
- **`Pageable`/`Page<Post>`** cho bảng tin và trang cá nhân; lọc theo `MediaType` qua tham số (`findByUserIdAndMediaType...`).
- `@EntityGraph(attributePaths = "user")` hoặc `JOIN FETCH` để tránh N+1 khi load danh sách bài.
- Đếm like/comment bằng query `GROUP BY` / count, hoặc `hibernate.default_batch_fetch_size=50` — **không** vòng lặp gọi query từng bài.

---

## 6. Kiến trúc & cấu trúc package

```
com.nhom2.social
├── SocialApplication.java
├── config/       SecurityConfig, WebConfig (resource handler /uploads/**), AppProperties (@ConfigurationProperties)
├── controller/   AuthController, FeedController, ProfileController, SearchController, SettingsController   (@Controller → trả view)
│   └── api/      PostApiController   (@RestController → JSON: like, comment)
├── dto/          record: RegisterForm, ProfileForm, PasswordForm, PostView, CommentView, UserCard, ProfileView ...
├── entity/       User, Post, Comment, PostLike, MediaType
├── exception/    NotFoundException, ForbiddenException, StorageException, GlobalExceptionHandler (@ControllerAdvice)
├── repository/   UserRepository, PostRepository, CommentRepository, PostLikeRepository
├── service/      UserService (+UserDetailsService), PostService, CommentService, LikeService, ProfileService
└── storage/      FileStorageService
```

Quy tắc phân tầng (chính là điều cần trình bày):
- **Controller**: nhận request, validate (`@Valid` + `BindingResult`), gọi service, trả view/redirect/JSON. **Không** chứa SQL hay nghiệp vụ.
- **Service**: nghiệp vụ + `@Transactional` (đọc: `readOnly = true`). **Trả DTO**, không trả entity ra view.
- **Repository**: chỉ truy vấn.
- Dùng **constructor injection** (như code hiện tại), không `@Autowired` trên field.
- `spring.jpa.open-in-view=false` → mọi dữ liệu lazy phải được map sang DTO **trong** service.
- Người dùng hiện tại lấy bằng `@AuthenticationPrincipal` / `Principal`, **bỏ hẳn** `session.getAttribute("username")`.

---

## 7. Các tính năng Spring Boot phải thể hiện (checklist trình bày)

Mỗi mục phải có chỗ dùng thật trong code, và ghi vào `docs/SPRING_BOOT_MAP.md` (xem Phase 6):

1. **Auto-configuration + starter** (web, data-jpa, thymeleaf, security, validation)
2. **Dependency Injection** qua constructor, `@Component/@Service/@Repository/@Controller`, `@Bean` (`PasswordEncoder`, `SecurityFilterChain`)
3. **Spring MVC**: `@GetMapping/@PostMapping`, `@PathVariable`, `@RequestParam`, `@ModelAttribute`, `MultipartFile`, `RedirectAttributes` (flash)
4. **`@RestController` + `ResponseEntity`** cho API like/comment (JSON, mã 400/403/404 đúng)
5. **Spring Data JPA**: derived query, `@Query`, `Pageable`, `@EntityGraph`, ràng buộc unique
6. **`@Transactional`** ở service
7. **Bean Validation**: `@NotBlank @Size @Email` + thông báo tiếng Việt hiển thị trên form
8. **Spring Security**: `SecurityFilterChain`, `UserDetailsService`, BCrypt, CSRF, phân quyền theo chủ sở hữu
9. **`@ControllerAdvice` + `@ExceptionHandler`** + trang lỗi thân thiện (`templates/error/404.html`, `403.html`, `500.html`)
10. **`@ConfigurationProperties`** (`app.upload.dir`, `app.upload.max-image-mb`, `app.upload.max-video-mb`, `app.feed.page-size`)
11. **Cấu hình ngoại hóa** `application.properties` + **profile** (`application-dev.properties` / `application-file.properties` để chuyển H2 sang file nếu muốn lưu dữ liệu)
12. **Thymeleaf**: layout fragment, `th:each/th:if/th:object/th:field`, fragment trả về từ controller cho "Tải thêm", `sec:authorize` nếu extras tương thích Boot 4 (nếu không → truyền cờ từ controller)
13. **Serve file tĩnh từ ngoài classpath** bằng `WebMvcConfigurer#addResourceHandlers` (`/uploads/**` → thư mục `./uploads`)
14. **Test**: `@DataJpaTest`, `@WebMvcTest`/`@SpringBootTest + MockMvc`
15. (Tùy chọn) **Actuator** `/actuator/health`

---

## 8. Upload file — quy tắc an toàn

`FileStorageService`:
- Ảnh cho phép: `image/jpeg, image/png, image/gif, image/webp`; video: `video/mp4, video/webm`. Kiểm tra **cả Content-Type lẫn đuôi file**; sai → `StorageException` → thông báo tiếng Việt.
- Giới hạn: ảnh ≤ 5 MB, video ≤ 50 MB (đọc từ `AppProperties`; đồng bộ `spring.servlet.multipart.max-file-size=50MB`, `max-request-size=55MB`). Bắt `MaxUploadSizeExceededException` trong `GlobalExceptionHandler`.
- **Không dùng tên file người dùng gửi.** Đặt tên `UUID + đuôi chuẩn hóa`, lưu dưới `uploads/{posts|avatars|covers}/`.
- Chống path traversal: `normalize()` rồi kiểm tra đường dẫn vẫn nằm trong thư mục upload.
- Khi thay avatar/cover hoặc xóa bài → xóa file cũ (best-effort, lỗi xóa không làm hỏng request).
- `uploads/` phải nằm trong `.gitignore`. Video phải tua được (resource handler của Spring hỗ trợ HTTP Range — kiểm tra bằng cách thật sự tua).

---

## 9. Routes mục tiêu

| Method | Path | Mô tả |
|---|---|---|
| GET/POST | `/login` | Form đăng nhập (Spring Security xử lý POST) |
| GET/POST | `/register` | Đăng ký |
| POST | `/logout` | Đăng xuất (kèm CSRF token, **không** dùng GET) |
| GET | `/` | Bảng tin (`?page=`) |
| GET | `/feed/more?page=` | Trả **fragment** HTML các bài tiếp theo |
| POST | `/posts` | Đăng bài (multipart: `content`, `media`) |
| POST | `/posts/{id}/delete` | Xóa bài (chỉ chủ bài) |
| POST | `/api/posts/{id}/like` | Toggle like → `{liked, likeCount}` |
| POST | `/api/posts/{id}/comments` | Thêm bình luận → `CommentView` JSON (201) |
| GET | `/search?q=&type=users\|posts` | Tìm kiếm |
| GET | `/users/{username}?tab=all\|photos\|videos&page=` | Trang cá nhân + bộ lọc |
| GET/POST | `/settings/profile` | Sửa thông tin cá nhân (multipart) |
| POST | `/settings/password` | Đổi mật khẩu |
| GET | `/uploads/**` | File người dùng tải lên |
| GET | `/h2-console` | Giữ cho demo |

Xóa `/posts/new`, `/posts/create`, `/comments/create` cũ (đã thay thế). Cập nhật `API_ROUTES.md` cho khớp.

---

## 10. Hướng dẫn giao diện (giống Facebook, nhưng gọn)

**Tokens:** xanh `#1877F2`, nền trang `#f0f2f5`, card trắng bo `12px` + bóng nhẹ, chữ `#050505`, chữ phụ `#65676B`, nút hover `#e4e6eb`. Font hệ thống (`system-ui, "Segoe UI", Roboto, sans-serif`) — không tải font ngoài. Đặt CSS riêng trong `static/css/app.css` (xóa khối `<style>` inline trong `fragment.html`), dùng CSS variables.

**Layout 3 cột** (≥ 992px), thu về 1 cột trên mobile:
- **Navbar dính trên** (trắng, bóng nhẹ): logo `SpringBook` xanh bên trái + ô tìm kiếm bo tròn nền xám; ở giữa/phải: Trang chủ, Trang cá nhân; avatar mở **dropdown** (Trang cá nhân, Chỉnh sửa, Đăng xuất).
- **Cột trái**: thẻ người dùng hiện tại (avatar + tên) + lối tắt (Trang chủ, Trang cá nhân, Cài đặt).
- **Cột giữa**: composer → bài viết. Composer: avatar + ô "Bạn đang nghĩ gì, {tên}?", nút **Ảnh/Video** (input file ẩn), xem trước + nút ✕ gỡ file, nút Đăng (vô hiệu khi trống).
- **Cột phải**: "Người dùng khác" (vài user ngẫu nhiên/mới, đủ để thấy tìm kiếm có ý nghĩa), kèm giới thiệu dự án/nhóm (giữ phần "Thành viên", "Mẹo demo" cũ nhưng gọn lại).

**Thẻ bài viết:** header (avatar, tên đậm, thời gian · nhỏ, menu `⋯` có Xóa nếu là chủ bài) → nội dung (`white-space: pre-wrap`) → media (ảnh `max-height: 560px; object-fit: cover`, video `controls`) → dòng số like · số bình luận → thanh nút **👍 Thích / 💬 Bình luận** → danh sách bình luận (bong bóng xám bo tròn như cũ) → ô viết bình luận (Enter để gửi).

**Trang cá nhân:** ảnh bìa cao ~300px (gradient mặc định khi chưa có), avatar 168px viền trắng đè lên bìa, tên lớn, bio, **tab** gạch chân xanh (Bài viết / Ảnh / Video, kèm số lượng); cột trái "Giới thiệu" (bio, email nếu là mình, ngày tham gia), cột phải bài viết hoặc lưới ảnh/video 3 cột vuông (video có icon ▶).

**Chất lượng UX bắt buộc:** empty state có chữ hướng dẫn ở mọi danh sách; flash message tự ẩn sau vài giây; lỗi validate hiện ngay dưới trường; nút gửi bị khóa khi đang gửi để chống bấm đúp; like cập nhật lạc quan và hoàn tác nếu lỗi; focus ring rõ; `alt` cho ảnh; responsive đến 360px.

**An toàn view:** chỉ dùng `th:text` (tự escape), **không** `th:utext` với dữ liệu người dùng. JS chèn bình luận mới phải dùng `textContent` / thẻ `<template>`, không nối chuỗi vào `innerHTML`. Mọi `fetch` POST phải gửi CSRF token (đọc từ thẻ `<meta name="_csrf">` đặt trong layout).

**Avatar:** nếu `avatarPath` null → giữ cơ chế chữ cái đầu + màu hiện có; nếu có → `<img>` tròn. Làm thành **một fragment** `avatar(user, size)` dùng lại mọi nơi.

**Cấu trúc template:**
```
templates/
├── layout.html            (head, navbar, flash, scripts — fragment dùng chung)
├── fragments/             avatar.html, post-card.html, comment.html
├── login.html  register.html  feed.html  search.html  profile.html  settings-profile.html
└── error/                 404.html 403.html 500.html
```
Xóa `post-form.html` (composer đã nằm ở feed) và gộp `fragment.html` vào `layout.html`.

---

## 11. Dữ liệu mẫu

- Giữ cơ chế `data.sql` + `defer-datasource-initialization=true`; cập nhật theo schema mới; giữ đoạn `ALTER TABLE ... RESTART WITH`.
- Mật khẩu: sinh **một lần** hash BCrypt của `123456` (dùng `BCryptPasswordEncoder` trong một test/scratch), dán vào `data.sql`. Không để plain text.
- Thêm 2 user (`dung`, `hoa`) → tổng 5 user, mỗi người có `bio`, để demo tìm kiếm.
- Seed khoảng 10–12 bài: có bài chỉ chữ, có bài ảnh, ít nhất 1–2 bài video (nếu tạo được file mp4 nhỏ < 1 MB hợp lệ, ví dụ bằng `ffmpeg`; nếu không, bỏ video seed và ghi chú rằng video demo bằng cách tải lên trực tiếp). Giữ nội dung về Spring Boot như bản cũ.
- Ảnh seed đặt ở `src/main/resources/static/seed/` (tự tạo bằng script/ImageIO: ảnh gradient/hình học có chữ, **không** lấy ảnh có bản quyền); `media_path` trỏ `/seed/xxx.jpg`. Ảnh user upload nằm ở `/uploads/...` — hai nơi tách biệt để mỗi lần khởi động dữ liệu vẫn sạch.
- Seed vài lượt like và vài bình luận (giữ 10 bình luận cũ).
- Cập nhật **`database/schema.sql` và `database/data.sql`** (bản nộp bài, cú pháp MySQL) cho khớp entity.

---

## 12. Quy trình làm việc — chia phase, xong phase nào commit phase đó

Sau **mỗi phase**: `./mvnw -q clean verify` (Windows: `.\mvnw.cmd -q clean verify`) phải xanh, app phải khởi động, rồi commit.

### Phase 0 — Nền
- Chạy app bản hiện tại, xác nhận chạy được. Tạo nhánh `feature/facebook-ui`.
- Thêm dependency (mục 3.6), tạo `AppProperties`, cấu hình `application.properties` (multipart, encoding, `open-in-view=false`, `default_batch_fetch_size`, `app.*`).
- **Xong khi:** build xanh, app lên cổng 8080.

### Phase 1 — Bảo mật & nền tảng
- `SecurityConfig` (form login `/login`, logout POST, cho phép `/login /register /css/** /js/** /seed/** /h2-console/**`, còn lại phải đăng nhập), `PasswordEncoder` BCrypt, `UserService implements UserDetailsService`.
- Đăng ký + validate. `GlobalExceptionHandler` + trang lỗi. Xóa toàn bộ `session.getAttribute` cũ.
- **Xong khi:** `an/123456` đăng nhập được (dữ liệu đã BCrypt), chưa đăng nhập vào `/` bị đẩy về `/login`, đăng ký user mới đăng nhập được, POST thiếu CSRF bị từ chối.

### Phase 2 — Mô hình dữ liệu
- Sửa entity, thêm `PostLike`, `MediaType`, repository, DTO, cập nhật `data.sql`.
- **Xong khi:** `@DataJpaTest` kiểm tra được: lọc theo `MediaType`, unique like, tìm user theo tên.

### Phase 3 — Bảng tin
- `FileStorageService`, resource handler `/uploads/**`, đăng bài có media, phân trang + `/feed/more`, like (API), bình luận (API), xóa bài, kiểm tra quyền chủ bài.
- **Xong khi:** đăng bài chữ / ảnh / video đều thấy trên bảng tin; like không trùng; bình luận hiện ngay; user khác không xóa được bài của mình (403); file `.exe` đổi đuôi `.jpg` bị từ chối.

### Phase 4 — Người dùng
- Tìm kiếm người dùng, trang cá nhân + tab lọc Tất cả/Ảnh/Video, sửa thông tin + avatar/cover, đổi mật khẩu.
- **Xong khi:** tìm "bin" ra `binh`; tab Ảnh chỉ có bài ảnh, tab Video chỉ có bài video, số đếm đúng; đổi avatar hiển thị khắp nơi; đổi mật khẩu xong đăng nhập bằng mật khẩu mới được; sửa email trùng người khác bị báo lỗi.

### Phase 5 — Hoàn thiện UI
- Áp dụng mục 10: `app.css`, layout 3 cột, dropdown, trang lỗi, empty state, flash, responsive.
- **Xong khi:** chụp được màn hình login, feed, profile, settings đẹp ở 1440px và 375px; không có lỗi console; không request ra internet (kiểm tra tab Network).

### Phase 6 — Kiểm thử & tài liệu
- Test: repository (lọc media, unique like), service (đổi mật khẩu sai mật khẩu cũ, quyền xóa bài), `FileStorageService` (từ chối file sai loại/quá lớn, chống `../`), MockMvc (chưa đăng nhập → redirect; like cần CSRF).
- Tài liệu: cập nhật `README.md`, `API_ROUTES.md`, `HUONG_DAN_CHAY_DEMO.md`, `BIEN_BAN_KIEM_THU.md` (ghi đúng kết quả **thực tế**, không bịa PASS), `database/*.sql`.
- Tạo **`docs/SPRING_BOOT_MAP.md`**: bảng "Tính năng Spring Boot (mục 7) → file:dòng → một câu giải thích" để sinh viên đọc trước khi bảo vệ, kèm sơ đồ luồng 1 request (Browser → Security filter → DispatcherServlet → Controller → Service → Repository → H2 → Thymeleaf).
- **Xong khi:** `clean verify` xanh, README chạy theo đúng từng bước được trên máy sạch.

---

## 13. Phong cách code

- Comment tiếng Việt **có dấu**, ngắn, giải thích *vì sao* và *annotation làm gì* ở những chỗ quan trọng (đây là code để trình bày). Không comment kiểu lặp lại tên hàm.
- Tên lớp/biến/hàm tiếng Anh; thông báo hiển thị cho người dùng tiếng Việt.
- Hàm ngắn, một nhiệm vụ. Không code chết, không `System.out.println` (dùng SLF4J nếu cần log).
- Không bắt `Exception` chung chung rồi nuốt lỗi. Lỗi nghiệp vụ → exception riêng → `@ControllerAdvice`.
- Không hard-code đường dẫn/giới hạn — lấy từ `AppProperties`.
- Mỗi service public method ghi rõ transaction (`@Transactional` hoặc `readOnly = true`).

---

## 14. Smoke test thủ công (chạy trước khi báo hoàn thành)

1. `an/123456` đăng nhập → thấy feed 3 cột; sai mật khẩu → báo lỗi đỏ.
2. Đăng ký `test1`, đăng nhập được.
3. Đăng bài chữ; đăng bài kèm ảnh (xem trước hiện); đăng bài kèm video (tua được).
4. Like 2 lần liên tiếp (thích rồi bỏ thích), số đếm đúng, F5 giữ nguyên.
5. Bình luận bằng Enter, hiện ngay, F5 vẫn còn.
6. Đăng nhập `binh` → không thấy nút Xóa trên bài của `an`; thử `POST /posts/{id}/delete` bằng curl → 403.
7. Tìm "an" và "Người dùng" → ra đúng người; bấm vào → trang cá nhân.
8. Trang cá nhân: chuyển tab Ảnh/Video, số lượng khớp.
9. Sửa tên + bio + avatar + cover → thấy cập nhật ở navbar, feed, profile. Đổi mật khẩu → đăng xuất → đăng nhập lại bằng mật khẩu mới.
10. Truy cập `/users/khong-ton-tai` → trang 404 thân thiện. Tải file 100 MB → báo lỗi dung lượng, không 500.
11. Khởi động lại app → dữ liệu về lại bộ mẫu sạch (file trong `uploads/` không ảnh hưởng).

---

## 15. Khi báo cáo hoàn thành

Trả lời ngắn gọn: (1) đã làm gì theo từng phase, (2) lệnh chạy, (3) kết quả `verify` và smoke test **thực tế** (nêu rõ mục nào chưa chạy được và vì sao), (4) các quyết định lệch khỏi file này kèm lý do, (5) việc còn dang dở. Nếu Spring Security/starter nào không tương thích Boot 4.1.1 và buộc phải đổi hướng (ví dụ dùng `HandlerInterceptor` + `spring-security-crypto` thay cho full Spring Security), **nói rõ** thay vì âm thầm đổi.
