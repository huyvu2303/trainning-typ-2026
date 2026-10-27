# Tuần 4 — Message Queue với RabbitMQ

## 1. Message Queue là gì?

Message Queue (MQ) là cơ chế để một thành phần gửi message cho một thành phần khác thông qua broker. Producer gửi message vào broker; broker lưu và định tuyến message đến queue; consumer nhận message rồi xử lý.

Producer và consumer không cần chạy cùng thời điểm, cũng không cần gọi trực tiếp nhau. Broker làm lớp trung gian và giữ message cho đến khi được consumer nhận/xác nhận theo cấu hình.

### Bài toán nếu xử lý tuần tự trong request

Ví dụ user đăng ký xong, backend cần lưu user, gửi email welcome, ghi log và cập nhật thống kê. Nếu request gọi tuần tự tất cả công việc:

```text
Client → API → lưu database → gửi email → ghi log → trả response
```

- Người dùng phải đợi cả email và các việc phụ hoàn tất nên request chậm.
- Email server chậm làm toàn bộ API chậm theo.
- Email lỗi có thể khiến API trả lỗi dù user đã được lưu thành công.
- Khi nhiều request đến cùng lúc, các tác vụ phụ chiếm tài nguyên xử lý request.

Với MQ, API xử lý phần chính, phát message rồi trả response; consumer xử lý việc phụ sau:

```text
Client → API → lưu user → publish message → trả response
                                ↓
                         RabbitMQ queue
                                ↓
                         consumer gửi email
```

MQ không tự làm cho hệ thống luôn thành công. Nó giúp tách rời và làm phẳng tải; vẫn cần xử lý message lỗi, gửi trùng, broker mất kết nối và quan sát queue.

### Synchronous và asynchronous communication

**Synchronous communication (giao tiếp đồng bộ)** là khi bên gửi gọi bên nhận và chờ phản hồi rồi mới tiếp tục. Ví dụ: API gọi service xác thực mật khẩu; cần biết mật khẩu đúng hay sai ngay để quyết định có cấp token không.

```text
API → AuthService: kiểm tra mật khẩu
API ← AuthService: đúng / sai
API tiếp tục xử lý
```

**Asynchronous communication (giao tiếp bất đồng bộ)** là khi bên gửi gửi công việc đi nhưng không chờ bên nhận xử lý xong. Bên nhận xử lý sau, thường thông qua Message Queue. Ví dụ: sau khi đăng ký user, API lưu tài khoản, gửi event vào RabbitMQ rồi trả response; consumer nhận event sau đó để gửi email welcome.

```text
API → RabbitMQ: UserRegistered
API → Client: đăng ký thành công
Consumer nhận message sau đó → gửi email
```

Tóm lại: đồng bộ chờ kết quả để đi tiếp; bất đồng bộ gửi việc đi rồi xử lý độc lập. Trong CourseShop, xác thực đăng nhập cần kết quả ngay nên xử lý đồng bộ; gửi email welcome phù hợp với xử lý bất đồng bộ.

---

## 2. Hai mô hình message

### Point-to-Point (Queue, competing consumers)

Producer gửi message vào một queue. Một trong các consumer đang cạnh tranh sẽ nhận message và xử lý. Khi có nhiều consumer cùng nghe một queue, mỗi message thường được giao cho một consumer trong nhóm, không phải gửi bản sao đến tất cả consumer.

```text
Producer → Queue → Consumer A hoặc Consumer B
```

Dùng khi một công việc chỉ cần xử lý một lần bởi một worker: gửi một email, tạo một hóa đơn, xử lý một đơn hàng.

### Publish/Subscribe (Pub/Sub qua Exchange và nhiều Queue)

Publisher gửi event tới exchange. Mỗi queue được bind phù hợp sẽ nhận một bản message; consumer của từng queue xử lý độc lập.

```text
                    ┌→ Queue email → Consumer email
Publisher → Exchange├→ Queue audit → Consumer audit
                    └→ Queue stats → Consumer thống kê
```

Dùng khi một sự kiện cần kích hoạt nhiều chức năng độc lập, ví dụ `UserRegistered` vừa gửi email, ghi audit vừa cập nhật thống kê.

### So sánh nhanh

| Câu hỏi | Chọn Queue/Point-to-Point | Chọn Pub/Sub |
| --- | --- | --- |
| Một việc có một nhóm worker xử lý? | Có | Không nhất thiết |
| Nhiều nhóm chức năng cần nhận cùng event? | Không | Có, mỗi nhóm có queue riêng |
| Muốn scale việc gửi email bằng nhiều worker? | Nhiều consumer cùng queue | Không cần thêm queue cho từng worker |
| Muốn thêm consumer thống kê mà không ảnh hưởng consumer email? | Không phù hợp nếu dùng chung một queue | Tạo queue riêng bind vào exchange |

