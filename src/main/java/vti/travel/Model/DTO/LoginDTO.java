package vti.travel.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vti.travel.Model.Entity.Role;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private int userId;
    private String username;
    private Role role;
    private String userAgent;
    private String token;
}
