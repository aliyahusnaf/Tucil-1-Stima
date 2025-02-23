package graphic;
import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame {
    private JButton loadButton, solveButton, saveButton;
    private JPanel gridPanel, buttonPanel, statsPanel;
    private JLabel statusLabel, timeLabel, casesLabel;
    private char[][] board;
    private int tipeKasus;
    private boolean adaSolusi = false;

    public GUI() {
        setTitle("IQ Puzzle Solver");
        setSize(600, 700);  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); 

        buttonPanel = new JPanel();
        loadButton = new JButton("Muat file");
        solveButton = new JButton("Solve");
        saveButton = new JButton("Simpan solusi");
        solveButton.setEnabled(false);
        saveButton.setEnabled(false);

        buttonPanel.add(loadButton);
        buttonPanel.add(solveButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.NORTH);

        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Informasi"));
        
        timeLabel = new JLabel("Waktu pencarian: -");
        casesLabel = new JLabel("Banyak kasus yang ditinjau: -");
        
        statsPanel.add(timeLabel);
        statsPanel.add(casesLabel);
        add(statsPanel, BorderLayout.EAST);

        gridPanel = new JPanel();
        add(gridPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Pilih file untuk memuat puzzle.", SwingConstants.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadPuzzleFromFile());
        solveButton.addActionListener(e -> solvePuzzle());
        saveButton.addActionListener(e -> saveSolution());

        setVisible(true);
    }

    private void loadPuzzleFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int pilihan = fileChooser.showOpenDialog(this);
    
        if (pilihan == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                parser p = new parser(selectedFile.getAbsolutePath());
    
                board = new char[p.getPanjang()][p.getLebar()];
                for (int i = 0; i < p.getPanjang(); i++) {
                    for (int j = 0; j < p.getLebar(); j++) {
                        board[i][j] = '.'; 
                    }
                }
    
                // Teruskan data ke solver
                solver.setBoardAndBlocks(board, p.getBlocks());
                solver.setDimensions(p.getLebar(), p.getPanjang(), p.getJmlblok());
                solver.setTipeKasus(p.getTipeKasus());
                solver.setHurufs(p.getHurufs());
    
                updateBoard();
                solveButton.setEnabled(true);
                statusLabel.setText("File berhasil dimuat. Klik Solve untuk mencari solusi!");
    
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal membaca file.");
            }
        }
    }
    
    

    private void updateBoard() {
        gridPanel.removeAll(); 
        gridPanel.setLayout(new GridLayout(board.length, board[0].length));

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                JLabel cell = new JLabel(String.valueOf(board[i][j]), SwingConstants.CENTER); // tampilan cell
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cell.setFont(new Font("Arial", Font.BOLD, 20));
                
                if (board[i][j] != '.') {
                    cell.setBackground(getColorForChar(board[i][j]));
                    cell.setOpaque(true);
                } else {
                    cell.setBackground(Color.WHITE);
                    cell.setOpaque(true);
                }
                
                gridPanel.add(cell);
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void saveSolution() {
        if (!adaSolusi) {
            JOptionPane.showMessageDialog(this, "Tidak ada solusi untuk disimpan!");
            return;
        }
    
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Apakah anda ingin menyimpan solusi?",
            "Simpan Solusi",
            JOptionPane.YES_NO_OPTION
        );
    
        if (choice == JOptionPane.YES_OPTION) {
            try {
                solver.saveSolution("solution.txt", "solution.png");
                
                JOptionPane.showMessageDialog(
                    this,
                    "Solusi berhasil disimpan ke solution.txt dan solution.png",
                    "Berhasil Menyimpan",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Gagal menyimpan solusi: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
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

    private void solvePuzzle() {
        solveButton.setEnabled(false);
        saveButton.setEnabled(false);
        statusLabel.setText("Mencari solusi...");
        
        long startTime = System.currentTimeMillis();
        solver.setWaktuMulai(startTime);
        adaSolusi = false;
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return solver.solve(0);
            }
            
            @Override
            protected void done() {
                try {
                    boolean solved = get();
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    tipeKasus = solver.getKasusDitinjau();
                    
                    timeLabel.setText(String.format("Waktu pencarian: %d ms", duration));
                    casesLabel.setText(String.format("Banyak kasus yang ditinjau: %d", tipeKasus));
                    
                    if (solved) {
                        adaSolusi = true;
                        char[][] solvedBoard = solver.getBoard();
                        for (int i = 0; i < solvedBoard.length; i++) {
                            for (int j = 0; j < solvedBoard[i].length; j++) {
                                board[i][j] = solvedBoard[i][j];
                            }
                        }
                        updateBoard();
                        statusLabel.setText("Solusi ditemukan!");
                        saveButton.setEnabled(true);
                    } else {
                        statusLabel.setText("Tidak ada solusi.");
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                }
                solveButton.setEnabled(true);
            }
        };
        
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