Trong RabbitMQ, queue là nơi message được giữ để consumer nhận. Exchange quyết định định tuyến message tới queue theo loại exchange và binding/routing key. Vì thế RabbitMQ vẫn có thể làm cả queue point-to-point lẫn pub/sub.

---

## 3. Chọn RabbitMQ và kiến trúc demo

### Vì sao chọn RabbitMQ cho bài này?

RabbitMQ dễ bắt đầu cho tác vụ nền, có queue, routing, acknowledgement và dead-letter exchange; phù hợp demo một API Spring Boot đẩy công việc gửi email. Kafka cũng phổ biến, mạnh cho event stream có throughput cao, lưu log lâu và consumer cần đọc lại dữ liệu; nhưng cách vận hành/partition và mô hình đọc log nhiều hơn mức cần thiết cho demo gửi email.

| Khái niệm | Vai trò trong RabbitMQ |
| --- | --- |
| Producer | Code gửi message, trong demo là `UserService` sau khi tạo user |
| Broker | RabbitMQ nhận, lưu và định tuyến message |
| Exchange | Nhận message từ producer rồi định tuyến tới queue phù hợp theo loại exchange và binding |
| Queue | Giữ message chờ consumer |
| Binding | Liên kết giữa exchange và queue, có thể kèm quy tắc lọc |
| Routing key | Nhãn producer gắn vào message để exchange xét khi định tuyến |
| Consumer | Code nhận và xử lý message, trong demo là consumer email welcome |
| Ack | Consumer xác nhận xử lý thành công để broker xóa message khỏi queue |
| DLX/DLQ | Dead-letter exchange/queue nhận message bị reject hoặc hết retry theo policy |

### Exchange, Binding và Routing key hoạt động cùng nhau

Ba khái niệm này phối hợp để quyết định **message nào được chuyển tới queue nào**:

```text
Producer -- publish(message, routing key) --> Exchange
                                                │
                                  xét loại exchange + bindings
                                                │
                                                ▼
                                           Queue → Consumer
```

#### Exchange

Exchange là điểm nhận message được producer publish. Nó **không phải nơi consumer đọc message và cũng không phải nơi lưu message chờ xử lý**; nhiệm vụ chính là chọn queue đích dựa trên loại exchange cùng các binding đã khai báo. Queue mới là nơi giữ message cho consumer.

Các loại exchange thường gặp:

- **Direct**: định tuyến khi routing key khớp chính xác với binding key.
- **Topic**: định tuyến theo mẫu nhiều đoạn; `*` khớp đúng một đoạn, `#` khớp không hoặc nhiều đoạn. Ví dụ `user.*` khớp `user.registered`, còn `user.#` khớp `user.registered.email`.
- **Fanout**: gửi bản sao đến mọi queue đã bind; bỏ qua routing key.
- **Headers**: quyết định theo các header của message thay vì routing key; ít dùng hơn trong luồng cơ bản.

Trong topology demo, `courseshop.events` là **topic exchange** để sau này nhiều queue có thể đăng ký nhận các loại event khác nhau.

#### Binding

Binding là liên kết/rule giữa một exchange và một queue. Có thể hình dung đó là “đăng ký nhận” message từ exchange. Với direct/topic exchange, binding thường có binding key để exchange so với routing key; với fanout thì key bị bỏ qua.

Queue không bắt buộc về mặt kỹ thuật phải được bind với exchange, nhưng **queue không có binding phù hợp sẽ không nhận message được publish vào exchange đó**. Vì vậy, để consumer nhận event, cần tạo queue và binding trước khi publish. Một queue có thể bind vào nhiều exchange; một exchange cũng có thể bind tới nhiều queue. Khi nhiều binding cùng khớp, một message có thể được route tới nhiều queue.

Trong demo, binding này nối email queue với event exchange:

```java
BindingBuilder.bind(userEmailQueue)
        .to(eventsExchange)
        .with("user.registered");
```

#### Routing key

Routing key là chuỗi producer gắn với message khi publish. Nó giống **nhãn phân loại** hơn là địa chỉ queue cố định: producer nói message thuộc loại nào; exchange so nhãn đó với binding của các queue rồi quyết định định tuyến. Một routing key có thể khớp nhiều binding nên message có thể tới nhiều queue.

Ý nghĩa của routing key phụ thuộc loại exchange:

- Với **direct**, so khớp chính xác với binding key.
- Với **topic**, so theo các đoạn ngăn bởi dấu chấm và wildcard `*`/`#`.
- Với **fanout**, routing key không được dùng để chọn queue.

