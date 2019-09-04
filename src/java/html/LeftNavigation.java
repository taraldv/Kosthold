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
public class LeftNavigation extends Element {

    private final ArrayList<Element> elements = new ArrayList<>();
    private final String textClass = "nav-text";
    private final String listClass = "nav-list";
    private final String anchorClass = "nav-anchor";

    public LeftNavigation() {
        super("nav-left");
        
        List statistikkList = new List(listClass);
        statistikkList.addElement(new Anchor("Kosthold", "/statistikk/kosthold/", anchorClass));
        elements.add(new Text("h2", "Statistikk", textClass));
        elements.add(statistikkList);

        List kostholdList = new List(listClass);
        kostholdList.addElement(new Anchor("Logg", "/kosthold/logg/", anchorClass));
        kostholdList.addElement(new Anchor("Måltider", "/kosthold/måltider/", anchorClass));
        kostholdList.addElement(new Anchor("Historikk", "/kosthold/historikk/", anchorClass));
        kostholdList.addElement(new Anchor("Matvaretabellen", "/kosthold/matvaretabellen/", anchorClass));
        elements.add(new Text("h2", "Kosthold", textClass));
        elements.add(kostholdList);

        List styrkeList = new List(listClass);
        styrkeList.addElement(new Anchor("Logg", "/styrke/logg/", anchorClass));
        styrkeList.addElement(new Anchor("Øvelser", "/styrke/øvelser/", anchorClass));
        styrkeList.addElement(new Anchor("Historikk", "/styrke/historikk/", anchorClass));
        elements.add(new Text("h2", "Styrke", textClass));
        elements.add(styrkeList);

        List kondisjonList = new List(listClass);
        kondisjonList.addElement(new Anchor("Logg", "/kondisjon/logg/", anchorClass));
        kondisjonList.addElement(new Anchor("Turer", "/kondisjon/turer/", anchorClass));
        kondisjonList.addElement(new Anchor("Historikk", "/kondisjon/historikk/", anchorClass));
        elements.add(new Text("h2", "Kondisjon", textClass));
        elements.add(kondisjonList);

        List helseList = new List(listClass);
        helseList.addElement(new Anchor("Logg", "/vekt/logg/", anchorClass));
        helseList.addElement(new Anchor("Historikk", "/vekt/historikk/", anchorClass));
        elements.add(new Text("h2", "Vekt", textClass));
        elements.add(helseList);
        
        List adminList = new List(listClass);
        adminList.addElement(new Anchor("Profil", "/admin/profil/", anchorClass));
        adminList.addElement(new Anchor("Kosthold", "/admin/kosthold/", anchorClass));
        adminList.addElement(new Anchor("Logg ut", "/admin/loggut/", anchorClass));
        elements.add(new Text("h2", "Admin", textClass));
        elements.add(adminList);

    }

    @Override
    public String toString() {
        String output = "<div " + getInfoString() + ">";

        for (Element element : elements) {
            if (element instanceof Text) {
                output += ((Text) element).toString();
            } else if (element instanceof List) {
                output += ((List) element).toString();
            }
        }

        return output += "</div>";
    }

}
