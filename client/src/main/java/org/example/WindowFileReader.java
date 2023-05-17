package org.example;

import client.generated.*;
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
    JButton readButton = new JButton("wczytaj dane z pliku TXT");
    JButton writeButton = new JButton("zapisz dane do pliku TXT");
    JButton validateButton = new JButton("waliduj dane");

    JButton readXMLButton = new JButton("wczytaj dane z pliku XML");

    JButton writeXMLButton = new JButton("zapisz dane do pliku XML");

    JButton readFromDatabaseByProducerButton = new JButton("FiltrujPoProducencie");
    JButton readFromDatabaseByMatrixButton = new JButton("FiltrujPoMatrycy");

    JButton readFromDatabaseByAspectRatioButton = new JButton("FiltrujPoProporcji");

    JButton writeToDatabaseButton = new JButton("zapisz dane do bazy danych");

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
        soapBeanService = new SoapBeanService();
        server = soapBeanService.getSoapBeanPort();
        showWindow();
    }

    private void readFile() {
        editMode = true;
        newRowlist.clear();
        String line = "";

        try {
            reader = new BufferedReader(new FileReader("src/main/resources/example.csv"));
            while ((line = reader.readLine()) != null) {
                newRowlist.add(new ArrayList<>(Arrays.asList(line.split(";", -1)).subList(0, headers.length)));
            }
        } catch ( Exception e) {
            e.printStackTrace();
        } finally {
            try { reader.close(); }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void readXmlFile() {
        editMode = true;
        newRowlist.clear();

        File xmlFile = new File("katalog2.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            NodeList nodeList = doc.getElementsByTagName("row");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String[] rowData = new String[headers.length];
                    for (int j = 0; j < headers.length; j++) {
                        Element element2 = (Element) element.getElementsByTagName(headers[j]).item(0);
                        rowData[j] = element2.getAttribute("value");
                    }
                    newRowlist.add(new ArrayList<>(Arrays.asList(rowData)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void readFromDbByProducer() {
        AnyTypeArrayArray encapsulatedData = server.findByProducer(this.producerComboBox.getSelectedItem().toString());
        readFromDatabase(encapsulatedData);
    }
    private void readFromDbByMatrix() {
        AnyTypeArrayArray encapsulatedData = server.findByMatrix(this.matrixComboBox.getSelectedItem().toString());
        readFromDatabase(encapsulatedData);
    }
    private void readFromDbByAspectRatio() {
        AnyTypeArrayArray encapsulatedData = server.findByAspectRatio(this.proportionComboBox.getSelectedItem().toString());
        readFromDatabase(encapsulatedData);
    }
    private void readFromDatabase(AnyTypeArrayArray encapsulatedData) {
        editMode = true;
        newRowlist.clear();
            List<AnyTypeArray> data = encapsulatedData.getItem();
            data.forEach((row) -> {
                String[] rowData = new String[headers.length];
                for (int j = 0; j < headers.length; j++) {
                    rowData[j] = row.getItem().get(j).toString();
                }
                newRowlist.add(new ArrayList<>(Arrays.asList(rowData)));
            });

    }

    private void updateTableData() {
        editedRows.clear();
        invalidCells=null;
        duplicates.clear();
        others.clear();
        tableModel.setNumRows(newRowlist.size()+rowlist.size());
        for (int row = 0; row < newRowlist.size(); row++){
            if(!rowlist.isEmpty() && isItDuplicate(newRowlist.get(row))){
                duplicates.add(row + rowlist.size());
            } else if (!rowlist.isEmpty()) {
                others.add(row + rowlist.size());
            }
            for (int col = 0; col < headers.length; col++) {
                tableModel.setValueAt(newRowlist.get(row).get(col), row + rowlist.size(), col);
            }
        }
        for (int col = 0; col < headers.length; col++) {
            table.getColumnModel().getColumn(col).setCellRenderer(new CustomRenderer());
        }

        allDuplicates.addAll(duplicates);
        rowlist.addAll(newRowlist);
        label.setText("Duplikaty: " + duplicates.size());
        editMode = false;

    }

    private boolean isItDuplicate(ArrayList<String> newRow) {

        return rowlist.stream().anyMatch(row -> {
            int duplicateCounter = 0;
            for(int i = 0; i < row.size(); i++) {
                if(row.get(i).equals(newRow.get(i))){
                    duplicateCounter++;
                }
            }
            if(duplicateCounter == row.size()){
                return true;
            }
            else{
                return false;
            }
        });

//        return rowlist.stream().anyMatch(row -> row.size() == newRow.size() && IntStream.range(0, row.size()).allMatch(i -> row.get(i).equals(newRow.get(i))));
//        return rowlist.stream().anyMatch(row -> row.equals(newRow));

    }

    private void exportDataToFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("src/main/resources/export.txt");
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                if(!allDuplicates.contains(row)) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        writer.print(tableModel.getValueAt(row, col));
                        writer.print(";");
                    }
                    writer.println();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void exportDataToXmlFile() throws IOException, XMLStreamException {

        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlWriter = xmlOutputFactory.createXMLStreamWriter(new FileWriter("katalog3.xml"));

        // Rozpoczynamy dokument XML
        xmlWriter.writeStartDocument();
        xmlWriter.writeCharacters("\n");
        xmlWriter.writeStartElement("table");
        xmlWriter.writeCharacters("\n");

        // Pobieramy liczbę kolumn i wierszy
        int columnCount = table.getColumnCount();
        int rowCount = table.getRowCount();

        // Pobieramy wartości z kolumn i tworzymy elementy XML z atrybutami
        for (int i = 0; i < rowCount; i++) {
            if (!allDuplicates.contains(i)) {
                xmlWriter.writeCharacters("\t");
                xmlWriter.writeStartElement("row");
                xmlWriter.writeCharacters("\n");
                for (int j = 0; j < columnCount; j++) {
                    String columnValue = table.getValueAt(i, j).toString();
                    String columnName = headers[j];
                    xmlWriter.writeCharacters("\t\t");
                    xmlWriter.writeStartElement(columnName);
                    xmlWriter.writeAttribute("value", columnValue);
                    xmlWriter.writeEndElement();
                    xmlWriter.writeCharacters("\n");
                }
                xmlWriter.writeCharacters("\t");
                xmlWriter.writeEndElement();
                xmlWriter.writeCharacters("\n");
            }
        }

        // Zamykamy dokument XML i zapisujemy plik
        xmlWriter.writeEndElement();
        xmlWriter.writeCharacters("\n");
        xmlWriter.writeEndDocument();
        xmlWriter.flush();
        xmlWriter.close();
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



    private void writeToDatabase() {
            ArrayList<String> sqls = new ArrayList<>();
            StringArray array = new StringArray();
            for (int i = 0; i< rowlist.size(); i++){
                if(!allDuplicates.contains(i)){
                String sql2 = "INSERT INTO computers (id, Marka, Rozmiar, Rozdzielczosc, Matryca, Dotykowy, Procesor, Liczba_rdzeni, Taktowanie, Ram, Rozmiar_dysku, Typ_dysku, Karta_graficzna, Pamiec_karty, System_operacyjny, Nagrywarka_DVD) VALUES \n" +
                        "("+(i+1)+", '"+tableModel.getValueAt(i, 0)+"', '"+tableModel.getValueAt(i, 1)+"', '"+tableModel.getValueAt(i, 2)+"', '"+tableModel.getValueAt(i, 3)+"', '"+tableModel.getValueAt(i, 4)+"', '"+tableModel.getValueAt(i, 5)+"', '"+tableModel.getValueAt(i, 6)+"', '"+tableModel.getValueAt(i, 7)+"', '"+tableModel.getValueAt(i, 8)+"', '"+tableModel.getValueAt(i, 9)+"', '"+tableModel.getValueAt(i, 10)+"', '"+tableModel.getValueAt(i, 11)+"', '"+tableModel.getValueAt(i, 12)+"', '"+tableModel.getValueAt(i, 13)+"', '"+tableModel.getValueAt(i, 14)+"');";
                sqls.add(sql2);
                array.getItem().add(sql2);
                }
            }
            server.writeToDatabase(array);
    }

    private boolean validateCell(String cell, String validator) {
        Pattern pattern = Pattern.compile(validator);
        Matcher matcher = pattern.matcher(cell);
        return matcher.find();
    }

    private void showWindow() {
        tableModel = new DefaultTableModel(new String[0][30], headers);
        BorderLayout borderLayout = new BorderLayout();
        setLayout(borderLayout);
        add(label, BorderLayout.NORTH);
        this.producerComboBox = new JComboBox(this.producer);
        this.matrixComboBox = new JComboBox(this.matrix);
        this.proportionComboBox = new JComboBox(this.proportion);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.add(producerComboBox);
        buttonPanel.add(matrixComboBox);
        buttonPanel.add(proportionComboBox);
        buttonPanel.add(readButton);
        buttonPanel.add(writeButton);
        buttonPanel.add(validateButton);
        buttonPanel.add(readXMLButton);
        buttonPanel.add(writeXMLButton);
        buttonPanel.add(readFromDatabaseByProducerButton);
        buttonPanel.add(readFromDatabaseByMatrixButton);
        buttonPanel.add(readFromDatabaseByAspectRatioButton);
        buttonPanel.add(writeToDatabaseButton);
        add(buttonPanel, BorderLayout.SOUTH);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        setSize(1600, 900);
        setVisible(true);
        table.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if(!editMode)
            if (e.getType() == TableModelEvent.UPDATE && !editMode && !tableModel.getValueAt(row, column).equals(rowlist.get(row).get(column))) {
                editedRows.add(row);
                for (int col = 0; col < headers.length; col++) {
                    table.getColumnModel().getColumn(col).setCellRenderer(new CustomRenderer());
                }
            }
        });

        readButton.addActionListener(e -> {
            readFile();
            updateTableData();
        });

        writeButton.addActionListener(e -> {
            exportDataToFile();
        });

        validateButton.addActionListener(e -> {
            validateTable();
        });

        readXMLButton.addActionListener(e -> {
            readXmlFile();
            updateTableData();
        });

        writeXMLButton.addActionListener(e -> {
            try {
                exportDataToXmlFile();
            } catch (IOException | XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
        });

        readFromDatabaseByProducerButton.addActionListener(e -> {
            readFromDbByProducer();
            updateTableData();
        });
        readFromDatabaseByMatrixButton.addActionListener(e -> {
            readFromDbByMatrix();
            updateTableData();
        });
        readFromDatabaseByAspectRatioButton.addActionListener(e -> {
            readFromDbByAspectRatio();
            updateTableData();
        });

        writeToDatabaseButton.addActionListener(e -> {
            writeToDatabase();
        });
    }



    class CustomRenderer extends DefaultTableCellRenderer
    {
        private static final long serialVersionUID = 6703872492730589499L;

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if(!duplicates.isEmpty() && duplicates.contains(row)){
                cellComponent.setBackground(Color.GREEN);
            }
            else if(!others.isEmpty() && others.contains(row)){
                cellComponent.setBackground(Color.GRAY);
            }
            else if((row < rowlist.size())){
                cellComponent.setBackground(Color.WHITE);
            }

            if(editedRows.contains(row)){
                cellComponent.setBackground(Color.YELLOW);
            }


            if(invalidCells!=null && invalidCells.length>0 && invalidCells[row][column] == 1){
                cellComponent.setBackground(Color.RED);}
            else{
//                cellComponent.setBackground(Color.WHITE);
            }

            return cellComponent;
        }
    }
}
