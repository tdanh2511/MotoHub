# MotoHub – Giải thích chi tiết 2 chức năng: **Yêu thích** & **Quản lý người dùng**

Tài liệu này giải thích theo đúng code hiện có trong project MotoHub (Java + XML + SQLite).

- **Yêu thích (Favorites)**: Người dùng bấm icon ❤️ để thêm/xóa xe khỏi danh sách yêu thích, và có màn hình xem danh sách yêu thích.
- **Quản lý người dùng (Admin)**: Admin xem danh sách tài khoản, thêm/sửa/xóa người dùng.

---

## 0) Tổng quan kiến trúc liên quan

- **SQLite**: khai báo bảng trong `MotoHubDbHelper`.
- **Repository**: thao tác DB (CRUD) nằm trong `FavoriteRepository`, `UserRepository`.
- **UI (Activity + RecyclerView/Adapter)**:
  - Favorites: `MotorbikeAdapter` (toggle icon), `FavoritesActivity` (màn hình danh sách yêu thích), `MotorbikeDetailActivity` (toggle ở màn chi tiết).
  - Users: `ManageUsersActivity` + `AdminUserAdapter` + dialog `dialog_user_form.xml`.
- **Session** (đang đăng nhập): lưu trong `SharedPreferences` tên `motohub_session`, key `user_id`.

---

## 1) Chức năng **Yêu thích (Favorites)**

### 1.1) CSDL: tạo bảng `favorites`
**File:** `app/src/main/java/com/example/motohub/database/MotoHubDbHelper.java`

#### Code (trích đoạn)
```java
01 | // Favorites table
02 | String createFavoritesTable = "CREATE TABLE " + TABLE_FAVORITES + " ("
03 |         + COL_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
04 |         + COL_FAVORITE_USER_ID + " INTEGER NOT NULL, "
05 |         + COL_FAVORITE_MOTORBIKE_ID + " INTEGER NOT NULL, "
06 |         + "UNIQUE(" + COL_FAVORITE_USER_ID + ", " + COL_FAVORITE_MOTORBIKE_ID + ")"
07 |         + ");";
08 | 
09 | db.execSQL(createFavoritesTable);
```

#### Giải thích dưới từng dòng
- **Dòng 01**: Comment để phân nhóm đoạn SQL tạo bảng favorites.
- **Dòng 02**: Bắt đầu dựng chuỗi lệnh `CREATE TABLE` cho bảng `TABLE_FAVORITES` (tên bảng = `favorites`).
- **Dòng 03**: Cột `id` (khóa chính) tự tăng (`AUTOINCREMENT`) để định danh mỗi bản ghi.
- **Dòng 04**: Cột `user_id` bắt buộc có (mỗi lượt yêu thích gắn với 1 user).
- **Dòng 05**: Cột `motorbike_id` bắt buộc có (mỗi lượt yêu thích gắn với 1 xe).
- **Dòng 06**: Ràng buộc `UNIQUE(user_id, motorbike_id)` để **không thể “thích trùng”** cùng một xe nhiều lần cho cùng user.
- **Dòng 07**: Kết thúc câu lệnh SQL.
- **Dòng 08**: Dòng trống cho dễ đọc.
- **Dòng 09**: Thực thi SQL để tạo bảng trong SQLite.

---

### 1.2) Lấy `userId` từ session để biết “ai đang yêu thích”
**File:** `app/src/main/java/com/example/motohub/activities/auth/LoginActivity.java`

#### Code (trích đoạn lưu session sau khi login)
```java
01 | private void saveSession(User user) {
02 |     SharedPreferences.Editor editor = prefs.edit();
03 |     editor.putBoolean(KEY_IS_LOGGED_IN, true);
04 |     editor.putInt(KEY_USER_ID, user.getId());
05 |     editor.putString(KEY_USERNAME, user.getUsername());
06 |     editor.putString(KEY_FULLNAME, user.getFullname());
07 |     editor.putString(KEY_ROLE, user.getRole());
08 |     editor.apply();
09 | }
```

