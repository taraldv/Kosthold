/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.mail;

import crypto.ResetToken;
import util.sql.Database;

/**
 *
 * @author
 */
public class SendMail {

    private final int duration;
    private final String epost;
    private final String token = ResetToken.generateToken();
    private final int id;
    private final String subject;
    private final String anchorDescription;
    private final String bodyDescription;
    private final String link;

    public SendMail(int duration, String epost, int id, String subject, String anchorDescription, String bodyDescription, String link) {
        this.duration = duration;
        this.epost = epost;
        this.id = id;
        this.subject = subject;
        this.anchorDescription = anchorDescription;
        this.bodyDescription = bodyDescription;
        this.link = link;
    }

    //TODO lage skikkelig exceptions
    public void send() throws Exception {
        if (setResetToken() != 1) {
            throw new Exception("SQL Reset token ikke satt");
        }
        if (runBashMail() != 0) {
            throw new Exception("Bash mail error");
        }
        //queueBashRemove skal ikke kjøres hvis duration er 0
        if (duration != 0 && queueBashRemove() != 0) {
            throw new Exception("Bash queue error");
        }

    }

    private int runBashMail() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();

        // String user = "noreply";
        //   String subject = "\"Få nytt passord på logglogg.no\"";
        String sender = "noreply@logglogg.no";
        String command = "mail -a \"Content-Type: text/html\""
                + " -s " + "\"" + subject + "\""
                + " -r " + sender
                //+ " -u " + user
                + " " + epost + " <<< " + getMailBody();

        processBuilder.command("bash", "-c", command);
        Process process = processBuilder.start();
        //0 means normal termination
        return process.waitFor();
    }

    private String getMailBody() {
        String html = "'<html><body>"
                //+ "<p>Hei, du har nylig bedt om et nytt passord.</p>"
                // + "<a href=" + link + token + ">Klikk her for å sette et nytt passord</a>"
                + "<p>" + bodyDescription + "</p>"
                + "<a href=" + link + token + ">" + anchorDescription + "</a>"
                + "<p>Linken virker kun i " + duration + " timer</p>"
                + "</body></html>'";
        return html;
    }

    private int queueBashRemove() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String query = "\"UPDATE users SET resetToken = 'NULL' WHERE brukerId = " + id + ";\"";
        String command = "echo \"mysql -u kosthold -D kosthold -e " + query + "\" | at today + " + duration + " hours";
        processBuilder.command("bash", "-c", command);
        Process process = processBuilder.start();
        //0 means normal termination
        return process.waitFor();
    }

    private int setResetToken() throws Exception {
        String query = "UPDATE users SET resetToken = ? WHERE brukerId = " + id + ";";
        return Database.singleUpdateQuery(query, new Object[]{token}, false);
    }
}
