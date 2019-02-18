/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.trening;

import crypto.ValidSession;
import util.TreningHistorikk;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashSet;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.database.Database;

/**
 *
 * @author Tarald
 */
public class Post extends HttpServlet {

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
            if (type.equals("history")) {
                out.print(getHistory());
            } 
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private String getHistory() throws Exception {
        String historyQuery = "SELECT dato,excercise,avg_weight FROM trening WHERE dato >= (SELECT distinct(dato) FROM trening ORDER BY id desc limit 5,1);";
        return Database.normalQuery(historyQuery, 0).getJSON();
    }

  

}
