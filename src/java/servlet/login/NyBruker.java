/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.login;

import crypto.SessionLogin;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.http.StandardResponse;

/**
 *
 * @author
 */
public class NyBruker extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StandardResponse sr = new StandardResponse(response);
        PrintWriter out = sr.getWriter();
        try {
            if (false) {
                sr.sendRedirect("https://kosthold.tarves.no/logginn/");
            } else {
                out.print("epost i bruk eller ugyldig");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

}
