/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package html;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author
 */
public class ErrorHandling {

    private final HttpServletRequest req;

    public ErrorHandling(HttpServletRequest req) {
        this.req = req;
    }

    @Override
    public String toString() {
        String beskjed = "";
        String error = req.getParameter("error");
        String msg = req.getParameter("msg");
        if (error != null) {
            int errorCode = Integer.parseInt(error);
            switch (errorCode) {
                //Index.java
                case 1:
                    beskjed = "<p>Feil passord og/eller epost</p><p>Etter 5 mislykket forsøk innen 15 minutter, blir din ip bannlyst i 2 timer</p>";
                    break;
                case 2:
                    beskjed = "SQL error";
                    break;
                case 3:
                    beskjed = "Ugyldig token";
                    break;
                case 4:
                    beskjed = "Fikk ikke sendt epost";
                    break;
                default:
                    beskjed = "Noe gikk galt, prøv igjen";
                    break;

            }
        } else if (msg != null) {
            int msgCode = Integer.parseInt(msg);
            switch (msgCode) {
                //GlemtPassord.java
                case 1:
                    beskjed = "Epost sendt, sjekk innboks eller spam";
                    break;
                default:
                    beskjed = "";
                    break;
            }
        }
        return "<div>" + beskjed + "</div>";
    }

}
