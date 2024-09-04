package vti.travel.Repository.Specification;

import org.springframework.data.jpa.domain.Specification;
import vti.travel.Model.Entity.Role;
import vti.travel.Model.Entity.Users;
import vti.travel.Model.Request.SearchUsers;

public class UserSpecification {
    public static Specification<Users> buildCondition(SearchUsers searchUsers) {
        return Specification.where(searchByFullname(searchUsers.getFullName()))
                .and(searchByRole(searchUsers.getRole()))
                .and(searchByEmail(searchUsers.getEmail()))
                .and(searchByUserName(searchUsers.getUsername()))
                .and(searchByPhone(searchUsers.getPhone()));
    }

    private static Specification<Users> searchByFullname(String fullName) {
        if (fullName != null) {
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.like(root.get("fullName"), "%" + fullName + "%");
            });
        } else {
            return null;
        }
    }

    private static Specification<Users> searchByRole(Role role) {
        if (role != null) {
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("role"), role);
            });
        } else {
            return null;
        }
    }

    private static Specification<Users> searchByEmail(String email) {
        if (email != null) {
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.like(root.get("email"), "%" + email + "%");
            });
        } else {
            return null;
        }
    }

    private static Specification<Users> searchByUserName(String username) {
        if (username != null) {
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.like(root.get("username"), "%" + username + "%");
            });
        } else {
            return null;
        }
    }

    private static Specification<Users> searchByPhone(String phone) {
        if (phone != null) {
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.like(root.get("phone"), "%" + phone + "%");
            });
        } else {
            return null;
        }
    }
}
