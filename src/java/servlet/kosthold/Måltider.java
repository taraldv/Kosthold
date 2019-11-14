/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.kosthold;

import crypto.ValidSession;
import html.Div;
import html.DivForm;
import html.Input;
import html.StandardHtml;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.sql.Database;
import util.http.Headers;
import util.sql.ResultSetContainer;

/**
 *
 * @author Tarald
 */
public class Måltider extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Headers.GET(resp);
        ValidSession.isValid(req, resp);
        PrintWriter out = resp.getWriter();
        try {
            StandardHtml html = new StandardHtml("Kosthold Måltider");
            DivForm form = getMåltiderForm();
            Div div = new Div("", "måltiderTabell", "div-table");
            Div containerDiv = new Div(form.toString() + div.toString(), "div-container");
            html.addBodyContent(containerDiv.toString());
            html.addBodyJS("test('måltiderTabell','/kosthold/måltider/');");
            html.addBodyJS("attachAutocompleteToClass('autocompleteInput');");
            //html.addBodyJS("autocomplete();");
            String tableArr = "['getMåltiderTabell','måltiderTabell','/kosthold/måltider/']";
            String deleteArr = "['deleteMatvare','matvareId','/kosthold/matvaretabellen/']";
            //html.addBodyJS("buildTable(" + tableArr + "," + deleteArr + ",0);");
            String paramArray = "['måltidNavn']";
            html.addBodyJS("insertRequest('måltiderSubmit','insertMåltider','/kosthold/måltider/'," + paramArray + "," + tableArr + "," + deleteArr + ",0);");
            // html.addBodyJS("attachServerRequestToButton('getDiv','ekstraInnhold','/kosthold/matvaretabellen/','matvaretabellForm')");
            out.print(html.toString());
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private DivForm getMåltiderForm() throws Exception {
        DivForm form = new DivForm("matvaretabellForm", "div-form");
        form.addElement(new Input("Navn", "Måltid navn", "text", "kostholdMåltiderNavnInput", "input"));
        form.addElement(new Div("Submit", "måltiderSubmit", "submit"));

        form.addElement(customInputDiv());
        form.addElement(customInputDiv());
        form.addElement(customInputDiv());
        form.addElement(customInputDiv());
        form.addElement(customInputDiv());

        return form;
    }

    private Div customInputDiv() {
        Input matvare = new Input("Matvare", "Matvare", "text", "", "input autocompleteInput");
        Input mengde = new Input("Mengde", "Mengde", "number", "", "input", "0.1");
        Div d = new Div(matvare.toString() + mengde.toString(), "måltiderInputDiv");
        return d;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Headers.POST(response);
        ValidSession.isValid(request, response);
        PrintWriter out = response.getWriter();
        String type = request.getParameter("type");
        /* stopper request hvis ugylid session */
        try {
            int brukerId = (int) request.getSession().getAttribute("brukerId");
            if (type.equals("insertMåltider")) {
                out.print(insertMåltider(brukerId, request.getParameter("måltidNavn"), request.getParameterMap()));
            } else if (type.equals("getMåltider")) {
                out.print(getMåltider(brukerId));
            } else if (type.equals("getMåltiderIngredienser")) {
                out.print(getMåltiderIngredienser(brukerId, Integer.parseInt(request.getParameter("måltidId"))));
            } else if (type.equals("getMåltiderTabell")) {
                out.print(getMåltiderTabell(brukerId));
            } else if (type.equals("deleteMåltider")) {
                out.print(deleteMåltider(brukerId, Integer.parseInt(request.getParameter("måltidId"))));
            } else if (type.equals("updateMåltider")) {
                out.print(updateMåltider(brukerId,
                        Integer.parseInt(request.getParameter("matvareMengde")),
                        Integer.parseInt(request.getParameter("matvareId")),
                        Integer.parseInt(request.getParameter("måltidId"))));
            } else if (type.equals("insertMåltiderIngredienser")) {
                out.print(insertMåltiderIngredienser(brukerId, Integer.parseInt(request.getParameter("måltidId"))));
            } else if (type.equals("deleteMåltidIngrediens")) {
                out.print(deleteMåltidIngrediens(brukerId,
                        //  Integer.parseInt(request.getParameter("måltidId")),
                        Integer.parseInt(request.getParameter("ingredienseId"))));
            } else if (type.equals("updateIngrediensMengde")) {
                out.print(updateIngrediensMengde(brukerId,
                        Integer.parseInt(request.getParameter("måltidId")),
                        Integer.parseInt(request.getParameter("ingredienseId")),
                        Integer.parseInt(request.getParameter("mengde"))));
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

    private int updateIngrediensMengde(int brukerId, int måltidId, int ingredienseId, int mengde) throws Exception {
        String query = "UPDATE ingredienser SET mengde = ? WHERE ingredienseId = ? AND måltidId = ? AND brukerId=" + brukerId + ";";
        return Database.singleUpdateQuery(query, new Object[]{mengde, ingredienseId, måltidId}, false);

    }

    private int deleteMåltider(int brukerId, int måltidId) throws Exception {
        //stored procedure som sjekker om gyldig bruker/måltid, sletter alle ingredienser til måltid og sletter selve måltid.
        //returnerer antall slettet rader
        String query = "CALL slettMåltid(?,?,?);";
        return Database.callProcedure(query, new Object[]{måltidId, brukerId, "@rader"});
    }

    private int deleteMåltidIngrediens(int brukerId, int ingredienseId) throws Exception {
        String query = "DELETE FROM ingredienser WHERE ingredienseId = ? AND brukerId = " + brukerId + ";";
        return Database.singleUpdateQuery(query, new Object[]{ingredienseId}, false);
    }

    private int updateMåltider(int brukerId, int mengde, int matvareId, int måltidId) throws Exception {
        //verifiserer at bruker eier valgt måltid
        String måltidQuery = "SELECT 1 FROM måltider WHERE brukerId = ? AND måltidId = ?;";
        //burde bli 1
        int validMåltid = Integer.parseInt(Database.multiQuery(måltidQuery, new Object[]{brukerId, måltidId}).getData()[0][0]);
        if (validMåltid == 1) {
            String query = "INSERT INTO ingredienser (brukerId,matvareId,måltidId,mengde) VALUES (" + brukerId + ",?,?,?)";
            return Database.singleUpdateQuery(query, new Object[]{matvareId, måltidId, mengde}, false);
        } else {
            return 0;
        }
    }

    /* Blir brukt av kosthold logg, kanskje endre navn eller flytte? */
    private int insertMåltiderIngredienser(int brukerId, int måltidId) throws Exception {
        String query = "SELECT matvareId,mengde FROM ingredienser WHERE måltidId = ?;";
        //arr = [[id,mengde],[id,mengde]] etc...
        String[][] arr = Database.multiQuery(query, new Object[]{måltidId}).getData();

        //vars bindes til ? en etter en, dvs. første vars obj bør være id, neste mengde etc....
        Object[] vars = new Object[2 * arr.length];
        String baseline = "INSERT INTO logg(dato,matvareId,mengde,brukerId) VALUES ";
        String row = "";
        for (int i = 0; i < arr.length; i++) {
            //id på plass 0,2,4,6 etc
            vars[2 * i] = Integer.parseInt(arr[i][0]);
            //mengde på plass 1,3,5, etc
            vars[(2 * i) + 1] = Double.parseDouble(arr[i][1]);
            if (i != 0) {
                row += ",";
            }
            row += "(CURDATE(),?,?," + brukerId + ")";
        }
        return Database.singleUpdateQuery(baseline + row, vars, false);
    }

    //git ikke måltider uten ingredienser, bør ikke være lov å lage måltid uten ingredienser
    private String getMåltiderTabell(int brukerId) throws Exception {
        String query = "SELECT m.måltidId,m.navn,i.ingredienseId,t.matvare,i.mengde FROM måltider m "
                + "RIGHT JOIN ingredienser i ON i.måltidId = m.måltidId "
                + "LEFT JOIN matvaretabellen t ON t.matvareId = i.matvareId "
                + "WHERE m.brukerId = ?;";
        return Database.multiQuery(query, new Object[]{brukerId}).getJSON();
    }

    private String getMåltiderIngredienser(int brukerId, int måltidId) throws Exception {
        String getMåltiderIngredienserQuery = "SELECT i.matvareId,m.matvare,mengde FROM ingredienser i"
                + " LEFT JOIN matvaretabellen m ON i.matvareId = m.matvareId"
                + " WHERE måltidId = ? AND i.brukerId = " + brukerId + ";";
        return Database.multiQuery(getMåltiderIngredienserQuery, new Object[]{måltidId}).getJSON();
    }

    private String getMåltider(int brukerId) throws Exception {
        String query = "SELECT måltidId,navn FROM måltider WHERE brukerId = " + brukerId + ";";
        return Database.normalQuery(query).getJSON();
    }

    /* Brukes til å filtrere empty strings fra paramter map, og gjøre matvare navn */
    private Object[] getIdFromStringArray(String[][] arr) throws SQLException, ClassNotFoundException {
        String[] navnOgVerdi = arr[2];

        ArrayList<Object> objList = new ArrayList<>();
        String query = "SELECT matvareId FROM matvaretabellen WHERE matvare = ?;";

        for (int i = 0; i < navnOgVerdi.length; i = i + 2) {
            String tempNavn = navnOgVerdi[i];
            ResultSetContainer rsc = Database.multiQuery(query, new Object[]{tempNavn});
            try {
                Integer id = Integer.parseInt(rsc.getData()[0][0]);
                Double verdi = Double.parseDouble(navnOgVerdi[i + 1]);
                objList.add(id);
                objList.add(verdi);
            } catch (ArrayIndexOutOfBoundsException e) {

            }
        }
        return objList.toArray();
    }

    /* TODO atomisk */
    private int insertMåltider(int brukerId, String navn, Map<String, String[]> map) throws Exception {
        int lastInsertedId = Database.singleUpdateQuery("INSERT INTO måltider(navn,brukerId) VALUES (?," + brukerId + ");", new Object[]{navn}, true);
        String[][] arr = map.values().toArray(new String[0][0]);
        //return Arrays.deepToString(arr);
        /* arr inneholder [[type][navn][id,id,id....][verdi,verdi,verdi.....]] */
        // Object[] vars = new Object[arr[2].length * 2];
        Object[] vars = getIdFromStringArray(arr);
        String baseline = "INSERT INTO ingredienser(brukerId,måltidId, matvareId, mengde) VALUES ";
        String row = "";
        for (int i = 0; i < vars.length / 2; i++) {
            /*vars[2 * i] = Integer.parseInt(arr[2][i]);
            vars[(2 * i) + 1] = Double.parseDouble(arr[3][i]);*/
            if (i != 0) {
                row += ",";
            }
            row += "(" + brukerId + "," + lastInsertedId + ",?,?)";
        }
        return Database.singleUpdateQuery(baseline + row, vars, false);
    }
}
