/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import ignore.GitHubIgnore;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.KostholdDatabase;
import util.Login;
import util.ResultSetConverter;
import util.TooManyColumns;

/**
 *
 * @author Tarald
 */
public class Post extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", GitHubIgnore.URL);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        PrintWriter out = response.getWriter();
        String type = request.getParameter("type");

        if (type.equals("passordGen")) {
            out.print(Login.generatePasswordHash(request.getParameter("passord")));
        }

        try {

            Login login = new Login(request.getParameter("brukernavn"), request.getParameter("passord"), request.getSession());

            /*hvis gyldig login, skip til brukerId */
            if (!login.validSession()) {
                /* ikke gyldig session, men prøver på login */
                if (login.validLogin()) {
                    out.print(1);
                    return;
                } else {
                    login.invalidate();
                    return;
                }
                /* gyldig session, og spør etter bekreftelse */
            } else if (type.equals("auth")) {
                out.print(1);
                return;
            }
            int brukerId = login.getBrukerId();
            /*  */
            if (type.equals("insertMatvaretabell")) {
                String matvareNavn = request.getParameter("navn");
                String[][] matvareIngrediensOgVerdier = mapToArrayInArray(request.getParameterMap(), 2);
                out.print(insertIntoMatvaretabellen(matvareNavn, matvareIngrediensOgVerdier));
            } else if (type.equals("insertMåltider")) {
                String[][] ingrediensOgVerdiArray = mapToArrayInArray(request.getParameterMap(), 2);
                String navn = request.getParameter("navn");
                int lastInsertedId = insertMåltidAndGetLastID(navn, brukerId);
                out.print(insertSQL(ingrediensOgVerdiArray, lastInsertedId));
            } else if (type.equals("getLogg")) {
                /*TODO henter 31 distinct(dato) rader*/
                out.print(brukerDefinertLogg(brukerId));
            } else if (type.equals("insertLogg")) {
                String[][] matvareOgMengdeArray = mapToArrayInArray(request.getParameterMap(), 1);
                out.print(insertIntoLogg(matvareOgMengdeArray, brukerId));
            } else if (type.equals("getMatvaretabell")) {
                out.print(ResultSetConverter.toJSON(KostholdDatabase.databaseQuery("SELECT matvareId,matvare FROM matvaretabellen;")));
            } else if (type.equals("getMåltider")) {
                out.print(ResultSetConverter.toJSON(KostholdDatabase.databaseQuery("SELECT * FROM måltider WHERE brukerId = " + brukerId + ";")));
            } else if (type.equals("getMåltiderIngredienser")) {
                String getMåltiderIngredienserQuery = "SELECT m.matvare,i.matvareId,mengde FROM ingredienser i"
                        + " LEFT JOIN matvaretabellen m ON i.matvareId = m.matvareId"
                        + " WHERE måltidId = ?;";
                out.print(ResultSetConverter.toJSON(KostholdDatabase.oneIntQuery(getMåltiderIngredienserQuery, Integer.parseInt(request.getParameter("måltidId")))));
            } else if (type.equals("autocomplete")) {
                String matchingParameter = request.getParameter("string");
                String whichTable = request.getParameter("table");
                String autocompleteQuery = "";
                if (whichTable.equals("matvaretabellen")) {
                    autocompleteQuery = "SELECT matvare,matvareId FROM matvaretabellen WHERE matvare LIKE ? LIMIT 15;";
                } else if (whichTable.equals("næringsinnhold")) {
                    autocompleteQuery = "SELECT næringsinnhold,benevning FROM benevninger WHERE næringsinnhold LIKE ? LIMIT 15;";
                }
                String completeJson = ResultSetConverter.toJSON(KostholdDatabase.oneStringQuery(autocompleteQuery, "%" + matchingParameter + "%"));
                if (completeJson.length() > 2) {
                    String jsonAddition = "\"search\":\"" + matchingParameter + "\",";
                    completeJson = new StringBuffer(completeJson).insert(1, jsonAddition).toString();
                }
                out.print(completeJson);
            } else {
                out.print(type);
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private String brukerDefinertLogg(int brukerId) throws Exception {
        String brukerDefinertQuery = "SELECT b.næringsinnhold FROM benevinger b "
                + "LEFT JOIN brukerBenevningMål bm ON b.benevningId = bm.benevningId WHERE bm.brukerId = " + brukerId + ";";
        String additionalStuff = ResultSetConverter.oneColumnToString(KostholdDatabase.databaseQuery(brukerDefinertQuery));
        
        //String additionalStuff = ",m.Kilokalorier,m.Fett,m.Karbohydrat,m.`Sukker, tilsatt`,m.kostfiber,m.Protein,m.Salt,m.Kalsium";
        String getLoggQuery = "SELECT m.matvare,mengde,dato" + additionalStuff + " FROM logg "
                + "LEFT JOIN matvaretabellen m ON logg.matvareId = m.matvareId "
                + "WHERE logg.brukerId = " + brukerId + ";";
        String output = ResultSetConverter.toJSON(KostholdDatabase.databaseQuery(getLoggQuery));
        return output;

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
    }

    private int insertIntoMatvaretabellen(String navn, String[][] arr) throws Exception {
        TooManyColumns tmc = new TooManyColumns(arr);
        String query = tmc.getQuery();
        ArrayList<Double> list = tmc.getList();
        Connection c = KostholdDatabase.getDatabaseConnection();
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, navn);
        for (int x = 0; x < list.size(); x++) {
            ps.setDouble(x + 2, list.get(x));
        }

        int result = ps.executeUpdate();
        c.close();
        return result;
    }

    private int insertMåltidAndGetLastID(String navn, int brukerId) throws Exception {
        Connection c = KostholdDatabase.getDatabaseConnection();
        String query = "INSERT INTO måltider(navn,brukerId) VALUES (?," + brukerId + ");";
        PreparedStatement ps = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, navn);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        String lastId = rs.getString("GENERATED_KEY");
        c.close();
        return Integer.parseInt(lastId);
    }

    private int insertSQL(String[][] arr, int lastId) throws Exception {
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

    private String[][] mapToArrayInArray(Map<String, String[]> map, int offset) {

        String[][] out = new String[(map.size() - offset) / 2][2];
        String[][] temp = map.values().toArray(new String[0][0]);

        for (int i = offset; i < temp.length; i += 2) {
            out[(i - offset) / 2][0] = temp[i][0];
            out[(i - offset) / 2][1] = temp[i + 1][0];
        }

        return out;
    }

}
