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
public class Text extends Element {

    private final String type;
    private final String string;

    public Text(String type, String string, String elementId, String elementClass) {
        super(elementId, elementClass);
        this.type = type;
        this.string = string;
    }
      public Text(String type, String string, String elementClass) {
        super(elementClass);
        this.type = type;
        this.string = string;
    }

    @Override
    public String toString() {
        return "<" + type + " " + getInfoString() + " >" + string + "</" + type + ">";
    }

}
