/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import crypto.SessionLogin;
import html.Anchor;
import html.IndexHtml;
import html.Input;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.http.Headers;

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

        IndexHtml html = new IndexHtml("LoggLogg");

        Input navn = new Input("skriv inn epost her", "epost", "text", "brukernavnInput", "input-login", "epost", "on");
        Input passord = new Input("skriv inn passord her", "passord", "password", "passordInput", "input-login", "passord", "on");
        String properSubmit = "<input id='loginSubmitInput' class='input-login' type='submit' value='logg inn'>";
        String properForm = "<form id='loginForm' class='form-login' method='POST' action=''>"
                + navn.toString()
                + passord.toString()
                + properSubmit
                + "</form>";
        Anchor glemtPassord = new Anchor("Glemt Passord", "/glemtpassord", "anchor-login");
        Anchor nyBruker = new Anchor("Ny Bruker", "/nybruker", "anchor-login");
        html.addBodyContent(properForm + glemtPassord.toString() + nyBruker.toString());

        out.print(html.toString());
    }

    private String encodeString(String url) {
        String output = "";
        String[] split = url.split("/");
        for (String string : split) {
            try {
                output += URLEncoder.encode(string, "UTF-8") + "/";
            } catch (Exception e) {
                output += string + "/";
            }
        }
        return output;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Headers.POST(response);
        PrintWriter out = response.getWriter();
        try {
            SessionLogin login = new SessionLogin(request.getParameter("epost"), request.getParameter("passord"), request.getSession());
            if (login.validLogin()) {
                String url = (String) request.getSession().getAttribute("url");
                if (url == null) {
                    url = "/admin/profil/";
                }
                //out.println(url);
                //out.println(URLDecoder.decode(url, "UTF-8"));

                response.sendRedirect("https://logglogg.no" + encodeString(url));
            } else {

                File log = new File("/home/tarves/passwordFail.log");
                String userAgent = request.getHeader("user-agent");
                String epost = request.getParameter("epost");
                String remoteHost = request.getRemoteHost();
                String remotePort = Integer.toString(request.getRemotePort());
                BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
                bw.write(remoteHost);
                bw.write(" [");
                bw.write(dato());
                bw.write("] epost: " + epost);
                bw.write(" remotePort: " + remotePort);
                bw.write(" userAgent: " + userAgent);
                bw.newLine();

                bw.close();
                response.sendRedirect("https://logglogg.no?feil=1");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private String dato() {
        Calendar date = new GregorianCalendar();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date.getTime());
    }
}
