/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.login;

import crypto.ResetToken;
import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.database.Kosthold;
import util.http.StandardResponse;
import util.sql.ResultSetContainer;

/**
 *
 * @author Tarald
 */
public class EndrePassord extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StandardResponse sr = new StandardResponse(response);
        PrintWriter out = sr.getWriter();
        ValidSession vs = new ValidSession(out, request.getSession());

        /* stopper request hvis ugylid session */
        if (!vs.validateSession()) {
            return;
        }
        int brukerId = vs.getId();
        try {
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
        return Kosthold.singleUpdateQuery(query, vars, false);
    }

    private boolean checkOldPassword(int brukerId, String oldPassword) throws Exception {

        String query = "SELECT passord FROM users WHERE brukerId = ?";
        ResultSetContainer rsc = Kosthold.multiQuery(query, new Object[]{brukerId});
        String hashedPassword = rsc.getData()[0][0];

        return crypto.BCrypt.checkpw(oldPassword, hashedPassword);
    }
}
