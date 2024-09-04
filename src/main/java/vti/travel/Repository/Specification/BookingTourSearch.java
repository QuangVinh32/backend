package vti.travel.Repository.Specification;

import org.springframework.data.jpa.domain.Specification;
import vti.travel.Model.Entity.Booking;
import vti.travel.Model.Request.SearchBookingRq;


public class BookingTourSearch {

    public static Specification<Booking> buildCondition(SearchBookingRq searchBookingRq) {
        return Specification.where(searchByFullName(searchBookingRq.getFullName()))
                .and(searchByAddress(searchBookingRq.getAddress()));
    }

    private static Specification<Booking> searchByFullName(String fullName) {
        if (fullName != null) {
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.like(root.get("fullName"), "%" + fullName + "%");
            });
        } else {
            return null;
        }
    }

    private static Specification<Booking> searchByAddress(String address) {
        if (address != null) {
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.like(root.get("fullName"), "%" + address + "%");
            });
        } else {
            return null;
        }
    }

}
