package vti.travel.Model.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vti.travel.Model.Entity.Role;


@Data
@AllArgsConstructor
@NoArgsConstructor


public class SearchUsers extends BaseRequest {

    private int userId;

    private String username;

    private String email;

    private String fullName;

    private String phone;

    private Role role;
}