#### Giải thích dưới từng dòng
- **Dòng 01**: Hàm ghi thông tin đăng nhập vào `SharedPreferences`.
- **Dòng 02**: Tạo `Editor` để ghi dữ liệu.
- **Dòng 03**: Đánh dấu đã đăng nhập.
- **Dòng 04**: Lưu `user_id` (quan trọng nhất cho Favorites/Cart/Orders…).
- **Dòng 05**: Lưu username để hiển thị hoặc dùng lại.
- **Dòng 06**: Lưu fullname.
- **Dòng 07**: Lưu role (`admin`/`user`) để điều hướng màn hình.
- **Dòng 08**: `apply()` ghi bất đồng bộ (nhanh hơn `commit()`).
- **Dòng 09**: Kết thúc hàm.

---

### 1.3) Tầng Repository: `FavoriteRepository` (CRUD Favorites)
**File:** `app/src/main/java/com/example/motohub/repository/FavoriteRepository.java`

#### 1.3.1) Kiểm tra một xe có đang được yêu thích không (`isFavorite`)
```java
01 | public boolean isFavorite(int userId, int motorbikeId) {
02 |     SQLiteDatabase db = dbHelper.getReadableDatabase();
03 |     Cursor cursor = db.rawQuery(
04 |             "SELECT id FROM " + MotoHubDbHelper.TABLE_FAVORITES + " WHERE user_id = ? AND motorbike_id = ?",
05 |             new String[]{String.valueOf(userId), String.valueOf(motorbikeId)}
06 |     );
07 | 
08 |     boolean exists = cursor.moveToFirst();
09 |     cursor.close();
10 |     return exists;
11 | }
```

Giải thích dưới từng dòng:
- **Dòng 01**: Khai báo hàm trả về `true/false` nếu bản ghi favorite tồn tại.
- **Dòng 02**: Mở DB ở chế độ **read-only**.
- **Dòng 03**: Chạy truy vấn raw SQL.
- **Dòng 04**: Query tìm 1 dòng trong `favorites` theo cặp `(user_id, motorbike_id)`.
- **Dòng 05**: Truyền tham số `?` để tránh ghép chuỗi trực tiếp.
- **Dòng 06**: Kết thúc gọi `rawQuery`.
- **Dòng 07**: Dòng trống.
- **Dòng 08**: `moveToFirst()` trả `true` nếu có ít nhất 1 bản ghi → tức là đang favorite.
- **Dòng 09**: Đóng cursor (bắt buộc để tránh leak).
- **Dòng 10**: Trả về kết quả.
- **Dòng 11**: Kết thúc hàm.

#### 1.3.2) Toggle yêu thích (`toggleFavorite`)
```java
01 | public boolean toggleFavorite(int userId, int motorbikeId) {
02 |     if (isFavorite(userId, motorbikeId)) {
03 |         return removeFavorite(userId, motorbikeId);
04 |     } else {
05 |         return addFavorite(userId, motorbikeId);
06 |     }
07 | }
```

Giải thích dưới từng dòng:
- **Dòng 01**: Hàm “đảo trạng thái” (đang thích → bỏ thích, chưa thích → thêm thích).
- **Dòng 02**: Gọi `isFavorite()` để biết trạng thái hiện tại.
- **Dòng 03**: Nếu đã thích, gọi `removeFavorite()` để xóa.
- **Dòng 04**: Nhánh ngược lại.
- **Dòng 05**: Nếu chưa thích, gọi `addFavorite()` để thêm.
- **Dòng 06**: Kết thúc if/else.
- **Dòng 07**: Kết thúc hàm.

