package vti.travel.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.travel.Model.Entity.Booking;
import vti.travel.Model.Entity.BookingStatus;
import vti.travel.Model.Request.BookingRequest;
import vti.travel.Model.Request.SearchBookingRq;
import vti.travel.Service.Class.BookingService;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
@CrossOrigin("*")
@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    // chức năng hiện bookings
    @GetMapping("get-all")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAll());
    }

    // chức năng tìm kiếm Booking theo id
    @GetMapping("/get/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable int bookingId) {
        Booking booking = bookingService.getById(bookingId);
        if (booking != null) {
            return ResponseEntity.ok(booking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // tạo mới booking
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest) {
        bookingService.createBooking(bookingRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // update booking
    @PutMapping("/update/{bookingId}")
    public ResponseEntity<Booking> updateBooking(@PathVariable int bookingId, @RequestBody BookingRequest bookingRequest) {
        try {
            Booking updatedBooking = bookingService.update(bookingId, bookingRequest);
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // xóa booking
    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable int bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    // tìm kiếm theo tên booking
    @PostMapping("/fullname")
    public ResponseEntity<List<Booking>> findByFullName(@RequestBody String fullName) {
        List<Booking> bookings = bookingService.findByFullName(fullName);
        if (bookings != null) {
            return ResponseEntity.ok(bookings);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("/search")
    public Page<Booking> search(@RequestBody SearchBookingRq searchBookingRq) {
        return bookingService.search(searchBookingRq);
    }
    // số lượng booking trong tháng
    @GetMapping("/count-by-month")
    public int countBookingsInMonth(@RequestParam int month) {
        return bookingService.countBookingsInMonth(Month.of(month));
    }

    // số lượng tiền booking đã thành công
    @GetMapping("/total-amount")
    public String  getTotalAmountInMonth(@RequestParam int month) {
        return bookingService.sumTotalAmountInMonth(Month.of(month));
    }

    // trả ra danh sách theo statust
    @GetMapping("/by-status")
    public List<Booking> getBookingsByStatus() {
        List<BookingStatus> statuses = Arrays.asList(BookingStatus.PENDING, BookingStatus.SUCCESSFUL, BookingStatus.FAILED);
        return bookingService.getBookingsWithStatuses(statuses);
    }
    // tính toán số lượng theo status
    @GetMapping("/count-by-status")
    public Long countBookingsByStatus() {
        List<BookingStatus> statuses = Arrays.asList( BookingStatus.SUCCESSFUL,BookingStatus.FAILED,BookingStatus.PENDING);
        return bookingService.countBookingsWithStatuses(statuses);
    }
    @GetMapping("/total-amount-by-month-in-year")
    public ResponseEntity<Map<String, Double>> getTotalAmountByMonthInYear(
            @RequestParam("year") int year
    ) {
        Map<String, Double> monthlyTotalAmounts = bookingService.getTotalAmountByMonthInYear(year);

        return ResponseEntity.ok(monthlyTotalAmounts);
    }

    @GetMapping("/count-pending")
    public ResponseEntity<Long> getCountPendingBookings() {
        Long count = bookingService.countPendingBookings();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-successful")
    public ResponseEntity<Long> getCountSuccessfulBookings() {
        Long count = bookingService.countSuccessfulBookings();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-cancelled")
    public ResponseEntity<Long> getCountCancelledBookings() {
        Long count = bookingService.countCancelledBookings();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-by-status-and-month")
    public Map<String, List<Integer>> getCountByStatusAndMonth() {
        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Tạo một danh sách chứa số lượng booking theo từng tháng và theo trạng thái
        Map<String, List<Integer>> countByStatusAndMonth = new LinkedHashMap<>();

        // Lặp qua các trạng thái
        for (BookingStatus status : BookingStatus.values()) {
            List<Integer> countByMonth = new ArrayList<>();

            // Lặp qua các tháng trong năm hiện tại
            for (int i = 1; i <= 12; i++) {
                YearMonth yearMonth = YearMonth.of(currentDate.getYear(), i);
                int totalAmount = bookingService.getCountByStatusAndMonth(status, yearMonth);
                countByMonth.add(totalAmount);
            }

            countByStatusAndMonth.put(status.name(), countByMonth);
        }

        return countByStatusAndMonth;
    }
    @GetMapping("/total-amount-by-month")
    public List<Double> getTotalAmountByMonth() {
        List<Double> totalAmountByMonth = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(currentDate.getYear(), month);
            double totalAmount = bookingService.getTotalAmountForMonth(yearMonth);
            totalAmountByMonth.add(totalAmount);
        }

        return totalAmountByMonth;
    }

}
