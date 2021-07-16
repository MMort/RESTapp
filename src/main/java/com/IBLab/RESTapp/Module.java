package com.IBLab.RESTapp;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Objects;

@Entity
class Module {

    private @Id @GeneratedValue Long id;
    private String name;
    private LocalDate expirationdate;

    Module() {}

    Module(String name, LocalDate expirationdate) {

        this.name = name;
        this.expirationdate = expirationdate;
    }
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) { this.name = name; }

    public LocalDate getexpirationdate() {
        return this.expirationdate;
    }

    public void setexpirationdate(LocalDate date) {
        this.expirationdate = date;
    }

/*    // TODO check if needed
    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.expirationdate);
    }

    // TODO check if needed
    @Override
    public String toString() {
        return "Module{" + "name=" + this.name + ", date='" + this.expirationdate + '\'' + '}';
    }*/
}