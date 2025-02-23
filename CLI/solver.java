package CLI;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class solver {
    private static char[][] board;
    private static List<char[][]> blocks;
    private static int lebar, panjang, jmlblok;
    private static String tipeKasus;
    private static int kasusDitinjau = 0;
    private static long waktuMulai;
    private static List<Character> hurufs;


    public static void setBoardAndBlocks(char[][] newBoard, List<char[][]> newBlocks) {
        board = newBoard;
        blocks = newBlocks;
    }
    
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Masukkan nama file: ");
            String filename = scanner.nextLine();
            
            if (!filename.toLowerCase().endsWith(".txt")) {
                System.out.println("File tidak valid. File harus berformat .txt");
                return;
            }
            File file = new File(filename);
            if (!file.exists()) {
                System.out.println("File tidak ditemukan di directory Tucil 1 IQ Puzzler Pro");
                return;
            }
    
            parser p = new parser(filename);
            hurufs = p.getHurufs();
            solver.lebar = p.getLebar();
            solver.panjang = p.getPanjang();
            solver.jmlblok = p.getJmlblok();
            solver.blocks = p.getBlocks();
            solver.tipeKasus = p.getTipeKasus();
            
            System.out.println("Ukuran Papan: " + lebar + " x " + panjang);
            System.out.println("Jumlah Blok: " + jmlblok);
            System.out.println("Tipe Kasus: " + tipeKasus);
            System.out.println("\nBlok Puzzle:");
            
            for (int i = 0; i < blocks.size(); i++) {
                System.out.println("Blok ke-" + (i + 1) + ":");
                cetakBlock(blocks.get(i));
                System.out.println();
            }

            board = new char[panjang][lebar];
            for (int i = 0; i < panjang; i++) {
                for (int j = 0; j < lebar; j++) {
                    board[i][j] = '.';
                }
            }
    
            waktuMulai = System.currentTimeMillis();
            if (!solve(0)) {
                System.out.println("Tidak ada solusinya.");
            }
        }
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void cetakBlock(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                char c = board[i][j];
                if (c != '.') {
                    String color = COLORS[(c - 'A')% COLORS.length];
                    System.out.print(color + c + RESET + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }
    
    private static char[][] rotate90(char[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        char[][] rotated = new char[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                rotated[j][m - 1 - i] = matrix[i][j];
            }
        }
        return rotated;
    }

    private static char[][] rotate(char[][] matrix, int degree) {
        char[][] result = matrix;
        for (int i = 0; i < degree / 90; i++) {
            result = rotate90(result);
        }
        return result;
    }

    private static char[][] flipHor(char[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        char[][] flipped = new char[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                flipped[i][j] = matrix[i][n - 1 - j];
            }
        }
        return flipped;
    }

    private static char[][] flipVer(char[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        char[][] flipped = new char[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                flipped[m - 1 - i][j] = matrix[i][j];
            }
        }
        return flipped;
    }

    public static boolean solve(int index) {
        if (index == blocks.size()) {
            if (!isBoardFull()) {
                return false;
            }
            long waktuAkhir = System.currentTimeMillis();
            System.out.println("Solusi ditemukan: ");
            cetakBlock(board);
            System.out.println("Waktu pencarian: " + (waktuAkhir - waktuMulai) + " ms");
            System.out.println("Banyak kasus yang ditinjau: " + kasusDitinjau);
            
            Scanner scanner = new Scanner(System.in);
            System.out.print("Apakah anda ingin menyimpan solusi? (ya/tidak): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("ya")) {
                try (FileWriter writer = new FileWriter("solution.txt")) {
                    for (int i = 0; i < board.length; i++) {
                        for (int j = 0; j < board[i].length; j++) {
                            writer.write(board[i][j] + " ");
                        }
                        writer.write("\n");
                    }
                    System.out.println("Solusi berhasil disimpan ke solution.txt");
                    image(board, "solution.png");
                } catch (IOException e) {
                    System.out.println("Gagal menyimpan solusi: " + e.getMessage());
                }
            }
            return true;
        }
    
        char[][] currentBlock = blocks.get(index);
        char blockChar = hurufs.get(index);
    
        for (int i = 0; i < 4; i++) {
            char[][] rotatedBlock = rotate(currentBlock, i*90);
            for (int j = 0; j < 2; j++) {
                char[][] flippedBlock = (j == 1) ? flipHor(rotatedBlock) : rotatedBlock;
                for (int k = 0; k < 2; k++) {
                    char[][] finalBlock = (k == 1) ? flipVer(flippedBlock) : flippedBlock;
                    for (int x = 0; x <= panjang - finalBlock.length; x++) {
                        for (int y = 0; y <= lebar - finalBlock[0].length; y++) {
                            if (muat(finalBlock, x, y)) {
                                taro(finalBlock, x, y, blockChar);
                                kasusDitinjau++; 
                                if (solve(index + 1)) { 
                                    return true;
                                }
                                taro(finalBlock, x, y, '.');  
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean muat(char[][] block, int x, int y){
        for (int i = 0; i < block.length; i++){
            for (int j = 0; j < block[i].length; j++){
                if (block[i][j] != '.' && board[x + i][y + j] != '.') {
                    return false;
                }
            }
        }
        return true;
    }

    private static void taro(char[][] block, int x, int y, char huruf){
        for (int i = 0; i < block.length; i++){
            for (int j = 0; j < block[i].length; j++){
                if (block[i][j] != '.'){
                    board[x+i][y+j] = huruf;
                }
            }
        }
    }

    private static boolean isBoardFull() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == '.') {
                    return false;
                }
            }
        }
        return true;
    }

    private static void image (char[][] board, String filename){
        int lebar = board[0].length * 40;
        int tinggi = board.length * 40;
        
        BufferedImage img = new BufferedImage(lebar, tinggi, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, lebar, tinggi);

        g2d.setFont(new Font("Poppins", Font.BOLD, 30));

        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[i].length; j++){
                char c = board[i][j];
                if (c != '.'){
                    g2d.setColor(getColorForChar(c));
                    g2d.drawString(String.valueOf(c), j * 40 + 10, (i + 1) * 40 - 10);
                }
            }
        }
        g2d.dispose();
        try{
            ImageIO.write(img, "png", new File(filename));
            System.out.println("Solusi berhasil disimpan sebagai gambar dalam " + filename);
        }
        catch (IOException e){
            System.out.println("Gagal menyimpan gambar");
        }
    }

    public static Color getColorForChar(char c) {
        Color[] colors = {
            Color.decode("#FF0000"),
            Color.decode("#00FF00"),
            Color.decode("#0000FF"),
            Color.decode("#FF00FF"),
            Color.decode("#00FFFF"),
            Color.decode("#FFA500"),
            Color.decode("#FFC0CB"),
            Color.decode("#A9A9A9"),
            Color.decode("#FFFF00"),
            Color.decode("#D3D3D3"),
            Color.decode("#8A2BE2"),
            Color.decode("#DC143C"),
            Color.decode("#20B2AA"),
            Color.decode("#ADFF2F"),
            Color.decode("#FF1493"),
            Color.decode("#FF6347"),
            Color.decode("#4682B4"),
            Color.decode("#32CD32"),
            Color.decode("#BA55D3"),
            Color.decode("#87CEEB"),
            Color.decode("#8B0000"),
            Color.decode("#FF8C00"),
            Color.decode("#1E90FF"),
            Color.decode("#556B2F"),
            Color.decode("#9932CC"),
            Color.decode("#FF00FF")
        };
        return colors[(c - 'A') % colors.length];
    }
    
    private static String[] COLORS = {
        "\u001B[38;5;196m",
        "\u001B[38;5;46m",
        "\u001B[38;5;21m",
        "\u001B[38;5;201m",
        "\u001B[38;5;51m",
        "\u001B[38;5;208m",
        "\u001B[38;5;213m",
        "\u001B[38;5;244m",
        "\u001B[38;5;226m",
        "\u001B[38;5;250m",
        "\u001B[38;5;99m",
        "\u001B[38;5;160m",
        "\u001B[38;5;37m",
        "\u001B[38;5;154m",
        "\u001B[38;5;200m",
        "\u001B[38;5;202m",
        "\u001B[38;5;67m",
        "\u001B[38;5;82m",
        "\u001B[38;5;141m",
        "\u001B[38;5;117m",
        "\u001B[38;5;52m",
        "\u001B[38;5;214m",
        "\u001B[38;5;27m",
        "\u001B[38;5;100m",
        "\u001B[38;5;128m",
        "\u001B[38;5;201m"
    };
    
    private static final String RESET = "\u001B[0m"; 
    
}
