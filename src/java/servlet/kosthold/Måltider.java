/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.kosthold;

import crypto.ValidSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.database.Kosthold;
import util.http.StandardResponse;

/**
 *
 * @author Tarald
 */
public class Måltider extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StandardResponse sr = new StandardResponse(response);
        PrintWriter out = sr.getWriter();
        ValidSession vs = new ValidSession(out, request.getSession());
        String type = request.getParameter("type");
        /* stopper request hvis ugylid session */
        if (!vs.validateSession()) {
            return;
        }
        int brukerId = vs.getId();
        try {
            if (type.equals("insertMåltider")) {
                out.print(insertMåltider(brukerId, request.getParameter("navn"), request.getParameterMap()));
            } else if (type.equals("getMåltider")) {
                out.print(Kosthold.normalQuery("SELECT * FROM måltider WHERE brukerId = " + brukerId + ";").getJSON());
            } else if (type.equals("getMåltiderIngredienser")) {
                //erstattet av insert
                //out.print(getMåltiderIngredienser(Integer.parseInt(request.getParameter("måltidId"))));
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
                        Integer.parseInt(request.getParameter("måltidId")),
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
        String måltidQuery = "SELECT 1 FROM måltider WHERE brukerId = ? AND måltidId = ?;";
        //burde bli 1
        int validMåltid = Integer.parseInt(Kosthold.multiQuery(måltidQuery, new Object[]{brukerId, måltidId}).getData()[0][0]);
        if (validMåltid == 1) {
            String query = "UPDATE ingredienser SET mengde = ? WHERE ingredienseId = ? AND måltidId = ?";
            return Kosthold.singleUpdateQuery(query, new Object[]{mengde, ingredienseId, måltidId}, false);
        } else {
            return 0;
        }
    }

    private int deleteMåltider(int brukerId, int måltidId) throws Exception {
        //stored procedure som sjekker om gyldig bruker/måltid, sletter alle ingredienser til måltid og sletter selve måltid.
        //returnerer antall slettet rader
        String query = "CALL slettMåltid(?,?,?);";
        return Kosthold.callProcedure(query, new Object[]{måltidId, brukerId, "@rader"});
    }

    private int deleteMåltidIngrediens(int brukerId, int måltidId, int ingredienseId) throws Exception {
        //verifiserer at bruker eier valgt måltid
        String måltidQuery = "SELECT 1 FROM måltider WHERE brukerId = ? AND måltidId = ?;";
        //burde bli 1
        int validMåltid = Integer.parseInt(Kosthold.multiQuery(måltidQuery, new Object[]{brukerId, måltidId}).getData()[0][0]);
        if (validMåltid == 1) {
            String query = "DELETE FROM ingredienser WHERE ingredienseId = ? AND måltidId = ?";
            return Kosthold.singleUpdateQuery(query, new Object[]{ingredienseId, måltidId}, false);
        } else {
            return 0;
        }
    }

    private int updateMåltider(int brukerId, int mengde, int matvareId, int måltidId) throws Exception {
        //verifiserer at bruker eier valgt måltid
        String måltidQuery = "SELECT 1 FROM måltider WHERE brukerId = ? AND måltidId = ?;";
        //burde bli 1
        int validMåltid = Integer.parseInt(Kosthold.multiQuery(måltidQuery, new Object[]{brukerId, måltidId}).getData()[0][0]);
        if (validMåltid == 1) {
            String query = "INSERT INTO ingredienser (matvareId,måltidId,mengde) VALUES (?,?,?)";
            return Kosthold.singleUpdateQuery(query, new Object[]{matvareId, måltidId, mengde}, false);
        } else {
            return 0;
        }
    }

    private int insertMåltiderIngredienser(int brukerId, int måltidId) throws Exception {
        String query = "SELECT matvareId,mengde FROM ingredienser WHERE måltidId = ?;";
        //arr = [[id,mengde],[id,mengde]] etc...
        String[][] arr = Kosthold.multiQuery(query, new Object[]{måltidId}).getData();

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
        return Kosthold.singleUpdateQuery(baseline + row, vars, false);
    }

    private String getMåltiderTabell(int brukerId) throws Exception {
        String query = "SELECT m.måltidId,m.navn,i.ingredienseId,t.matvare,i.mengde FROM måltider m "
                + "RIGHT JOIN ingredienser i ON i.måltidId = m.måltidId "
                + "LEFT JOIN matvaretabellen t ON t.matvareId = i.matvareId "
                + "WHERE m.brukerId = ?;";
        return Kosthold.multiQuery(query, new Object[]{brukerId}).getJSON();
    }

    private String getMåltiderIngredienser(int måltidId) throws Exception {
        String getMåltiderIngredienserQuery = "SELECT m.matvare,i.matvareId,mengde FROM ingredienser i"
                + " LEFT JOIN matvaretabellen m ON i.matvareId = m.matvareId"
                + " WHERE måltidId = ?;";
        return Kosthold.multiQuery(getMåltiderIngredienserQuery, new Object[]{måltidId}).getJSON();
    }

    private int insertMåltider(int brukerId, String navn, Map<String, String[]> map) throws Exception {
        int lastInsertedId = Kosthold.singleUpdateQuery("INSERT INTO måltider(navn,brukerId) VALUES (?," + brukerId + ");", new Object[]{navn}, true);
        String[][] arr = map.values().toArray(new String[0][0]);
        /* arr inneholder [[type][navn][id,id,id....][verdi,verdi,verdi.....]] */
        Object[] vars = new Object[arr[2].length * 2];
        String baseline = "INSERT INTO ingredienser(måltidId, matvareId, mengde) VALUES ";
        String row = "";
        for (int i = 0; i < arr[2].length; i++) {
            vars[2 * i] = Integer.parseInt(arr[2][i]);
            vars[(2 * i) + 1] = Double.parseDouble(arr[3][i]);
            if (i != 0) {
                row += ",";
            }
            row += "(" + lastInsertedId + ",?,?)";
        }
        return Kosthold.singleUpdateQuery(baseline + row, vars, false);
    }
}
