package CLI;
import java.io.*;
import java.util.*;

public class parser{
    private int lebar;
    private int panjang;
    private int jmlblok;
    private String tipeKasus;
    private List<char[][]> blocks;

    public parser(String namaFile) throws IOException{
        System.out.println("Membaca file: " + namaFile);
        blocks = new ArrayList<>();
        parseFile(namaFile);
    }
    
    private List<Character> hurufs = new ArrayList<>();

    private void parseFile(String namaFile) throws IOException {
        Scanner scanner = new Scanner(new File(namaFile));
        lebar = scanner.nextInt();
        panjang = scanner.nextInt();
        jmlblok = scanner.nextInt();
        scanner.nextLine();
        tipeKasus = scanner.nextLine();
        
        int currentBlock = 0;
        String nextLine = scanner.nextLine();
        
        while(currentBlock < jmlblok) {
            List<String> blockLines = new ArrayList<>();
            blockLines.add(nextLine);
            String firstChar = nextLine.trim().substring(0, 1);
            hurufs.add(firstChar.charAt(0));
            
            while(scanner.hasNextLine()) {
                nextLine = scanner.nextLine();
                String nextFirstChar = nextLine.trim().substring(0, 1);
                if(!nextFirstChar.equals(firstChar)) {
                    break;
                }
                blockLines.add(nextLine);
            }
            
            blocks.add(matrixin(blockLines));
            currentBlock++;
        }
        scanner.close();
    }
    

    private char[][] matrixin(List<String> blines) {
        int rows = blines.size();
        int maxCols = 0;
        for (String line : blines) {
            maxCols = Math.max(maxCols, line.length());
        }
        
        char[][] blockMat = new char[rows][maxCols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < maxCols; j++) {
                blockMat[i][j] = '.';
            }
        }
        
        for (int i = 0; i < rows; i++) {
            char[] lineChars = blines.get(i).toCharArray();
            for (int j = 0; j < lineChars.length; j++) {
                blockMat[i][j] = (lineChars[j] == ' ') ? '.' : lineChars[j];
            }
        }
        
        return blockMat;
    }
    // getter
    public int getLebar() { return lebar; }
    public int getPanjang() { return panjang; }
    public int getJmlblok() { return jmlblok; }
    public String getTipeKasus() { return tipeKasus; }
    public List<char[][]> getBlocks() { return blocks; }
    public List<Character> getHurufs() { return hurufs;}
}