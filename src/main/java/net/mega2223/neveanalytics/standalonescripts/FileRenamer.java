package net.mega2223.neveanalytics.standalonescripts;

import net.mega2223.neveanalytics.Constants;

import java.io.File;

import static net.mega2223.neveanalytics.standalonescripts.FileSorter.validFormats;

public class FileRenamer {
    //quando o bulk downloader do EarthExplorer falha, ele tenta de novo.
    //todavia ele não deleta o arquivo antigo, mas como ele baixa em arquivos parciais pra depois
    //transferir pro slot, o arquivo antigo fica vazio e o novo arquivo é nomeado com a convenção
    //"[nomenormal] (x).TIF", onde x é o número de tentativas + 1
    //isso zoa com o separador de arquivos que depende do nome dos arquivos pra implicar sua função
    //essa classe só tira o adendo na frente, uma vez que eu já filtrei e esvaziei os arquivos corrompidos

    public static void main(String[] args) {
        System.out.println("Hygienizing names");
        File root = new File(Constants.DATA_PATH);
        File[] files = root.listFiles();
        for (File act : files) {
            String[] name = act.getName().split("\\.");
            String format = name[name.length - 1];
            if (validFormats.contains(format)) {
                String n = act.getName();
                boolean hasAlteration = false;
                for (int j = 0; j < 10; j++) {
                    String target = " (" + j + ")";
                    hasAlteration = hasAlteration || n.contains(target);
                    n = n.replace(target, "");
                }
                if(hasAlteration){
                    act.renameTo(new File(Constants.DATA_PATH + "\\" + n));
                }
            }
        }
    }
}
