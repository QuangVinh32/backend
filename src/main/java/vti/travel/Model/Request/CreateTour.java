package vti.travel.Model.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTour {
    private String title;
    private String city;
    private String address;
    private double distance;
    private String photo;
    private String desc;
    private double price;
    private int maxGroupSize;
    private boolean featured;
}
