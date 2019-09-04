/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package html;

import util.sql.Database;

/**
 *
 * @author
 */
public class Select extends Element {

    private String options;
    private final String kolonneId;
    private final String tableName;
    private final String optionClass;
    private int brukerId;

    public Select(String kolonneId, String tableName, int brukerId, String elementId, String elementClass) throws Exception {
        super(elementId, elementClass);
        this.kolonneId = kolonneId;
        this.tableName = tableName;
        this.brukerId = brukerId;
        optionClass = elementClass + "-option";
        String query = "SELECT " + kolonneId + ",navn FROM " + tableName + " WHERE brukerId = " + brukerId + " ORDER BY navn;";
        getOptions(query);
    }

    public Select(String kolonneId, String tableName, String elementId, String elementClass) throws Exception {
        super(elementId, elementClass);
        this.kolonneId = kolonneId;
        this.tableName = tableName;
        optionClass = elementClass + "-option";
        String query = "SELECT " + kolonneId + ",navn FROM " + tableName + ";";
        getOptions(query);
    }

    private void getOptions(String query) throws Exception {
        String[][] data = Database.normalQuery(query).getData();
        //[[id,navn],[id,navn]] etc
        options = "";
        for (String[] strings : data) {
            options += "<option class='" + optionClass + "' data-id='" + strings[0] + "'>" + strings[1] + "</option>";
        }
    }

    @Override
    public String toString() {
        return "<select " + getInfoString() + ">" + options + "</select>";
    }

}
