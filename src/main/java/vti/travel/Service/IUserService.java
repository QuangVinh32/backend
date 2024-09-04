package vti.travel.Service;

import org.springframework.data.domain.Page;
import vti.travel.Model.Entity.Users;
import vti.travel.Model.Request.SearchUsers;
import vti.travel.Model.Request.UserRequest;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    List<Users> getAllUser();

    List<Users> searchUsersByUsername(String username);

    Optional<Users> findByUsername(String username);

    Optional<Users> getUserById(Integer userId);

    void CreateUser(UserRequest userRequest);

    Users updateUser(Integer userId, UserRequest userRequest);

    boolean deleteUser(Integer userId);

    Page<Users> search(SearchUsers searchUsers);
}
