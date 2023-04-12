<!-- PROJECT INFO -->
<div align="center">
  <h4 align="center">Khóa luận tốt nghiệp năm 2022-2023</h4>
  <h2 align="center">Tìm hiểu Nextjs và Spring Boot xây dựng ứng dụng trao đổi tài liệu học tập cho sinh viên</h2>
  <h5 align="center">GVHD: ThS.Nguyễn Thanh Sang</h5>
</div>

<div>
<h5>Thành viên nhóm :</h5>
  <table>
    <tr>
      <th>MSSV</th>
      <th>Họ và tên</th>    
    </tr>
    <tr>
      <td>3119410294</td>
      <td>Võ Hoàng Quỳnh Như Front-end</td>    
    </tr>
    <tr>
      <td>3119560070</td>
      <td>Phạm Nguyễn Minh Thuận Back-end</td>    
    </tr>
  </table>
</div>

<h5>Introduce :</h5>

    Website support students to exchange.

    Mentor: ThS.Nguyễn Thanh Sang

    Begin  Date: 02/2023

    Finish Date: 06/2011

## Tentative technologies and frameworks

- Java 17
- Spring boot 3
- Spring security 6
- Node v16
- NextJS 13
- Tailwincss
- Docker

## Table of Contents

<ol>
    <li> 
        <a href="#about"> About The Project </a>
        <ul>
            <li><a href="#function">Functions</a></li>           
            <li><a href="#database">Database</a></li>
            <li><a href="#install">Install and Configuration</a>
            <ul>
              <li><a href='#backend'>Back End</a></li>
              <li><a href='#frontend'>Front end</a></li>
            </ul>
            </li>
        </ul>
    </li>
    <li> <a href='#usage'> Usage</a></li>
</ol>

<!-- USER CASE -->

### <div id='function'>Functions</div>

```
1. Khách hàng
  1.1 Đăng ký
  1.2 Đăng nhập
  1.3 Quên mật khẩu
  1.4 Đăng xuất
  1.5 Xem danh mục
  1.6 Xem danh sách bài đăng
  1.7 Tìm kiếm bài đăng

2. Thành viên - Kế thừa chức năng ‘Khách hàng’ ngoài ra có thêm các chức năng:
  2.1 Quản lý bài đăng
  2.2 Đăng bài
  2.3 Đã bán/Ẩn bài đăng
  2.4 Sửa bài đăng
  2.5 Xem chi tiết
  2.6 Xem thông bài đăng cá nhân
  2.7 Quản lý danh sách yêu thích
    ●	Xem danh sách
    ●	Xem chi tiết
    ●	Lưu/Bỏ lưu
  2.8 Quản lý tài khoản:
    ●	Đổi mật khẩu
    ●	Sửa thông tin cá nhân
  2.9 Quản lý mua hàng:
    ●	Mua hàng
    ●	Thanh toán
    ●	Xem lịch sử mua hàng
    ●	Xem  trạng thái đơn mua
  2.10 Quản lý bán hàng:
    ●	Xác nhận/từ chối đơn đặt hàng
    ●	Theo dõi trình trạng

3. Quản trị viên
  3.1 Quản lý slide: CRUD, Tìm kiếm
  3.2 Quản lý danh mục:CRUD,Tìm kiếm
  3.3 Quản lý bài đăng:
    ●	Ẩn bài viết
    ●	Xem chi tiết, Tìm kiếm
  3.4 Quản lý thành viên:
    ●	Khoá/Mở khoá thành viên
    ●	Xem chi tiết, Tìm kiếm
  3.5 Quản lý hoá đơn:Xem

```

<!-- BACKEND -->

### <div id='backend'># Back End</div>

<h4><i>Requirements :</i></h4>

1. Java JDK 17: https://www.oracle.com/java/

<h4><i>Technology :</i></h4>
<ul>
  <li>Java spring boot</li>
  <li>Microservice</li>
  <li>Restfull api</li>
</ul>
<h4><i>Architecture :</i></h4>
<img width="583" alt="image" src="https://user-images.githubusercontent.com/87434092/231483883-6732eda5-c96e-4d2a-b5ea-41b6d1440605.png">
