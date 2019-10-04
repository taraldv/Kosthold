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
    private boolean disabled;
    private int index = 1;

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

    public Select(String kolonneId, String tableName, String elementId, String elementClass, int index, boolean disabled) throws Exception {
        super(elementId, elementClass);
        this.kolonneId = kolonneId;
        this.tableName = tableName;
        this.index = index;
        this.disabled = disabled;
        optionClass = elementClass + "-option";
        String query = "SELECT " + kolonneId + ",navn FROM " + tableName + ";";
        getOptions(query);
    }

    private void getOptions(String query) throws Exception {
        String[][] data = Database.normalQuery(query).getData();
        //[[id,navn],[id,navn]] etc
        options = "";
        for (int i = 0; i < data.length; i++) {
            if ((i + 1) == index) {
                options += "<option selected class='" + optionClass + "' data-id='" + data[i][0] + "'>" + data[i][1] + "</option>";
            } else {
                options += "<option class='" + optionClass + "' data-id='" + data[i][0] + "'>" + data[i][1] + "</option>";
            }
        }
    }

    @Override
    public String toString() {
        if (disabled) {
            return "<select disabled " + getInfoString() + ">" + options + "</select>";
        }
        return "<select " + getInfoString() + ">" + options + "</select>";
    }

}
