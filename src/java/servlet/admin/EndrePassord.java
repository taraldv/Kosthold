/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.admin;

import crypto.ResetToken;
import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.sql.Database;
import util.http.Headers;
import util.sql.ResultSetContainer;

/**
 *
 * @author Tarald
 */
public class EndrePassord extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Headers.POST(response);
        ValidSession.isValid(request, response);
        PrintWriter out = response.getWriter();
        try {
            int brukerId = (int) request.getSession().getAttribute("brukerId");
            if (checkOldPassword(brukerId, request.getParameter("oldPassword"))) {
                out.print(updatePassword(brukerId, request.getParameter("newPassword")));
            } else {
                out.print("0");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private int updatePassword(int brukerId, String newPassword) throws Exception {
        String query = "UPDATE users SET passord = ? WHERE brukerId = ?;";
        String newPasswordHash = crypto.SessionLogin.generatePasswordHash(newPassword);
        Object[] vars = {newPasswordHash, brukerId};
        return Database.singleUpdateQuery(query, vars, false);
    }

    private boolean checkOldPassword(int brukerId, String oldPassword) throws Exception {

        String query = "SELECT passord FROM users WHERE brukerId = ?";
        ResultSetContainer rsc = Database.multiQuery(query, new Object[]{brukerId});
        String hashedPassword = rsc.getData()[0][0];

        return crypto.BCrypt.checkpw(oldPassword, hashedPassword);
    }
}
