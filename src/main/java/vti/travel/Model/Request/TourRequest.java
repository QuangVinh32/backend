package vti.travel.Model.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vti.travel.Model.Entity.Review;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourRequest {
    private String title;
    private String city;
    private String address;
    private double distance;
    private String photo;
    private String desc;
    private double price;
    private int maxGroupSize;
    private boolean featured;
    private List<Review> reviews;
}
