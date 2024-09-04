package vti.travel.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vti.travel.Model.Entity.Booking;
import vti.travel.Model.Entity.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {
    List<Booking> findBookingByFullName(String fullName);

    List<Booking> findByBookingAtBetween(LocalDate start, LocalDate end);

    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.bookingAt BETWEEN :start AND :end AND b.status = :status")
    Double calculateTotalAmountInMonth1(@Param("start") LocalDate start, @Param("end") LocalDate end, @Param("status") BookingStatus status);

    List<Booking> findByStatusIn(List<BookingStatus> statuses);

    Long countByStatusIn(List<BookingStatus> statuses);

    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = :status AND b.bookingAt BETWEEN :startDate AND :endDate")
    Double calculateTotalAmountInMonth(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") BookingStatus status
    );

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'PENDING'")
    Long countPendingBookings();

    // Số lượng booking đã thành công
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'SUCCESSFUL'")
    Long countSuccessfulBookings();

    // Số lượng booking đã hủy
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'FAILED'")
    Long countCancelledBookings();

    List<Booking> findByStatusAndBookingAtBetween(BookingStatus status, LocalDate startDate, LocalDate endDate);
}
