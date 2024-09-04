package vti.travel.Model.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vti.travel.Model.Entity.BookingStatus;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String note;
    private Integer guestSize;

    private double totalAmount;

    private BookingStatus status;

    private int tour;
}
