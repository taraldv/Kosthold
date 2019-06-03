/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import crypto.SessionLogin;
import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.HTML;
import util.http.StandardResponse;

/**
 *
 * @author
 */
public class Index extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        //ValidSession vs = new ValidSession(request, response);

        HTML html = new HTML("LoggLogg");
        html.addCSS("../css/main.css");
        html.addMobileCSS("../css/mobile.css");
        Object obj = request.getSession().getAttribute("brukerId");
        if (obj instanceof Integer) {
            html.addJS("../js/main.js");
        } else {
            String body = "<form id='loginForm' class='logginnForm' method='POST' action=''>";
            /* try {
                int feil = Integer.parseInt(request.getParameter("feil"));
                body += "<div>Feil passord eller brukernavn</div>";
            } catch (Exception e) {
                
            }*/

            body += "<div>"
                    + "<div>epost</div>"
                    + "<input class='logginnInput' name='epost' id='brukernavnInput' type='text'>"
                    + "</div>"
                    + "<div>"
                    + "<div>passord</div>"
                    + "<input class='logginnInput' name='passord' id='passordInput' type='password'>"
                    + "</div>"
                    // + "<input name='url' id='url' hidden=' value='>"
                    + "<input class='logginnSubmit' id='loggInnSubmit' type='submit' value='logg inn'>"
                    + "</form>"
                    + "<a class='logginnLink' id='glemtPassordLink' href='/logginn/glemt_passord'>Glemt passord</a>"
                    + "<a class='logginnLink' id='nyBrukerLink' href='/logginn/ny_bruker'>Ny bruker</a>";
            // + "<script type='text/javascript'>"
            // + "let urlInput = document.getElementById('url');"
            // + "const urlParams = new URLSearchParams(window.location.search);"
            // + "urlInput.setAttribute('value',urlParams.get('url'));"
            //  + "</script>");
            html.addBody(body);
        }
        out.print(html.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StandardResponse sr = new StandardResponse(response);
        PrintWriter out = sr.getWriter();
        try {
            SessionLogin login = new SessionLogin(request.getParameter("epost"), request.getParameter("passord"), request.getSession());
            if (login.validLogin()) {
                String url = (String) request.getSession().getAttribute("url");
                if (url == null) {
                    url = "";
                }
                sr.sendRedirect("https://logglogg.no" + url);
            } else {
                sr.sendRedirect("https://logglogg.no?feil=1");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}
