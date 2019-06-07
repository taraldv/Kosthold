/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.http;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Tarald
 */
public class Headers {

    public static void POST(HttpServletResponse resp){
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setContentType("application/json;charset=UTF-8");
    }
    
    public static void GET(HttpServletResponse resp){
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setContentType("text/html;charset=UTF-8");
    }

}
