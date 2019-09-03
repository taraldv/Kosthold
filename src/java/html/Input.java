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
    private String autocomplete = "off";
    private String name;
    private String step = "";

    public Input(String placeholder, String label, String inputType, String elementId, String elementClass) {
        super(elementId, elementClass);
        this.placeholder = placeholder;
        this.label = label;
        this.inputType = inputType;
        this.labelClass = elementClass + "-label";
    }

    public Input(String placeholder, String label, String inputType, String elementId, String elementClass, String step) {
        super(elementId, elementClass);
        this.placeholder = placeholder;
        this.label = label;
        this.inputType = inputType;
        this.labelClass = elementClass + "-label";
        this.step = step;
    }

    public Input(String placeholder, String label, String inputType, String elementId, String elementClass, String name, String autocomplete) {
        super(elementId, elementClass);
        this.placeholder = placeholder;
        this.label = label;
        this.inputType = inputType;
        this.labelClass = elementClass + "-label";
        this.name = name;
        this.autocomplete = autocomplete;
    }

    @Override
    public String toString() {
        return "<label class='" + labelClass + "'>" + label + "<input " + getInfoString() + " "
                + "placeholder='" + placeholder + "' "
                + "type='" + inputType + "' "
                + "name='" + name + "' "
                + "step='" + step + "' "
                + "autocomplete='" + autocomplete + "'"
                + "></label>";
    }

}
