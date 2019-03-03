/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import crypto.SessionLogin;
import java.io.PrintWriter;
import util.http.StandardResponse;

/**
 *
 * @author Tarald
 */
public class Login extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StandardResponse sr = new StandardResponse(response);
        PrintWriter out = sr.getWriter();
        String url = request.getParameter("url");
        try {
            SessionLogin login = new SessionLogin(request.getParameter("brukernavn"), request.getParameter("passord"), request.getSession());
            if (login.validLogin()) {
                if (url != null) {
                    sr.sendRedirect(url);
                } else {
                    sr.sendRedirect("https://www.tarves.no");
                }
            } else {
                login.invalidate();
                out.print("feil passord eller brukernavn");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}
