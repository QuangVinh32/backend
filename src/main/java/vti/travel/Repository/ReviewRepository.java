package vti.travel.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vti.travel.Model.Entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
}
