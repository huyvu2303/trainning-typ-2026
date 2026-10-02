# Phần 2: Lý Thuyết Lập Trình Hướng Đối Tượng (OOP) & DI/IoC trong Java

---

## 1. Bốn Tính Chất Cốt Lõi Của OOP (Object-Oriented Programming)

Lập trình hướng đối tượng là mô hình lập trình xoay quanh các "đối tượng" (Objects), bao gồm dữ liệu (thuộc tính - Attributes) và hành vi (phương thức - Methods).

```
                      +------------------------------------------+
                      |         4 Trụ Cột Cơ Bản Của OOP         |
                      +------------------------------------------+
                       /          |              |              \
                      /           |              |               \
        [Encapsulation]    [Inheritance]   [Polymorphism]    [Abstraction]
        (Tính Đóng Gói)    (Tính Kế Thừa)  (Tính Đa Hình)   (Tính Trừu Tượng)
```

### 1.1 Encapsulation (Tính Đóng Gói)
* **Khái niệm:** Gom nhóm dữ liệu và các phương thức thao tác trên dữ liệu đó vào trong cùng một đơn vị (Class), đồng thời che giấu trạng thái bên trong của đối tượng khỏi sự can thiệp trực tiếp từ bên ngoài. gồm: `private`, `public`, `protected`
* **Cách thực hiện trong Java:**
  * Khai báo các thuộc tính (fields) với access modifier là `private`.
  * Cung cấp các phương thức truy cập gián tiếp: `getter` (đọc dữ liệu) và `setter` (ghi dữ liệu kèm kiểm tra tính hợp lệ).
* **Mục đích:** Bảo vệ tính toàn vẹn của dữ liệu, ngăn việc sửa đổi trạng thái đối tượng một cách tùy tiện không kiểm soát.
```java
public class PhanSo {
    private long tu, mau;
    public PhanSo(long tu, long mau) {
        this.tu = tu;
        this.mau = mau;
    }
    public long gcd(long a, long b){
        while(b>0){
            long tmp = a%b;
            a=b;
            b=tmp;
        }
        return a;
    }
    public long getTu(){
        return this.tu;
    }
    public void setTu(long tuso){
        this.tu = tuso;
    }
    public void RutGon(){
        long g = gcd(this.tu,this.mau);
        this.tu /= g;
        this.mau /= g;
    }
    @Override
    public String toString(){
        return this.tu+"/"+this.mau;
    }
}
```
### 1.2 Inheritance (Tính Kế Thừa)
* **Khái niệm:** Cho phép một lớp con (Subclass/Derived Class) tái sử dụng các thuộc tính và phương thức của một lớp cha (Superclass/Base Class) đã có sẵn mà không cần viết lại từ đầu.
* **Cách thực hiện trong Java:** Sử dụng từ khóa `extends`. Lưu ý: Java là ngôn ngữ **đơn kế thừa** lớp (một class chỉ được kế thừa trực tiếp từ một class cha duy nhất để tránh xung đột Diamond Problem).
* **Mục đích:** Thể hiện mối quan hệ "IS-A" (Là một), tăng khả năng tái sử dụng mã nguồn và thiết lập cấu trúc phả hệ rõ ràng.
```java
public class Users {
    private String name, ngaysinh;
    private String email;

    public Users(String name, String ngaysinh, String email) {
        this.name = name;
        this.ngaysinh = ngaysinh;
        this.email = email;
    }

    public void printinfo(){
        System.out.println(name + " " + ngaysinh + " " + email);
    }
}
public class SinhVien extends Users {
    private String msv;
    private double gpa;

    public SinhVien(String name, String ngaysinh, String email, String msv, double gpa) {
        super(name, ngaysinh, email);
        this.msv = msv;
        this.gpa = gpa;
    }

    @Override
    public void printinfo(){
        super.printinfo();
        System.out.println(msv + " " + gpa);
    }
}
```

### 1.3 Polymorphism (Tính Đa Hình)
* **Khái niệm:** Một hành vi hoặc tên phương thức có thể biểu hiện dưới nhiều hình thức thực thi khác nhau tùy thuộc vào đối tượng đang gọi nó.
* **Hai dạng đa hình trong Java:**
  1. **Compile-time Polymorphism (Đa hình lúc biên dịch - Method Overloading):** Trong cùng một class có nhiều phương thức trùng tên nhưng khác nhau về danh sách tham số (số lượng, kiểu dữ liệu, thứ tự).
  2. **Runtime Polymorphism (Đa hình lúc chạy - Method Overriding):** Lớp con cung cấp một cài đặt cụ thể cho phương thức đã được định nghĩa ở lớp cha (dùng từ khóa `@Override`). Trình biên dịch quyết định phiên bản phương thức nào được gọi tại thời điểm chương trình thực thi (Dynamic Method Dispatch) dựa trên kiểu thực tế của đối tượng.
