/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import crypto.SessionLogin;
import html.IndexHtml;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.http.Headers;

/**
 *
 * @author
 */
public class NyBruker extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Headers.GET(response);
        PrintWriter out = response.getWriter();
        try {
            IndexHtml html = new IndexHtml("LoggLogg Ny Bruker");
            out.print(html);
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
            if (false) {
                response.sendRedirect("https://logglogg.no/");
            } else {
                out.print("epost i bruk eller ugyldig");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
    
}
