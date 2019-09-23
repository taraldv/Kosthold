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
import util.HTML;
import util.sql.Database;
import util.http.Headers;
import util.mail.SendMail;
import util.sql.ResultSetContainer;

/**
 *
 * @author Tarald
 */
public class GlemtPassord extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Headers.GET(resp);
        PrintWriter out = resp.getWriter();
        String error = req.getParameter("error");
        String msg = req.getParameter("msg");
        String beskjed = "";
        HTML html = new HTML("LoggLogg Glemt passord");
        html.addBody("<form method='POST' action=''>"
                + "<div>"
                + "<div>epost</div>"
                + "<input name='epost' id='brukernavnInput' type='text'>"
                + "</div>"
                + "<input type='submit' value='Send link'>"
                + "</form>");
        if (error != null) {
            //error = 1, sql
            //error = 2, epost sending eller bash queue
            //error = 3, nytt passord token ugyldig
            //error = 4, noe gikk galt under nytt passord sql
            beskjed = "Noe gikk galt, prøv igjen";
        } else if (msg != null) {
            beskjed = "Epost sendt, sjekk innboks eller spam";
        }
        html.addBody("<div>" + beskjed + "</div>");

        out.print(html.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Headers.POST(response);
        PrintWriter out = response.getWriter();
        try {
            String epost = request.getParameter("epost");
            int brukerId = getBrukerId(epost);
            SendMail sm = new SendMail(4, epost, brukerId,
                    "Få nytt passord på logglogg.no",
                    "Klikk her for å sette et nytt passord",
                    "Hei, du har nylig bedt om et nytt passord.",
                    "https://logglogg.no/epostlink/");
            sm.send();
            response.sendRedirect("/glemtpassord/?msg=sendt");
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    /* kan denne hoppes over? epost er jo unik */
    private int getBrukerId(String epost) throws Exception {
        String query = "SELECT brukerId FROM users WHERE brukernavn = ? AND epostAktivert = 1;";
        Object[] vars = {epost};
        ResultSetContainer rsc = Database.multiQuery(query, vars);
        int id = Integer.parseInt(rsc.getData()[0][0]);
        return id;
    }

    /* private int queueRemove(String token) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String command = "echo \"rm /home/tarves/login/glemt_passord/" + token + ".html\" | at today + 4 hours";
        processBuilder.command("bash", "-c", command);
        Process process = processBuilder.start();
        return process.waitFor();
    }*/
}
