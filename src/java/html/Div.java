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
public class Div extends Element {

    private final String content;

    public Div(String content, String elementId, String elementClass) {
        super(elementId, elementClass);
        this.content = content;
    }

    public Div(String content, String elementClass) {
        super(elementClass);
        this.content = content;
    }

    @Override
    public String toString() {
        return "<div " + getInfoString() + ">" + content + "</div>";
    }

}
