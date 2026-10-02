-- he thong quan ly sinh vien



use sql_coban;
set sql_safe_updates = 0;

-- 1. ddl: tao bang va rang buoc

create table faculty (
    id int auto_increment primary key,
    name varchar(100) not null
);

create table subjects (
    id int auto_increment primary key,
    name varchar(100) not null,
    credit tinyint not null check (credit > 0)
);

create table students (
    id int auto_increment primary key,
    faculty_id int,
    name varchar(50) not null,
    gpa float,
    email varchar(255) unique,
    date_of_birth date,
    gender varchar(10) default 'nam',
    constraint fk_students_faculty foreign key (faculty_id) references faculty(id) on delete set null,
    constraint chk_student_valid check (gpa is null or (gpa >= 0 and gpa <= 4.0))
);

create table registrations (
    id int auto_increment primary key,
    student_id int not null,
    subject_id int not null,
    register_time datetime default current_timestamp,
    constraint fk_reg_student foreign key (student_id) references students(id) on delete cascade,
    constraint fk_reg_subject foreign key (subject_id) references subjects(id) on delete cascade
);

create table grades (
    id int auto_increment primary key,
    student_id int not null,
    subject_id int not null,
    score float check (score >= 0 and score <= 10),
    constraint fk_grades_student foreign key (student_id) references students(id) on delete cascade,
    constraint fk_grades_subject foreign key (subject_id) references subjects(id) on delete cascade
);

create table student_accounts (
    student_id int primary key,
    balance decimal(12, 2) default 0.00,
    tuition_fee decimal(12, 2) default 0.00,
    constraint fk_acc_student foreign key (student_id) references students(id) on delete cascade
);

-- alter & drop
alter table students add column phone_number varchar(15);
alter table students drop column phone_number;

-- 2. dml: them du lieu mau

insert into faculty (name) values 
('cong nghe thong tin'), 
('quan tri kinh doanh'), 
('an toan thong tin'),
('dien tu vien thong');

insert into subjects (name, credit) values 
('co so du lieu', 3),
('lap trinh java', 4),
('cau truc du lieu va giai thuat', 3),
('kien truc may tinh', 3),
('mang may tinh', 3);

insert into students (faculty_id, name, email, gpa, date_of_birth, gender) values 
(1, 'do huy vu', 'huyvudz@gmail.com', 3.8, '2006-03-23', 'nam'),
(1, 'tran van cuong', 'cuong@gmail.com', 3.2, '2006-05-15', 'nam'),
(2, 'le van tuan', 'tuan@gmail.com', 3.7, '2006-10-08', 'nam'),
(3, 'nguyen van chien', 'chien@gmail.com', 2.8, '2006-03-10', 'nam'),
(1, 'nguyen minh phuong', 'phuong@gmail.com', 3.6, '2006-11-20', 'nu'),
(2, 'hoang quang minh', 'minh@gmail.com', 2.9, '2006-07-12', 'nam'),
(null, 'nguyen van an', 'an@gmail.com', 3.1, '2006-01-01', 'nam');

insert into registrations (student_id, subject_id, register_time) values 
(1, 1, '2026-09-01 08:00:00'),
(1, 2, '2026-09-01 08:05:00'),
(1, 3, '2026-09-01 08:10:00'),
(2, 1, '2026-09-01 09:00:00'),
(3, 2, '2026-09-02 10:00:00'),
(5, 1, '2026-09-02 14:00:00'),
(5, 2, '2026-09-02 14:05:00');

insert into grades (student_id, subject_id, score) values 
(1, 1, 9.5),
(1, 2, 8.8),
(1, 3, 9.0),
(2, 1, 7.0),
(3, 2, 8.0),
(5, 1, 8.5);

insert into student_accounts (student_id, balance, tuition_fee) values 
(1, 15000000.00, 7500000.00),
(2, 8000000.00, 7500000.00),
(3, 2000000.00, 5000000.00);

-- update & delete
update students set gpa = gpa + 0.1 where gpa < 3.0;
delete from students where email is null;

select * from students;

-- 3. queries: join, aggregate functions, group by, having, order by

-- inner join
select s.id, s.name as student_name, s.gpa, f.name as faculty_name
from students s
inner join faculty f on s.faculty_id = f.id;

-- left join
select f.name as faculty_name, s.name as student_name
from faculty f
left join students s on f.id = s.faculty_id;

-- right join
select f.name as faculty_name, s.name as student_name
from faculty f
right join students s on f.id = s.faculty_id;

-- aggregate functions, group by, having, order by
select 
    f.name as faculty_name,
    count(s.id) as total_students,
    round(avg(s.gpa), 2) as avg_gpa,
    max(s.gpa) as max_gpa,
    min(s.gpa) as min_gpa
from faculty f
inner join students s on f.id = s.faculty_id
group by f.id, f.name
having total_students >= 2
order by avg_gpa desc;

-- 4. index & explain

create table big_students (
    id int auto_increment primary key,
    student_code varchar(50),
    email varchar(100),
    admission_year int
);

delimiter //
create procedure seedbigstudents()
begin
    declare i int default 1;
    while i <= 100000 do
        insert into big_students (student_code, email, admission_year)
        values (
            concat('b24dccn', lpad(i, 4, '0')),
            concat('student_', i, '@ptit.edu.vn'),
            2020 + (i % 6)
        );
        set i = i + 1;
    end while;
end //
delimiter ;

call seedbigstudents();

-- truoc khi co index
explain select * from big_students where email = 'student_88888@ptit.edu.vn';

-- tao index
create index idx_big_students_email on big_students(email);

-- sau khi co index
explain select * from big_students where email = 'student_88888@ptit.edu.vn';

-- 5. phan trang (pagination)

-- offset-based
select * from big_students
order by id asc
limit 10 offset 40;

-- cursor-based
select * from big_students
where id > 40
order by id asc
limit 10;

-- 6. transaction & locking

-- transaction thanh cong
start transaction;
    update student_accounts 
    set balance = balance - 7500000.00 
    where student_id = 1;

    update student_accounts 
    set tuition_fee = tuition_fee - 7500000.00 
    where student_id = 1;
commit;

-- transaction rollback
start transaction;
    update student_accounts 
    set balance = balance - 5000000.00 
    where student_id = 3;
    rollback;