package vti.travel.Model.Entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String city;
    private String address;
    private double distance;
    private String photo;
    @Column(name = "`desc`")
    private String desc;
    private double price;
    private int maxGroupSize;
    private boolean featured;
    @OneToMany(mappedBy = "tour") // Trường "tour" trong Review liên kết với đối tượng Tour
    private List<Review> reviews;
}
