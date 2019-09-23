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
                    beskjed = "<p>Feil passord og/eller epost</p><p>Etter 5 mislykket forsøk innen 15 minutter, blir din du utestengt i 2 timer</p>";
                    break;
                case 2:
                    beskjed = "SQL error";
                    break;
                case 3:
                    beskjed = "Ugyldig token";
                    break;
                //NyBruker.java
                case 4:
                    beskjed = "<p>Fikk ikke sendt epost</p>";
                    break;
                //Index.java
                case 5:
                    beskjed = "<p>Epost har ikke blitt aktivert</p>";
                    break;
                //Index.java
                case 6:
                    beskjed = "<p>Epost/Passord felt kan ikke være tomt</p>";
                    break;
                //NyBruker.java
                case 7:
                    beskjed = "<p>Passord felt kan ikke være tomt</p>";
                    break;
                //NyBruker.java
                case 8:
                    beskjed = "<p>Server error, bash script feilet. Vennligst prøv igjen</p>";
                    break;

                default:
                    beskjed = "Noe gikk galt, mangler error kode";
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
