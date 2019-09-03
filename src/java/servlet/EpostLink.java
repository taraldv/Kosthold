/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import static crypto.SessionLogin.generatePasswordHash;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.HTML;
import util.sql.Database;
import util.http.Headers;

/**
 *
 * @author Tarald
 */
public class EpostLink extends HttpServlet {

    /* Testing? */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Headers.GET(resp);
        //ValidSession.isValid(request, response);
        PrintWriter out = resp.getWriter();
        String token = req.getPathInfo().substring(1);
        try {
            if (validToken(token) == 1) {
                out.print(finishedHTML(token).toString());
            } else {
                resp.sendRedirect("/glemtpassord/?error=3");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Headers.POST(response);
        PrintWriter out = response.getWriter();
        try {
            String token = request.getParameter("token");
            String nyttPassord = request.getParameter("nyttPassord");
            int passordEndring = endrePassord(token, nyttPassord);
            if (passordEndring > 0) {
                response.sendRedirect("https://logglogg.no/");
            } else {
                response.sendRedirect("/glemtpassord/?error=4");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private int endrePassord(String token, String nyttPassord) throws Exception {
        String hashedPassword = generatePasswordHash(nyttPassord);
        String query = "UPDATE users SET passord = ?, resetToken = NULL WHERE resetToken = ?;";
        Object[] vars = {hashedPassword, token};
        return Database.singleUpdateQuery(query, vars, false);
    }

    private int validToken(String token) throws Exception {
        String query = "SELECT 1 FROM users WHERE resetToken LIKE ?";
        String[][] rsc = Database.multiQuery(query, new Object[]{token}).getData();
        return Integer.parseInt(rsc[0][0]);
    }

    private HTML finishedHTML(String token) throws Exception {
        HTML html = new HTML("LoggLogg nytt passord");
        String form = "<form method='POST' action=''>"
                + "<div>"
                + "<div>"
                + "nytt passord"
                + "</div>"
                + "<input name='nyttPassord' type='text'>"
                + "<input name='token' type='text' value=" + token + " hidden=''>"
                + "</div>"
                + "<input type='submit' value='Send nytt passord'>"
                + "</form>";
        html.addBody(form);
        return html;
    }

}