#### 1.3.3) Lấy danh sách xe yêu thích của một user (`getFavoriteMotorbikes`)
```java
01 | public List<Motorbike> getFavoriteMotorbikes(int userId) {
02 |     List<Motorbike> favorites = new ArrayList<>();
03 |     SQLiteDatabase db = dbHelper.getReadableDatabase();
04 | 
05 |     String query = "SELECT m.* FROM " + MotoHubDbHelper.TABLE_MOTORBIKES + " m " +
06 |             "INNER JOIN " + MotoHubDbHelper.TABLE_FAVORITES + " f ON m." +
07 |             MotoHubDbHelper.COL_ID + " = f." + MotoHubDbHelper.COL_FAVORITE_MOTORBIKE_ID +
08 |             " WHERE f." + MotoHubDbHelper.COL_FAVORITE_USER_ID + " = ?";
09 | 
10 |     Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
11 | 
12 |     if (cursor.moveToFirst()) {
13 |         do {
14 |             Motorbike motorbike = new Motorbike();
15 |             motorbike.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_ID)));
16 |             motorbike.setName(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_NAME)));
17 |             motorbike.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_BRAND)));
18 |             motorbike.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_PRICE)));
19 |             motorbike.setImage(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_IMAGE)));
20 |             motorbike.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_STOCK)));
21 | 
22 |             favorites.add(motorbike);
23 |         } while (cursor.moveToNext());
24 |     }
25 | 
26 |     cursor.close();
27 |     return favorites;
28 | }
```

Giải thích dưới từng dòng:
- **Dòng 01**: Hàm trả về `List<Motorbike>` theo `userId`.
- **Dòng 02**: Khởi tạo list kết quả rỗng.
- **Dòng 03**: Mở DB đọc.
- **Dòng 04**: Dòng trống.
- **Dòng 05**: Tạo query lấy tất cả cột của bảng `motorbikes` (bí danh `m`).
- **Dòng 06**: `INNER JOIN` với bảng favorites (bí danh `f`) để chỉ lấy xe mà user đã thích.
- **Dòng 07**: Điều kiện join: `m.id = f.motorbike_id`.
- **Dòng 08**: Điều kiện lọc: `f.user_id = ?`.
- **Dòng 09**: Dòng trống.
- **Dòng 10**: Chạy query với tham số `userId`.
- **Dòng 11**: Dòng trống.
- **Dòng 12**: Nếu cursor có dữ liệu, nhảy về dòng đầu.
- **Dòng 13**: Duyệt từng dòng bằng `do...while`.
- **Dòng 14**: Tạo model `Motorbike` mới.
- **Dòng 15**: Đọc cột `id` và set vào object.
- **Dòng 16**: Đọc `name`.
- **Dòng 17**: Đọc `brand`.
- **Dòng 18**: Đọc `price`.
- **Dòng 19**: Đọc `image`.
- **Dòng 20**: Đọc `stock`.
- **Dòng 21**: Dòng trống.
- **Dòng 22**: Thêm xe vào list kết quả.
- **Dòng 23**: Chuyển sang bản ghi tiếp theo; hết thì thoát.
- **Dòng 24**: Kết thúc if.
- **Dòng 25**: Dòng trống.
- **Dòng 26**: Đóng cursor.
- **Dòng 27**: Trả về danh sách favorites.
- **Dòng 28**: Kết thúc hàm.

---

### 1.4) UI: Toggle icon yêu thích ngay trên danh sách xe (`MotorbikeAdapter`)
**File:** `app/src/main/java/com/example/motohub/adapters/MotorbikeAdapter.java`

#### Code (trích đoạn phần xử lý icon ❤️)
```java
01 | boolean isFav = favoriteRepository.isFavorite(userId, motorbike.getId());
02 | holder.imgFavorite.setImageResource(
03 |         isFav ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
04 | );
05 | 
06 | holder.imgFavorite.setOnClickListener(v -> {
07 |     if (userId <= 0) {
08 |         Toast.makeText(context, "Vui lòng đăng nhập để thêm yêu thích", Toast.LENGTH_SHORT).show();
09 |         return;
10 |     }
11 | 
12 |     boolean wasAdded = favoriteRepository.toggleFavorite(userId, motorbike.getId());
13 |     boolean newState = favoriteRepository.isFavorite(userId, motorbike.getId());
14 | 
15 |     holder.imgFavorite.setImageResource(
16 |             newState ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
17 |     );
18 | 
19 |     String message = newState ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích";
20 |     Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
21 | 
22 |     if (favoriteChangeListener != null) {
23 |         favoriteChangeListener.onFavoriteChanged();
24 |     }
25 | });
```

