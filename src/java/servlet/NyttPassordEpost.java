/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import static crypto.SessionLogin.generatePasswordHash;
import html.IndexHtml;
import html.Input;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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
public class NyttPassordEpost extends HttpServlet {

    /* Blir sendt til denne siden fra URL i epost */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Headers.GET(resp);
        IndexHtml html = new IndexHtml("LoggLogg Nytt Passord");
        PrintWriter out = resp.getWriter();
        String token = req.getPathInfo().substring(1);
        try {
            if (validToken(token) == 1) {
                Input epost = new Input("skriv nytt passord her", "nytt passord", "password", "brukernavnInput", "input-login", "nyttPassord", "off");
                String properSubmit = "<input id='loginSubmitInput' class='input-login' type='submit' value='oppdater passord'>";
                String hiddenInput = "<input name='token' type='text' value=" + token + " hidden=''>";
                String properForm = "<form id='registrerForm' class='form-login' method='POST' action=''>"
                        + epost.toString()
                        + hiddenInput
                        + properSubmit
                        + "</form>";
                html.addBodyContent(properForm);
                out.print(html.toString());
            } else {
                resp.sendRedirect("/glemtpassord/?error=3");
            }
        } catch (ClassNotFoundException | SQLException e) {
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
                response.sendRedirect("/glemtpassord/?error=9");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(out);
        }
    }

    private int endrePassord(String token, String nyttPassord) throws SQLException, ClassNotFoundException {
        String hashedPassword = generatePasswordHash(nyttPassord);
        String query = "UPDATE users SET passord = ?, resetToken = NULL WHERE resetToken = ?;";
        Object[] vars = {hashedPassword, token};
        return Database.singleUpdateQuery(query, vars, false);
    }

    private int validToken(String token) throws SQLException, ClassNotFoundException {
        String query = "SELECT 1 FROM users WHERE resetToken LIKE ?";
        String[][] rsc = Database.multiQuery(query, new Object[]{token}).getData();
        return Integer.parseInt(rsc[0][0]);
    }
}
