public class transactionHistory {

    private static final String[] history = new String[100];  // add final
    private static final int[] ownerIDs = new int[100];       // add final
    private static int count = 0;

    public static void save(int customerID, String receiptLines, double totalAmount, int totalPoints) {
        if (count >= 100) return;
        history[count] = "\n========== TRANSACTION ==========\n" +
                receiptLines +
                "---------------------------------\n" +
                String.format("Total Amount : PHP %.2f%n", totalAmount) +
                String.format("Total Points : %d pts%n", totalPoints) +
                "=================================";
        ownerIDs[count] = customerID;
        count++;
    }

    public static void showHistory(int customerID) {
        System.out.println("\n=== TRANSACTION HISTORY ===");
        boolean found = false;
        int txNum = 1;

        // Check memory first
        for (int i = 0; i < count; i++) {
            if (ownerIDs[i] == customerID) {
                System.out.println("Transaction #" + txNum++);
                System.out.println(history[i]);
                found = true;
            }
        }

        if (!found) {
            Repository repo = Repository.getInstance();
            String[][] records = repo.getTransactions(customerID);
            if (records != null) {
                for (String[] r : records) {
                    System.out.println("Transaction #" + txNum++);
                    System.out.println("  Date      : " + r[0]);
                    System.out.println("  Ticket    : " + r[1] + " (Age: " + r[2] + ")");
                    System.out.println("  Appointment Date : " + r[3]);
                    System.out.println("  Status    : " + r[4]);
                    System.out.println("  =================================");
                    found = true;
                }
            }
        }

        if (!found) System.out.println("No transactions found.");
    }
}
