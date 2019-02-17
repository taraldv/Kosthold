/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.kosthold;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.database.KostholdDatabase;
import util.http.StandardResponse;

/**
 *
 * @author Tarald
 */
public class Vekt extends HttpServlet {

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
            if (type.equals("getVektLogg")) {
                out.print(getVektLogg(brukerId));
            } else if (type.equals("insertVekt")) {
                Double kiloVekt = Double.parseDouble(request.getParameter("kilo"));
                out.print(insertVektLogg(kiloVekt, brukerId));
            } else if (type.equals("deleteVekt")) {
                int vektId = Integer.parseInt(request.getParameter("vektId"));
                out.print(deleteVekt(vektId, brukerId));
            } else if (type.equals("updateVekt")) {
                int vektId = Integer.parseInt(request.getParameter("vektId"));
                Double kiloVekt = Double.parseDouble(request.getParameter("kilo"));
                String dato = request.getParameter("dato");
                out.print(updateVekt(vektId, brukerId, kiloVekt, dato));
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int updateVekt(Integer vektId, int brukerId, Double kiloVekt, String dato) throws Exception {
        String updateQuery = "UPDATE vekt SET kilo = ?, dato = ? WHERE vektId = ? AND brukerId = " + brukerId + ";";
        Object[] vars = {kiloVekt, dato, vektId};
        return KostholdDatabase.singleUpdateQuery(updateQuery, vars, false);
    }

    private int deleteVekt(int vektId, int brukerId) throws Exception {
        String deleteQuery = "DELETE FROM vekt WHERE vektId = ? AND brukerId = " + brukerId + ";";
        Object[] vars = {vektId};
        return KostholdDatabase.singleUpdateQuery(deleteQuery, vars, false);
    }

    private String getVektLogg(int brukerId) throws Exception {
        String målQuery = "SELECT vektId,dato,kilo FROM vekt WHERE brukerId =" + brukerId + ";";
        return KostholdDatabase.normalQuery(målQuery).getJSON();
    }

    private int insertVektLogg(Double kiloVekt, int brukerId) throws Exception {
        String insertQuery = "INSERT INTO vekt(dato,kilo,brukerId) VALUES (CURDATE(),?," + brukerId + ");";
        Object[] vars = {kiloVekt};
        return KostholdDatabase.singleUpdateQuery(insertQuery, vars, false);
    }
}
