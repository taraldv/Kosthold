/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

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
import util.database.KostholdDatabase;
import crypto.SessionLogin;
import crypto.ValidSession;
import util.sql.ResultSetConverter;
import util.http.StandardResponse;
import util.TooManyColumns;

/**
 *
 * @author Tarald
 */
public class Post extends HttpServlet {

    /* TODO, erstatte type med en annen URL */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StandardResponse sr = new StandardResponse(response);
        PrintWriter out = sr.getWriter();
        String type = request.getParameter("type");
        ValidSession vs = new ValidSession(out, request.getSession());
        
        /* stopper request hvis ugylid session */
        if (!vs.validateSession()) {
            return;
        }
        int brukerId = vs.getId();

        out.print(request.getSession().getAttribute("brukernavn") + "\n");
        out.print(request.getSession().getAttribute("brukerId") + "\n");

        /* nullPointerException hvis type ikke er en del av request */
        try {

            if (type.equals("passordGen")) {
                out.print(SessionLogin.generatePasswordHash(request.getParameter("passord")));
            }

            
            /*  */
             if (type.equals("autocomplete")) {
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
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }



  

   


}
