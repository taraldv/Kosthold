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
public class List extends Element {

    private final ArrayList<Element> elements = new ArrayList<>();

    public List(String elementClass) {
        super(elementClass);
    }

    public void addElement(Element e) {
        elements.add(e);
    }

    @Override
    public String toString() {
        String output = "<ul " + getInfoString() + ">";
        for (Element element : elements) {
            if (element instanceof Anchor) {
                output += "<li>" + ((Anchor) element).toString() + "</li>";
            }
        }
        return output += "</ul>";
    }

}
