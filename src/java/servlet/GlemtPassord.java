/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import crypto.ResetToken;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.HTML;
import util.sql.Database;
import util.http.Headers;
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
            String token = ResetToken.generateToken();

            int exitStatus = send(epost, token);
            int queueRM = queueRemove(brukerId, 1);
            String msg = "/glemtpassord/?";

            /* 0 betyr bash ble utført */
            if (exitStatus == 0 && queueRM == 0) {
                // makeTempHTMLFile(token);
                int sql = setResetToken(token, brukerId);
                if (sql > 0) {
                    msg += "msg=sendt";
                } else {
                    msg += "error=1";
                }
            } else {
                msg += "error=2";
            }
            response.sendRedirect(msg);
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private int setResetToken(String token, int brukerId) throws Exception {
        String query = "UPDATE users SET resetToken = ? WHERE brukerId = " + brukerId + ";";
        return Database.singleUpdateQuery(query, new Object[]{token}, false);
    }

    /* kan denne hoppes over? epost er jo unik */
    private int getBrukerId(String epost) throws Exception {
        String query = "SELECT brukerId FROM users WHERE brukernavn = ?;";
        Object[] vars = {epost};
        ResultSetContainer rsc = Database.multiQuery(query, vars);
        int id = Integer.parseInt(rsc.getData()[0][0]);
        return id;
    }

    private int queueRemove(int id, int hours) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String query = "'UPDATE users SET resetToken = '' WHERE brukerId = " + id + ";'";
        String command = "echo \"mysql -u kosthold -D kosthold -e " + query + "\" | at today + " + hours + " hours";
        processBuilder.command("bash", "-c", command);
        Process process = processBuilder.start();
        return process.waitFor();
    }

    /* private int queueRemove(String token) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String command = "echo \"rm /home/tarves/login/glemt_passord/" + token + ".html\" | at today + 4 hours";
        processBuilder.command("bash", "-c", command);
        Process process = processBuilder.start();
        return process.waitFor();
    }*/
    private int send(String epost, String token) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();

        String link = "https://logglogg.no/epostlink/";
        String user = "noreply";
        String subject = "\"Få nytt passord på logglogg.no\"";
        String sender = "noreply@logglogg.no";
        String body = getMailBody(link, token);
        String command = "mail -a \"Content-Type: text/html\""
                + " -s " + subject
                + " -r " + sender
                //+ " -u " + user
                + " " + epost + " <<< " + body;

        processBuilder.command("bash", "-c", command);
        Process process = processBuilder.start();

        return process.waitFor();
    }

    private String getMailBody(String link, String token) {
        String html = "'<html><body>"
                + "<p>Hei, du har nylig bedt om et nytt passord.</p>"
                + "<a href=" + link + token + ">Klikk her for å sette et nytt passord</a>"
                + "<p>Linken virker kun i 4 timer</p>"
                + "</body></html>'";
        return html;
    }

}
