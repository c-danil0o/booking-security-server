package com.komsije.booking.service.interfaces;

import com.komsije.booking.dto.ReviewDto;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.model.Review;
import com.komsije.booking.service.interfaces.crud.CrudService;

import java.util.List;

public interface ReviewService extends CrudService<ReviewDto, Long> {
    ReviewDto saveNewReview(ReviewDto reviewDto);

    void deleteHostReview(Long hostId, Long authorId);

    void deleteAccommodationReview(Long accommodationId, Long authorId);

    public List<ReviewDto> getApprovedReviews();

    List<ReviewDto> getUnapprovedReviews();

    void reportReview(Long id);

    public void setApproved(Long id) throws ElementNotFoundException;
    public List<ReviewDto> findByAccommodationId(Long id) throws ElementNotFoundException;
    public List<ReviewDto> findByHostId(Long id) throws ElementNotFoundException;

    ReviewDto findHostReview(Long hostId, Long authorId);

    ReviewDto findAccommodationReview(Long accommodationId, Long authorId);
}
