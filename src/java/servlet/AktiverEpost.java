/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.http.Headers;
import util.sql.Database;

/**
 *
 * @author
 */
public class AktiverEpost extends HttpServlet {

    /* Blir sendt til denne siden fra URL i epost */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Headers.GET(resp);
        //ValidSession.isValid(request, response);
        PrintWriter out = resp.getWriter();
        String token = req.getPathInfo().substring(1);
        try {
            if (validToken(token)) {
                resp.sendRedirect("https://logglogg.no/");
            } else {
                resp.sendRedirect("/nybruker/?error=invalidToken");
            }
        } catch (ArrayIndexOutOfBoundsException a) {
            resp.sendRedirect("/nybruker/?error=invalidToken");
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    //TODO blir aldri false, kaster bare errors
    private boolean validToken(String token) throws Exception {
        String query = "SELECT 1 FROM users WHERE resetToken LIKE ?";
        String[][] rsc = Database.multiQuery(query, new Object[]{token}).getData();
        int exists = Integer.parseInt(rsc[0][0]);
        if (exists == 1) {
            String validQuery = "UPDATE users SET resetToken = NULL, epostAktivert = 1 WHERE resetToken = ?;";
            int deleteToken = Database.singleUpdateQuery(validQuery, new Object[]{token}, false);
            return deleteToken == 1;

        }
        return false;
    }
}
