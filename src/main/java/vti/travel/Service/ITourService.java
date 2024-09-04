package vti.travel.Service;

import vti.travel.Model.Entity.Tour;
import vti.travel.Model.Request.CreateTour;
import vti.travel.Model.Request.TourRequest;

import java.util.List;

public interface ITourService {
    List<Tour> getAllTours();
    List<Tour> getAllTourspage(int page, int pageSize);
    Tour getTourById(int id);
    void createTour(CreateTour tourRequest);
    Tour updateTour(int tourId,TourRequest tourRequest);
    void deleteTour(int id);
    List<Tour> getTourBySearch(String city, double distance, int maxGroupSize);
    int getTourCount();
}
