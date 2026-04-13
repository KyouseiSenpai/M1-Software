import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Repository {

    private final String dbURL;
    private static Repository instance; // ← singleton

    private Repository(String dbURL) {
        this.dbURL = dbURL;
    }

    public static Repository getInstance() { // ← singleton getter
        if (instance == null) {
            instance = new RepositoryBuilder().setDatabasePath().build();
        }
        return instance;
    }

    public String getDbURL() {
        return dbURL;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbURL);
    }

    public int generateCustomerID() {
        int customerID = 10000;
        try (Connection conn = DriverManager.getConnection(dbURL)) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT customerID FROM tbl_customerDetails ORDER BY customerID ASC;");
            while (rs.next()) {
                if (rs.getInt("customerID") == customerID) customerID++;
                else break;
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return customerID;
    }

    public void saveCustomer(int customerID, String fullName, String contactNumber, int age) {
        String registrationDate = LocalDate.now().toString();
        String sql = "INSERT INTO tbl_customerDetails (accountDateCreated, customerID, customerFullName, customerContactNumber, customerAge) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, registrationDate);
            pstmt.setInt(2, customerID);
            pstmt.setString(3, fullName);
            pstmt.setString(4, contactNumber);
            pstmt.setInt(5, age);
            pstmt.executeUpdate();

            String recordSql = "INSERT INTO tbl_customerRecords (customerID, membershipType, freebiesCount) VALUES (?, ?, ?)";
            try (PreparedStatement recordPstmt = conn.prepareStatement(recordSql)) {
                recordPstmt.setInt(1, customerID);
                recordPstmt.setString(2, "Regular");
                recordPstmt.setInt(3, 0);
                recordPstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public int findCustomerByID(int loginID) {
        String sql = "SELECT customerID, customerFullName FROM tbl_customerDetails WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loginID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String formattedLDT = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                String updateSql = "UPDATE tbl_customerDetails SET dateTimeIn = ? WHERE customerID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, formattedLDT);
                    updateStmt.setInt(2, loginID);
                    updateStmt.executeUpdate();
                }
                System.out.println("Login Successful! Welcome Back, " + rs.getString("customerFullName"));
                return rs.getInt("customerID");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return -1;
    }

    public int findCustomerByDetails(String forgotFullName, String forgotContactNumber) {
        String sql = "SELECT customerID, customerFullName, customerContactNumber " +
                "FROM tbl_customerDetails WHERE customerFullName = ? AND customerContactNumber = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, forgotFullName);
            pstmt.setString(2, forgotContactNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("customerID");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return -1;
    }

    public void trackLogOut(int loginID) {
        String formattedLDT = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        String updateSql = "UPDATE tbl_customerDetails SET dateTimeOut = ? WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setString(1, formattedLDT);
            pstmt.setInt(2, loginID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void updateMembershipType(int customerID, String membershipType) {
        String sql = "UPDATE tbl_customerRecords SET membershipType = ?, freebiesCount = ? WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, membershipType);
            pstmt.setInt(2, 1);
            pstmt.setInt(3, customerID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public String getMembershipType(int customerID) {
        String sql = "SELECT membershipType FROM tbl_customerRecords WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("membershipType");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

    public int getFreebiesCount(int customerID) {
        String sql = "SELECT freebiesCount FROM tbl_customerRecords WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("freebiesCount");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return 0;
    }

    public void useFreebies(int customerID) {
        String sql = "UPDATE tbl_customerRecords SET freebiesCount = freebiesCount - 1 WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void loyaltyPoints(int customerID, int totalPoints) {
        String sql = "UPDATE tbl_customerRecords SET customerPoints = COALESCE(customerPoints, 0) + ? WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, totalPoints);
            pstmt.setInt(2, customerID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public String getCustomerName(int customerID) {
        String sql = "SELECT customerFullName FROM tbl_customerDetails WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("customerFullName");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return "Unknown";
    }

    public int getCustomerAge(int customerID) {
        String sql = "SELECT customerAge FROM tbl_customerDetails WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("customerAge");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return 0;
    }

    public void saveTicketRecord(int customerID, String ticketName, int ticketAge) {
        String sql = "INSERT INTO tbl_ticketRecords (customerID, dateBought, ticketAge, ticketName) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            pstmt.setString(2, LocalDate.now().toString());
            pstmt.setInt(3, ticketAge);
            pstmt.setString(4, ticketName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void updateTicketSummary(int customerID, double price) {
        String sql = "UPDATE tbl_customerRecords SET " +
                "ticketBought = COALESCE(ticketBought, 0) + 1, " +
                "totalCost = COALESCE(totalCost, 0) + ? " +
                "WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, price);
            pstmt.setInt(2, customerID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public int getPoints(int customerID) {
        String sql = "SELECT customerPoints FROM tbl_customerRecords WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("customerPoints");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return 0;
    }

    public void deductPoints(int customerID, int points) {
        String sql = "UPDATE tbl_customerRecords SET customerPoints = customerPoints - ? WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, points);
            pstmt.setInt(2, customerID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public String[][] getTransactions(int customerID) {
        String sql = "SELECT dateBought, ticketName, ticketAge, appointmentDate, paymentStatus " +
                "FROM tbl_ticketRecords WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();

            String[][] temp = new String[100][5];
            int count = 0;
            while (rs.next() && count < 100) {
                temp[count][0] = rs.getString("dateBought");
                temp[count][1] = rs.getString("ticketName");
                temp[count][2] = String.valueOf(rs.getInt("ticketAge"));
                temp[count][3] = rs.getString("appointmentDate");
                temp[count][4] = rs.getString("paymentStatus");
                count++;
            }
            if (count == 0) return null;

            String[][] result = new String[count][5];
            for (int i = 0; i < count; i++) result[i] = temp[i];
            return result;

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

    public int getQueuePosition(String membership) {
        if ("VIP".equalsIgnoreCase(membership)) return 0;
        String sql = "SELECT COUNT(*) as count FROM tbl_queue WHERE membershipType = 'REGULAR' AND status = 'WAITING'";
        try (Connection conn = DriverManager.getConnection(dbURL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("count") + 1;
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return 1;
    }

    public String[][] getAppointments(int customerID) {
        String sql = "SELECT appointmentDate, paymentStatus FROM tbl_ticketRecords WHERE customerID = ? AND appointmentDate IS NOT NULL";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();

            String[][] temp = new String[100][2];
            int count = 0;
            while (rs.next() && count < 100) {
                temp[count][0] = rs.getString("appointmentDate");
                temp[count][1] = rs.getString("paymentStatus");
                count++;
            }
            if (count == 0) return null;

            String[][] result = new String[count][2];
            for (int i = 0; i < count; i++) result[i] = temp[i];
            return result;

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

    public void saveQueueEntry(int customerID, String className, String type, int position, String status) {
        String sql = "INSERT OR REPLACE INTO tbl_queue " +
                "(customerID, className, membershipType, queuePosition, status, timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            pstmt.setString(2, className);
            pstmt.setString(3, type);
            pstmt.setInt(4, position);
            pstmt.setString(5, status);
            pstmt.setString(6, timestamp);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void updateQueueStatus(int customerID, String status) {
        String sql = "UPDATE tbl_queue SET status = ? WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, customerID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public String[] getQueueStatus(int customerID) {
        String sql = "SELECT queuePosition, status, className FROM tbl_queue WHERE customerID = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new String[]{
                        rs.getString("className"),
                        rs.getString("status"),
                        String.valueOf(rs.getInt("queuePosition"))
                };
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

    public void saveAppointmentRecord(int customerID, String appointmentDate, String status) {
        String sql = "UPDATE tbl_ticketRecords SET appointmentDate = ?, paymentStatus = ? " +
                "WHERE customerID = ? AND appointmentDate IS NULL";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, appointmentDate);
            pstmt.setString(2, status);
            pstmt.setInt(3, customerID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void cancelAppointment(int customerID, String appointmentDate) {
        String sql = "UPDATE tbl_ticketRecords SET paymentStatus = 'CANCELLED' WHERE customerID = ? AND appointmentDate = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            pstmt.setString(2, appointmentDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void moveAppointment(int customerID, String oldDate, String newDate) {
        String sql = "UPDATE tbl_ticketRecords SET appointmentDate = ? WHERE customerID = ? AND appointmentDate = ?";
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newDate);
            pstmt.setInt(2, customerID);
            pstmt.setString(3, oldDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static class RepositoryBuilder {
        private String path;

        public RepositoryBuilder setDatabasePath() {
            this.path = "jdbc:sqlite:C:/Users/jeffr/Downloads/M1/src/CustomerDetails.db";
            return this;
        }

        public Repository build() {
            if (path == null) throw new IllegalStateException("Database path not set!");
            return new Repository(path);
        }
    }
}
