/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.wta.mfxloto.util;

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
public class Converter {

    private void gerarCombinacoesArquivo(File folder) {
        File file = new File("D:\\w_tel\\OneDrive\\Documentos\\Megasena\\Comb\\saida.txt");
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.canRead()) {
                String path = fileEntry.getAbsolutePath();
                try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(" ");
                        String nsorteado = "?,";
                        String sorteado = "t,";
                        String[] array = new String[60];
                        for (int i = 0; i < array.length; i++) {
                            array[i] = nsorteado;
                            for (int j = 0; j < values.length; j++) {
                                int comp = i + 1;
                                int dezena = Integer.parseInt(values[j]);
                                if (comp == dezena) {
                                    array[i] = sorteado;
                                }
                            }
                            sb.append(array[i]);
                        }
                        sb.append("AC");
                        sb.append(System.lineSeparator());
                    }
                    FileUtils.writeStringToFile(file, sb.toString(), true);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println(fileEntry.getName());
            }
        }
    }

    private void gerarCombinacoesInline(File path) {
        String[][] resu = new String[1947][8];
        try {
            BufferedReader bf = new BufferedReader(new FileReader(path));
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
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
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
            FileUtils.writeStringToFile(file, sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        //File pathInLine = new File("D:\\w_tel\\OneDrive\\Documentos\\Megasena\\megasena_prototipo.csv");
        File pathOutR = new File("D:\\w_tel\\OneDrive\\Documentos\\Megasena\\Comb");
        Converter nc = new Converter();
        //nc.gerarCombinacoesInline(pathInLine);
        nc.gerarCombinacoesArquivo(pathOutR);
    }
}
