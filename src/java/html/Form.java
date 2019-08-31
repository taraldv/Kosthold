/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package html;

import java.util.ArrayList;

/**
 *
 * @author
 */
public class Form extends Element {

    //select x inputs div for submit
    private final ArrayList<Element> elements = new ArrayList<>();

    public Form(String elementId, String elementClass) {
        super(elementId, elementClass);
    }

    public void addElement(Element e) {
        elements.add(e);
    }

    @Override
    public String toString() {
        String output = "<div "+getInfoString()+">";

        //enten div, select eller input
        for (Element element : elements) {
            if (element instanceof Div) {
                output += ((Div) element).toString();
            } else if (element instanceof Input) {
                output += ((Input) element).toString();
            } else if (element instanceof Select) {
                output += ((Select) element).toString();
            }
        }
        return output += "</div>";
    }

}
