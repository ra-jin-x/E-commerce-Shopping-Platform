# E-commerce Shopping Platform - Module 5 (Returns, Refunds, Reviews)

My files (package `com.ecommerce`):

| Folder | Files |
|---|---|
| `model/` | `ReturnRequest.java`, `Refund.java`, `Review.java` |
| `repository/` | `ReturnRepository.java`, `RefundRepository.java`, `ReviewRepository.java` |
| `services/returns/` | `ReturnService.java`, `RefundService.java`, `ReviewService.java` |
| `cli/` | `ReturnReviewCLI.java` (called from CustomerCLI, SellerCLI, AdminCLI) |

`Main.java` is only a small launcher so Module 5 can be run alone.

## How to run
```
javac -d out $(find src -name "*.java")
java -cp out com.ecommerce.Main
```

## How the team connects it
- `CustomerCLI`: `cli.customerMenu(customerId)`
- `SellerCLI`: `cli.sellerMenu()`
- `AdminCLI`: `cli.adminMenu()`
- On cancellation (Module 3): `refundService.refundForCancelledOrder(orderId, customerId, status, total, paymentMethod)`
- After `returnService.approveReturn`: add `getQuantity()` back to product stock (Module 1) and mark the payment Refunded (Module 4).
