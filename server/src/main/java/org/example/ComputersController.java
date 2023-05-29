package org.example;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/computers")
public class ComputersController {

    ComputerFinder computerFinder = new ComputerFinder();

    @GetMapping("/findByProducer/{producer}")
    public Object[][] findByProducer(@PathVariable String producer) {
        ArrayList<ArrayList<String>> rows = computerFinder.findComputers();

        return rows.stream().filter(row -> row.get(0).equals(producer)).map(ArrayList::toArray).toArray(Object[][]::new);
    }
    @GetMapping("/findByMatrix/{matrix}")
    public Object[][] findByMatrix(@PathVariable String matrix) {
        ArrayList<ArrayList<String>> rows = computerFinder.findComputers();

        return rows.stream().filter(row -> row.get(3).equals(matrix)).map(ArrayList::toArray).toArray(Object[][]::new);
    }
    @GetMapping("/findByAspectRatio/{ratio}")
    public Object[][] findByAspectRatio(@PathVariable String ratio) {
        ArrayList<ArrayList<String>> rows = computerFinder.findComputers();

        return rows.stream().filter(row -> row.get(2).equals(ratio)).map(ArrayList::toArray).toArray(Object[][]::new);
    }

    @PutMapping
    public void writeToDatabase(@RequestBody String[] sqls) {
        try {
            ComputerRepository.overwriteDatabase(sqls);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping
    public void saveNewComputer(@RequestBody String sql){
        try {
            ComputerRepository.saveNewComputer(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @DeleteMapping
    public void deleteAll(){
        try {
            ComputerRepository.deleteAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    @PostMapping("/{id}/edit-services")
//    public Room editServices(@PathVariable final Long id, @RequestBody final List<Service> editedServicesList){
//        Room room = this.roomService.findById(id);
//        List<Service> services = room.getServices();
//        services = services.stream().filter(service -> !service.isItemless()).collect(Collectors.toList());
//        services.addAll(editedServicesList);
//        room.setServices(services);
//        return this.roomService.editRoomServices(room);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteRoom(@PathVariable Long id) {
//        this.roomService.markAsDeleted(id);
//    }
}
