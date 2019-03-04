/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crypto;

import java.util.Random;
/**
 *
 * @author Tarald
 */
public class ResetToken {


    /* 48 til 57, eller 65 til 90, eller 97 til 122 */
    public static String generateToken() {
        String token = "";
        Random ran = new Random();
        int min = 48;
        int range = 74;
        for (int i = 0; i < 140; i++) {
            /* next int genererer random fra 0 til 74 + 48 dvs. fra 48 til 122 */
            char tilfeldigChar = (char) (ran.nextInt(range) + min);
            /* ugyldig chars er  58-64,91-96*/
            while (tilfeldigChar <= 64 && tilfeldigChar >= 58 || tilfeldigChar <= 96 && tilfeldigChar >= 91) {
                tilfeldigChar = (char) (ran.nextInt(range) + min);
            }
            token += tilfeldigChar;
        }
        return token;
    }

}
