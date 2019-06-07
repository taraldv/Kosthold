/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.login;

import static crypto.SessionLogin.generatePasswordHash;
import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.sql.Database;
import util.http.Headers;

/**
 *
 * @author Tarald
 */
public class NewPassword extends HttpServlet {

    
    /* Testing? */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Headers.GET(response);
        //ValidSession.isValid(request, response);
        PrintWriter out = response.getWriter();

        try {
            String epost = request.getParameter("epost");
            String token = request.getParameter("token");
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet NewServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NewServlet at " + request.getContextPath() + "</h1>");
            out.println("<h1>Epost: " + epost + "</h1>");
            out.println("<h1>Token: " + token + "</h1>");
            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Headers.POST(response);
        PrintWriter out = response.getWriter();
        try {
            String token = request.getParameter("token");
            deleteFile(token);
            String nyttPassord = request.getParameter("nyttPassord");
            int passordEndring = endrePassord(token, nyttPassord);
            if (passordEndring > 0) {
                response.sendRedirect("https://logglogg.no/");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    /* er ikke så viktig å slette fila, skal være i queue for sletting 
    og kan lage en cron job for weekly cleanup */
    private void deleteFile(String token) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String command = "rm /home/tarves/login/glemt_passord/" + token + ".html";
        processBuilder.command("bash", "-c", command);
        processBuilder.start();
    }

    private int endrePassord(String token, String nyttPassord) throws Exception {
        String hashedPassword = generatePasswordHash(nyttPassord);
        String query = "UPDATE users SET passord = ?, resetToken = NULL WHERE resetToken = ?;";
        Object[] vars = {hashedPassword, token};
        return Database.singleUpdateQuery(query, vars, false);
    }

}
