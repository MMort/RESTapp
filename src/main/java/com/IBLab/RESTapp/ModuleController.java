package com.IBLab.RESTapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
class ModuleController {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    private final ModuleRepository repository;

    private final ModuleModelAssembler assembler;

    ModuleController(ModuleRepository repository, ModuleModelAssembler assembler) {

        this.repository = repository;
        this.assembler = assembler;
    }

    @Autowired
    ModuleService service;

    @PostMapping("/")
    ResponseEntity<?> newModule(@RequestBody Module module) {
        addNewModule(module); //for adding to repository
        EntityModel<Module> entityModel = assembler.toModel(module); //get model for return
        return ResponseEntity //triggers HTTP 201 Created (requirement!)
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/modules")
    CollectionModel<EntityModel<Module>> getAllModules() {

        List<EntityModel<Module>> modules = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(modules, linkTo(methodOn(ModuleController.class).getAllModules()).withSelfRel());
    }

    //needed to set the links of the ModuleAssembler
    EntityModel<Module> getOneModule(@PathVariable Module module) {
        return assembler.toModel(module);
    }

    @GetMapping("/{module}")
    LocalDate getModule(@PathVariable Long module) {
        // module had to be changed from Module to Long due to module=null --> err 500 problem, when using a module id
        // that is not known --> could not be fixed, hence the workaround. applies to all 'Long module' in Mappings.
        // TIL never set up an primary key id when module is module is wanted as kay var
        return getDateFromModule(module);
    }

    @PutMapping("/{module}")
    void updateModule(@RequestParam String newExpirationDate, @PathVariable Long module) {
        updateExpirationDate(module, newExpirationDate);
    }

    @DeleteMapping("/{module}")
    ResponseEntity<?> deleteModule(@PathVariable Long module) {
        deleteModuleInRepository(module);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("isExpired/")
    JSONObject checkAllDateOfExpiration() {
        return getAllDateExpiredJson();
    }

    @GetMapping("/isExpired/{module}")
    JSONObject checkOneDateOfExpiration(@PathVariable Long module) {
        return getDateExpiredJson(module);
    }

    @GetMapping("/echo")
    String echo(@RequestParam String echo){
        return echo;
    }

    // curl -v -X POST localhost:8080/ -H 'Content-Type:application/json' -d '{"name": "Obi Wan", "expirationdate": "01.01.2022"}'
    // curl -v -X GET localhost:8080/modules
    // curl -v -X GET localhost:8080/1
    // curl -X PUT localhost:8080/<module>?newExpirationDate=01.12.1999
    // curl -v -X GET localhost:8080/isExpired/
    // curl -v -X GET localhost:8080/isExpired/1
    // curl -X DELETE localhost:8080/1
    // curl -v -X GET localhost:8080/echo?echo=HelloThere



    //TODO STUFF THAT HAS TO GO TO @SERVICE
    void addNewModule(Module newModule) {
        repository.save(newModule);
    }

    public boolean isModuleRegistered(Long id) {
        Module questionableModule = repository.findById(id)
                .orElseThrow(ModuleNotFoundException::new);
        return true;
    }

    public Module getModuleById(Long id) {
        return repository.findById(id)
                .orElseThrow(ModuleNotFoundException::new);
    }

    public LocalDate getDateFromModule(Long id) {
        LocalDate date;
        Module questionableModule = repository.findById(id)
            .orElseThrow(ModuleNotFoundException::new);
        date =  questionableModule.getexpirationdate();
        return date;
    }

    JSONObject getDateExpiredJson(Long id) {
        JSONObject dateExpiredJson = new JSONObject();
        dateExpiredJson.put(getModuleById(id).getName(), isDateExpired(getModuleById(id).getexpirationdate()));
        return dateExpiredJson;
    }

    public JSONObject getAllDateExpiredJson() {
        List<EntityModel<Module>> modules = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        JSONObject allDateExpiredJson = new JSONObject();

        for (EntityModel<Module> module : modules) {
            boolean isExpired = isDateExpired(Objects.requireNonNull(module.getContent()).getexpirationdate());
            String name = module.getContent().getName();
           allDateExpiredJson.put(name, isExpired);
        }
        return allDateExpiredJson;
    }

    public void deleteModuleInRepository(Long id){
        if (isModuleRegistered(id)) {
            repository.deleteById(id);
        }
    }

    public boolean isDateExpired(LocalDate expirationdate) {
        return !expirationdate.isAfter(LocalDate.now()); //Why !isAFTER? cause isEQUAL is also not expired
    }

    public LocalDate parseStringToLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            return LocalDate.parse(dateStr, formatter);
        }catch(Exception broadSwordException) {
            throw new ExpirationDateInvalidException();
        }
    }

    public void updateExpirationDate(Long id, String newDate) {
        if (isModuleRegistered(id)) {
            Module updatedModule = getModuleById(id);
            updatedModule.setexpirationdate(parseStringToLocalDate(newDate));
            repository.save(updatedModule);
        }
    }
}