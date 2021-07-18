package com.IBLab.RESTapp;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class ModuleModelAssembler implements RepresentationModelAssembler<Module, EntityModel<Module>> {
    //Converts a non-model obj Module into a model-based one (EntityModel<Module)

    @Override
    public EntityModel<Module> toModel(Module module) {

        return EntityModel.of(module,
                linkTo(methodOn(ModuleController.class).getOneModule(module)).withSelfRel(),
                linkTo(methodOn(ModuleController.class).getAllModules()).withRel("modules"));
    }
}