```java
class Animal {
    // Runtime Polymorphism: Phương thức sẽ được lớp con ghi đè
    void sound() {
        System.out.println("Động vật phát ra âm thanh");
    }

    void run() {
        System.out.println("Đang chạy bình thường");
    }

    void run(int speed) {
        System.out.println("Đang chạy với tốc độ: " + speed + " km/h");
    }
}
class Dog extends Animal {
    @Override
    void sound() {
        System.out.println("Chó sủa: Gâu gâu!");
    }
}
class Cat extends Animal {
    @Override
    void sound() {
        System.out.println("Mèo kêu: Meo meo!");
    }
}

public class MainPolymorphism {
    public static void main(String[] args) {
        // 1. Demo Overloading (lúc biên dịch)
        Animal a = new Animal();
        a.run();
        a.run(20);

        // 2. Demo Overriding (lúc thực thi - cùng lệnh a1.sound() nhưng hành vi khác nhau)
        Animal a1 = new Dog();
        Animal a2 = new Cat();

        a1.sound(); // Output: Chó sủa: Gâu gâu!
        a2.sound(); // Output: Mèo kêu: Meo meo!
    }
}
```

### 1.4 Abstraction (Tính Trừu Tượng)
* **Khái niệm:** Tập trung vào các hành động cốt lõi mà đối tượng có thể làm (**What to do**) thay vì chi tiết cụ thể hành động đó được cài đặt như thế nào (**How to do**). Che giấu sự phức tạp của hệ thống và chỉ phơi bày những giao diện cần thiết cho người dùng.
* **Cách thực hiện trong Java:** Thông qua `abstract class` hoặc `interface`.

```java
import java.util.Date;

public abstract class GeometricObject {
    // Thuộc tính private (Tính đóng gói - Encapsulation)
    private String color = "white";
    private boolean filled = false;
    private Date dateCreated;


    protected GeometricObject(String color, boolean filled) {
        this.color = color;
        this.filled = filled;
        this.dateCreated = new Date();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    // Phương thức trừu tượng (Bắt buộc lớp con phải Override)
    public abstract double getArea();
    public abstract double getPerimeter();

    @Override
    public String toString() {
        return dateCreated  + color  + filled;
    }
}

class Circle extends GeometricObject {
    private double radius;

    public Circle(double radius, String color, boolean filled) {
        super(color, filled);
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
    @Override
    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }
    @Override
    public String toString() {
        return super.toString() + radius;
    }
}

class Rectangle extends GeometricObject {
    private double width;
    private double height;

    public Rectangle(double width, double height, String color, boolean filled) {
        super(color, filled);
        this.width = width;
        this.height = height;
    }


    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        if (height > 0) {
            this.height = height;
        } else {
            System.out.println("Chiều cao phải lớn hơn 0!");
        }
    }

    @Override
    public double getArea() {
        return width * height;
    }
    @Override
    public double getPerimeter() {
        return 2 * (width + height);
    }
    @Override
    public String toString() {
        return super.toString()  + width  + height;
    }
}

class MainTest {
    public static void main(String[] args) {
        GeometricObject circle = new Circle(5.0, "red", true);
        GeometricObject rectangle = new Rectangle(4.0, 6.0, "blue", false);

        System.out.println(circle);
        System.out.printf("%.2f\n", circle.getArea());
        System.out.printf("2f\n", circle.getPerimeter());

        System.out.println(rectangle);
        System.out.printf("%.2f\n", rectangle.getArea());
        System.out.printf("%.2f\n", rectangle.getPerimeter());
    }
}
```
```java
//đa kế thừa bằng interface

public interface Flyable {
    public abstract void fly();
}

public interface Runable {
    public abstract void run();
}

public class Bird implements Runable, Flyable {

    @Override
    public void run() {
        System.out.println("Bird can run");
    }

    @Override
    public void fly() {
        System.out.println("Bird can fly");
    }
}
```
---

## 2. So Sánh Class, Abstract Class và Interface

