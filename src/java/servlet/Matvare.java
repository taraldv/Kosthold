/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.TooManyColumns;
import util.database.KostholdDatabase;
import util.http.StandardResponse;
import util.sql.ResultSetConverter;

/**
 *
 * @author Tarald
 */
public class Matvare extends HttpServlet {

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
            if (type.equals("insertMatvaretabell")) {
                String matvareNavn = request.getParameter("navn");
                String[][] matvareIngrediensOgVerdier = mapToArrayInArray(request.getParameterMap(), 2);
                out.print(insertIntoMatvaretabellen(matvareNavn, matvareIngrediensOgVerdier));
            }else if (type.equals("getMatvaretabell")) {
                out.print(ResultSetConverter.toJSON(KostholdDatabase.normalQuery("SELECT matvareId,matvare FROM matvaretabellen;")));
            } 
        } catch (Exception e) {
            e.printStackTrace(out);
        }
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
}
