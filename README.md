Hướng Dẫn Sử Dụng Đồ Án 8-Puzzle Game
1. Giới thiệu
Đồ án 8-Puzzle Game là một ứng dụng Java được phát triển bằng Swing, sử dụng các thuật toán tìm kiếm thông minh như BFS (Breadth-First Search) và A* để giải bài toán xếp ô số (8-puzzle). Ứng dụng cho phép người dùng tương tác trực tiếp với lưới 3x3, tùy chỉnh trạng thái, và quan sát quá trình giải bằng các heuristic khác nhau. Mục tiêu của đồ án là cung cấp một công cụ giáo dục và giải trí, giúp người dùng hiểu rõ hơn về thuật toán và logic giải đố.
2. Yêu cầu hệ thống
•	Phần mềm: Java Development Kit (JDK) phiên bản 11 hoặc cao hơn, môi trường phát triển như NetBeans hoặc IntelliJ IDEA.
•	Phần cứng: Máy tính với RAM tối thiểu 4GB và CPU đủ mạnh để xử lý thuật toán trong thời gian hợp lý.
3. Cài đặt và chạy ứng dụng
•	Cài đặt dự án: 
o	Tải mã nguồn từ kho lưu trữ hoặc sao chép các file Java (Main.java, PuzzleGUI.java, Node.java, State.java, Solver.java, Result.java, PuzzleState.java) vào một dự án Java trong NetBeans.
o	Đảm bảo thư mục src/main/resources/images/ tồn tại và chứa file puzzle_image.png nếu muốn sử dụng tính năng tải ảnh.
•	Chạy ứng dụng: 
o	Nhấp chuột phải vào Main.java, chọn Run để khởi động giao diện.
o	Cửa sổ ứng dụng sẽ xuất hiện với kích thước 500x600 pixel, hiển thị lưới 3x3 ban đầu với các số từ 1 đến 8 và ô trống.
4. Hướng dẫn sử dụng giao diện
4.1. Các thành phần giao diện
•	Lưới 3x3 (trung tâm): Hiển thị trạng thái hiện tại của puzzle, với các ô có số từ 1 đến 8 và ô trống (màu xám).
•	Khu vực điều khiển (phía bắc): 
o	Combo box "Thuật toán": Chọn giữa BFS hoặc A*.
o	Combo box "Heuristic": Chọn heuristic (Số ô sai vị trí, Khoảng cách Manhattan, Số ô sai hàng/cột, Khoảng cách Euclid) khi dùng A*.
o	Nút "Giải": Bắt đầu quá trình giải tự động.
o	Nút "Đặt lại": Khôi phục trạng thái ban đầu.
o	Nút "Xáo trộn": Tạo trạng thái ngẫu nhiên.
o	Nút "Tùy chỉnh": Nhập thủ công trạng thái puzzle.
•	Khu vực kết quả (phía đông): Hiển thị số node duyệt, số bước, và lời giải khi giải thành công.
•	Nhãn trạng thái (phía nam): Cung cấp thông tin về hành động hiện tại (ví dụ: "Đang giải..." hoặc "Chúc mừng!").
4.2. Cách sử dụng
•	Di chuyển ô thủ công: 
o	Nhấp chuột trái vào ô liền kề với ô trống để hoán đổi vị trí. Nếu di chuyển không hợp lệ, nhãn trạng thái sẽ hiển thị "Ô không thể di chuyển!".
o	Khi hoàn thành puzzle (trạng thái giống mục tiêu), thông báo "Chúc mừng! Bạn đã giải được puzzle!" sẽ xuất hiện.
•	Giải puzzle tự động: 
o	Chọn thuật toán (BFS hoặc A*) và heuristic (nếu dùng A*) từ combo box.
o	Nhấp nút "Giải". Ứng dụng sẽ xử lý và hiển thị kết quả trong khu vực kết quả. Nếu chọn "Có" trong hộp thoại "Tự chạy lời giải", trạng thái sẽ tự động chạy từng bước (mỗi bước cách nhau 1 giây).
o	Nếu trạng thái không có lời giải, thông báo lỗi sẽ hiện lên.
•	Đặt lại trạng thái: 
o	Nhấp "Đặt lại" để khôi phục lưới về trạng thái ban đầu (1, 2, 3, 4, 5, 6, 7, 8, 0).
•	Xáo trộn trạng thái: 
o	Nhấp "Xáo trộn" để tạo trạng thái ngẫu nhiên. Nếu trạng thái không khả thi, ứng dụng sẽ tự động xáo lại.
•	Tùy chỉnh trạng thái: 
o	Nhấp "Tùy chỉnh" để mở cửa sổ nhập liệu 3x3. Nhập các số từ 0 đến 8 (0 là ô trống), không trùng lặp, rồi nhấn OK. Nếu nhập sai, thông báo lỗi sẽ xuất hiện.
5. Lưu ý khi sử dụng
•	Hạn chế thời gian: Thuật toán sẽ dừng sau 120 giây nếu không tìm thấy lời giải, tránh treo máy.
•	Hiệu suất: Với các trạng thái phức tạp, A* với heuristic h4 (Khoảng cách Euclid) thường hiệu quả nhất, nhưng có thể tốn thời gian tính toán ban đầu.
