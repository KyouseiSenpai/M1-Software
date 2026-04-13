public class checkOut {

    public static void showReceipt(int customerID, String membershipType,
                                   StringBuilder receiptLines, double totalAmount,
                                   int totalPoints, String appointmentDate,
                                   String paymentStatus) {

        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║     THEME PARK RESORT        ║");
        System.out.println("║      OFFICIAL RECEIPT        ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println("Customer ID  : " + customerID);
        System.out.println("Membership   : " + membershipType);
        System.out.println("--------------------------------");
        System.out.print(receiptLines);
        System.out.println("--------------------------------");
        System.out.printf("Total Amount : PHP %.2f%n", totalAmount);
        System.out.printf("Points Earned: %d pts%n", totalPoints);
        System.out.println("Appointment  : " + appointmentDate);
        System.out.println("Status       : " + paymentStatus);
        System.out.println("================================");
        if ("PAID".equals(paymentStatus)) {
            System.out.println("   ✔ Payment Successful!");
        } else {
            System.out.println("   ⏳ Please pay at the counter.");
        }
        System.out.println("   Thank you for visiting!");
        System.out.println("================================");

        transactionHistory.save(customerID, receiptLines.toString(), totalAmount, totalPoints);
    }
}
