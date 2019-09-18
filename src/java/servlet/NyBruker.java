/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import crypto.BCrypt;
import crypto.SessionLogin;
import html.IndexHtml;
import html.Input;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.http.Headers;
import util.mail.SendMail;
import util.sql.Database;

/**
 *
 * @author
 */
public class NyBruker extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Headers.GET(response);
        PrintWriter out = response.getWriter();
        try {
            IndexHtml html = new IndexHtml("LoggLogg Ny Bruker");
            Input navn = new Input("skriv inn epost her", "epost", "text", "brukernavnInput", "input-registrer", "epost", "on");
            Input passord = new Input("skriv inn passord her", "passord", "password", "passordInput", "input-registrer", "passord", "on");
            String properSubmit = "<input id='registrerSubmitInput' class='input-registrer' type='submit' value='registrer'>";
            String properForm = "<form id='registrerForm' class='form-login' method='POST' action=''>"
                    + navn.toString()
                    + passord.toString()
                    + properSubmit
                    + "</form>";
            html.addBodyContent(properForm);
            out.print(html);
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
            String epost = request.getParameter("epost");
            String pw = request.getParameter("passord");
            //tror ikke nyBruker kan bli noe annet en 1 eller exception
            int brukerId = nyBruker(epost, pw);
            SendMail sm = new SendMail(4, epost, brukerId,
                    "Aktiver din bruker på logglogg.no",
                    "Klikk her for å aktivere din bruker",
                    "Hei, din epost har blitt brukt til å lage en konto på logglogg.no");
            sm.send();
            response.sendRedirect("/glemtpassord/?msg=sendt");

        } catch (Exception e) {
            response.sendRedirect("https://logglogg.no/nybruker?error=1");
            // e.printStackTrace(out);
        }
    }

    private int nyBruker(String epost, String passord) throws Exception {
        String query = "INSERT INTO users(brukernavn,passord) VALUES (?,?);";
        return Database.singleUpdateQuery(query, new Object[]{epost, SessionLogin.generatePasswordHash(passord)}, true);
    }

}
