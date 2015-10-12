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

    private Set<EstruturaPreco> recheiosPrice;
    private Set<EstruturaPreco> flowerPrice;

    private Discount discount;
    private boolean hasDiscount;


    public EncomendaProperties(){
        this.estruturas = new HashSet<>();
        this.recheios = new ArrayList<>();
        this.tipoDeFlor = new ArrayList<>();
        this.recheiosPrice = new HashSet<>();
        this.flowerPrice = new HashSet<>();
        this.discount = new Discount();
    }

    public boolean isHasDiscount() {
        return hasDiscount;
    }

    public void setHasDiscount(boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
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

    public Set<EstruturaPreco> getRecheiosPrice() {
        return recheiosPrice;
    }
    public List<EstruturaPreco> getRecheiosPriceList() {
        List<EstruturaPreco> list = new ArrayList<EstruturaPreco>(recheiosPrice);
        return list;
    }

    public void setRecheiosPrice(Set<EstruturaPreco> recheiosPrice) {
        this.recheiosPrice = recheiosPrice;
    }

    public Set<EstruturaPreco> getFlowerPrice() {
        return flowerPrice;
    }
    public List<EstruturaPreco> getFlowerPriceList() {
        List<EstruturaPreco> list = new ArrayList<EstruturaPreco>(flowerPrice);
        return list;
    }

    public void setFlowerPrice(Set<EstruturaPreco> flowerPrice) {
        this.flowerPrice = flowerPrice;
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

    public class Discount {
        public String type;
        public double value;
        public double quantity;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

    }
}
