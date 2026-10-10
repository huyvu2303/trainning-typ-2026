# TUẦN 2: FRAMEWORK, RESTFUL API VÀ ORM


## PHẦN 1: FRAMEWORK VÀ RESTFUL API

### 1.1. Chọn: Spring Boot

### 1.2. Khởi tạo project

Dùng Spring Initializr (https://start.spring.io) với các thông số trong ảnh

![alt text](image.png)

### 1.3. Cấu trúc thư mục

Phân chia theo trách nhiệm (kiến trúc phân tầng):

| Thành phần | Thư mục | Vai trò | Trong Spring Boot |
|---|---|---|---|
| Controller | `controller` | Nhận request, trả response | Class gắn `@RestController` |
| Service | `service` | Xử lý nghiệp vụ | Class gắn `@Service` |
| Repository | `repository` | Truy cập CSDL | Interface kế thừa `JpaRepository` |
| Model / Entity | `entity` | Ánh xạ bảng CSDL | Class gắn `@Entity` |
| Routes | (không có file riêng) chứa bên trong Controller | Đường dẫn khai báo ngay trên controller | `@GetMapping`, `@PostMapping`... |
![alt text](image-1.png)

```
src/main/java/com/example/courseshop/
├── CourseshopApplication.java       // điểm khởi động
├── controller/   CourseController, UserController
├── service/      CourseService, UserService
├── repository/   CourseRepository, UserRepository
├── entity/       User, Course, Role, CourseLevel, CourseStatus
├── dto/          CourseRequest, CourseResponse, UserRequest, UserResponse,
│                 PageResponse, ErrorResponse
└── exception/    NotFoundException, BadRequestException, ConflictException,
                  GlobalExceptionHandler
src/main/resources/
└── application.properties
```

**Luồng xử lý một request:**

```
Client ──► Controller ──► Service ──► Repository ──► MySQL
  ▲                                                     │
  └──────────────────── JSON response ◄─────────────────┘
```


### 1.4. File cấu hình `application.properties`

File nằm tại `src/main/resources/`, gồm các dòng `khóa=giá trị`, Spring đọc khi khởi động. Cho phép đổi cổng, thông tin DB... mà không sửa code.

> **Quy tắc quan trọng:** trong file `.properties`, chú thích phải nằm **ở dòng riêng** bắt đầu bằng `#`. Viết chú thích ngay sau giá trị sẽ biến nó thành một phần của giá trị và gây lỗi.

```properties
# Sai:  server.port=8080   # cong chay
# Dung:
server.port=8080
```

Cấu hình của dự án:

```properties
server.port=8080 
spring.application.name=courseshop

# Đường dẫn kết nối CSDL (thay my_database bằng tên database của bạn)
spring.datasource.url=jdbc:mysql://localhost:3306/course_shop
# Tài khoản và mật khẩu truy cập MySQL
spring.datasource.username=root
spring.datasource.password=matkhau-cuaban
# Driver kết nối MySQL
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# Cấu hình Hibernate / JPA (Tự động tạo hoặc cập nhật bảng trong CSDL)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

Các giá trị của `spring.jpa.hibernate.ddl-auto`:

| Giá trị | Ý nghĩa | Khi dùng |
|---|---|---|
| `none` | Không làm gì | DB đã có sẵn (trường hợp của dự án) |
| `validate` | Chỉ kiểm tra entity khớp bảng | Muốn kiểm tra chặt (có thể báo lệch với cột `ENUM` của MySQL) |
| `update` | Tự thêm cột/bảng còn thiếu | Thử nghiệm nhanh trên DB trống |
| `create-drop` | Tạo khi chạy, xóa khi tắt | Test |


### 1.5. RESTful API

**REST** (Representational State Transfer) là phong cách thiết kế API trên giao thức HTTP, trong đó:

- Mọi thứ là **tài nguyên (resource)**, mỗi tài nguyên có một địa chỉ (URL), ví dụ `/courses/5`.
- Hành động được thể hiện bằng **HTTP method**, không đặt trong URL.
- Mỗi request độc lập (stateless): server không nhớ request trước.
- Dữ liệu trao đổi thường là **JSON**.

**Quy ước đặt URL:** dùng danh từ số nhiều (`/courses`, không phải `/getCourses`), quan hệ lồng nhau (`/users/2/courses`), lọc bằng query param (`/courses?category=Lap trinh`).

**HTTP method và CRUD:**

| Hành động | Method | URL | An toàn (không đổi dữ liệu) | Lặp lại cho cùng kết quả (idempotent) | Mã thành công |
|---|---|---|---|---|---|
| Lấy danh sách | GET | `/courses` | Có | Có | 200 |
| Lấy một | GET | `/courses/{id}` | Có | Có | 200 |
| Tạo mới | POST | `/courses` | Không | Không | 201 |
| Thay toàn bộ | PUT | `/courses/{id}` | Không | Có | 200 |
| Sửa một phần | PATCH | `/courses/{id}` | Không | Thường không | 200 |
| Xóa | DELETE | `/courses/{id}` | Không | Có | 204 |

PUT gửi **toàn bộ** thông tin mới của tài nguyên; PATCH chỉ gửi các trường cần đổi. Dự án dùng PUT.

### 1.6. Xử lý request

Có ba cách truyền dữ liệu từ client vào hàm xử lý:

```java
// 1. Path Variable: tu nhap id va truy cap vao bang co id do
@GetMapping("/todos/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id){
        Todo todoData = this.todoService.getTodoById(id);
        return ResponseEntity.ok().body(todoData);
    }
```
![alt text](image-2.png)
```java
// 2. Query Param: sau dau ? dien thong tin
@GetMapping("/chao")
    public String chaoban(@RequestParam(name = "name") String name ) {
        return "Xin chào! " + name;
    }
```
![alt text](image-3.png)
```java
// 3. Request Body: JSON trong than request,   POST method
@PostMapping("/todos")
    public ResponseEntity<Todo> createTodo(@RequestBody Todo input) {
        Todo newTodo = this.todoService.handleCreateTodo(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTodo);
    }
```
![alt text](image-4.png)


### 1.7. Xử lý response và mã trạng thái

Spring tự chuyển object trả về thành JSON. Dùng `ResponseEntity` để điều khiển mã trạng thái.

| Mã | Tên | Dùng khi |
|---|---|---|
| 200 | OK | Lấy/sửa thành công |
| 201 | Created | Tạo mới thành công |
| 204 | No Content | Xóa thành công, không có nội dung trả về |
| 400 | Bad Request | Dữ liệu client gửi sai (thiếu trường, sai kiểu, `page=0`) |
| 401 / 403 | Unauthorized / Forbidden | Chưa đăng nhập / không đủ quyền (làm ở tuần sau) |
| 404 | Not Found | Không tìm thấy tài nguyên |
| 409 | Conflict | Xung đột (trùng email, xóa dữ liệu đang được tham chiếu) |
| 500 | Internal Server Error | Lỗi phía server (bug, DB sập) |

### 1.8. Xử lý lỗi thống nhất

**Phân biệt:** nhóm **4xx là lỗi do client** (client gửi sai, có thể sửa và gửi lại); nhóm **5xx là lỗi do server** (client không có lỗi). Với lỗi 5xx không được trả stack trace ra ngoài (lộ thông tin nội bộ), chỉ ghi log phía server.

**Cấu trúc lỗi thống nhất** cho toàn bộ API:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Du lieu khong hop le",
  "details": ["title: khong duoc de trong", "price: phai >= 0"]
}
```

Cách làm trong Spring: viết **một** class gắn `@RestControllerAdvice` bắt các exception từ mọi controller và đổi thành response chuẩn ở trên (code ở mục 5.3).

### 1.9. Phân trang trong API

Lý do: không trả cả triệu dòng trong một lần; giảm tải server, mạng và trình duyệt. Hai kỹ thuật (Tuần 1):

| | Offset-based | Cursor-based |
|---|---|---|
| Truy vấn | `LIMIT size OFFSET (page-1)*size` | `WHERE id > last_id LIMIT size` |
| Nhảy đến trang bất kỳ | Được | Không |
| Trang sâu | Chậm dần (phải đọc rồi bỏ các dòng đầu) | Nhanh ổn định |
| Dữ liệu thay đổi liên tục | Dễ lặp/sót dòng | Ổn định |
| Phù hợp | Danh sách có số trang (trang quản trị) | Cuộn vô hạn, dữ liệu lớn |

Dự án dùng **offset-based** với `page` bắt đầu từ 1. Định dạng response theo đề:

```json
{ "data": [ ... ], "page": 1, "size": 10, "totalPages": 3, "totalElements": 25 }
```

**Bước 1: phân trang trên dữ liệu trong `List` (khi chưa có DB):**

```java
public record PageResponse<T>(List<T> data, int page, int size, int totalPages, long totalElements) { }

// trong service, all la List<Course> luu tam
int from = (page - 1) * size;
List<Course> slice = all.subList(Math.min(from, all.size()), Math.min(from + size, all.size()));
int totalPages = (int) Math.ceil((double) all.size() / size);
return new PageResponse<>(slice, page, size, totalPages, all.size());
```

**Bước 2: phân trang trên DB thật:** dùng `PageRequest`; Spring Data tự sinh `LIMIT/OFFSET` và thêm một câu `COUNT(*)` để có `totalElements`. Lưu ý Spring Data đếm trang từ 0 nên phải đổi `page - 1`.

---

## PHẦN 2: ORM VÀ SPRING DATA JPA

### 2.1. ORM là gì, lợi ích

**ORM (Object-Relational Mapping)** là kỹ thuật ánh xạ giữa thế giới hướng đối tượng và CSDL quan hệ:

| Thế giới quan hệ | Thế giới đối tượng |
|---|---|
| Bảng | Class (entity) |
| Dòng | Object |
| Cột | Thuộc tính |
| Khóa ngoại | Tham chiếu giữa các object |

**Lợi ích:** không phải viết SQL lặp lại cho CRUD; code hướng đối tượng, dễ đọc; dễ đổi hệ quản trị CSDL; có sẵn cơ chế cache, quản lý transaction, optimistic locking (`@Version`).

**Nhược điểm:** khó kiểm soát SQL sinh ra, truy vấn phức tạp phải viết tay (JPQL/native SQL); dễ dính **vấn đề N+1**; thêm một tầng cần học.

### 2.2. JPA, Hibernate, Spring Data JPA

| Thành phần | Vai trò |
|---|---|
| **JPA** (Jakarta Persistence) | Đặc tả: tập interface và annotation chuẩn (`@Entity`, `@Id`...) |
| **Hibernate** | Bản cài đặt JPA phổ biến nhất, thực sự sinh SQL |
| **Spring Data JPA** | Lớp bọc trên JPA: chỉ cần khai báo interface Repository, Spring tự cài đặt |

### 2.3. Entity mapping và Repository

Các annotation chính:

| Annotation | Ý nghĩa |
|---|---|
| `@Entity` | Class này ánh xạ một bảng |
| `@Table(name = "users")` | Chỉ rõ tên bảng |
| `@Id` | Khóa chính |
| `@GeneratedValue(strategy = IDENTITY)` | Khóa tự tăng (`AUTO_INCREMENT`) |
| `@Column(name, nullable, length, unique)` | Tuỳ chỉnh cột |
| `@Enumerated(EnumType.STRING)` | Lưu enum dạng chữ (không dùng số thứ tự) |
| `@ManyToOne`, `@OneToMany`, `@JoinColumn` | Quan hệ giữa các entity |
| `@Version` | Optimistic locking (liên hệ Tuần 1) |

**Repository** chỉ là interface:

```java
public interface CourseRepository extends JpaRepository<Course, Long> { }
```

Chỉ vậy là đã có sẵn `save`, `findById`, `findAll`, `deleteById`, `existsById`, `count`, `findAll(Pageable)`. Ngoài ra có thể **sinh truy vấn từ tên phương thức**:

```java
Page<Course> findByCategory(String category, Pageable pageable);
// SQL:  select ... from courses where category = ? limit ?, ?
boolean existsByEmail(String email);
```
---
## PHẦN 3: DEMO
DATABASE
![alt text](image-5.png)
---
Lệnh Get ID
![alt text](image-6.png)
Lệnh Phân Trang
![alt text](image-7.png)
Lệnh POST
![alt text](image-8.png)
Lệnh PUT
![alt text](image-9.png)
Lệnh DELETE
![alt text](image-10.png)
sau CRUD
![alt text](image-11.png)
![alt text](image-12.png)
