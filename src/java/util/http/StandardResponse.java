/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.http;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Tarald
 */
public class StandardResponse {

    HttpServletResponse response;

    public StandardResponse(HttpServletResponse response) {
        this.response = response;
        applyHeaders();
    }

    private void applyHeaders() {
        response.setContentType("application/json;charset=UTF-8");
        //response.setHeader("Access-Control-Allow-Origin", "https://logglogg.no");
        
    }

    public PrintWriter getWriter() throws IOException {
        return response.getWriter();
    }

    public void sendRedirect(String url) throws IOException {
        response.sendRedirect(url);
    }

}
