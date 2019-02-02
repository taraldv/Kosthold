/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crypto;

import java.io.PrintWriter;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Tarald
 */
public class ValidSession {

    private final PrintWriter out;
    private final HttpSession session;
    private int id;

    public ValidSession(PrintWriter out, HttpSession session) {
        this.out = out;
        this.session = session;
    }

    public boolean validateSession() {
        try {
            id = (int) session.getAttribute("brukerId");
            return true;
        } catch (Exception e) {
            e.printStackTrace(out);
            return false;
        }
    }

    public int getId() {
        return id;
    }

}
