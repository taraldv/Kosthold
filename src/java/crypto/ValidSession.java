/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crypto;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Tarald
 */
public class ValidSession {

    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private int id;

    public ValidSession(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.req = req;
        this.resp = resp;
        try {
            id = (int) req.getSession().getAttribute("brukerId");
        } catch (NullPointerException e) {
            req.getSession().setAttribute("url", req.getServletPath());
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            resp.sendRedirect("/");
        }
    }

    public int getBrukerId() {
        return (int) req.getSession().getAttribute("brukerId");
    }

    public static void isValid(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!(req.getSession().getAttribute("brukerId") instanceof Integer)) {
            req.getSession().setAttribute("url", req.getServletPath());
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            resp.sendRedirect("/");
        }
    }

}