Giải thích dưới từng dòng:
- **Dòng 01**: Hỏi DB xem xe này đã được user thích chưa để hiển thị icon đúng.
- **Dòng 02**: Set icon cho `ImageView`.
- **Dòng 03**: Nếu là favorite → icon filled, ngược lại → icon border.
- **Dòng 04**: Kết thúc lời gọi `setImageResource`.
- **Dòng 05**: Dòng trống.
- **Dòng 06**: Khi user bấm vào icon ❤️.
- **Dòng 07**: Nếu chưa đăng nhập (`userId <= 0`).
- **Dòng 08**: Thông báo yêu cầu đăng nhập.
- **Dòng 09**: Dừng xử lý.
- **Dòng 10**: Kết thúc if.
- **Dòng 11**: Dòng trống.
- **Dòng 12**: Gọi `toggleFavorite` để thêm/xóa trong bảng `favorites`.
- **Dòng 13**: Đọc lại trạng thái mới (để chắc chắn UI đồng bộ DB).
- **Dòng 14**: Dòng trống.
- **Dòng 15**: Cập nhật icon theo trạng thái mới.
- **Dòng 16**: Chọn icon filled/border.
- **Dòng 17**: Kết thúc set.
- **Dòng 18**: Dòng trống.
- **Dòng 19**: Tạo message tùy theo trạng thái.
- **Dòng 20**: Show toast phản hồi thao tác.
- **Dòng 21**: Dòng trống.
- **Dòng 22**: Nếu Activity cha có đăng ký callback.
- **Dòng 23**: Báo “favorite changed” để Activity có thể reload danh sách (đặc biệt hữu ích ở `FavoritesActivity`).
- **Dòng 24**: Kết thúc if.
- **Dòng 25**: Kết thúc listener.

> Ghi chú: Biến `wasAdded` hiện không dùng về sau; nhưng không ảnh hưởng chức năng.

---

### 1.5) UI: Màn hình “Sản phẩm yêu thích” (`FavoritesActivity`)
**File:** `app/src/main/java/com/example/motohub/activities/user/FavoritesActivity.java`

#### Code (trích đoạn load user + load favorites + empty state)
```java
01 | private void loadUserData() {
02 |     SharedPreferences prefs = getSharedPreferences("motohub_session", MODE_PRIVATE);
03 |     userId = prefs.getInt("user_id", -1);
04 | }
05 | 
06 | private void loadFavorites() {
07 |     favoriteMotorbikes = favoriteRepository.getFavoriteMotorbikes(userId);
08 | 
09 |     if (favoriteMotorbikes.isEmpty()) {
10 |         showEmptyState();
11 |     } else {
12 |         showFavorites();
13 |     }
14 | }
15 | 
16 | private void showEmptyState() {
17 |     rvFavorites.setVisibility(View.GONE);
18 |     layoutEmptyFavorites.setVisibility(View.VISIBLE);
19 | }
20 | 
21 | private void showFavorites() {
22 |     layoutEmptyFavorites.setVisibility(View.GONE);
23 |     rvFavorites.setVisibility(View.VISIBLE);
24 | 
25 |     motorbikeAdapter = new MotorbikeAdapter(this, favoriteMotorbikes, this, userId, favoriteRepository, this);
26 |     rvFavorites.setAdapter(motorbikeAdapter);
27 | }
```

