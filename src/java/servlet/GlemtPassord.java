/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import html.ErrorHandling;
import html.IndexHtml;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        ErrorHandling errorHandling = new ErrorHandling(req);
        IndexHtml html = new IndexHtml("LoggLogg Glemt Passord");
        html.addBodyContent("<form method='POST' action=''>"
                + "<div>"
                + "<div>epost</div>"
                + "<input name='epost' id='brukernavnInput' type='text'>"
                + "</div>"
                + "<input type='submit' value='Send link'>"
                + "</form>");

        html.addBodyContent(errorHandling.toString());
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
            response.sendRedirect("/glemtpassord/?msg=1");
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
