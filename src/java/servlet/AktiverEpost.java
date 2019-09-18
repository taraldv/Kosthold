/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import static crypto.SessionLogin.generatePasswordHash;
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

    /* Testing? */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Headers.GET(resp);
        //ValidSession.isValid(request, response);
        PrintWriter out = resp.getWriter();
        String token = req.getPathInfo().substring(1);
        try {
            if (validToken(token)) {
                resp.sendRedirect("logglogg.no");
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
            int deleteToken = Database.singleUpdateQuery("UPDATE users SET resetToken = NULL WHERE resetToken = ?;", new Object[]{token}, false);
            return deleteToken == 1;

        }
        return false;
    }
}
