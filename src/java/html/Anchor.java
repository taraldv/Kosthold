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
public class Anchor extends Element {

    private final String content;
    private final String href;

    public Anchor(String content, String href, String elementClass) {
        super(elementClass);
        this.content = content;
        this.href = href;
    }

    @Override
    public String toString() {
        return "<a " + getInfoString() + " href='" + href + "'>" + content + "</a>";
    }

}
