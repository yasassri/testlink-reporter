package wso2.qa.tl.reporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yasassri on 2/27/17.
 */
public class TCPercentagebyUserStory {
    private static String projectName = null;
    // Queries
    static String getStoryList = "select a.id from testlink1915.TL_requirements a join (SELECT id FROM testlink1915.TL_req_specs where testproject_id = (select id from TL_nodes_hierarchy where name = ?)) b on b.id = a.srs_id";
    static String getTestCasesbyStory = "select count(id) from TL_tcversions join (select req_id,testcase_id,a.id as realtc_id"
            + "        from testlink1915.TL_req_coverage b join (select id,parent_id from TL_nodes_hierarchy) a on b.testcase_id = a.parent_id"
            + "        where req_id = ? ) d on d.realtc_id = TL_tcversions.id where execution_type = 1";
    static String getStoryName = "select name from TL_nodes_hierarchy where id = ?";
    static String getTotalTCsforaStory = "select count(*)" + "from TL_req_coverage"
            + "        where req_id = ?";

    public TCPercentagebyUserStory(String projName){
        projectName = projName;
    }

    public void getReport(Connection conn){

        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;
        PreparedStatement stmt3 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;

        try {

            // Creating the CSV report
            String csvFile = "TC_Percentage_By_story.csv";

            stmt = conn.prepareStatement(getStoryList);
            stmt.setString(1,projectName);
            rs = stmt.executeQuery();

            int totalTCs =0, manualTCs = 0;
            String storyName = "Story Name Not Found!!!!";

            // Report CSV Headings
            List<String> reportTable = new ArrayList<String>();
            reportTable.add("Story Name,No of Manual TCs,No of Automated TCs,Total TCs,Automated TC %");

            int counter = 0;
            while (rs.next()) {
                counter++;
                stmt3 = conn.prepareStatement(getStoryName);
                stmt3.setString(1,Integer.toString(rs.getInt("id")));
                rs3 = stmt3.executeQuery();
                if (rs3.next()){
                    storyName = rs3.getString(1);
                }
                stmt2 = conn.prepareStatement(getTestCasesbyStory);
                stmt2.setString(1,Integer.toString(rs.getInt("id")));
                rs2 = stmt2.executeQuery();
                if(rs2.next()){
                    manualTCs = rs2.getInt(1);
                }
                PreparedStatement stmt4 = conn.prepareStatement(getTotalTCsforaStory);
                stmt4.setString(1,Integer.toString(rs.getInt("id")));
                ResultSet rs4 = stmt4.executeQuery();
                if (rs4.next()) {
                    totalTCs = rs4.getInt(1);
                }

                double automatedPercentage = 0;
                if (totalTCs>0){
                    automatedPercentage = 100*(totalTCs-manualTCs)/totalTCs;
                }

                reportTable.add(storyName+","+Integer.toString(manualTCs)+","+Integer.toString(totalTCs - manualTCs)+","+Integer.toString(totalTCs)+","+Double.toString(automatedPercentage).concat("%"));

                System.out.println("Requirement : " +storyName + " || No Of Manual TCs : " +manualTCs+ " || Total TCs : " +totalTCs);
                String fileName = "csvreport.csv";
                CSVUtils.generateFile(fileName,reportTable);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (Exception e){
            System.out.println("Exception Occured : " +e.getMessage());
        }

        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                }
            }
            if (rs2 != null) {
                try {
                    rs2.close();
                } catch (SQLException sqlEx) {
                }
            }
            if (rs3 != null) {
                try {
                    rs3.close();
                } catch (SQLException sqlEx) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {

                }
            }
            if (stmt2 != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {

                }
            }
            if (stmt3 != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {

                }
            }
        }

    }

}
