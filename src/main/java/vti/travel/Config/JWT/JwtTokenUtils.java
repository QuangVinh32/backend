package vti.travel.Config.JWT;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vti.travel.Exception.AppException;
import vti.travel.Model.DTO.LoginDTO;
import vti.travel.Model.Entity.Role;
import vti.travel.Model.Entity.Token;
import vti.travel.Repository.TokenRepository;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;


@Slf4j
@Component
public class JwtTokenUtils {
    private static final long EXPIRATION = 864000000; // Thời gian hết hạn của JWT (10 ngày)
    private static final String SECRET = "123456"; // Mã bí mật sử dụng để ký JWT (Thay đổi thành mã bí mật thực tế)
    private static final String PREFIX_TOKEN = "Bearer"; // Ký tự đầu của chuỗi JWT
    private static final String AUTHORIZATION = "Authorization"; // Header key của JWT

    @Autowired
    private TokenRepository tokenRepository;

    // Hàm này dùng để tạo JWT (Access Token)
    public String createAccessToken(LoginDTO loginDTO) {
        // Tạo thời hạn của JWT
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION);

        // Tạo JWT
        String token = Jwts.builder()
                .setId(String.valueOf(loginDTO.getUserId())) // ID của JWT
                .setSubject(loginDTO.getUserAgent()) // Chủ đề (Subject) của JWT
                .setIssuedAt(new Date())
                .setIssuer("VTI") // Người tạo ra JWT
                .setExpiration(expirationDate) // Thời hạn JWT
                .signWith(SignatureAlgorithm.HS512, SECRET) // Ký JWT bằng mã bí mật
                .claim("authorities", loginDTO.getRole().name()) // Thêm thông tin tùy chỉnh vào JWT
                .claim("user-Agent", loginDTO.getUserAgent()).compact(); // Thêm thông tin tùy chỉnh khác

        // Lưu JWT vào cơ sở dữ liệu
        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setExpiration(expirationDate);
        tokenEntity.setUserAgent(loginDTO.getUserAgent());
        tokenRepository.save(tokenEntity);
        return token;
    }

    // Hàm này dùng để giải mã JWT
    public LoginDTO parseAccessToken(String token) {
        LoginDTO loginDto = new LoginDTO();

        if (!token.isEmpty()) {
            try {
                // Loại bỏ tiền tố "Bearer" nếu có
                token = token.replace(PREFIX_TOKEN, "").trim();

                // Giải mã JWT
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(token).getBody();

                // Lấy thông tin từ JWT
                String user = claims.getSubject();
                Role role = Role.valueOf(claims.get("authorities").toString());
                String userAgent = claims.get("user-Agent").toString();

                // Gán thông tin vào đối tượng LoginDto
                loginDto.setUsername(user);
                loginDto.setRole(role);
                loginDto.setUserAgent(userAgent);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        return loginDto;
    }

    // Hàm này dùng để kiểm tra tính hợp lệ của JWT
    public boolean checkToken(String token, HttpServletResponse response, HttpServletRequest httpServletRequest) {
        try {
            if (StringUtils.isBlank(token) || !token.startsWith(PREFIX_TOKEN)) { // Kiểm tra token có hợp lệ không
                responseJson(response, new AppException("Token không hợp lệ", 401, httpServletRequest.getRequestURI()));
                return false;
            }

            // Loại bỏ tiền tố "Bearer"
            token = token.replace(PREFIX_TOKEN, "").trim();

            // Kiểm tra xem JWT có tồn tại trong cơ sở dữ liệu không
            Token tokenEntity = tokenRepository.findByToken(token);
            if (tokenEntity == null) {
                responseJson(response, new AppException("Token không tồn tại", 401, httpServletRequest.getRequestURI()));
                return false;
            }

            // Kiểm tra thời hạn của JWT
            if (tokenEntity.getExpiration().after(new Date(System.currentTimeMillis() + EXPIRATION))) {
                responseJson(response, new AppException("Token hết hiệu lực", 401, httpServletRequest.getRequestURI()));
                return false;
            }
        } catch (Exception e) {
            responseJson(response, new AppException(e.getMessage(), 401, httpServletRequest.getRequestURI()));
            return false;
        }
        return true;
    }

    // Hàm này dùng để trả về thông báo lỗi trong dạng JSON khi có lỗi xảy ra
    private void responseJson(HttpServletResponse response, AppException appException) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(appException.getCode());
        try {
            response.getWriter().print(JSON.toJSONString(appException));
        } catch (IOException e) {
            log.debug(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
