package vti.travel.Controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vti.travel.Config.JWT.JwtTokenUtils;
import vti.travel.Exception.AppException;
import vti.travel.Exception.ErrorResponseBase;
import vti.travel.Model.DTO.LoginDTO;
import vti.travel.Model.Entity.Role;
import vti.travel.Model.Entity.Users;
import vti.travel.Model.Request.LoginRequest;
import vti.travel.Model.Request.UserRequest;
import vti.travel.Repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@Validated
@Component
public class AuthController {
    // sô lần nhập sai tối đa
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    // số time minutes giới hạn khi bị khóa
    private static final int LOCKOUT_DURATION_MINUTES = 10;
    @Autowired
    JwtTokenUtils jwtTokenUtils;
    @Autowired
    private UserRepository repository;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private HttpServletRequest httpServletRequest;
    // lưu số lần nhập sai
    private Map<String, Integer> loginAttemptMap = new HashMap<>();
    // thời gian có đang lưu
    private Map<String, LocalDateTime> lockoutMap = new HashMap<>();

    // chức năng đăng nhập sử dụng mã hóa và token
    @PostMapping("/login")
    public LoginDTO loginJWT(@RequestBody LoginRequest request) {
        String username = request.getUsername();

        // Kiểm tra xem tài khoản có đang bị khóa không
        LocalDateTime lockoutTime = lockoutMap.get(username);
        if (lockoutTime != null && lockoutTime.isAfter(LocalDateTime.now())) {
            throw new AppException(ErrorResponseBase.Login_locked);
        }

        // Kiểm tra số lần đăng nhập sai trước đó của người dùng
        int loginAttempts = loginAttemptMap.getOrDefault(username, 0);
        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            // Khóa tài khoản và đặt thời gian khóa
            LocalDateTime lockoutEndTime = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
            lockoutMap.put(username, lockoutEndTime);

            throw new AppException(ErrorResponseBase.Login_locked);
        }
        // tìm kiếm username nếu không có thì sẽ thông báo lỗi
        Optional<Users> userOptional = repository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new AppException(ErrorResponseBase.Login_username);
        }

        // đang tìm kiếm trả ra là 1 optional thì phải chuyển đổi .get()
        Users user = userOptional.get();
        // giải mã so sánh giữa password nhập vào và password dang có trong database có trùng khớp hay không
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            // Tăng số lần đăng nhập sai của người dùng
            loginAttempts++;
            loginAttemptMap.put(username, loginAttempts);
            if (loginAttempts == 4) {
                throw new AppException(ErrorResponseBase.valueOf("số lần đăng nhập còn 1 lần nếu bạn nhập sai lần nữa sẽ bị khóa "));

            }
            // Kiểm tra xem tài khoản đã vượt quá số lần đăng nhập sai để khóa không
            if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {

                // Khóa tài khoản và đặt thời gian khóa
                LocalDateTime lockoutEndTime = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
                lockoutMap.put(username, lockoutEndTime);

                throw new AppException(ErrorResponseBase.Login_locked);
            }

            throw new AppException(ErrorResponseBase.Login_password);
        }

        // Đăng nhập thành công, đặt lại số lần đăng nhập sai và xóa khóa tài khoản (nếu có)
        loginAttemptMap.remove(username);
        lockoutMap.remove(username);
        // lưu xuống token
        LoginDTO loginDTO = new LoginDTO();
        BeanUtils.copyProperties(user, loginDTO);
        loginDTO.setUserAgent(httpServletRequest.getHeader("User-Agent"));
        String token = jwtTokenUtils.createAccessToken(loginDTO);
        loginDTO.setToken(token);
        return loginDTO;
    }


    // chức năng đăng kí
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {
        // kiểm tra username đã tồn tại hay chưa
        Optional<Users> existingUser = repository.findByUsername(userRequest.getUsername());
        if (existingUser.isPresent()) {
            throw new AppException(ErrorResponseBase.USERNAME_EXISTS);
        }
        // kiểm tra email đã tồn tại chưa
        Optional<Users> existingEmail = repository.findByEmail(userRequest.getEmail());
        if (existingEmail.isPresent()) {
            throw new AppException(ErrorResponseBase.DOUBLE_EMAIL_EX);
        }
        // tạo đối tượng để lưu xuống database
        Users newUser = new Users();
        newUser.setEmail(userRequest.getEmail());
        newUser.setPassword(encoder.encode(userRequest.getPassword()));
        newUser.setUsername(userRequest.getUsername());
        newUser.setPhone(userRequest.getPhone());
        newUser.setRole(Role.USER);
        newUser.setFullName(userRequest.getFullName());
        repository.save(newUser);
        // thông báo ra két quả
        return ResponseEntity.status(HttpStatus.CREATED).body("thêm mới thành công ");
    }

}
