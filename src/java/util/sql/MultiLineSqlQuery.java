/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.sql;

/**
 *
 * @author Tarald
 */
public class MultiLineSqlQuery {

    public static String getStringFromArrayLength(int arrLength, String baseline, String valueRow) {
        for (int i = 0; i < arrLength; i++) {
            if (i != 0) {
                baseline += ",";
            }
            baseline += valueRow;
        }
        baseline += ";";
        return baseline;
    }

    /*private int insertSQL(String[][] arr, int lastId) throws Exception {
        Connection c = KostholdDatabase.getDatabaseConnection();
        String query = "INSERT INTO ingredienser (måltidId, matvareId, mengde) VALUES ";
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                query += ",";
            }
            query += "(" + lastId + ",?,?)";
        }
        query += ";";
        PreparedStatement ps = c.prepareStatement(query);
        for (int j = 0; j < arr.length; j++) {
            ps.setInt(1 + (2 * j), Integer.parseInt(arr[j][0]));
            ps.setDouble(2 + (2 * j), Double.parseDouble(arr[j][1]));

        }

        int result = ps.executeUpdate();
        c.close();
        return result;
    }

    private int insertIntoLogg(String[][] arr, int brukerId) throws Exception {
        Connection c = KostholdDatabase.getDatabaseConnection();
        String query = "INSERT INTO logg(dato,matvareId,mengde,brukerId) VALUES ";
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                query += ",";
            }
            query += "(CURDATE(),?,?," + brukerId + ")";
        }
        query += ";";
        PreparedStatement ps = c.prepareStatement(query);
        for (int j = 0; j < arr.length; j++) {
            ps.setInt(1 + (2 * j), Integer.parseInt(arr[j][0]));
            ps.setDouble(2 + (2 * j), Double.parseDouble(arr[j][1]));
        }
        int result = ps.executeUpdate();
        c.close();
        return result;
    }*/
}