Giải thích dưới từng dòng:
- **Dòng 01**: Hàm đọc thông tin session.
- **Dòng 02**: Lấy `SharedPreferences` đúng tên `motohub_session`.
- **Dòng 03**: Lấy `user_id`; nếu không có thì `-1`.
- **Dòng 04**: Kết thúc hàm.
- **Dòng 05**: Dòng trống.
- **Dòng 06**: Hàm nạp danh sách xe yêu thích.
- **Dòng 07**: Gọi repository join DB để lấy `List<Motorbike>` yêu thích.
- **Dòng 08**: Dòng trống.
- **Dòng 09**: Nếu danh sách rỗng.
- **Dòng 10**: Hiển thị UI trạng thái rỗng (empty state).
- **Dòng 11**: Nhánh ngược lại.
- **Dòng 12**: Hiển thị danh sách favorites.
- **Dòng 13**: Kết thúc if/else.
- **Dòng 14**: Kết thúc hàm.
- **Dòng 15**: Dòng trống.
- **Dòng 16**: Hàm bật UI “chưa có favorites”.
- **Dòng 17**: Ẩn RecyclerView.
- **Dòng 18**: Hiện layout empty.
- **Dòng 19**: Kết thúc hàm.
- **Dòng 20**: Dòng trống.
- **Dòng 21**: Hàm bật UI danh sách favorites.
- **Dòng 22**: Ẩn empty layout.
- **Dòng 23**: Hiện RecyclerView.
- **Dòng 24**: Dòng trống.
- **Dòng 25**: Tạo `MotorbikeAdapter`:
  - `this` (context)
  - `favoriteMotorbikes` (data)
  - `this` (OnMotorbikeClickListener)
  - `userId`, `favoriteRepository`
  - `this` (OnFavoriteChangeListener) để khi bỏ thích thì reload.
- **Dòng 26**: Gắn adapter vào RecyclerView.
- **Dòng 27**: Kết thúc hàm.

---

### 1.6) UI: Toggle yêu thích trong màn chi tiết (`MotorbikeDetailActivity`)
**File:** `app/src/main/java/com/example/motohub/activities/user/MotorbikeDetailActivity.java`

#### Code (trích đoạn cập nhật icon + onClick)
```java
01 | private void updateFavoriteIcon() {
02 |     if (userId > 0) {
03 |         boolean isFavorite = favoriteRepository.isFavorite(userId, motorbike.getId());
04 |         imgFavorite.setImageResource(
05 |                 isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
06 |         );
07 |     }
08 | }
09 | 
10 | imgFavorite.setOnClickListener(v -> {
11 |     if (userId <= 0) {
12 |         Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
13 |         return;
14 |     }
15 | 
16 |     favoriteRepository.toggleFavorite(userId, motorbike.getId());
17 |     updateFavoriteIcon();
18 | 
19 |     boolean isFavorite = favoriteRepository.isFavorite(userId, motorbike.getId());
20 |     String message = isFavorite ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích";
21 |     Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
22 | });
```

Giải thích dưới từng dòng:
- **Dòng 01**: Hàm đồng bộ icon ❤️ theo trạng thái DB.
- **Dòng 02**: Chỉ xử lý khi có user đăng nhập.
- **Dòng 03**: Query DB để xem xe hiện tại đã favorite chưa.
- **Dòng 04**: Set icon cho `imgFavorite`.
- **Dòng 05**: Chọn icon filled/border.
- **Dòng 06**: Kết thúc gọi `setImageResource`.
- **Dòng 07**: Kết thúc if.
- **Dòng 08**: Kết thúc hàm.
- **Dòng 09**: Dòng trống.
- **Dòng 10**: Bắt sự kiện bấm icon yêu thích trong màn chi tiết.
- **Dòng 11**: Nếu chưa đăng nhập.
- **Dòng 12**: Thông báo.
- **Dòng 13**: Dừng xử lý.
- **Dòng 14**: Kết thúc if.
- **Dòng 15**: Dòng trống.
- **Dòng 16**: Đảo trạng thái favorite trong DB.
- **Dòng 17**: Cập nhật lại icon để UI phản ánh đúng DB.
- **Dòng 18**: Dòng trống.
- **Dòng 19**: Đọc lại trạng thái để tạo message.
- **Dòng 20**: Chọn nội dung toast.
- **Dòng 21**: Hiển thị toast.
- **Dòng 22**: Kết thúc listener.

---

