/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.trening;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Tarald
 */
public class Kalender extends HttpServlet {

  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "https://trening.tarves.no");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        PrintWriter out = response.getWriter();
        ValidSession vs = new ValidSession(out, request.getSession());

        String type = request.getParameter("type");
        /* stopper request hvis ugylid session */
        if (!vs.validateSession()) {
            return;
        }

        try {
            if (type.equals("kalender")) {
                out.print(util.TreningHistorikk.getJSON());
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

 
}
