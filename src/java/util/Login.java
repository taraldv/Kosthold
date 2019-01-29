/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpSession;

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

    public Boolean checkPassword() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection c = DriverManager.getConnection("jdbc:mysql://localhost/users", "kosthold", "");
            String query = "SELECT passord FROM users WHERE brukernavn LIKE ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, brukernavn);
            ResultSet rs = ps.executeQuery();
            rs.first();
            String hashedPassword = rs.getString(1);
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