## 2) Chức năng **Quản lý người dùng (Admin)**

### 2.1) UI: hiển thị danh sách user bằng RecyclerView
**File:** `app/src/main/java/com/example/motohub/activities/admin/ManageUsersActivity.java`

#### Code (trích đoạn `loadUsers()` + callback edit/xóa)
```java
01 | private void loadUsers() {
02 |     List<User> users = userRepository.getAllUsers();
03 |     adapter = new AdminUserAdapter(this, users, new AdminUserAdapter.OnUserActionListener() {
04 |         @Override
05 |         public void onEdit(User user) {
06 |             showEditUserDialog(user);
07 |         }
08 | 
09 |         @Override
10 |         public void onDelete(User user) {
11 |             showDeleteConfirmDialog(user);
12 |         }
13 |     });
14 |     recyclerView.setAdapter(adapter);
15 | }
```

Giải thích dưới từng dòng:
- **Dòng 01**: Hàm load lại danh sách user từ DB.
- **Dòng 02**: Gọi repository để lấy tất cả users.
- **Dòng 03**: Tạo adapter hiển thị, đồng thời truyền listener xử lý hành động.
- **Dòng 04**: Override callback edit.
- **Dòng 05**: Khi bấm “Sửa” trên 1 user.
- **Dòng 06**: Mở dialog sửa thông tin user.
- **Dòng 07**: Kết thúc hàm.
- **Dòng 08**: Dòng trống.
- **Dòng 09**: Override callback delete.
- **Dòng 10**: Khi bấm “Xóa” trên 1 user.
- **Dòng 11**: Mở dialog xác nhận xóa.
- **Dòng 12**: Kết thúc hàm.
- **Dòng 13**: Kết thúc tạo listener.
- **Dòng 14**: Gắn adapter vào RecyclerView để render list.
- **Dòng 15**: Kết thúc hàm.

---

### 2.2) Quy tắc nghiệp vụ: không cho xóa tài khoản admin
**File:** `app/src/main/java/com/example/motohub/activities/admin/ManageUsersActivity.java`

#### Code (trích đoạn `showDeleteConfirmDialog`)
```java
01 | private void showDeleteConfirmDialog(User user) {
02 |     if ("admin".equalsIgnoreCase(user.getRole())) {
03 |         Toast.makeText(this, "Không thể xóa tài khoản admin", Toast.LENGTH_SHORT).show();
04 |         return;
05 |     }
06 | 
07 |     new AlertDialog.Builder(this)
08 |             .setTitle("Xác nhận xóa")
09 |             .setMessage("Bạn có chắc muốn xóa người dùng " + user.getFullname() + "?")
10 |             .setPositiveButton("Xóa", (dialog, which) -> {
11 |                 int result = userRepository.deleteUser(user.getId());
12 |                 if (result > 0) {
13 |                     Toast.makeText(this, "Xóa người dùng thành công", Toast.LENGTH_SHORT).show();
14 |                     loadUsers();
15 |                 } else {
16 |                     Toast.makeText(this, "Xóa người dùng thất bại", Toast.LENGTH_SHORT).show();
17 |                 }
18 |             })
19 |             .setNegativeButton("Hủy", null)
20 |             .show();
21 | }
```

Giải thích dưới từng dòng:
- **Dòng 01**: Hàm confirm xóa.
- **Dòng 02**: Nếu role là `admin`.
- **Dòng 03**: Thông báo không cho xóa.
- **Dòng 04**: Dừng hàm.
- **Dòng 05**: Kết thúc if.
- **Dòng 06**: Dòng trống.
- **Dòng 07**: Tạo `AlertDialog`.
- **Dòng 08**: Set tiêu đề.
- **Dòng 09**: Set nội dung, hiển thị fullname.
- **Dòng 10**: Nút “Xóa”: nếu xác nhận thì chạy code trong lambda.
- **Dòng 11**: Gọi repository xóa user theo `id`.
- **Dòng 12**: Nếu xóa thành công (số dòng bị xóa > 0).
- **Dòng 13**: Toast thành công.
- **Dòng 14**: Reload danh sách.
- **Dòng 15**: Nhánh thất bại.
- **Dòng 16**: Toast thất bại.
- **Dòng 17**: Kết thúc if.
- **Dòng 18**: Kết thúc xử lý nút positive.
- **Dòng 19**: Nút “Hủy” đóng dialog.
- **Dòng 20**: Hiển thị dialog.
- **Dòng 21**: Kết thúc hàm.

