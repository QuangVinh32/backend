package vti.travel.Service;

import org.springframework.data.domain.Page;
import vti.travel.Model.Entity.Booking;
import vti.travel.Model.Entity.BookingStatus;
import vti.travel.Model.Request.BookingRequest;
import vti.travel.Model.Request.SearchBookingRq;

import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface IBookingService {
    List<Booking> getAll();

    Booking getById(int bookingId);

    void createBooking(BookingRequest bookingRequest);

    Booking update(int bookingId, BookingRequest bookingRequest);

    void deleteBooking(int bookingId);

    List<Booking> findByFullName(String fullName);

    Page<Booking> search(SearchBookingRq searchBookingRq);
    int countBookingsInMonth(Month month);
    String sumTotalAmountInMonth(Month month);
    List<Booking> getBookingsWithStatuses(List<BookingStatus> statuses);
    Long countBookingsWithStatuses(List<BookingStatus> statuses);
    Map<String, Double> getTotalAmountByMonthInYear(int year);
    double getTotalAmountForMonth(YearMonth yearMonth);
    int getCountByStatusAndMonth(BookingStatus status, YearMonth yearMonth);
}
