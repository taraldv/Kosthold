/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crypto;

import javax.servlet.http.HttpSession;
import util.database.Kosthold;
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

    public boolean validLogin() throws Exception {
        return checkPassword();
    }

    private boolean checkPassword() throws Exception {
        /* sjeker om strings ikke er null, ikke vits med sql spørring med tom string */
        if (validReqestStrings()) {
            String query = "SELECT passord,brukerId FROM users WHERE brukernavn LIKE ?";
            ResultSetContainer rsc = Kosthold.multiQuery(query, new Object[]{brukernavn});
            String hashedPassword = rsc.getData()[0][0];
            int brukerId = Integer.parseInt(rsc.getData()[0][1]);

            /* hvis gyldig passord, set attributes */
            if (crypto.BCrypt.checkpw(passord, hashedPassword)) {
                setSession(brukerId);
                session.setMaxInactiveInterval(0);
                return true;
            }
        }
        return false;
    }

    public void invalidate() {
        session.invalidate();
    }

    private void setSession(int brukerId) {
        session.setAttribute("brukerId", brukerId);
        session.setAttribute("brukernavn", brukernavn);
    }
}
