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
public abstract class Element {

    private final String elementId;
    private final String elementClass;

    public Element(String elementId, String elementClass) {
        this.elementId = elementId;
        this.elementClass = elementClass;
    }

    public Element(String elementClass) {
        this.elementClass = elementClass;
        elementId = "";
    }

    protected String getInfoString() {
        return "class='" + elementClass + "' id='" + elementId + "'";
    }

}