| Tiêu chí | Regular Class (Lớp Thường) | Abstract Class (Lớp Trừu Tượng) | Interface (Giao Diện) |
| :--- | :--- | :--- | :--- |
| **Khởi tạo đối tượng (`new`)** | Có thể khởi tạo trực tiếp. | **Không thể** khởi tạo trực tiếp qua `new`. | **Không thể** khởi tạo trực tiếp qua `new`. |
| **Phương thức** | Chỉ chứa phương thức có phần thân đầy đủ (concrete method). | Chứa cả phương thức cụ thể lẫn phương thức trừu tượng (`abstract method`). | Mặc định chứa phương thức trừu tượng; từ Java 8 hỗ trợ thêm `default` và `static` method; từ Java 9 hỗ trợ `private` method. |
| **Thuộc tính (Fields)** | Khai báo tùy ý mọi loại biến với mọi modifier. | Khai báo tùy ý mọi loại biến với mọi modifier. | Mặc định luôn luôn là `public static final` (Hằng số). Không có biến instance. |
| **Khả năng kế thừa / triển khai** | Đơn kế thừa (`extends 1 Class`). | Đơn kế thừa (`extends 1 Abstract Class`). | **Đa triển khai** (Một class có thể `implements` nhiều Interface cùng lúc). |
| **Bản chất thiết kế** | Khuôn mẫu tạo ra các đối tượng cụ thể. | Thể hiện mối quan hệ bản chất danh tính: **IS-A** (Là một). | Thể hiện mối quan hệ năng lực, hợp đồng hành vi: **CAN-DO** (Có khả năng làm gì). |

### Khi nào nên dùng cái nào?
* **Dùng Class:** Khi bạn cần tạo ra đối tượng cụ thể với dữ liệu và logic hoàn chỉnh để sử dụng ngay trong chương trình.
* **Dùng Abstract Class:** Khi các lớp liên quan có quan hệ phả hệ chặt chẽ (cùng bản chất), cần chia sẻ chung một lượng lớn mã nguồn, biến trạng thái (state) hoặc hàm khởi tạo (Constructor chung).
* **Dùng Interface:** Khi bạn muốn thiết lập một bản **hợp đồng tiêu chuẩn (Contract)** để các lớp hoàn toàn khác nhau về bản chất vẫn có thể triển khai chung một hành vi (ví dụ: `Comparable`, `Serializable`, `Runnable`). Đây là nền tảng để đạt được kiến trúc ghép lỏng (Loose Coupling).

---

## 3. Dependency Injection (DI) & Inversion of Control (IoC)

### 3.1 Khái niệm IoC và DI

* **Inversion of Control (IoC - Đảo ngược điều khiển):**
  * Là một **nguyên lý kiến trúc phần mềm**. Trong lập trình truyền thống, bản thân class chủ động kiểm soát việc tạo mới các đối tượng phụ thuộc (Dependencies) bằng toán tử `new`. 
  * Áp dụng IoC: Quyền kiểm soát luồng và vòng đời khởi tạo đối tượng bị "đảo ngược" — class không tự tạo dependency nữa mà giao quyền đó cho một bộ phận bên ngoài (bên thứ ba hoặc IoC Container).
* **Dependency Injection (DI - Tiêm phụ thuộc):**
  * Là một **Design Pattern cụ thể nhằm hiện thực hóa nguyên lý IoC**. Các dependency cần thiết được "tiêm" (inject) vào bên trong class từ bên ngoài, thông qua Constructor, Setter hoặc Interface.

### 3.2 Minh họa bằng Java thuần

#### Cách làm truyền thống (Tightly Coupled - Ghép chặt, CHƯA có DI):
```java
// Lớp phụ thuộc cấp thấp
class MySQLDatabase {
    public void query(String sql) {
        System.out.println("Thực thi query trên MySQL: " + sql);
    }
}

// Lớp cấp cao tự khởi tạo phụ thuộc bằng 'new'
class UserRepository {
    private MySQLDatabase database;

    public UserRepository() {
        // Tightly Coupled: UserRepository bị gắn chặt chết cứng với MySQLDatabase
        this.database = new MySQLDatabase(); 
    }

    public void getUser() {
        database.query("SELECT * FROM users");
    }
}
```
*Nhược điểm:* Nếu muốn đổi sang `PostgreSQLDatabase`, ta buộc phải mở code của `UserRepository` ra để sửa lại code, vi phạm nguyên lý Open/Closed trong SOLID.

---

#### Cách làm chuẩn áp dụng DI & IoC (Loosely Coupled - Ghép lỏng):

