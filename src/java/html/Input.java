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
public class Input extends Element {

    private final String placeholder;
    private final String label;
    private final String labelClass;
    private final String inputType;

    public Input(String placeholder, String label, String inputType, String elementId, String elementClass) {
        super(elementId, elementClass);
        this.placeholder = placeholder;
        this.label = label;
        this.inputType = inputType;
        this.labelClass = elementClass + "-label";
    }

    @Override
    public String toString() {
        return "<label class='" + labelClass + "'>" + label + "<input " + getInfoString() + " "
                + "placeholder='" + placeholder + "' "
                + "type='" + inputType + "' "
                + "autocomplete='off'></label>";
    }

}
