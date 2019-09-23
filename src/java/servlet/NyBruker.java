/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import crypto.SessionLogin;
import html.ErrorHandling;
import html.IndexHtml;
import html.Input;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.exceptions.BashMailException;
import util.exceptions.BashQueueException;
import util.exceptions.TokenSetException;
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
        ErrorHandling errorHandling = new ErrorHandling(request);
        try {
            IndexHtml html = new IndexHtml("LoggLogg Ny Bruker");
            Input navn = new Input("skriv inn epost her", "epost", "text", "brukernavnInput", "input-login", "epost", "on");
            Input passord = new Input("skriv inn passord her", "passord", "password", "passordInput", "input-login", "passord", "on");
            String properSubmit = "<input id='loginSubmitInput' class='input-login' type='submit' value='registrer'>";
            String properForm = "<form id='registrerForm' class='form-login' method='POST' action=''>"
                    + navn.toString()
                    + passord.toString()
                    + properSubmit
                    + "</form>";
            html.addBodyContent(properForm);
            html.addBodyContent(errorHandling.toString());
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
            String escapedEpost = escape(epost);
            String pw = request.getParameter("passord");
            if (pw.length() == 0) {
                response.sendRedirect("https://logglogg.no/nybruker?error=7");
            }
            //tror ikke nyBruker kan bli noe annet en 1 eller exception
            int brukerId = nyBruker(escapedEpost, pw);
            SendMail sm = new SendMail(0, escapedEpost, brukerId,
                    "Aktiver din bruker på logglogg.no",
                    "Klikk her for å aktivere din bruker",
                    "Hei, din epost har blitt brukt til å lage en konto på logglogg.no",
                    "https://logglogg.no/aktiverepost/");
            sm.send();
            response.sendRedirect("/nybruker/?msg=sendt");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(out);
        } catch (TokenSetException e) {
            response.sendRedirect("https://logglogg.no/nybruker?error=3");
        } catch (BashQueueException e) {
            response.sendRedirect("https://logglogg.no/nybruker?error=8");
        } catch (BashMailException e) {
            response.sendRedirect("https://logglogg.no/nybruker?error=4");
        }
    }

    private int nyBruker(String epost, String passord) throws ClassNotFoundException, SQLException {
        String query = "INSERT INTO users(brukernavn,passord) VALUES (?,?);";
        return Database.singleUpdateQuery(query, new Object[]{epost, SessionLogin.generatePasswordHash(passord)}, true);
    }

    private String escape(String epost) {
        String removeSemicolon = epost.replaceAll(";", "");
        String removeHyp = removeSemicolon.replaceAll("'", "");
        String removeComma = removeHyp.replaceAll(",", "");
        return removeComma.replaceAll("\"", "");
    }

}
