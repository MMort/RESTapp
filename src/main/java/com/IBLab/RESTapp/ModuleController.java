package com.IBLab.RESTapp;

import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
class ModuleController {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    ModuleController() { /*Nothing to init here*/ }

    @Autowired
    ModuleService service;

    @PostMapping("/")
    ResponseEntity<?> newModule(@RequestBody Module module) {
        EntityModel<Module> entityModel = service.addNewModule(module);
        return ResponseEntity //triggers HTTP 201 Created (requirement!)
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/modules")
    CollectionModel<EntityModel<Module>> getAllModules() {
        List<EntityModel<Module>> modules = service.getListOfModules();
        return CollectionModel.of(modules, linkTo(methodOn(ModuleController.class).getAllModules()).withSelfRel());
    }

    //needed to set the links of the ModuleAssembler
    EntityModel<Module> getOneModule(@PathVariable Module module) {
        return service.getOneModule(module);
    }

    @GetMapping("/{module}")
    LocalDate getModule(@PathVariable Long module) {
        // module had to be changed from Module to Long due to module=null --> err 500 problem, when using a module id
        // that is not known --> could not be fixed, hence the workaround. applies to all 'Long module' in Mappings.
        // TIL do not set up a primary key id when module is wanted as key var.
        // I just did not want to have a module name as primary key due to uniqueness concerns.
        return service.getDateFromModule(module);
    }

    @PutMapping("/{module}")
    void updateModule(@RequestParam String newExpirationDate, @PathVariable Long module) {
        service.updateExpirationDate(module, newExpirationDate);
    }

    @DeleteMapping("/{module}")
    ResponseEntity<?> deleteModule(@PathVariable Long module) {
        service.deleteModuleInRepository(module);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("isExpired/")
    JSONObject checkAllDateOfExpiration() {
        return service.getAllExpirationDates();
    }

    @GetMapping("/isExpired/{module}")
    JSONObject checkOneDateOfExpiration(@PathVariable Long module) {
        return service.getExpirationDate(module);
    }

    @GetMapping("/echo")
    String echo(@RequestParam String echo){
        return echo;
    }
}