/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.wta.mfxloto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author w_tel
 */
public class NewClass {

    public static void main(String args[]) {
        String[][] resu = new String[1947][8];
        try {
            BufferedReader bf = new BufferedReader(new FileReader("D:\\w_tel\\OneDrive\\Documentos\\Megasena\\megasena_prototipo.csv"));
            String line = null;
            int i = 0;
            while ((line = bf.readLine()) != null) {
                String[] tokens = line.split(",");
                for (int j = 0; j < tokens.length; j++) {
                    resu[i][j] = tokens[j];
                }
                i++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringBuilder sb = new StringBuilder();
        String nsorteado = "?,";
        String sorteado = "t,";
        String[] array = new String[60];
        for (int i = 0; i < resu.length; i++) {
            for (int j = 0; j < array.length; j++) {
                array[j] = nsorteado;
                int comp = j + 1;
                for (int k = 0; k < resu[i].length - 2; k++) {
                    int dezena = Integer.parseInt(resu[i][k]);
                    if (comp == dezena) {
                        array[j] = sorteado;
                    }
                }
                sb.append(array[j]);
            }
            sb.append("'" + resu[i][6] + "',");
            sb.append(resu[i][7]);
            sb.append(System.lineSeparator());
        }
        System.out.println(sb);
        File file = new File("D:\\w_tel\\OneDrive\\Documentos\\Megasena\\teste.txt");
        try {
            FileUtils.writeStringToFile(file,sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
