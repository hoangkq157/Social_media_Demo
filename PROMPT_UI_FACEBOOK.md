# PROMPT — Chỉnh giao diện SpringBook giống Facebook hơn

> Cách dùng: mở agent trong thư mục project, **đính kèm ảnh chụp Facebook thật** làm tham chiếu, rồi dán toàn bộ phần dưới vạch.
> Chỉ sửa giao diện (template, CSS, JS nhẹ, icon). **Không đổi** logic Java, route, entity, bảo mật.

---

Bạn là front-end engineer. Hãy chỉnh lại **giao diện** project SpringBook (Spring Boot + Thymeleaf + Bootstrap 5 nhúng sẵn) để trông **gần giống Facebook bản desktop hiện tại** nhất có thể. Ảnh đính kèm là Facebook thật; so sánh với bản hiện tại của mình rồi sửa theo các mục bên dưới.

## 0. Ràng buộc
- Chỉ đụng vào `src/main/resources/templates/**`, `static/css/app.css`, `static/js/app.js`, `static/icons/**`. Không sửa Java, route, tên field form, tên biến model, `th:action`, CSRF token, id/class mà JS đang dùng (nếu đổi thì sửa JS tương ứng).
- **Chạy offline**: không CDN, không Google Fonts, không icon font từ mạng. Mọi thứ nằm trong `static/`.
- **Không dùng emoji làm icon** (hiện đang dùng 🏠👤⚙💾📷👍💬🔍 — thay hết bằng SVG).
- **Không** dùng logo/ảnh/asset gốc của Meta, không cắt ảnh từ ảnh chụp tham chiếu. Logo thương hiệu: hình tròn xanh `#0866FF` chứa chữ **S** trắng đậm (không dùng chữ "f").
- Không thêm nút/mục giả không hoạt động (không chuông thông báo, Messenger, Marketplace, quảng cáo "Được tài trợ"...). Chỉ hiện thứ có chức năng thật.
- Vẫn responsive (≥1200 3 cột, 992–1199 2 cột, <992 1 cột) và không lỗi console.

## 1. Bộ icon (thay emoji)
Dùng icon **mã nguồn mở, bản fill/solid bo tròn** — gần phong cách Facebook nhất: **Phosphor Icons (MIT)** kiểu `fill`/`regular`, hoặc **Bootstrap Icons (MIT)** bản `-fill`. Lấy từ npm (`@phosphor-icons/core` hoặc `bootstrap-icons`) về máy rồi **copy các SVG cần dùng** vào project, không để runtime phụ thuộc mạng.

- Gộp thành một sprite: `static/icons/sprite.svg` với các `<symbol id="...">`, dùng `fill="currentColor"`.
- Tạo fragment Thymeleaf `fragments/icon.html` → `th:replace="~{fragments/icon :: icon('home', 24)}"` sinh `<svg class="icon" width height aria-hidden="true"><use th:href="@{/icons/sprite.svg#home}"/></svg>`.
- Danh sách icon tối thiểu: `home`, `home-fill`, `user`, `user-fill`, `users`, `search`, `plus`, `image`(ảnh/video), `video`, `like`, `like-fill`, `comment`, `more`(3 chấm ngang), `edit`(bút), `settings`, `logout`, `trash`, `x`, `chevron-down`, `database`, `play-fill`, `camera`, `globe`, `calendar`, `mail`.
- Màu: icon mặc định `#65676B`; active/đã like `#0866FF`.
- Ghi nguồn + giấy phép vào `static/icons/LICENSE.txt` và 1 dòng trong `README.md`.

## 2. Design tokens (đặt trong `:root` của `app.css`)
```
--fb-blue:#0866FF; --fb-blue-hover:#0759DB;
--bg:#F0F2F5; --card:#FFFFFF;
--text:#050505; --text-2:#65676B; --divider:#CED0D4;
--btn-gray:#E4E6EB; --btn-gray-hover:#D8DADF;
--hover:rgba(0,0,0,.05);
--shadow-1:0 1px 2px rgba(0,0,0,.2);
--shadow-nav:0 2px 4px rgba(0,0,0,.1);
--radius:8px;
```
Font: `system-ui, "Segoe UI", Helvetica, Arial, sans-serif`. Cỡ chữ gốc 15px; tên người/tiêu đề mục 15–17px `font-weight:600`; chữ phụ 13px `--text-2`. Bỏ toàn bộ tiêu đề **IN HOA** kiểu "LỐI TẮT NHANH", "NGƯỜI DÙNG GỢI Ý".

