package com.ecommerce;

import java.util.Scanner;
import com.ecommerce.cli.ReturnReviewCLI;
import com.ecommerce.repository.RefundRepository;
import com.ecommerce.repository.ReturnRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.services.returns.RefundService;
import com.ecommerce.services.returns.ReturnService;
import com.ecommerce.services.returns.ReviewService;


public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        RefundService refundService = new RefundService(new RefundRepository());
        ReturnService returnService = new ReturnService(new ReturnRepository(), refundService);
        ReviewService reviewService = new ReviewService(new ReviewRepository());
        ReturnReviewCLI cli = new ReturnReviewCLI(in, returnService, refundService, reviewService);

        while (true) {
            System.out.println("\n===== E-commerce Shopping Platform =====");
            System.out.println("1. Customer\n2. Seller\n3. Admin\n0. Exit");
            System.out.print("Login as: ");
            String choice = in.hasNextLine() ? in.nextLine().trim() : "0";
            if (choice.equals("1")) {
                System.out.print("Customer id (example C1): ");
                cli.customerMenu(in.nextLine().trim());
            } else if (choice.equals("2")) {
                cli.sellerMenu();
            } else if (choice.equals("3")) {
                cli.adminMenu();
            } else if (choice.equals("0")) {
                break;
            } else {
                System.out.println("Invalid choice");
            }
        }
        System.out.println("Goodbye!");
    }
}
