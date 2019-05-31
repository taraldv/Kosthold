/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.login;

import crypto.ResetToken;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.sql.Database;
import util.http.StandardResponse;
import util.sql.ResultSetContainer;

/**
 *
 * @author Tarald
 */
public class ResetPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StandardResponse sr = new StandardResponse(response);
        PrintWriter out = sr.getWriter();
        try {
            String epost = request.getParameter("epost");
            int brukerId = getBrukerId(epost);
            String token = ResetToken.generateToken();

            int exitStatus = send(epost, token);
            int queueRM = queueRemove(token);

            /* 0 betyr bash ble utført */
            if (exitStatus == 0 && queueRM == 0) {
                makeTempHTMLFile(token);
                int sql = setResetToken(token, brukerId);
                if (sql > 0) {
                    sr.sendRedirect("https://login.tarves.no/glemt_passord/");
                }
            }
            out.print("noe gikk galt, prøv igjen");
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

    private int queueRemove(String token) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String command = "echo \"rm /home/tarves/login/glemt_passord/" + token + ".html\" | at today + 4 hours";
        processBuilder.command("bash", "-c", command);
        Process process = processBuilder.start();
        return process.waitFor();
    }

    private void makeTempHTMLFile(String token) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter("/home/tarves/login/glemt_passord/" + token + ".html"));
        bw.write("<!DOCTYPE html>");
        bw.write("<html>");
        bw.write("<head>");
        bw.write("<meta charset='UTF-8'");
        bw.write("</head>");
        bw.write("<body>");
        bw.write("<form method='POST' action='https://tomcat.tarves.no/TomcatServlet/NewPassword'>");
        bw.write("<div>");
        bw.write("<div>");
        bw.write("nytt passord");
        bw.write("</div>");
        bw.write("<input name='nyttPassord' type='text'>");
        bw.write("<input name='token' type='text' value=" + token + " hidden=''>");
        bw.write("</div>");
        bw.write("<input type='submit' value='Send nytt passord'>");
        bw.write("</form>");
        bw.write("</body>");
        bw.write("</html>");
        bw.flush();
        bw.close();
    }

    private int send(String epost, String token) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();

        String link = "https://login.tarves.no/glemt_passord/";

        String subject = "\"Få nytt passord på tarves.no\"";
        String sender = "noreply@tarves.no";
        String body = getMailBody(link, token);
        String command = "mail -a \"Content-Type: text/html\" -s " + subject + " -r "
                + sender + " " + epost + " <<< " + body;

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
