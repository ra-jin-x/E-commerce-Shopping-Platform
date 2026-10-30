package com.ecommerce.services.returns;

import java.util.List;
import com.ecommerce.model.Review;
import com.ecommerce.repository.ReviewRepository;

/** Review rules and low-rating flagging (Module 5). */
public class ReviewService {
    private static final int MIN_REVIEWS = 10;
    private static final double LOW_AVERAGE = 2.0;

    private ReviewRepository reviewRepo;

    public ReviewService(ReviewRepository reviewRepo) {
        this.reviewRepo = reviewRepo;
    }

    /** Only after the order is Delivered, one review per product per order, rating 1 to 5. */
    public Review addReview(String orderId, String productId, String customerId,
                            boolean delivered, int rating, String comment) {
        if (!delivered) {
            throw new IllegalStateException("You can review a product only after it is Delivered");
        }
        if (reviewRepo.exists(orderId, productId)) {
            throw new IllegalStateException("Only one review per product per order is allowed");
        }
        Review review = new Review(reviewRepo.nextId(), orderId, productId, customerId, rating, comment);
        reviewRepo.save(review);
        checkAndFlag(productId);
        return review;
    }

    public List<Review> getReviews(String productId) {
        return reviewRepo.findByProduct(productId);
    }

    public double getAverage(String productId) {
        List<Review> list = reviewRepo.findByProduct(productId);
        if (list.isEmpty()) return 0;
        int total = 0;
        for (Review r : list) total += r.getRating();
        return (double) total / list.size();
    }

    public List<String> getFlaggedProducts() {
        return reviewRepo.findFlagged();
    }

    /** Average below 2.0 after at least 10 reviews: flag the product for admin review. */
    private void checkAndFlag(String productId) {
        if (getReviews(productId).size() >= MIN_REVIEWS && getAverage(productId) < LOW_AVERAGE) {
            reviewRepo.flag(productId);
        }
    }
}
