/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import javax.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Tarald
 */
public class Login {

    private final String brukernavn;
    private final String passord;

    public Login(String brukernavn, String passord) {
        this.brukernavn = brukernavn;
        this.passord = passord;
    }

    static public String generatePasswordHash(String password) {
        Random rand = new Random();
        int randomNum = 15 + rand.nextInt((15) + 1);
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(randomNum));
        return hashed;
    }

    public Boolean checkPassword() throws Exception {
        try {
            Connection c = KostholdDatabase.getDatabaseConnection();
            String query = "SELECT passord FROM users WHERE brukernavn LIKE ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, brukernavn);
            ResultSet rs = ps.executeQuery();
            rs.first();
            String hashedPassword = rs.getString(1);
            c.close();
            //  return hashedPassword;
            return org.mindrot.jbcrypt.BCrypt.checkpw(passord, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    public void setSession(HttpSession session) {
        session.setAttribute("bruker", brukernavn);
    }
}