---

### 2.3) Mapping role hiển thị (Spinner) ↔ role lưu DB (`admin`/`user`)
**File:** `app/src/main/java/com/example/motohub/activities/admin/ManageUsersActivity.java`

#### Code (trích đoạn `setupRoleSpinner` và `getRoleValue`)
```java
01 | private void setupRoleSpinner(Spinner spinnerRole, String selectedRole) {
02 |     ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
03 |             this,
04 |             android.R.layout.simple_spinner_item,
05 |             ROLE_DISPLAY_VALUES
06 |     );
07 |     roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
08 |     spinnerRole.setAdapter(roleAdapter);
09 |     spinnerRole.setSelection("admin".equalsIgnoreCase(selectedRole) ? 1 : 0);
10 | }
11 | 
12 | private String getRoleValue(Spinner spinnerRole) {
13 |     return spinnerRole.getSelectedItemPosition() == 1 ? "admin" : "user";
14 | }
```

Giải thích dưới từng dòng:
- **Dòng 01**: Hàm cấu hình spinner role và chọn sẵn giá trị.
- **Dòng 02**: Tạo adapter cho spinner.
- **Dòng 03**: Context.
- **Dòng 04**: Layout item mặc định của Android.
- **Dòng 05**: Nguồn dữ liệu hiển thị (`{"Người dùng", "Quản trị viên"}`).
- **Dòng 06**: Kết thúc tạo adapter.
- **Dòng 07**: Layout khi dropdown.
- **Dòng 08**: Gắn adapter vào spinner.
- **Dòng 09**: Set lựa chọn mặc định: admin → index 1, user → index 0.
- **Dòng 10**: Kết thúc hàm.
- **Dòng 11**: Dòng trống.
- **Dòng 12**: Hàm chuyển từ lựa chọn UI sang giá trị lưu DB.
- **Dòng 13**: Nếu chọn index 1 → `admin`, ngược lại → `user`.
- **Dòng 14**: Kết thúc hàm.

---

### 2.4) Tầng Repository: `UserRepository` (CRUD Users)
**File:** `app/src/main/java/com/example/motohub/repository/UserRepository.java`

#### 2.4.1) Lấy tất cả user (`getAllUsers`)
```java
01 | public List<User> getAllUsers() {
02 |     List<User> users = new ArrayList<>();
03 |     SQLiteDatabase db = dbHelper.getReadableDatabase();
04 | 
05 |     Cursor cursor = db.rawQuery("SELECT * FROM " + MotoHubDbHelper.TABLE_USERS + " ORDER BY id DESC", null);
06 | 
07 |     if (cursor.moveToFirst()) {
08 |         do {
09 |             User user = new User();
10 |             user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_USER_ID)));
11 |             user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_USERNAME)));
12 |             user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_PASSWORD)));
13 |             user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_FULLNAME)));
14 |             user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(MotoHubDbHelper.COL_ROLE)));
15 |             user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
16 |             user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
17 |             user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
18 |             users.add(user);
19 |         } while (cursor.moveToNext());
20 |     }
21 | 
22 |     cursor.close();
23 |     db.close();
24 |     return users;
25 | }
```

