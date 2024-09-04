package vti.travel.Service.Class;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vti.travel.Exception.AppException;
import vti.travel.Exception.ErrorResponseBase;
import vti.travel.Model.Entity.Tour;
import vti.travel.Model.Request.CreateTour;
import vti.travel.Model.Request.TourRequest;
import vti.travel.Repository.TourRepository;
import vti.travel.Service.ITourService;

import java.util.List;

@Service
public class TourService implements ITourService {

    @Autowired
    private  TourRepository tourRepository;


    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    public List<Tour> getAllTourspage(int page, int pageSize) {
        Page<Tour> tourPage = tourRepository.findAll(PageRequest.of(page, pageSize));
        return tourPage.getContent();
    }

    public Tour getTourById(int id) {
        return tourRepository.findById(id);
    }

    @Override
    public void createTour(CreateTour tourRequest) {
        Tour tour = new Tour();
        tour.setTitle(tourRequest.getTitle());
        tour.setCity(tourRequest.getCity());
        tour.setAddress(tourRequest.getAddress());
        tour.setDistance(tourRequest.getDistance());
        tour.setPhoto(tourRequest.getPhoto());
        tour.setDesc(tourRequest.getDesc());
        tour.setPrice(tourRequest.getPrice());
        tour.setMaxGroupSize(tourRequest.getMaxGroupSize());
        tour.setFeatured(tourRequest.isFeatured());
         tourRepository.save(tour);
    }

    @Override
    public Tour updateTour(int tourId, TourRequest tourRequest) {
        Tour tour = tourRepository.findById(tourId);
        if (tour!=null){
        tour.setTitle(tourRequest.getTitle());
        tour.setCity(tourRequest.getCity());
        tour.setAddress(tourRequest.getAddress());
        tour.setDistance(tourRequest.getDistance());
        tour.setPhoto(tourRequest.getPhoto());
        tour.setDesc(tourRequest.getDesc());
        tour.setPrice(tourRequest.getPrice());
        tour.setMaxGroupSize(tourRequest.getMaxGroupSize());
        tour.setFeatured(tourRequest.isFeatured());
        tour.setReviews(tourRequest.getReviews());
        return tourRepository.save(tour);
    }
        return null;
    }


    public void deleteTour(int id) {
        tourRepository.deleteById(id);
    }

    public List<Tour> getTourBySearch(String city, double distance, int maxGroupSize) {
        return tourRepository.findTourByCityAndDistanceAndMaxGroupSize(city, distance, maxGroupSize);
    }

    public int getTourCount() {
        List<Tour> tours = tourRepository.findAll();
        return tours.size();
    }
}


