/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.index;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.database.FjernDenne;
import crypto.SessionLogin;
import crypto.ValidSession;
import util.http.StandardResponse;
import util.sql.ResultSetContainer;

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

        /* nullPointerException hvis type ikke er en del av request */
        try {

            if (type.equals("logout")) {
                vs.logOut();
            } else if (type.equals("auth")) {
                out.print(1);
            } else if (type.equals("passordGen")) {
                out.print(SessionLogin.generatePasswordHash(request.getParameter("passord")));
            } else if (type.equals("autocomplete")) {
                String matchingParameter = request.getParameter("string");
                String whichTable = request.getParameter("table");
                String autocompleteQuery = "";
                if (whichTable.equals("matvaretabellen")) {
                    autocompleteQuery = "SELECT matvare,matvareId FROM matvaretabellen WHERE matvare LIKE ? LIMIT 30;";
                } else if (whichTable.equals("næringsinnhold")) {
                    autocompleteQuery = "SELECT næringsinnhold,benevning FROM benevninger WHERE næringsinnhold LIKE ? LIMIT 15;";
                }
                ResultSetContainer rsc = FjernDenne.multiQuery(autocompleteQuery, new Object[]{"%" + matchingParameter + "%"});
                String completeJson = rsc.getJSON();
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
