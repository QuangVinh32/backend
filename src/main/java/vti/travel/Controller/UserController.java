package vti.travel.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vti.travel.Model.Entity.Users;
import vti.travel.Model.Request.SearchUsers;
import vti.travel.Model.Request.UserRequest;
import vti.travel.Service.Class.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    // lấy tất cả thông tin chỉ ADMIN mới được xem
    @GetMapping("get-all")
    public ResponseEntity<List<Users>> getAllUser() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUser());
    }

    // tìm kiếm theo tên chỉ có ADMIN mới được xem
    @GetMapping("/searchName")
    public ResponseEntity<List<Users>> searchUsersByUsername(@RequestParam String username) {
        List<Users> users = userService.searchUsersByUsername(username);
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);

        } else {
            return ResponseEntity.noContent().build();
        }
    }

    // lấy thông tin chi tiết chỉ có ADMIN mới có quyền xem
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable Integer userId) {


        Optional<Users> optionalUsers = userService.getUserById(userId);

        if (optionalUsers.isPresent()) {
            Users users = optionalUsers.get();
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("không có thông tin về id được tìm kiếm ");
        }
    }

    // thêm mới user những role là user vẫn có quyền thêm mới

    @PostMapping("/create")
    public ResponseEntity<?> CreateUser(@RequestBody UserRequest userRequest) {
        userService.CreateUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    // Chỉnh sửa user, có role là user vẫn có thể sửa
    @PutMapping("/edit/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Integer userId, @Valid @RequestBody UserRequest userRequest) {
        Optional<Users> optionalUsers = userService.getUserById(userId);
        if (optionalUsers.isPresent()) {
            Users user = optionalUsers.get();
            // Kiểm tra xem người dùng có vai trò ADMIN hay không
            if (user.getRole().name().equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không thể chỉnh sửa user có role là ADMIN");
            }
            userService.updateUser(userId, userRequest);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("USER không có thông tin");
        }
    }

    // xóa user
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) {
        // Kiểm tra vai trò của người dùng được xóa
        Optional<Users> userToDelete = userService.getUserById(userId);
        if (userToDelete.isPresent()) {
            Users user = userToDelete.get();
            // Kiểm tra xem người dùng có vai trò ADMIN hay không
            if (user.getRole().name().equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("không thể xóa user có role là ADMIN");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("USER không có thông tin ");
        }

        // Tiếp tục xóa người dùng
        boolean delete = userService.deleteUser(userId);
        if (delete) {
            return ResponseEntity.ok("xóa user thành công ");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("xóa không thành công ");
        }
    }


    // chia trang
    @PostMapping("/search")
    public Page<Users> search(@RequestBody SearchUsers searchUsers) {
        return userService.search(searchUsers);
    }

}
