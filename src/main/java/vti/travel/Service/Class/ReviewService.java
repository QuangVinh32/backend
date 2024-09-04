package vti.travel.Service.Class;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vti.travel.Model.Entity.Review;
import vti.travel.Model.Entity.Tour;
import vti.travel.Model.Request.ReviewRequest;
import vti.travel.Repository.ReviewRepository;
import vti.travel.Repository.TourRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TourRepository tourRepository;

    // Phương thức để lưu đánh giá vào cơ sở dữ liệu
    public void addReviewToTour(ReviewRequest reviewRequest) {
        Tour tour = tourRepository.findById(reviewRequest.getTour());
        if (tour != null) {
            Review review = new Review();
            review.setTour(tour);
            review.setUsername(reviewRequest.getUsername());
            review.setReviewText(reviewRequest.getReviewText());
            review.setRating(reviewRequest.getRating());
            reviewRepository.save(review);
        }
    }


}