Ví dụ producer publish:

```java
rabbitTemplate.convertAndSend(
        "courseshop.events", // exchange
        "user.registered",   // routing key
        event);               // message body
```

Binding email queue có key `user.registered`, nên topic exchange route message này vào queue `user.registered.email`. Sau này có thể bind queue audit bằng mẫu `user.#`; cùng event sẽ được chuyển thêm tới queue audit mà producer không cần biết queue đó tồn tại.

Lưu ý: RabbitMQ có **default exchange** đặc biệt (tên rỗng). Khi publish trực tiếp tới default exchange, routing key chính là tên queue; đây là trường hợp tiện cho gửi thẳng một queue, nhưng demo dùng named exchange để thể hiện routing và pub/sub rõ hơn.

Ví dụ topology:

```text
UserService (producer)
  → exchange: courseshop.events (topic)
  → routing key: user.registered
  → queue: user.registered.email
  → WelcomeEmailConsumer

user.registered.email --reject/retry exhausted--> courseshop.dlx
  → queue: user.registered.email.dlq
```

Topic exchange cho phép sau này bind thêm các queue như `user.registered.audit` mà producer không cần biết từng consumer.


---

## 4. Phát triển từ CourseShop

Xem code

## 5. Các vấn đề cần trình bày

### Message bị gửi trùng (Duplicate Messages) & Tính vô hiệu (Idempotency)

Nguyên nhân: Lỗi mạng khiến Producer gửi message nhưng không nhận được ACK từ Broker nên gửi lại; hoặc Consumer đã xử lý xong nhưng bị crash trước khi kịp gửi ACK về Broker, khiến Broker phân phối lại message đó cho Consumer khác.

Giải pháp (Idempotency - Xử lý nhiều lần cho cùng một kết quả):

- Sử dụng Unique Message ID (Idempotency Key): Mỗi message khi tạo ra phải được gắn một ID duy nhất.

- Kiểm tra trạng thái (Deduplication Check): Trước khi xử lý logic, Consumer kiểm tra ID này trong Cache (Redis) hoặc Database (tra bảng processed_messages). Nếu ID đã tồn tại, bỏ qua không xử lý lại.

- Thiết kế thao tác vô hiệu ở Database:

    + Thay vì dùng UPDATE balance = balance + 100, hãy dùng câu lệnh cập nhật có điều kiện dựa trên trạng thái hoặc dùng UPSERT / INSERT IGNORE với Unique Constraint.

### Message bị mất (Message Loss) & Cơ chế Acknowledge (ACK)

Message có thể bị mất ở cả 3 mốc: Producer $\rightarrow$ Broker $\rightarrow$ Consumer.
- At Producer: Gửi không tới Broker do rớt mạng.
    - Giải pháp: Bật cơ chế Publisher Confirm / Transaction, cài đặt tự động retry nếu không nhận được phản hồi từ Broker.
- At Broker: Broker nhận được message nhưng chưa kịp lưu xuống đĩa cứng thì bị sập nguồn.
    - Giải pháp: Bật cấu hình Durable Queue và Persistent Message (ép ghi dữ liệu xuống ổ cứng/disk), kết hợp chạy cụm (Clustering/Replication) để nhân bản dữ liệu sang các node dự phòng.
- At Consumer: Consumer vừa lấy message về thì bị sập (crash) khi chưa kịp xử lý xong.
    - Giải pháp: Tắt chế độ Auto-ACK. Bắt buộc dùng Manual ACK — Consumer chỉ gửi tín hiệu ACK (xác nhận thành công) về cho Broker sau khi đã hoàn thành xong toàn bộ logic kinh doanh và ghi dữ liệu thành công vào Database.

### Consumer chết giữa chừng & Dead Letter Queue (DLQ)

Hiện tượng: Consumer bị tắt đột ngột (OOM, mất điện) hoặc gặp phải "Poison Message" (message chứa dữ liệu lỗi, null pointer, đính kèm sai format...) làm Consumer crash liên tục mỗi khi nhận lại.

Giải pháp:

- Tự động Re-queue: Nếu Consumer sập khi đang xử lý (chưa gửi ACK), Broker sẽ phát hiện ngắt kết nối và đẩy message đó lại vào hàng đợi để Consumer khác nhận xử lý.

- Cơ chế Retry & Backoff: Khi gặp lỗi trong quá trình xử lý, Consumer nên tiến hành thử lại (Retry) sau một khoảng thời gian chờ tăng dần (Exponential Backoff, ví dụ: chờ 1s, 5s, 15s...).

