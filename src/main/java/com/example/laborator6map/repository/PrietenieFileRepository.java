package com.example.laborator6map.repository;

import com.example.laborator6map.domain.Prietenie;
import com.example.laborator6map.domain.Tuple;
import com.example.laborator6map.domain.Utilizator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrietenieFileRepository {
    String fileName;
    List<Prietenie> prietenii;
    public PrietenieFileRepository(String fileName){
        this.fileName=fileName;
        this.prietenii=new ArrayList<Prietenie>();
        loadData();
    }

    private void loadData() {
        Path path = Paths.get(fileName);
        try {
            List<String> lista = Files.readAllLines(path);
            lista.forEach((line) -> {
                String[] args = line.split(";");
                Prietenie prietenie = extractEntity(Arrays.asList(args));
                prietenii.add(prietenie);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Prietenie extractEntity(List<String> attributes) {
        Prietenie prietenie=new Prietenie();
        prietenie.setId(new Tuple<Long,Long>(Long.parseLong(attributes.get(0)),Long.parseLong(attributes.get(1))));
        return prietenie;
    }


    public Prietenie findOne(Tuple<Long,Long> tuple){
        if(tuple.getLeft()==null || tuple.getRight()==null)
            throw new IllegalArgumentException("id must be not null");
        for(int i=0;i<prietenii.size();i++){
            if(prietenii.get(i).getId().getLeft().equals(tuple.getLeft()) && prietenii.get(i).getId().getRight().equals(tuple.getRight())||
                    prietenii.get(i).getId().getLeft().equals(tuple.getRight()) && prietenii.get(i).getId().getRight().equals(tuple.getLeft()))
                return prietenii.get(i);
        }
        return null;
    }

    public List<Prietenie> findAll(){
        return this.prietenii;
    }

    public void save(Prietenie prietenie){
        prietenii.add(prietenie);
        writeToFile(prietenie);
    }

    private void writeToFile(Prietenie prietenie) {
        String entityAsString = createEntityAsString(prietenie);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true))) {
            bufferedWriter.write(entityAsString);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createEntityAsString(Prietenie prietenie) {
        return prietenie.getId().getLeft()+";"+prietenie.getId().getRight();
    }

    public void delete(Tuple<Long,Long> tuple){
        if(tuple.getLeft()==null || tuple.getRight()==null)
            throw new IllegalArgumentException("id must be not null");
        for(int i=0;i<prietenii.size();i++){
            if(prietenii.get(i).getId().getLeft().equals(tuple.getLeft()) && prietenii.get(i).getId().getRight().equals(tuple.getRight())||
                    prietenii.get(i).getId().getLeft().equals(tuple.getRight()) && prietenii.get(i).getId().getRight().equals(tuple.getLeft())) {
                prietenii.remove(i);
                rewriteFile();
                return;
            }
        }
    }

    protected void rewriteFile(){
        FileOutputStream writer = null;
        try {
            writer = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            writer.write(("").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Prietenie prietenie:this.prietenii){
            writeToFile(prietenie);
        }
    }
}
