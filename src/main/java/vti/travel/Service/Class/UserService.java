package vti.travel.Service.Class;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import vti.travel.Exception.AppException;
import vti.travel.Exception.ErrorResponseBase;
import vti.travel.Model.Entity.Role;
import vti.travel.Model.Entity.Users;
import vti.travel.Model.Request.BaseRequest;
import vti.travel.Model.Request.SearchUsers;
import vti.travel.Model.Request.UserRequest;
import vti.travel.Repository.Specification.UserSpecification;
import vti.travel.Repository.UserRepository;
import vti.travel.Service.IUserService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Validated
@Service
public class UserService implements IUserService, UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> optional = userRepository.findByUsername(username);
        if (optional.isPresent()) {
            Users user = optional.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
            return new User(user.getUsername(), user.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException(username);
        }

    }


    @Override
    public List<Users> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public List<Users> searchUsersByUsername(String username) {

        Optional<Users> optionalUsers = userRepository.findByUsername(username);
        return optionalUsers.map(Collections::singletonList).orElse(Collections.emptyList());

    }

    @Override
    public Optional<Users> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<Users> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void CreateUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorResponseBase.DOUBLE_EMAIL_EX);
        }
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new AppException(ErrorResponseBase.DOUBLE_USERNAME_EX);
        }
        // tạo đối tượng để lưu database
        Users users = new Users();
        users.setEmail(userRequest.getEmail());
        users.setPassword(encoder.encode(userRequest.getPassword()));
        users.setUsername(userRequest.getUsername());
        users.setPhone(userRequest.getPhone());
        users.setRole(Role.MANAGER);
        users.setFullName(userRequest.getFullName());
        userRepository.save(users);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Users updateUser(Integer userId, UserRequest userRequest) {
        Users users = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorResponseBase.Id_not));
        users.setUsername(userRequest.getUsername());
        users.setPassword(encoder.encode(userRequest.getPassword()));
        users.setPhone(userRequest.getPhone());
        users.setFullName(userRequest.getFullName());
        users.setEmail(userRequest.getEmail());
        return userRepository.save(users);
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean deleteUser(Integer userId) {
        Optional<Users> optionalUsers = userRepository.findById(userId);
        if (optionalUsers.isPresent()) {
            userRepository.delete(optionalUsers.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Page<Users> search(SearchUsers searchUsers) {

        Specification<Users> condition = UserSpecification.buildCondition(searchUsers);
        PageRequest pageRequest = BaseRequest.buildPageRequest(searchUsers);

        return userRepository.findAll(condition, pageRequest);
    }


}