## 3. Thanh điều hướng (khác biệt lớn nhất so với hiện tại)
Chiều cao **56px**, nền trắng, `box-shadow: var(--shadow-nav)`, `position:sticky; top:0; z-index:100`, chia 3 vùng bằng grid `1fr auto 1fr`:

- **Trái**: logo tròn 40px (chữ S) + ô tìm kiếm **hình viên thuốc** nền `--bg`, cao 40px, icon search xám bên trong, placeholder "Tìm kiếm trên SpringBook"; **bỏ chữ "SpringBook" cạnh logo**. Khi màn hình < 1100px thu ô tìm kiếm thành nút tròn 40px.
- **Giữa**: các **tab icon** rộng ~110px × cao 56px (chỉ icon 28px, **không có chữ**): Trang chủ (`/`), Mọi người (`/search`), Trang cá nhân (`/users/{me}`). Tab đang active: icon xanh (bản fill) + **gạch chân xanh 3px** sát đáy navbar; tab không active: icon xám, hover nền `--hover` bo 8px (chừa lề trên/dưới 4px). Ẩn khi < 768px.
- **Phải**: các nút **tròn 40px nền `--btn-gray`**: dấu `+` (mở modal tạo bài, xem mục 5), và **avatar 40px** có huy hiệu mũi tên nhỏ ở góc dưới phải → mở dropdown (Bootstrap) gồm: thẻ người dùng (avatar + tên + "Xem trang cá nhân của bạn"), Chỉnh sửa trang cá nhân, Đăng xuất. Bỏ tên người dùng viết thẳng trên navbar.

## 4. Ba cột nội dung
Nền trang `--bg`. Cột trái ~300px, cột giữa tối đa **680px**, cột phải ~300px (đều `position:sticky; top:72px; max-height: calc(100vh - 72px); overflow:auto` cho 2 cột bên).

**Cột trái — KHÔNG dùng card trắng.** Danh sách trơn trên nền xám: mỗi dòng cao 52px, bo 8px, hover `--hover`, `padding:8px`, icon/avatar 36px bên trái + chữ 15px `font-weight:500`. Thứ tự: **dòng đầu là avatar + tên mình** (bấm → trang cá nhân), rồi Trang chủ, Trang cá nhân, Mọi người, Chỉnh sửa thông tin, (Quản trị CSDL H2 nếu muốn giữ — ghi nhỏ, tách bằng đường kẻ). Icon dùng màu nhấn riêng từng mục (xanh dương, tím, cam, xanh lá...) trên nền tròn nhạt như Facebook, không dùng xám đồng loạt. Cuối cột: dòng chữ xám 13px (tên đồ án · © Nhóm 2).

**Cột phải — KHÔNG dùng card trắng.** Chia khối bằng **đường kẻ mảnh** `--divider`. Mỗi khối có tiêu đề 17px `font-weight:600` màu `--text-2` (Sentence case, ví dụ "Những người bạn có thể biết"), bên phải là link xanh "Xem tất cả" (→ `/search`). Mỗi người một dòng: avatar 36px + tên 15px/600 + `@username` 13px xám; **nút "Xem"** đổi thành nút xám `--btn-gray` bo 6px, đậm 600 (không dùng nút viền xanh). Khối "Đồ án Nhóm 2" gộp thành đoạn chữ xám ngắn, không viền, không emoji.