Giải thích dưới từng dòng:
- **Dòng 01**: Hàm trả về list user.
- **Dòng 02**: Tạo list kết quả.
- **Dòng 03**: Mở DB đọc.
- **Dòng 04**: Dòng trống.
- **Dòng 05**: Query tất cả user, sắp xếp mới nhất trước.
- **Dòng 06**: Dòng trống.
- **Dòng 07**: Kiểm tra có dữ liệu.
- **Dòng 08**: Duyệt từng bản ghi.
- **Dòng 09**: Tạo object `User`.
- **Dòng 10**: Set `id`.
- **Dòng 11**: Set `username`.
- **Dòng 12**: Set `password` (hiện đang lưu plaintext).
- **Dòng 13**: Set `fullname`.
- **Dòng 14**: Set `role`.
- **Dòng 15**: Set `phone`.
- **Dòng 16**: Set `email`.
- **Dòng 17**: Set `address`.
- **Dòng 18**: Add user vào list.
- **Dòng 19**: Chuyển dòng tiếp theo.
- **Dòng 20**: Kết thúc if.
- **Dòng 21**: Dòng trống.
- **Dòng 22**: Đóng cursor.
- **Dòng 23**: Đóng DB.
- **Dòng 24**: Trả về list.
- **Dòng 25**: Kết thúc hàm.

#### 2.4.2) Thêm user (`addUser`) và cập nhật (`updateUser`)
```java
01 | public long addUser(User user) {
02 |     SQLiteDatabase db = dbHelper.getWritableDatabase();
03 |     ContentValues values = new ContentValues();
04 |     values.put(MotoHubDbHelper.COL_USERNAME, user.getUsername());
05 |     values.put(MotoHubDbHelper.COL_PASSWORD, user.getPassword());
06 |     values.put(MotoHubDbHelper.COL_FULLNAME, user.getFullname());
07 |     values.put(MotoHubDbHelper.COL_ROLE, user.getRole());
08 |     values.put(MotoHubDbHelper.COL_PHONE, user.getPhone());
09 |     values.put(MotoHubDbHelper.COL_EMAIL, user.getEmail());
10 |     values.put(MotoHubDbHelper.COL_ADDRESS, user.getAddress());
11 | 
12 |     long result = db.insert(MotoHubDbHelper.TABLE_USERS, null, values);
13 |     db.close();
14 |     return result;
15 | }
```

Giải thích dưới từng dòng:
- **Dòng 01**: Hàm thêm user, trả về rowId (hoặc -1 nếu lỗi).
- **Dòng 02**: Mở DB ghi.
- **Dòng 03**: Tạo `ContentValues` để map cột → giá trị.
- **Dòng 04**: Gán username.
- **Dòng 05**: Gán password.
- **Dòng 06**: Gán fullname.
- **Dòng 07**: Gán role.
- **Dòng 08**: Gán phone.
- **Dòng 09**: Gán email.
- **Dòng 10**: Gán address.
- **Dòng 11**: Dòng trống.
- **Dòng 12**: Insert vào bảng users.
- **Dòng 13**: Đóng DB.
- **Dòng 14**: Trả về kết quả.
- **Dòng 15**: Kết thúc hàm.

---

## 3) Checklist test nhanh (thực tế chạy app)

### Favorites
1. Đăng nhập user thường.
2. Vào Home/danh sách xe → bấm icon ❤️ trên 1 xe.
3. Vào màn “Sản phẩm yêu thích” (`FavoritesActivity`) → thấy xe vừa lưu.
4. Bấm ❤️ lần nữa để bỏ thích → danh sách reload (empty state nếu hết).

### Manage Users (Admin)
1. Đăng nhập admin.
2. Vào “Quản lý người dùng”.
3. Thêm user mới → danh sách cập nhật.
4. Sửa user → danh sách cập nhật.
5. Xóa user thường → ok.
6. Thử xóa admin → bị chặn.

---

## 4) Ghi chú kỹ thuật (nên biết)

- Bảng `favorites` có `UNIQUE(user_id, motorbike_id)` nên thao tác “thêm trùng” sẽ fail (đúng kỳ vọng).
- Password hiện đang lưu **plaintext** trong SQLite (phù hợp demo/lab; không an toàn cho production).
- Favorites được reload ở `FavoritesActivity.onResume()` để đảm bảo khi quay về từ màn chi tiết, UI luôn mới.
