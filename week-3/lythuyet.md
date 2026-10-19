### Tuần 3 — Authentication & Authorization
---

## Authentication và Authorization

**Authentication (xác thực)** xác minh danh tính: “Bạn là ai?”. thường gắn liền với bước đăng nhập hệ thống. Sử dụng username password hoặc sinh trắc học để đăng nhập

**Authorization (phân quyền)** kiểm tra hành động được phép: “Bạn được làm gì?”. Xác thực thành công không đồng nghĩa có mọi quyền. Sau khi đăng nhập xong, người dùng đó có thể truy cập những quyền gì của hệ thống (admin sẽ có quyền khác với user bình thường)

## Session và token

**Session-based:** client gửi credential qua HTTPS; server xác minh, tạo session và lưu trạng thái; gửi cookie session ID; trình duyệt gửi cookie trong request tiếp theo; server tra session. Logout/hết hạn hủy session. Cần cookie HttpOnly, Secure, SameSite và chống CSRF. Khi scale nhiều instance cần kho session dùng chung.

**Token-based:** server xác minh login rồi phát access token; client gửi Bearer token; API xác minh chữ ký/hạn dùng. Token stateless giảm tra cứu session, nhưng thu hồi tức thời và refresh rotation vẫn cần state. Session phù hợp web truyền thống cần thu hồi tập trung; token phù hợp API/mobile/multi-service. Cân nhắc cách lưu token, XSS và CSRF.

## JWT

JWT có dạng `header.payload.signature`. Header nêu loại/thuật toán; payload chứa claims như `sub`, `exp`, `iss`, `aud`, role; signature phát hiện nội dung bị sửa. JWT ký không được mã hóa, không đặt bí mật trong payload. Ưu điểm: gọn, mang claims, dễ xác minh phân tán. Hạn chế: khó thu hồi trước hạn, token bị đánh cắp dùng được đến exp, claims role có thể cũ. Dùng HTTPS, khóa mạnh, thuật toán allowlist, kiểm tra exp/iss/aud, access token ngắn hạn, không log token.

## Password hashing

Không lưu plain text vì rò database làm lộ mật khẩu. MD5/SHA nhanh, giúp brute force hàng loạt; rainbow/precomputed tables hữu ích khi thiếu salt. Dùng bcrypt/Argon2id: salt ngẫu nhiên riêng cho mỗi password, lưu cùng encoded hash; cost factor làm mỗi lần đoán tốn tài nguyên. Benchmark cost trên máy triển khai, không tự thiết kế hash. Demo dùng BCryptPasswordEncoder.

## Refresh token và demo

Access token ngắn hạn giới hạn thời gian token bị lộ; refresh token cho phép xin access mới mà không nhập lại password. Flow: phát refresh token ngẫu nhiên, lưu fingerprint + user + hạn + trạng thái; khi đổi token xác minh rồi revoke token cũ và cấp token mới (rotation). Logout revoke refresh token; access JWT vẫn hợp lệ đến exp nếu không có denylist. Project riêng ở [springboot-auth-demo](./springboot-auth-demo/README.md). Luồng demo: register → login → gọi `/api/me` → role USER bị 403 ở `/api/admin` → refresh → logout; H2 console cho phép nâng role ADMIN để demo 200.

### Ghi chú production

TLS, secret manager, rate limit login/refresh, lưu refresh token an toàn, phát hiện reuse/revoke family, DB bền vững, kiểm thử quyền mặc định từ chối, cấu hình CSRF/CORS phù hợp. H2 in-memory và secret demo không dùng production.