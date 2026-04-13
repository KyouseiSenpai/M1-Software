import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Scanner;

public class appointmentSystem {

    private static int[] apptOwners = new int[100];
    private static String[] apptDates = new String[100];
    private static String[] apptStatus = new String[100];
    private static int apptCount = 0;

    public static String pickDate() {
        Scanner sc = new Scanner(System.in);

        String[] dates = new String[6];
        String[] dayNames = new String[6];
        int found = 0;
        LocalDate day = LocalDate.now().plusDays(1);

        while (found < 6) {
            if (day.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dates[found] = day.toString();
                dayNames[found] = day.getDayOfWeek().toString();
                found++;
            }
            day = day.plusDays(1);
        }

        System.out.println("\n=== APPOINTMENT DATE ===");
        for (int i = 0; i < 6; i++)
            System.out.printf(" [%d] %s (%s)%n", i + 1, dates[i], dayNames[i]);
        System.out.println(" [7] Custom date");
        System.out.print("Choose: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 7) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            return sc.nextLine();
        }
        return dates[choice - 1];
    }

    public static void saveAppointment(int customerID, String date, String status) {
        if (apptCount >= 100) return;
        apptOwners[apptCount] = customerID;
        apptDates[apptCount] = date;
        apptStatus[apptCount] = status;
        apptCount++;
    }

    public static void showAppointments(int customerID) {
        System.out.println("\n=== MY APPOINTMENTS ===");

        // Check memory first
        boolean found = false;
        int num = 1;
        for (int i = 0; i < apptCount; i++) {
            if (apptOwners[i] == customerID && !"CANCELLED".equals(apptStatus[i])) {
                System.out.println("  [" + num++ + "] " + apptDates[i] + " - " + apptStatus[i]);
                found = true;
            }
        }

        if (!found) {
            Repository repo = Repository.getInstance();
            String[][] dbAppts = repo.getAppointments(customerID);
            if (dbAppts != null) {
                for (int i = 0; i < dbAppts.length; i++) {
                    if (!"CANCELLED".equals(dbAppts[i][1])) {
                        System.out.println("  [" + num++ + "] " + dbAppts[i][0] + " - " + dbAppts[i][1]);
                        found = true;
                    }
                }
            }
        }

        if (!found) System.out.println("No appointments found.");
    }

    public static boolean cancelAppointment(int customerID, int num) {
        int count = 0;
        for (int i = 0; i < apptCount; i++) {
            if (apptOwners[i] == customerID && !"CANCELLED".equals(apptStatus[i])) {
                count++;
                if (count == num) {
                    apptDates[i] = "CANCELLED";
                    apptStatus[i] = "CANCELLED";
                    return true;
                }
            }
        }

        // fallback to DB
        Repository repo = Repository.getInstance();
        String[][] dbAppts = repo.getAppointments(customerID);
        if (dbAppts != null) {
            for (int i = 0; i < dbAppts.length; i++) {
                if (!"CANCELLED".equals(dbAppts[i][1])) {
                    count++;
                    if (count == num) {
                        repo.cancelAppointment(customerID, dbAppts[i][0]);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean moveAppointment(int customerID, int num, String newDate) {
        int count = 0;
        for (int i = 0; i < apptCount; i++) {
            if (apptOwners[i] == customerID && !"CANCELLED".equals(apptStatus[i])) {
                count++;
                if (count == num) {
                    apptDates[i] = newDate;
                    return true;
                }
            }
        }

        Repository repo = Repository.getInstance();
        String[][] dbAppts = repo.getAppointments(customerID);
        if (dbAppts != null) {
            for (int i = 0; i < dbAppts.length; i++) {
                if (!"CANCELLED".equals(dbAppts[i][1])) {
                    count++;
                    if (count == num) {
                        repo.moveAppointment(customerID, dbAppts[i][0], newDate);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
