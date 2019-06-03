/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;

/**
 *
 * @author
 */
public class HTML {

    private final String title;
    private final ArrayList<String> css = new ArrayList<>();
    private final ArrayList<String> js = new ArrayList<>();
    private String body = "";

    public HTML(String title) {
        this.title = title;
    }

    public void addCSS(String href) {
        css.add("<link rel='stylesheet' media='(min-width:600px)' type='text/css' href='" + href + "'>");
    }

    public void addMobileCSS(String href) {
        css.add("<link rel='stylesheet' media='(max-width:600px)' type='text/css' href='" + href + "'>");
    }

    public void addJS(String href) {
        js.add("<script type='text/javascript' src='" + href + "'></script>");
    }

    public void addBody(String body) {
        this.body = body;
    }

    public void addStandard() {
        css.add("<link rel='stylesheet' media='(min-width:600px)' type='text/css' href='../../css/main.css'>");
        css.add("<link rel='stylesheet' media='(max-width:600px)' type='text/css' href='../../css/mobile.css'>");
        js.add("<script type='text/javascript' src='../../js/main.js'></script>");
    }

    @Override
    public String toString() {
        String output = "<!DOCTYPE html lang='nb'>"
                + "<html>"
                + "<head>"
                + "<title>" + title + "</title>"
                + "<meta charset='UTF-8'>";

        for (int i = 0; i < css.size(); i++) {
            output += css.get(i);
        }

        output += "</head>"
                + "<body>"
                + body;

        for (int j = 0; j < js.size(); j++) {
            output += js.get(j);
        }

        output += "</body>"
                + "</html>";

        return output;
    }

}
