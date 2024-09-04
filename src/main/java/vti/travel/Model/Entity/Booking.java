package vti.travel.Model.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;

    @Column(name = "fullName", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "note", nullable = false)
    private String note;

    @Column(name = "guestSize")
    private Integer guestSize;

    private LocalDate bookingAt;

    private double totalAmount;

    private BookingStatus status;

    private String titleTour;

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;

}
