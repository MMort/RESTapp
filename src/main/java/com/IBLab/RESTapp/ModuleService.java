package com.IBLab.RESTapp;

import com.IBLab.RESTapp.Exceptions.ExpirationDateInvalidException;
import com.IBLab.RESTapp.Exceptions.ModuleNotFoundException;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ModuleService {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    private final ModuleRepository repository;
    private final ModuleModelAssembler assembler;

    ModuleService(ModuleRepository repository, ModuleModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    EntityModel<Module> addNewModule(Module newModule) {
        repository.save(newModule);
        return assembler.toModel(newModule); //wrap module with RESTful metadata
    }

    EntityModel<Module> getOneModule(Module newModule) {
        return assembler.toModel(newModule); //wrap module with RESTful metadata
    }

    List<EntityModel<Module>> getListOfModules() {
        List<EntityModel<Module>> modules = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return modules;
    }

    void deleteModuleInRepository(Long id){
        if (isModuleRegistered(id)) {
            repository.deleteById(id);
        }
    }

    boolean isModuleRegistered(Long id) {
        Module questionableModule = repository.findById(id)
                .orElseThrow(ModuleNotFoundException::new);
        return true;
    }

    Module getModuleById(Long id) {
        return repository.findById(id)
                .orElseThrow(ModuleNotFoundException::new);
    }

    LocalDate getDateFromModule(Long id) {
        LocalDate date;
        Module questionableModule = repository.findById(id)
                .orElseThrow(ModuleNotFoundException::new);
        date =  questionableModule.getexpirationdate();
        return date;
    }

    JSONObject getExpirationDate(Long id) {
        JSONObject dateExpiredJson = new JSONObject();
        dateExpiredJson.put("expired", isExpirationDateExpired(getModuleById(id).getexpirationdate()));
        return dateExpiredJson;
    }

    JSONObject getAllExpirationDates() {
        List<EntityModel<Module>> modules = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        JSONObject allDateExpiredJson = new JSONObject();

        for (EntityModel<Module> module : modules) {
            boolean isExpired = isExpirationDateExpired(Objects.requireNonNull(module.getContent()).getexpirationdate());
            String name = module.getContent().getName();
            allDateExpiredJson.put(name, isExpired);
        }
        return allDateExpiredJson;
    }

    void updateExpirationDate(Long id, String newDate) {
        if (isModuleRegistered(id)) {
            Module updatedModule = getModuleById(id);
            updatedModule.setexpirationdate(parseStringToLocalDate(newDate));
            repository.save(updatedModule);
        }
    }

    boolean isExpirationDateExpired(LocalDate expirationDate) {
        return !expirationDate.isAfter(LocalDate.now());
    }

    LocalDate parseStringToLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        // Not clear why the autoformat class does not affect parse() default format
        try {
            return LocalDate.parse(dateStr, formatter);
        }catch(Exception broadSwordException) {
            throw new ExpirationDateInvalidException();
        }
    }

}
