/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.kosthold;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.HTML;
import util.sql.Database;
import util.http.StandardResponse;

/**
 *
 * @author Tarald
 */
public class Logg extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        ValidSession.isValid(req, resp);
        HTML html = new HTML("Kosthold Logg");
        html.addStandard();
        html.addJS("../../js/kostholdLogg.js");
        resp.getWriter().print(html.toString());

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StandardResponse sr = new StandardResponse(response);
        PrintWriter out = sr.getWriter();
        String type = request.getParameter("type");
        try {
            ValidSession vs = new ValidSession(request, response);
            int brukerId = vs.getBrukerId();
            if (type.equals("getLoggTabell")) {
                out.print(getLoggTabell(brukerId, 2));
            } else if (type.equals("insertLogg")) {
                out.print(insertLogg(request.getParameterMap(), brukerId));
            } else if (type.equals("deleteLogg")) {
                out.print(deleteLogg(brukerId, Integer.parseInt(request.getParameter("loggId"))));
            } else if (type.equals("updateLogg")) {
                int loggId = Integer.parseInt(request.getParameter("rowId"));
                Double mengde = Double.parseDouble(request.getParameter("mengde"));
                String dato = request.getParameter("dato");
                out.print(updateLogg(brukerId, loggId, mengde, dato));
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int updateLogg(int brukerId, int loggId, Double mengde, String dato) throws Exception {
        String updateQuery = "UPDATE logg SET mengde = ?, dato = ? WHERE loggId = ? AND brukerId = " + brukerId + ";";
        Object[] vars = {mengde, dato, loggId};
        return Database.singleUpdateQuery(updateQuery, vars, false);
    }

    private int deleteLogg(int brukerId, int loggId) throws Exception {
        String deleteQuery = "DELETE FROM logg WHERE loggId = ? AND brukerId = " + brukerId + ";";
        return Database.singleUpdateQuery(deleteQuery, new Object[]{loggId}, false);
    }

    //henter logg fra de siste 31 dagene
    private String getLoggTabell(int brukerId, int interval) throws Exception {
        String query = "SELECT loggId,m.matvare,ROUND(mengde) as mengde,ROUND(m.Kilokalorier/100*mengde) as kcal,dato FROM logg "
                + "LEFT JOIN matvaretabellen m ON m.matvareId = logg.matvareId"
                + " WHERE logg.brukerId = " + brukerId + " AND dato <= curdate() AND dato > DATE_SUB(curdate(),INTERVAL " + interval + " DAY)"
                + " ORDER BY loggId DESC;";
        return Database.normalQuery(query).getJSON();
    }

    private int insertLogg(Map<String, String[]> map, int brukerId) throws Exception {
        String[][] arr = map.values().toArray(new String[0][0]);
        /* arr inneholder [[type][id,id,id....][verdi,verdi,verdi.....]] etc*/
        Object[] vars = new Object[arr[1].length * 2];
        String baseline = "INSERT INTO logg(dato,matvareId,mengde,brukerId) VALUES ";
        String row = "";
        for (int i = 0; i < arr[1].length; i++) {
            vars[2 * i] = Integer.parseInt(arr[1][i]);
            vars[(2 * i) + 1] = Double.parseDouble(arr[2][i]);
            if (i != 0) {
                row += ",";
            }
            row += "(CURDATE(),?,?," + brukerId + ")";
        }
        //return baseline + row + " , " + Arrays.toString(vars) + " , " + Arrays.deepToString(arr);
        return Database.singleUpdateQuery(baseline + row, vars, false);
    }

}
