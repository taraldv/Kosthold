/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.database.KostholdDatabase;
import util.http.StandardResponse;
import util.insert.ParameterMap;
import util.sql.MultiLineSqlQuery;
import util.sql.ResultSetContainer;

/**
 *
 * @author Tarald
 */
public class Måltid extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StandardResponse sr = new StandardResponse(response);
        PrintWriter out = sr.getWriter();
        ValidSession vs = new ValidSession(out, request.getSession());
        String type = request.getParameter("type");
        /* stopper request hvis ugylid session */
        if (!vs.validateSession()) {
            return;
        }
        int brukerId = vs.getId();
        try {
            if (type.equals("insertMåltider")) {
                out.print(insertMåltider(brukerId, request.getParameter("navn"), ParameterMap.convertMapToArray(request.getParameterMap(), 2)));
            } else if (type.equals("getMåltider")) {
                ResultSetContainer rsc = KostholdDatabase.normalQuery("SELECT * FROM måltider WHERE brukerId = " + brukerId + ";");
                out.print(rsc.getJSON());
            } else if (type.equals("getMåltiderIngredienser")) {
                String getMåltiderIngredienserQuery = "SELECT m.matvare,i.matvareId,mengde FROM ingredienser i"
                        + " LEFT JOIN matvaretabellen m ON i.matvareId = m.matvareId"
                        + " WHERE måltidId = ?;";
                ResultSetContainer rsc = KostholdDatabase.oneIntQuery(getMåltiderIngredienserQuery, Integer.parseInt(request.getParameter("måltidId")));
                out.print(rsc.getJSON());
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int insertMåltider(int brukerId, String navn, String[][] arr) throws Exception {
        int lastInsertedId = KostholdDatabase.oneStringInsert("INSERT INTO måltider(navn,brukerId) VALUES (?," + brukerId + ");", navn);
        String baseline = "INSERT INTO ingredienser (måltidId, matvareId, mengde) VALUES ";
        String row = "(" + lastInsertedId + ",?,?)";
        String multiQuery = MultiLineSqlQuery.getStringFromArrayLength(arr.length, baseline, row);
        return KostholdDatabase.multiInsertQuery(arr, multiQuery);
    }
}
