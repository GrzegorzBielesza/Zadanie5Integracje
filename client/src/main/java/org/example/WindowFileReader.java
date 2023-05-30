package org.example;

import client.generated.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WindowFileReader extends JFrame {
    BufferedReader reader = null;

    String[] headers = {"Marka", "Rozmiar", "Rozdzielczosc", "Matryca", "Dotykowy", "Procesor", "Liczba_rdzeni", "Taktowanie", "Ram", "Rozmiar_dysku", "Typ_dysku", "Karta_graficzna", "Pamiec_karty", "System_operacyjny", "Nagrywarka_DVD"};
    String[] rowValidators = {"[a-zA-Z]", "\\d+(\\.\\d+)?\"", "^\\d{2,4}x\\d{2,4}$", "^(matowa|blyszczaca)$", "^(tak|nie)$", ".", "^(1|2|4|8|16|32|64|128)$", "^\\d+$", "^\\d+(?:\\.\\d+)?[MG]B$", "^\\d+(?:\\.\\d+)?[MG]B$", "^(SSD|HDD)$", ".", "^\\d+(?:\\.\\d+)?[MG]B$", ".", "."};
    ArrayList<ArrayList<String>> rowlist = new ArrayList<>();
    ArrayList<ArrayList<String>> newRowlist = new ArrayList<>();
    String[][] newRows = new String[24][30];

    ArrayList<Integer> duplicates = new ArrayList<>();
    ArrayList<Integer> others = new ArrayList<>();

    ArrayList<Integer> allDuplicates = new ArrayList<>();


    DefaultTableModel tableModel;
    JLabel label = new JLabel("Duplikaty: 0");
    JTable table;
    JButton readButton = new JButton("wczytaj TXT");
    JButton writeButton = new JButton("zapisz TXT");
    JButton validateButton = new JButton("waliduj dane");

    JButton readXMLButton = new JButton("wczytaj XML");

    JButton writeXMLButton = new JButton("zapisz XML");

    JButton readFromDatabaseByProducerButton = new JButton("FiltrujPoProducencie");
    JButton readFromDatabaseByMatrixButton = new JButton("FiltrujPoMatrycy");

    JButton readFromDatabaseByAspectRatioButton = new JButton("FiltrujPoProporcji");

    JButton overrideDatabaseButton = new JButton("Nadpisz bazę danych");
    JButton clearDatabaseButton = new JButton("Wyczyść bazę danych");

    JButton writeToDatabaseButton = new JButton("Zapisz do bazy danych");
    JTextField producerTextField = new JTextField();
    JTextField matrixTextField = new JTextField();
    JTextField aspectRatioTextField = new JTextField();
    JTextField sizeTextField = new JTextField();
    JTextField touchableTextField = new JTextField();
    JTextField processorTextField = new JTextField();
    JTextField numberOfThreadsTextField = new JTextField();
    JTextField frequencyTextField = new JTextField();
    JTextField ramTextField = new JTextField();
    JTextField diskSizeTextField = new JTextField();
    JTextField diskTypeTextField = new JTextField();
    JTextField graphicsCardTextField = new JTextField();
    JTextField graphicsCardMemoryTextField = new JTextField();
    JTextField operationSystemTextField = new JTextField();
    JTextField dvdTextField = new JTextField();


    int[][] invalidCells;

    boolean editMode = false;
    ArrayList<Integer> editedRows = new ArrayList<>();

    SoapBeanService soapBeanService;
    SoapInterface server;

    String[] producer = new String[]{"Dell", "Asus", "Fujitsu", "Huawei", "MSI", "Samsung", "Sony"};
    String[] matrix = new String[]{"matowa", "blyszczaca"};
    String[] proportion = new String[]{"1280x800", "1366x768", "1600x900", "1920x1080"};

    JComboBox<String> producerComboBox;

    JComboBox<String> matrixComboBox;

    JComboBox<String> proportionComboBox;

    public WindowFileReader() {
//        soapBeanService = new SoapBeanService();
//        server = soapBeanService.getSoapBeanPort();
        soapBeanService = null;
        server = null;
        showWindow();
    }

    private void readFromDbByProducerRest() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/computers/findByProducer/" + this.producerComboBox.getSelectedItem().toString())).build();
            readFromDatabaseRest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFromDbByMatrixRest() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/computers/findByMatrix/" + this.matrixComboBox.getSelectedItem().toString())).build();
            readFromDatabaseRest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFromDbByAspectRatioRest() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/computers/findByAspectRatio/" + this.proportionComboBox.getSelectedItem().toString())).build();
            readFromDatabaseRest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFromDatabaseRest(HttpRequest request) {
        editMode = true;
        newRowlist.clear();
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            newRowlist = parseStringBodyToList(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<ArrayList<String>> parseStringBodyToList(String body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<ArrayList<String>> result = objectMapper.readValue(body, new TypeReference<>() {
        });
        return result;
    }

    private void updateTableData() {
        editedRows.clear();
        tableModel.setNumRows(newRowlist.size());
        for (int row = 0; row < newRowlist.size(); row++){
            for (int col = 0; col < headers.length; col++) {
                tableModel.setValueAt(newRowlist.get(row).get(col), row, col);
            }
        }

        rowlist.addAll(newRowlist);
        editMode = false;
    }

    public void validateTable() {
        invalidCells = new int[tableModel.getRowCount()][tableModel.getColumnCount()];
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                Object cell = tableModel.getValueAt(row, col);
                if (cell == null || !validateCell((String) cell, rowValidators[col])) {
                    invalidCells[row][col] = 1;
                }
            }
        }
    }


    private void saveNewComputerToDatabase() {
            String sql = "INSERT INTO computers (id, Marka, Rozmiar, Rozdzielczosc, Matryca, Dotykowy, Procesor, Liczba_rdzeni, Taktowanie, Ram, Rozmiar_dysku, Typ_dysku, Karta_graficzna, Pamiec_karty, System_operacyjny, Nagrywarka_DVD) VALUES " +
                    "("+0+", '"+producerTextField.getText()+"', '"+sizeTextField.getText()+"', '"+aspectRatioTextField.getText()+"', '"+matrixTextField.getText()+"', '"+touchableTextField.getText()+"', '"+processorTextField.getText()+"', '"+numberOfThreadsTextField.getText()+"', '"+frequencyTextField.getText()+"', '"+ramTextField.getText()+"', '"+diskSizeTextField.getText()+"', '"+diskTypeTextField.getText()+"', '"+graphicsCardTextField.getText()+"', '"+graphicsCardMemoryTextField.getText()+"', '"+operationSystemTextField.getText()+"', '"+dvdTextField.getText()+"');";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBodyJson = objectMapper.writeValueAsString(sql);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/computers"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearDatabase() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/computers"))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void overrideDatabaseRest() {
        ArrayList<String> sqls = new ArrayList<>();
        for (int i = 0; i< newRowlist.size(); i++){
                String sql2 = "INSERT INTO computers (id, Marka, Rozmiar, Rozdzielczosc, Matryca, Dotykowy, Procesor, Liczba_rdzeni, Taktowanie, Ram, Rozmiar_dysku, Typ_dysku, Karta_graficzna, Pamiec_karty, System_operacyjny, Nagrywarka_DVD) VALUES \n" +
                        "("+(i+1)+", '"+tableModel.getValueAt(i, 0)+"', '"+tableModel.getValueAt(i, 1)+"', '"+tableModel.getValueAt(i, 2)+"', '"+tableModel.getValueAt(i, 3)+"', '"+tableModel.getValueAt(i, 4)+"', '"+tableModel.getValueAt(i, 5)+"', '"+tableModel.getValueAt(i, 6)+"', '"+tableModel.getValueAt(i, 7)+"', '"+tableModel.getValueAt(i, 8)+"', '"+tableModel.getValueAt(i, 9)+"', '"+tableModel.getValueAt(i, 10)+"', '"+tableModel.getValueAt(i, 11)+"', '"+tableModel.getValueAt(i, 12)+"', '"+tableModel.getValueAt(i, 13)+"', '"+tableModel.getValueAt(i, 14)+"');";
                sqls.add(sql2);
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBodyJson = objectMapper.writeValueAsString(sqls);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/computers"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateCell(String cell, String validator) {
        Pattern pattern = Pattern.compile(validator);
        Matcher matcher = pattern.matcher(cell);
        return matcher.find();
    }

     void setTextPlaceholders(){
        producerTextField.setText("<"+headers[0]+">");
        new Placeholder(producerTextField, "<"+headers[0]+">");
         sizeTextField.setText("<"+headers[1]+">");
        new Placeholder(sizeTextField, "<"+headers[1]+">");
         aspectRatioTextField.setText("<"+headers[2]+">");
        new Placeholder(aspectRatioTextField, "<"+headers[2]+">");
         matrixTextField.setText("<"+headers[3]+">");
        new Placeholder(matrixTextField, "<"+headers[3]+">");
         touchableTextField.setText("<"+headers[4]+">");
        new Placeholder(touchableTextField, "<"+headers[4]+">");
         processorTextField.setText("<"+headers[5]+">");
        new Placeholder(processorTextField, "<"+headers[5]+">");
         numberOfThreadsTextField.setText("<"+headers[6]+">");
        new Placeholder(numberOfThreadsTextField, "<"+headers[6]+">");
         frequencyTextField.setText("<"+headers[7]+">");
        new Placeholder(frequencyTextField, "<"+headers[7]+">");
         ramTextField.setText("<"+headers[8]+">");
        new Placeholder(ramTextField, "<"+headers[8]+">");
         diskSizeTextField.setText("<"+headers[9]+">");
        new Placeholder(diskSizeTextField, "<"+headers[9]+">");
         diskTypeTextField.setText("<"+headers[10]+">");
        new Placeholder(diskTypeTextField, "<"+headers[10]+">");
         graphicsCardTextField.setText("<"+headers[11]+">");
        new Placeholder(graphicsCardTextField, "<"+headers[11]+">");
         graphicsCardMemoryTextField.setText("<"+headers[12]+">");
        new Placeholder(graphicsCardMemoryTextField, "<"+headers[12]+">");
         operationSystemTextField.setText("<"+headers[13]+">");
        new Placeholder(operationSystemTextField, "<"+headers[13]+">");
         dvdTextField.setText("<"+headers[14]+">");
        new Placeholder(dvdTextField, "<"+headers[14]+">");
    }

    private void showWindow() {
        tableModel = new DefaultTableModel(new String[0][30], headers);
        BorderLayout borderLayout = new BorderLayout();
        setLayout(borderLayout);
        this.producerComboBox = new JComboBox(this.producer);
        this.matrixComboBox = new JComboBox(this.matrix);
        this.proportionComboBox = new JComboBox(this.proportion);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.add(producerComboBox);
        buttonPanel.add(matrixComboBox);
        buttonPanel.add(proportionComboBox);
        buttonPanel.add(readFromDatabaseByProducerButton);
        buttonPanel.add(readFromDatabaseByMatrixButton);
        buttonPanel.add(readFromDatabaseByAspectRatioButton);
        buttonPanel.add(clearDatabaseButton);
        buttonPanel.add(overrideDatabaseButton);

        buttonPanel2.add(writeToDatabaseButton);

        buttonPanel2.add(producerTextField);
        buttonPanel2.add(sizeTextField);
        buttonPanel2.add(aspectRatioTextField);
        buttonPanel2.add(matrixTextField);
        buttonPanel2.add(touchableTextField);
        buttonPanel2.add(processorTextField);
        buttonPanel2.add(numberOfThreadsTextField);
        buttonPanel2.add(frequencyTextField);
        buttonPanel2.add(ramTextField);
        buttonPanel2.add(diskSizeTextField);
        buttonPanel2.add(diskTypeTextField);
        buttonPanel2.add(graphicsCardTextField);
        buttonPanel2.add(graphicsCardMemoryTextField);
        buttonPanel2.add(operationSystemTextField);
        buttonPanel2.add(dvdTextField);
        
        
        producerTextField.setPreferredSize(new Dimension(60, 30));
        sizeTextField.setPreferredSize(new Dimension(70, 30));
        aspectRatioTextField.setPreferredSize(new Dimension(100, 30));
        matrixTextField.setPreferredSize(new Dimension(60, 30));
        touchableTextField.setPreferredSize(new Dimension(70, 30));
        processorTextField.setPreferredSize(new Dimension(70, 30));
        numberOfThreadsTextField.setPreferredSize(new Dimension(100, 30));
        frequencyTextField.setPreferredSize(new Dimension(90, 30));
        ramTextField.setPreferredSize(new Dimension(50, 30));
        diskSizeTextField.setPreferredSize(new Dimension(110, 30));
        diskTypeTextField.setPreferredSize(new Dimension(80, 30));
        graphicsCardTextField.setPreferredSize(new Dimension(100, 30));
        graphicsCardMemoryTextField.setPreferredSize(new Dimension(110, 30));
        operationSystemTextField.setPreferredSize(new Dimension(130, 30));
        dvdTextField.setPreferredSize(new Dimension(120, 30));

        setTextPlaceholders();

        add(buttonPanel, BorderLayout.NORTH);
        add(buttonPanel2, BorderLayout.SOUTH);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        setSize(1920, 768);
        setVisible(true);
        table.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if(!editMode)
            if (e.getType() == TableModelEvent.UPDATE && !editMode && !tableModel.getValueAt(row, column).equals(newRowlist.get(row).get(column))) {
                editedRows.add(row);
                for (int col = 0; col < headers.length; col++) {
                    table.getColumnModel().getColumn(col).setCellRenderer(new CustomRenderer());
                }
            }
        });

        readFromDatabaseByProducerButton.addActionListener(e -> {
//            readFromDbByProducerSoap();
            readFromDbByProducerRest();
            updateTableData();
        });
        readFromDatabaseByMatrixButton.addActionListener(e -> {
//            readFromDbByMatrixSoap();
            readFromDbByMatrixRest();
            updateTableData();
        });
        readFromDatabaseByAspectRatioButton.addActionListener(e -> {
//            readFromDbByAspectRatioSoap();
            readFromDbByAspectRatioRest();
            updateTableData();
        });

        writeToDatabaseButton.addActionListener(e -> {
//            writeToDatabaseSoap();
            saveNewComputerToDatabase();
        });

        overrideDatabaseButton.addActionListener(e -> {
//            writeToDatabaseSoap();
            overrideDatabaseRest();
        });

        clearDatabaseButton.addActionListener(e -> {
//            writeToDatabaseSoap();
            clearDatabase();
            readFromDbByProducerRest();
            updateTableData();
        });
    }



    class CustomRenderer extends DefaultTableCellRenderer
    {
        private static final long serialVersionUID = 6703872492730589499L;

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if(editedRows.contains(row)){
                cellComponent.setBackground(Color.YELLOW);
            }
            else{
                cellComponent.setBackground(Color.WHITE);
            }

            return cellComponent;
        }
    }
}