```java
// Bước 1: Trừu tượng hóa phụ thuộc bằng Interface
interface Database {
    void execute(String sql);
}

// Bước 2: Các triển khai cụ thể
class MySQLDatabase implements Database {
    @Override
    public void execute(String sql) {
        System.out.println("[MySQL] Chạy query: " + sql);
    }
}

class PostgreSQLDatabase implements Database {
    @Override
    public void execute(String sql) {
        System.out.println("[PostgreSQL] Chạy query: " + sql);
    }
}

// Bước 3: Áp dụng Constructor Injection
class UserRepository {
    private final Database database; // Phụ thuộc vào Interface, không phụ thuộc Class cụ thể

    // Dependency được TIÊM từ bên ngoài vào
    public UserRepository(Database database) {
        this.database = database;
    }

    public void getUser() {
        database.execute("SELECT * FROM users");
    }
}

// Bước 4: Chương trình điều phối (Đóng vai trò như một IoC Container thủ công)
public class Main {
    public static void main(String[] args) {
        // Có thể linh hoạt hoán đổi Database tùy ý mà không sửa một dòng code nào của UserRepository
        Database db1 = new MySQLDatabase();
        UserRepository repo1 = new UserRepository(db1);
        repo1.getUser();

        Database db2 = new PostgreSQLDatabase();
        UserRepository repo2 = new UserRepository(db2);
        repo2.getUser();
    }
}
```

### 3.3 Tại sao DI giúp code dễ bảo trì, dễ thay đổi và dễ viết Unit Test?

1. **Dễ thay đổi (Loose Coupling):**
   * Class cấp cao chỉ giao tiếp thông qua abstraction (`interface`). Khi cần thay đổi thư viện, đổi cơ chế lưu trữ (từ SQL sang NoSQL) hay đổi cổng thanh toán (từ VNPay sang Momo), ta chỉ cần viết class mới hiện thực interface đó mà không gây tác động hay làm gãy vỡ code cũ.
2. **Dễ Unit Test (Khả năng Mocking):**
   * Nếu không có DI, khi test `UserRepository`, phương thức sẽ kết nối trực tiếp đến database thật ngoài đời, khiến test chạy rất chậm hoặc làm sai lệch dữ liệu thật.
   * Với DI, trong bài test ta dễ dàng tiêm một bản giả lập (**Mock Database**) vào `UserRepository` để kiểm tra logic mà không cần kết nối mạng hay database vật lý:
     ```java
     class MockDatabase implements Database {
         public boolean isExecuted = false;
         @Override
         public void execute(String sql) {
             this.isExecuted = true; // Ghi nhận hành vi để kiểm thử
         }
     }
     ```

---

## 4. Ứng Dụng Của DI/IoC Trong Spring Framework

Trong hệ sinh thái Spring (Spring Core, Spring Boot), IoC và DI là linh hồn của toàn bộ kiến trúc:

1. **Spring IoC Container (`ApplicationContext`):**
   * Đóng vai trò là "nhà máy" trung tâm chịu trách nhiệm quét (scan), khởi tạo đối tượng, cấu hình các thuộc tính, giải quyết cây quan hệ phụ thuộc và quản lý toàn bộ vòng đời (Life Cycle) của các đối tượng từ khi sinh ra cho tới khi ứng dụng tắt.
   * Các đối tượng được quản lý bởi Spring Container được gọi là **Spring Beans**.
2. **Đăng ký Spring Beans:**
   * Sử dụng các Stereotype Annotations đặt trên đầu Class: `@Component`, `@Service` (tầng nghiệp vụ), `@Repository` (tầng truy xuất dữ liệu DAO), `@Controller` / `@RestController` (tầng điều hướng web).
   * Hoặc định nghĩa thủ công qua phương thức đánh dấu `@Bean` bên trong class cấu hình `@Configuration`.
3. **Tiêm phụ thuộc (Dependency Injection) trong Spring:**
   * **`@Autowired`:** Yêu cầu Spring tự động tìm kiếm Bean tương ứng trong Container và tiêm vào đối tượng cần dùng.
   * **Constructor Injection (Cách tốt nhất, được Spring khuyến nghị):**
     ```java
     @Service
     public class OrderService {
         private final PaymentService paymentService;

         // Spring tự động tiêm PaymentService Bean vào đây khi khởi tạo OrderService
         public OrderService(PaymentService paymentService) {
             this.paymentService = paymentService;
         }
     }
     ```
   * Spring hoàn toàn tự động hóa việc kết nối các thành phần với nhau, lập trình viên chỉ cần khai báo interface và annotation mà không cần viết mã khởi tạo đối tượng thủ công.