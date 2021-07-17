package com.IBLab.RESTapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.*;

import java.time.LocalDate;
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
    ModuleService moduleservice;

    /*
    @PostMapping("/")
    @GetMapping("/modules")

    @GetMapping("/{module}")
    @PutMapping("/{module}")
    @DeleteMapping("/{module}")

    @GetMapping("/isExpired/{module}")
    @GetMapping("IsExpired/")

    @GetMapping("/echo")
    */

    @PostMapping("/")
    ResponseEntity<?> newModule(@RequestBody Module newModule) {
        EntityModel<Module> entityModel = assembler.toModel(repository.save(newModule));
        //TODO move repo stuff to @Service
        return ResponseEntity //triggers HTTP 201 Created (requirement!)
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
                // TODO call @Service to check for InvalidExpirationDateException (that throws 400 then)
    }

    //works
    @GetMapping("/modules")
    CollectionModel<EntityModel<Module>> getAllModules() {

        List<EntityModel<Module>> modules = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(modules, linkTo(methodOn(ModuleController.class).getAllModules()).withSelfRel());
    }

    //@GetMapping("/{module}") Now only used to set the links of the ModuleAssembler
    EntityModel<Module> getOneModule(@PathVariable Module module) {
        return assembler.toModel(module);
    }

    @GetMapping("/{module}")
    LocalDate getExpirationDateFromModule(@PathVariable Module module) {
        //TODO return dateOfExpiration OR 404 if module not found, should be done by @Service
        return getDateFromModule(module);
    }

    //TODO GO THREW IT ONCE AND ADAPT
    @PutMapping("/{module}")
    ResponseEntity<?> replaceModule(@RequestBody Module newModule, @PathVariable Long id) {

        Module updatedModule = repository.findById(id)
                .map(module -> {
                    module.setName(newModule.getName());
                    module.setexpirationdate(newModule.getexpirationdate());
                    return repository.save(module);
                })
                .orElseGet(() -> {
                    newModule.setId(id);
                    return repository.save(newModule);
                });

        EntityModel<Module> entityModel = assembler.toModel(updatedModule);
        //TODO return 200 if okay OR 400 if expiration date is not valid (check by @Service)
        return ResponseEntity //triggers HTML 201 Created instead of 200 Ok
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{module}")
    ResponseEntity<?> deleteModule(@PathVariable Module module) {
        deleteModuleInRepository(module);
        //TODO @Service has to check if ok (200) or module not found (404) --> return
        return ResponseEntity.noContent().build(); // TODO get HTTP responses into log when deleting.
    }

    @GetMapping("isExpired/")
    JSONObject checkAllDateOfExpiration() throws JsonProcessingException {
        return getAllDateExpiredJson();
    }

    //WORKS
    @GetMapping("/isExpired/{module}")
    JsonNode checkOneDateOfExpiration(@PathVariable Module module) throws JsonProcessingException {
        return getDateExpiredJson(module);
    }

    //TODO STUFF THAT HAS TO GO TO @SERVICE
    JsonNode getDateExpiredJson(Module module) throws JsonProcessingException {
        String dateExpiredMsg = "{\"expired\":" + isDateExpired(module.getexpirationdate()) + "}";
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(dateExpiredMsg);
    }

    public boolean isModuleRegistered(Module module) {
        Module questionableModule = repository.findById(module.getId()) //
                .orElseThrow(() -> new ModuleNotFoundException(module.getId()));
        return true;
    }

    public LocalDate getDateFromModule(Module module) {
        return module.getexpirationdate();
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
        log.info("ALL DATE EXPIRE MSG: " + allDateExpiredJson);
        return allDateExpiredJson;
    }

    public void deleteModuleInRepository(Module module){
        try {
        //TODO fix Exception (currently throws 500 instead.
        repository.deleteById(module.getId());
        } catch(Exception e) {
            throw new ModuleNotFoundException(module.getId());
        }
    }

    public boolean isDateExpired(LocalDate expirationdate) {
        boolean isExpired = !expirationdate.isAfter(LocalDate.now()); //Why !isAFTER? cause isEQUAL is also not expired
        return isExpired;
    }
}