## 5. Ô soạn bài (composer) — làm giống Facebook
- Card trắng bo 8px `--shadow-1`, padding 12px 16px.
- Hàng trên: avatar 40px + **nút hình viên thuốc** nền `--bg` (không phải textarea), chữ xám "Bạn đang nghĩ gì, {tên}?", hover đậm hơn một chút. Bấm vào → mở **modal Bootstrap** "Tạo bài viết".
- Đường kẻ mảnh, dưới là một hàng nút phẳng căn giữa: icon ảnh **xanh lá `#45BD62`** + chữ "Ảnh/Video" (bấm → mở modal và tự bật chọn file).
- **Modal "Tạo bài viết"**: tiêu đề giữa, đậm, cỡ 20px, nút đóng tròn xám góc phải, đường kẻ dưới tiêu đề; avatar + tên; `textarea` không viền, cỡ chữ 24px khi chưa có media (giảm xuống 15px khi có media), placeholder "Bạn đang nghĩ gì, {tên}?"; vùng xem trước ảnh/video có nút ✕ tròn gỡ file; hàng "Thêm vào bài viết" với icon ảnh/video; nút **Đăng** xanh full-width, 36px, bo 6px, **xám & vô hiệu khi chưa có chữ hoặc media**. Giữ nguyên `name`, `enctype="multipart/form-data"`, CSRF của form hiện tại. Sau khi gửi khóa nút chống bấm đúp.

## 6. Thẻ bài viết
- Card trắng bo 8px, `--shadow-1`, khoảng cách giữa các bài 16px.
- Header (padding 12px 16px): avatar 40px · tên 15px/600 (hover gạch chân) · dòng dưới "18:00 18/10/2026 · icon globe 12px" 13px xám · bên phải nút `⋯` (icon `more`) tròn 36px hover xám, mở dropdown có "Xóa bài viết" (icon thùng rác) — **chỉ hiện với chủ bài**.
- Nội dung: 15px, `line-height:1.33`, `white-space:pre-wrap`, padding 4px 16px 16px. Bài **chỉ chữ và ngắn (< 85 ký tự, không media)** thì chữ to 24px như Facebook.
- **Ảnh/video tràn sát hai mép card** (không bo, không padding), ảnh `width:100%; max-height:680px; object-fit:cover`, video `controls` cùng kích thước, nền đen khi không vừa khung.
- Hàng thống kê (padding 10px 16px): trái = icon like tròn xanh 18px + số lượt thích (ẩn nếu 0, **bỏ chữ "lượt thích"**); phải = "N bình luận" xám (ẩn nếu 0).
- Đường kẻ mảnh, rồi **thanh hành động**: 2 nút bằng nhau (Thích · Bình luận), cao 36px, icon 20px + chữ 15px/600 màu xám, hover nền `--hover` bo 6px; trạng thái **đã thích**: icon fill + chữ màu xanh `--fb-blue`. Đường kẻ mảnh phía dưới.
- Bình luận: avatar 32px + **bong bóng** nền `--bg` bo 18px, padding 8px 12px, tên 13px/600, nội dung 15px; dưới bong bóng là thời gian 12px xám (padding-left 12px). Khoảng cách giữa các bình luận 8px.
- Ô viết bình luận: avatar 32px + input viên thuốc nền `--bg`, **không viền**, placeholder "Viết bình luận...", Enter để gửi; bỏ nút/nền xám bao quanh khu vực bình luận hiện tại (chuyển về nền trắng như phần còn lại của card).

## 7. Trang cá nhân
- Card trắng đầy chiều rộng tối đa 940px canh giữa, nền xám phía ngoài.
- **Ảnh bìa** cao ~348px (desktop), bo góc dưới 8px; chưa có ảnh → gradient `#4facfe → #0866FF` (không để khung trống).
- Avatar **168px**, viền trắng 4px, đè lên bìa ~ −84px, canh trái 32px (mobile: canh giữa, 128px). Bên cạnh: tên 32px/800, dòng "N bài viết · Tham gia 10/2026" 15px xám, bio 15px.
- Nút "Chỉnh sửa trang cá nhân" (chỉ với chủ trang): nút xám `--btn-gray` có icon bút, góc phải.
- Dưới có đường kẻ, rồi **thanh tab**: Bài viết · Ảnh · Video — chữ 15px/600, cao 60px; active: chữ xanh + gạch chân xanh 3px; không active: xám, hover nền `--hover`. Kèm số lượng nhỏ cạnh chữ.
- Thân trang 2 cột: trái card "Giới thiệu" (bio, email nếu là mình, ngày tham gia, mỗi dòng có icon), phải = bài viết (tab Bài viết) hoặc **lưới ảnh vuông 3 cột** gap 4px (tab Ảnh/Video; video có icon ▶ `play-fill` trắng ở giữa, nền mờ). Click ảnh trong lưới → mở ảnh lớn trong modal.
- Trang trống: icon lớn xám + chữ "Chưa có ảnh nào" căn giữa.

