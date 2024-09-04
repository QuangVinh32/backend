package vti.travel.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vti.travel.Model.Entity.Tour;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Integer> {
    List<Tour> findTourByCityAndDistanceAndMaxGroupSize(String city, double distance, int maxGroupSize);
    Tour findById(int id);
}
