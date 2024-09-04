package vti.travel.Controller.ConfigADMIN;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Aspect
@Component // dánh dấu nó là 1 bean
public class AdminAccessAspect {

    @Pointcut("execution(* vti.travel.Controller.UserController.getUserDetails(..))")
    public void getUserDetails() {
    }

    @Pointcut("execution(* vti.travel.Controller.UserController.deleteUser(..))")
    public void deleteUser() {
    }

    @Pointcut("execution(* vti.travel.Controller.UserController.getAllUser(..))")
    public void getAllUser() {
    }

    @Pointcut("execution(* vti.travel.Controller.UserController.search(..))")
    public void search() {
    }

    @Pointcut("execution(* vti.travel.Controller.UserController.searchUsersByUsername(..))")
    public void searchUsersByUsername() {
    }

    // trước khi gọi các phương thức sẽ kiểm tra quyền truy cập thông qua role là ADMIN hay không
    @Before("getUserDetails() || deleteUser() || getAllUser()|| search() || searchUsersByUsername()")
    public void checkAdminAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));
        if (!isAdmin) {
            throw new AccessDeniedException("bạn không có quyền truy cập trang này ");
        }
    }
}
