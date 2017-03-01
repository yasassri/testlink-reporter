/**
 * Created by yasassri on 2/22/17.
 */
import wso2.qa.tl.reporter.TCPercentagebyUserStory;

import java.sql.*;
import java.util.Scanner;

public class Main {

    static final String DB_URL = "192.168.8.50/testlink1915";
    static final String USER = "tl_ro";
    static String projectName = null;

    public static void main(String[] args) {
        Connection conn = null;

        try {

            String PASS = getUserInputs();
            conn = DriverManager.getConnection("jdbc:mysql://" + DB_URL + "?" +
                    "user=" + USER + "&password=" + PASS);

            TCPercentagebyUserStory report = new TCPercentagebyUserStory(projectName);
            report.getReport(conn);
            conn.close();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlEx) {
                }
            }
        }
    }

    public static String getUserInputs(){
        String passwd;
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("||| Avaiable Projects |||\n");
        System.out.println("1 - [C5] WSO2  Identity Access Management");
        System.out.print("\nPlease Enter the ID of your Project : ");
        int projectSelection = Integer.parseInt(reader.nextLine());
        if (projectSelection==1){
            projectName = "[C5] WSO2  Identity Access Management";
        } else {
            System.out.println("Please Enter a Valid Project!!!!");
            System.exit(0);
        }
        System.out.print("\nEnter DB Password: ");
        passwd = reader.nextLine();
        return passwd;
    }
}