- Dead Letter Queue (DLQ): Sau khi đã retry quá số lần quy định (ví dụ: 3–5 lần) mà vẫn thất bại, message sẽ được đẩy sang một hàng đợi riêng gọi là DLQ. Cơ chế này giúp hàng đợi chính không bị tắc nghẽn (Blocking), đồng thời lưu trữ lại các tin nhắn lỗi để lập trình viên kiểm tra, sửa lỗi hoặc xử lý thủ công sau.

### Durability và publisher confirmation

Để hạn chế mất message khi broker restart, dùng durable exchange/queue và persistent message. Durable queue không tự đảm bảo producer biết broker đã lưu message; production nên cấu hình publisher confirms/returns và xử lý xác nhận publish. Nếu dùng Outbox, chỉ đánh dấu event đã phát sau khi nhận confirm.

### Thứ tự message (ordering)

Thứ tự có quan trọng không?

- Không quan trọng: Khi các tác vụ hoàn toàn độc lập (VD: gửi email xác nhận, gửi thông báo push, nén ảnh). Xử lý song song bằng nhiều Consumer để tối ưu hiệu năng.
- Rất quan trọng: Khi các sự kiện có tính phụ thuộc lẫn nhau theo thời gian (VD: Tạo đơn hàng $\rightarrow$ Thanh toán $\rightarrow$ Giao hàng, hoặc chuỗi biến động số dư tài khoản).

Thách thức: Nếu có nhiều Consumer cùng rút tin nhắn từ một Queue để xử lý song song, message đến trước có thể bị xử lý chậm hơn message đến sau, dẫn đến sai lệch dữ liệu.

Giải pháp:
- Partition / Sharding Key: Đảm bảo toàn bộ các message thuộc cùng một đối tượng (VD: cùng 1 order_id hoặc user_id) luôn được gửi vào cùng một Partition/Queue.
- Single Consumer Per Partition: Phân công duy nhất 1 Consumer (hoặc 1 Thread) chịu trách nhiệm xử lý Partition/Queue đó để đảm bảo các bước được thực hiện lần lượt theo đúng thứ tự FIFO (First In, First Out).

---


### Câu hỏi trình bày và câu trả lời gợi ý

**MQ là gì?** Broker trung gian nhận, lưu, định tuyến message giữa producer và consumer; hai bên không cần gọi trực tiếp hay chạy cùng lúc.

**Nếu không dùng MQ thì flow có vấn đề gì?** API phải chờ email/log/tác vụ phụ; phụ thuộc thời gian hoạt động của các dịch vụ đó; lỗi tác vụ phụ dễ kéo theo request chính; khó hấp thụ burst tải.

**Khi nào gọi trực tiếp, khi nào dùng MQ?** Gọi trực tiếp khi cần kết quả ngay. Dùng MQ cho công việc nền, có thể xử lý sau response và cần tách rời/retry.

**Queue khác Pub/Sub thế nào?** Queue phân phối một message cho một worker trong nhóm consumer. Pub/Sub định tuyến event tới nhiều queue để nhiều nhóm consumer cùng nhận bản sao.

**Vì sao RabbitMQ?** Bài demo cần một broker tác vụ nền dễ hiểu; RabbitMQ có queue, exchange, ack và DLQ, phù hợp gửi email bất đồng bộ. Kafka phù hợp hơn khi cần event log lớn, throughput cao, replay và stream processing.

**Nếu consumer chết thì sao?** Nếu chưa ack, broker có thể giao lại message. Nếu message liên tục lỗi, giới hạn retry rồi chuyển DLQ để không lặp vô hạn.

**MQ có đảm bảo không trùng không?** Không nên mặc định như vậy. Thiết kế consumer idempotent bằng `eventId` và lưu dấu event đã xử lý.

**API có chờ email gửi xong không?** Không. API chờ lưu user và phát message; consumer gửi email bất đồng bộ sau đó. Đảm bảo publish thành công/outbox cần được thiết kế riêng.

---

## Nếu không dùng MQ thì gặp các vấn đề

Những vấn đề có thể gặp:
- Đăng ký chậm: API phải đợi máy chủ email phản hồi mới trả kết quả.
- Email lỗi làm ảnh hưởng đăng ký: Nếu xử lý exception không tách biệt, việc gửi email thất bại có thể khiến request báo lỗi dù user đã được lưu thành công.
- Khó chịu tải tăng: Nhiều request đăng ký cùng lúc đồng nghĩa nhiều request cùng chờ gửi email.
- Khó gửi lại khi lỗi: Nếu SMTP tạm thời không hoạt động, ứng dụng phải tự xây dựng cơ chế retry và theo dõi các email thất bại.
