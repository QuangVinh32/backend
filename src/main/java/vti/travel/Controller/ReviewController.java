package vti.travel.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.travel.Model.Request.ReviewRequest;
import vti.travel.Service.Class.ReviewService;
import vti.travel.Service.Class.TourService;

@CrossOrigin("*")
@RestController
@RequestMapping("/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private TourService tourService;

    @PostMapping("/create")
    public ResponseEntity<?> addReviewToTour(

            @RequestBody ReviewRequest reviewRequest
    ) {
        reviewService.addReviewToTour(reviewRequest);
        return ResponseEntity.ok("Đánh giá đã được thêm thành công");
    }
}
