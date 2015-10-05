package models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by alvaro.joao.silvino on 05/10/2015.
 */
@Document
public class EncomendaProperties {
    @Id
    private String id;
    
    private Set<Estrutura> estruturas;
    private List<String> recheios;
    private List<String> tipoDeFlor;
    private boolean availableEncomenda;

    public EncomendaProperties(){
        this.estruturas = new HashSet<>();
        this.recheios = new ArrayList<>();
        this.tipoDeFlor = new ArrayList<>();
    }

    public boolean isAvailableEncomenda() {
        return availableEncomenda;
    }

    public void setAvailableEncomenda(boolean availableEncomenda) {
        this.availableEncomenda = availableEncomenda;
    }
    public void addEstrutura(Estrutura estrutura) {
        estruturas.add(estrutura);
    }
    public Set<Estrutura> getEstruturas() {
        return estruturas;
    }
    public List<Estrutura> getEstruturasList() {
        List<Estrutura> list = new ArrayList<Estrutura>(estruturas);
        return list;
    }

    public void setEstruturas(Set<Estrutura> estruturas) {
        this.estruturas = estruturas;
    }

    public List<String> getRecheios() {
        return recheios;
    }

    public void setRecheios(List<String> recheios) {
        this.recheios = recheios;
    }

    public List<String> getTipoDeFlor() {
        return tipoDeFlor;
    }

    public void setTipoDeFlor(List<String> tipoDeFlor) {
        this.tipoDeFlor = tipoDeFlor;
    }
}
