/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import crypto.SessionLogin;
import html.Anchor;
import html.ErrorHandling;
import html.IndexHtml;
import html.Input;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.exceptions.EpostAktivertException;
import util.exceptions.LoginFormInputEmptyException;
import util.exceptions.LoginPasswordException;
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
        ErrorHandling errorHandling = new ErrorHandling(request);
        //ValidSession vs = new ValidSession(request, response);

        IndexHtml html = new IndexHtml("LoggLogg");

        Input navn = new Input("Skriv inn epost her", "Epost", "text", "brukernavnInput", "input-login", "epost", "on");
        Input passord = new Input("Skriv inn passord her", "Passord", "password", "passordInput", "input-login", "passord", "on");
        String properSubmit = "<input id='loginSubmitInput' class='input-login' type='submit' value='logg inn'>";
        String properForm = "<form id='loginForm' class='form-login' method='POST' action=''>"
                + navn.toString()
                + passord.toString()
                + properSubmit
                + "</form>";
        Anchor glemtPassord = new Anchor("Glemt Passord", "/glemtpassord", "anchor-login");
        Anchor nyBruker = new Anchor("Ny Bruker", "/nybruker", "anchor-login");
        html.addBodyContent(properForm + glemtPassord.toString() + nyBruker.toString());
        html.addBodyContent(errorHandling.toString());
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
        SessionLogin login = new SessionLogin(request.getParameter("epost"), request.getParameter("passord"), request.getSession());
        try {
            login.checkPassword();

            String url = (String) request.getSession().getAttribute("url");
            if (url == null) {
                url = "/admin/profil/";
            }
            //out.println(url);
            //out.println(URLDecoder.decode(url, "UTF-8"));

            response.sendRedirect("https://logglogg.no" + encodeString(url));

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(out);
        } catch (EpostAktivertException e) {
            response.sendRedirect("https://logglogg.no?error=5");
        } catch (LoginFormInputEmptyException e) {
            response.sendRedirect("https://logglogg.no?error=6");
        } catch (LoginPasswordException e) {
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
            response.sendRedirect("https://logglogg.no?error=1");
        }
    }

    private String dato() {
        /* fail2ban regex <HOST> 5 attempts*/
        Calendar date = new GregorianCalendar();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date.getTime());
    }
}
