/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Tarald
 */
public class Login {

    private final String brukernavn;
    private final String passord;
    private final HttpSession session;

    public Login(String brukernavn, String passord, HttpSession session) {
        this.brukernavn = brukernavn;
        this.passord = passord;
        this.session = session;
    }

    static public String generatePasswordHash(String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(17));
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
            Connection c = KostholdDatabase.getDatabaseConnection();
            String query = "SELECT passord,brukerId FROM users WHERE brukernavn LIKE ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, brukernavn);
            ResultSet rs = ps.executeQuery();
            rs.first();
            String hashedPassword = rs.getString(1);
            int brukerId = rs.getInt(2);
            c.close();

            /* hvis gyldig passord, set attributes */
            if (org.mindrot.jbcrypt.BCrypt.checkpw(passord, hashedPassword)) {
                setSession(brukerId);
                session.setMaxInactiveInterval(0);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean validSession() {
        if (session.getAttribute("brukerId") != null && session.getAttribute("brukernavn") != null) {
            return true;
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
