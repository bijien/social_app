package com.example.laborator6map.repository;

import com.example.laborator6map.domain.Entity;
import com.example.laborator6map.validators.Validator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    String fileName;

    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * load data from file
     */
    private void loadData() {
        Path path = Paths.get(fileName);
        try {
            List<String> lista = Files.readAllLines(path);
            lista.forEach((line) -> {
                String[] args = line.split(";");
                E entity = extractEntity(Arrays.asList(args));
                super.save(entity);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * extract entity  - template method design pattern
     * creates an entity of type E having a specified list of @code attributes
     *
     * @param attributes
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);

    /**
     * creates a string having entity fields
     * @param entity
     * @return entity as a string
     */
    protected abstract String createEntityAsString(E entity);

    @Override
    public E save(E entity) {
        E result = super.save(entity);
        if (result == null) {
            writeToFile(entity);
        }
        return result;
    }

    @Override
    public E delete(ID id){
        E result=super.delete(id);
        if(result != null)
            rewriteFile();
        return result;
    }

    /**
     * write to file the entity as a string
     * @param entity
     */
    protected void writeToFile(E entity) {
        String entityAsString = createEntityAsString(entity);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true))) {
            bufferedWriter.write(entityAsString);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * rewrite all the file with entities from InMemoryRepository
     */
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
        for(E e:this.entities.values()){
            writeToFile(e);
        }
    }

}
