package org.example;

import jakarta.jws.WebService;

import java.util.ArrayList;


@WebService(endpointInterface = "org.example.SoapInterface")
public class SoapBean implements SoapInterface {

    ComputerFinder computerFinder = new ComputerFinder();

    @Override
    public Object[][] findByProducer(String producer) {
        ArrayList<ArrayList<String>> rows = computerFinder.findComputers();

        return rows.stream().filter(row -> row.get(0).equals(producer)).map(ArrayList::toArray).toArray(Object[][]::new);
    }

    @Override
    public Object[][] findByMatrix(String matrix) {
        ArrayList<ArrayList<String>> rows = computerFinder.findComputers();
        return rows.stream().filter(row -> row.get(3).equals(matrix)).map(ArrayList::toArray).toArray(Object[][]::new);
    }

    @Override
    public Object[][] findByAspectRatio(String proportion) {
        ArrayList<ArrayList<String>> rows = computerFinder.findComputers();

        return rows.stream().filter(row -> row.get(2).equals(proportion)).map(ArrayList::toArray).toArray(Object[][]::new);
    }

    @Override
    public void writeToDatabase(String[] sqls) {
        try {
            ComputerRepository.overwriteDatabase(sqls);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
