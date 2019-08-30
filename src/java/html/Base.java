/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package html;

/**
 *
 * @author
 */
public abstract class Base {

    private final String doctype = "<!DOCTYPE html lang='nb'>";
    private final String title;
    private final String viewport = "<meta name='viewport' content='width=device-width'/>";
    private final String mobileCSS;
    private final String normalCSS;
    private final String body;
    private final String js;

    public Base(String title, String mobileCSS, String normalCSS, String js, String body) {
        this.title = "<title>" + title + "</title>";
        this.mobileCSS = "<link rel='stylesheet' media='(max-width:600px)' type='text/css' href='" + mobileCSS + "'>";
        this.normalCSS = "<link rel='stylesheet' media='(min-width:600px)' type='text/css' href='" + normalCSS + "'>";
        this.js = "<script type='text/javascript' src='" + js + "'></script>";
        this.body = body;
    }

    @Override
    public String toString() {
        return doctype + "<head>" + mobileCSS + normalCSS + viewport + title + "</head><body>" + body + js + "</body></html>";
    }

}
