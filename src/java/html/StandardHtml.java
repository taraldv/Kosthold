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
public class StandardHtml extends Base {

    public StandardHtml(String title) {
        super(title,
                "../../css/newMobile.css",
                "../../css/new.css",
                "../../js/LoggLogg.js",
                new LeftNavigation().toString());
        addBodyJS("toggleMenuHide('hideMenuButton','toggleHide','mobile-hide');");
    }

}
