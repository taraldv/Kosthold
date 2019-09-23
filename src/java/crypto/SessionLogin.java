/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crypto;

import java.sql.SQLException;
import javax.servlet.http.HttpSession;
import util.exceptions.EpostAktivertException;
import util.exceptions.LoginFormInputEmptyException;
import util.exceptions.LoginPasswordException;
import util.sql.Database;
import util.sql.ResultSetContainer;

/**
 *
 * @author Tarald
 */
public class SessionLogin {

    private final String brukernavn;
    private final String passord;
    private final HttpSession session;

    public SessionLogin(String brukernavn, String passord, HttpSession session) {
        this.brukernavn = brukernavn;
        this.passord = passord;
        this.session = session;
    }

    static public String generatePasswordHash(String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(7));
        return hashed;
    }

    private boolean validReqestStrings() {
        return (brukernavn != null && passord != null);
    }

    public int getBrukerId() {
        return (int) session.getAttribute("brukerId");
    }

    public void checkPassword() throws SQLException, ClassNotFoundException,
            EpostAktivertException, LoginFormInputEmptyException,
            LoginPasswordException {
        /* sjeker om strings ikke er null, ikke vits med sql spørring med tom string */
        if (validReqestStrings()) {
            String query = "SELECT passord,brukerId,epostAktivert FROM users WHERE brukernavn LIKE ?";
            ResultSetContainer rsc = Database.multiQuery(query, new Object[]{brukernavn});
            String hashedPassword = rsc.getData()[0][0];
            int brukerId = Integer.parseInt(rsc.getData()[0][1]);
            int aktivert = Integer.parseInt(rsc.getData()[0][2]);

            /* hvis epost aktivert & gyldig passord, set attributes */
            if (aktivert == 1) {
                if (crypto.BCrypt.checkpw(passord, hashedPassword)) {
                    setSession(brukerId);
                } else {
                    throw new LoginPasswordException();
                }
            } else {
                throw new EpostAktivertException();
            }
        } else {
            throw new LoginFormInputEmptyException();
        }
    }

    private void setSession(int brukerId) {
        session.setAttribute("brukerId", brukerId);
        session.setAttribute("brukernavn", brukernavn);
    }
}
