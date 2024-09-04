package vti.travel.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.travel.Model.Entity.Tour;
import vti.travel.Model.Entity.Users;
import vti.travel.Model.Request.CreateTour;
import vti.travel.Model.Request.TourRequest;
import vti.travel.Service.Class.TourService;

import java.util.List;
import java.util.Optional;


@CrossOrigin("*")
@RestController
@RequestMapping("/tours")
public class TourController {
    @Autowired
    private TourService tourService;


    @GetMapping("/get-all")
    public List<Tour> getAllTours() {
        return tourService.getAllTours();
    }

    @GetMapping("/page")
    public ResponseEntity<List<Tour>> getAllTours(@RequestParam int page,
                                                  @RequestParam int pageSize) {
        List<Tour> tours = tourService.getAllTourspage(page, pageSize);
        return ResponseEntity.ok(tours);
    }

    @GetMapping("/getbyid")
    public Tour getTourById(@RequestParam int id) {
        return tourService.getTourById(id);
    }

    @PostMapping("/create")
    public void createTour(@RequestBody CreateTour tourRequest) {
         tourService.createTour(tourRequest);
    }

    @PutMapping("/update/{tourId}")
    public ResponseEntity<?> updateTour(@PathVariable int tourId, @RequestBody TourRequest tourRequest) {
        Optional<Tour> optional = Optional.ofNullable(tourService.getTourById(tourId));
      if (optional.isPresent()){
          Tour tour = optional.get();
          tourService.updateTour(tourId,tourRequest);
          return ResponseEntity.status(HttpStatus.OK).build();
      }else {
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tour không có thông tin");
      }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Tour>> getFilteredTours(@RequestParam String city, @RequestParam double distance, @RequestParam int maxGroupSize) {
        List<Tour> filteredTours = tourService.getTourBySearch(city, distance, maxGroupSize);
        return new ResponseEntity<>(filteredTours, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTour(@PathVariable int id) {
        tourService.deleteTour(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getTourCount")
    public int getTourdCount() {
        return tourService.getTourCount();
    }
}