## 8. Trang tìm kiếm, đăng nhập, đăng ký, cài đặt
- **Tìm kiếm**: card trắng bo 8px; hai tab "Mọi người" / "Bài viết" kiểu pill (active nền xanh nhạt `#E7F3FF` chữ xanh). Mỗi người một dòng: avatar 60px, tên 17px/600, `@username` + bio 1 dòng cắt `…`, nút xám "Xem trang cá nhân" bên phải.
- **Đăng nhập/đăng ký**: bố cục 2 cột giống trang đăng nhập Facebook (trái: logo S xanh + khẩu hiệu 28px; phải: card trắng bo 8px, đổ bóng, input cao 52px bo 6px, nút "Đăng nhập" xanh full-width 48px đậm; đường kẻ; nút xanh lá `#42B72A` "Tạo tài khoản mới" dẫn tới `/register`). Giữ dòng gợi ý tài khoản mẫu nhưng ở cỡ chữ nhỏ, màu xám.
- **Cài đặt hồ sơ**: card 680px; mỗi trường có label 13px/600 xám; nút Lưu xanh, nút Hủy xám; hiển thị ảnh đại diện/bìa hiện tại kèm nút đổi.

## 9. Avatar & chi tiết nhỏ
- Fragment `avatar(user, size)` dùng thống nhất mọi nơi: có `avatarPath` → `<img>` tròn `object-fit:cover`; không có → vòng tròn chữ cái đầu. **Màu chữ cái phải khác nhau giữa các user** (hiện `an` và `binh` cùng màu tím): chọn màu theo `id % palette.length` thay vì hash username, palette 8 màu dịu (`#0866FF #E4405F #F7B928 #42B72A #A033FF #FF6A3D #00A6A6 #6C757D`).
- Thời gian hiển thị: "Vừa xong", "5 phút", "2 giờ", "Hôm qua lúc 14:20", còn lại "18/10/2026" — làm bằng helper trong `Post`/DTO **hoặc** thuần JS `Intl.RelativeTimeFormat('vi')`; ưu tiên cách không phải sửa nhiều Java. Thêm `title` hiển thị thời gian đầy đủ khi hover.
- `:focus-visible` có viền xanh rõ; nút icon có `aria-label`; ảnh có `alt`.
- Nút và link: không dùng viền đen/xanh mặc định của Bootstrap; bo 6px; transition 100ms.
- Tab trình duyệt: `<title>` dạng "Trang chủ | SpringBook"; thêm favicon SVG tròn xanh chữ S vào `static/`.
- Scrollbar mảnh trên 2 cột sticky (`scrollbar-width: thin`).

## 10. Cách làm & nghiệm thu
1. Làm theo thứ tự: tokens + icon sprite → navbar → composer + modal → thẻ bài viết → hai cột bên → profile → search/login/settings → chi tiết nhỏ. Commit sau mỗi bước (`feat(ui): ...`).
2. Sau mỗi bước chạy app (`.\mvnw.cmd spring-boot:run`), dùng trình duyệt headless (Playwright hoặc Chrome headless) chụp ảnh ở **1440×900** và **390×844** các trang: `/login`, `/`, `/users/an`, `/users/an?tab=photos`, `/search?q=an`, `/settings/profile`; xem lại và tự sửa chỗ lệch **trước khi** báo xong. Nếu không chụp được ảnh, nói rõ.
3. Nghiệm thu — tất cả phải đúng:
   - Không còn emoji nào dùng làm icon; không request mạng ngoài `localhost` (kiểm tra Network).
   - Navbar 56px với 3 vùng như mục 3; cột trái/phải **không còn dạng card trắng**.
   - Composer là nút viên thuốc mở modal; đăng bài chữ / ảnh / video vẫn hoạt động.
   - Like, bình luận, xóa bài, tìm kiếm, tab Ảnh/Video, sửa hồ sơ **vẫn chạy như trước** (thử tay từng cái).
   - Không lỗi console; không tràn ngang ở 390px; `./mvnw clean verify` vẫn xanh.
4. Báo cáo cuối: danh sách file đã sửa, ảnh chụp trước/sau (nếu có), và các điểm **chưa** giống Facebook kèm lý do.
