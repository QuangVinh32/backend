package vti.travel.Service.Class;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vti.travel.Model.Entity.Booking;
import vti.travel.Model.Entity.BookingStatus;
import vti.travel.Model.Entity.Tour;
import vti.travel.Model.Request.BaseRequest;
import vti.travel.Model.Request.BookingRequest;
import vti.travel.Model.Request.SearchBookingRq;
import vti.travel.Repository.BookingRepository;
import vti.travel.Repository.Specification.BookingTourSearch;
import vti.travel.Repository.TourRepository;
import vti.travel.Service.IBookingService;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;


@Service
public class BookingService implements IBookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    TourRepository tourRepository;

    @Autowired
    TourService tourService;

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getById(int bookingId) {
        Optional<Booking> optional = bookingRepository.findById(bookingId);

        return optional.orElse(null);
    }

    @Override
    public void createBooking(BookingRequest bookingRequest) {
        Tour tour = tourService.getTourById(bookingRequest.getTour());
        if (tour == null) {
            throw new RuntimeException("Không tìm thấy tour với ID: " + bookingRequest.getTour());
        }
        if (bookingRequest.getGuestSize() <= tour.getMaxGroupSize()) {
            Booking booking = new Booking();
            BeanUtils.copyProperties(bookingRequest, booking);
            booking.setAddress(bookingRequest.getAddress());
            booking.setTour(tour);
            booking.setTitleTour(tour.getTitle());
            booking.setEmail(bookingRequest.getEmail());
            booking.setFullName(bookingRequest.getFullName());
            booking.setNote(bookingRequest.getNote());
            booking.setPhoneNumber(bookingRequest.getPhoneNumber());
            booking.setGuestSize(bookingRequest.getGuestSize());
            booking.setBookingAt(LocalDate.now());
            booking.setStatus(BookingStatus.PENDING);
            bookingRepository.save(booking);
            // Cập nhật lại maxGroupSize của tour
            int updatedMaxGroupSize = tour.getMaxGroupSize() - bookingRequest.getGuestSize();
            tour.setMaxGroupSize(updatedMaxGroupSize);
            tourRepository.save(tour);
        } else {
            throw new RuntimeException("Không thể đặt tour vì số lượng booking đã vượt quá số lượng slot trống.");
        }
    }


    @Override
    public Booking update(int bookingId, BookingRequest bookingRequest) {


        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking existingBooking = optionalBooking.get();
            Tour tour = tourService.getTourById(bookingRequest.getTour());

            if (tour != null) {
                BeanUtils.copyProperties(bookingRequest, existingBooking);
                existingBooking.setTitleTour(tour.getTitle());
                existingBooking.setAddress(bookingRequest.getAddress());
                existingBooking.setEmail(bookingRequest.getEmail());
                existingBooking.setFullName(bookingRequest.getFullName());
                existingBooking.setNote(bookingRequest.getNote());
                existingBooking.setGuestSize(bookingRequest.getGuestSize());
                existingBooking.setPhoneNumber(bookingRequest.getPhoneNumber());
                existingBooking.setTotalAmount(bookingRequest.getTotalAmount());
                existingBooking.setStatus(bookingRequest.getStatus());
                existingBooking.setTour(tour);
                bookingRepository.save(existingBooking);
                if (BookingStatus.FAILED.equals(bookingRequest.getStatus())){
                    tour.setMaxGroupSize(tour.getMaxGroupSize()+ existingBooking.getGuestSize());
                    tourRepository.save(tour);
                }
                return existingBooking;
            } else {
                throw new RuntimeException("Không tìm thấy tour với ID: " + bookingRequest.getTour());
            }
        } else {
            throw new RuntimeException("Không tìm thấy booking với ID: " + bookingId);
        }
    }


    @Override
    public void deleteBooking(int bookingId) {
        Optional<Booking> optional = bookingRepository.findById(bookingId);
        if (optional.isPresent()) {
            bookingRepository.deleteById(bookingId);
        } else {
            throw new RuntimeException("Booking bạn đang muốn xóa không tồn tại");
        }
    }

    @Override
    public List<Booking> findByFullName(String fullName) {
        List<Booking> bookings = bookingRepository.findBookingByFullName(fullName);
        if (bookings != null) {
            return bookings;
        }
        return null;
    }

    @Override
    public Page<Booking> search(SearchBookingRq searchBookingRq) {
        Specification<Booking> specification = BookingTourSearch.buildCondition(searchBookingRq);
        PageRequest pageRequest = BaseRequest.buildPageRequest(searchBookingRq);
        return bookingRepository.findAll(specification, pageRequest);
    }

    // tính toán booking trong tháng
    @Override
    public int countBookingsInMonth(Month month) {
        LocalDate startOfMonth = LocalDate.now().withMonth(month.getValue()).withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        List<Booking> bookingsInMonth = bookingRepository.findByBookingAtBetween(startOfMonth, endOfMonth);
        return bookingsInMonth.size();
    }

    @Override
    public String sumTotalAmountInMonth(Month month) {
        LocalDate startOfMonth = LocalDate.now().withMonth(month.getValue()).withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        Double totalAmount = bookingRepository.calculateTotalAmountInMonth1(startOfMonth, endOfMonth, BookingStatus.SUCCESSFUL);

        // Kiểm tra nếu totalAmount là null, thì gán nó bằng 0
        if (totalAmount == null) {
            totalAmount = 0.0;
        }

        // Định dạng số tiền thành đơn vị tiền tệ VND
        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedAmount = vndFormat.format(totalAmount);

        return formattedAmount;
    }


    @Override
    public List<Booking> getBookingsWithStatuses(List<BookingStatus> statuses) {
        return bookingRepository.findByStatusIn(statuses);
    }

    @Override
    public Long countBookingsWithStatuses(List<BookingStatus> statuses) {
        return bookingRepository.countByStatusIn(statuses);
    }

    @Override
    public Map<String, Double> getTotalAmountByMonthInYear(int year) {
        Map<String, Double> monthlyTotalAmounts = new HashMap<>();

        for (int month = 1; month <= 12; month++) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);

            double totalAmount = bookingRepository.calculateTotalAmountInMonth(startDate, endDate, BookingStatus.SUCCESSFUL);

            monthlyTotalAmounts.put(startDate.getMonth().toString(), totalAmount);
        }

        return monthlyTotalAmounts;
    }

    @Override
    public double getTotalAmountForMonth(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Booking> bookings = bookingRepository.findByBookingAtBetween(startDate, endDate);

        double totalAmount = 0;

        for (Booking booking : bookings) {
            totalAmount += booking.getTotalAmount();
        }

        return totalAmount;
    }

    @Override
    public int getCountByStatusAndMonth(BookingStatus status, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Booking> bookings = bookingRepository.findByStatusAndBookingAtBetween(status, startDate, endDate);

        return bookings.size();
    }

    public Long countPendingBookings() {
        return bookingRepository.countPendingBookings();
    }

    public Long countSuccessfulBookings() {
        return bookingRepository.countSuccessfulBookings();
    }

    public Long countCancelledBookings() {
        return bookingRepository.countCancelledBookings();
    }
}
