package controllers;

import com.mongodb.DBObject;
import com.mongodb.BasicDBList;

import models.*;
import models.Collection;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import play.*;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.*;

import services.MongoService;
import views.html.*;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.Logger;
import java.text.DecimalFormat;

public class EncomendaController extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        EncomendaProperties encomendaprop = MongoService.getEncomendaProperties();
        if(encomendaprop==null)
            encomendaprop = new EncomendaProperties();
        Map<String, List<Estrutura>> maps
                = encomendaprop.getEstruturasList().stream()
                .collect(Collectors.groupingBy(Estrutura::getEstrutura));
        return ok(encomenda.render(encomendaprop,maps));
    }


    @RequireCSRFCheck
    @Security.Authenticated(Secured.class)
    public static Result saveEncomenda(){

        //Http.MultipartFormData dataFiles = request().body().asMultipartFormData();
        Map<String, String[]> dataFiles = request().body().asFormUrlEncoded();

        String[] recheio = (dataFiles.get("recheio") != null && dataFiles.get("recheio").length > 0) ? dataFiles.get("recheio") : null;
        String[] productType = (dataFiles.get("productType") != null && dataFiles.get("productType").length > 0) ? dataFiles.get("productType") : null;
        String[] color = (dataFiles.get("color") != null && dataFiles.get("color").length > 0) ? dataFiles.get("color") : null;
        String[] productEstrutura = (dataFiles.get("productEstrutura") != null && dataFiles.get("productEstrutura").length > 0) ? dataFiles.get("productEstrutura") : null;
        String available = (dataFiles.get("available") != null && dataFiles.get("available").length > 0) ? dataFiles.get("available")[0] : null;
        String hasDiscount = (dataFiles.get("hasDiscount") != null && dataFiles.get("hasDiscount").length > 0) ? dataFiles.get("hasDiscount")[0] : null;
        
        String[] florPriceLabel = (dataFiles.get("florPriceLabel") != null && dataFiles.get("florPriceLabel").length > 0) ? dataFiles.get("florPriceLabel") : null;
        String[] priceFlor = (dataFiles.get("priceFlor") != null && dataFiles.get("priceFlor").length > 0) ? dataFiles.get("priceFlor") : null;

        String[] recheioPriceLabel = (dataFiles.get("recheioPriceLabel") != null && dataFiles.get("recheioPriceLabel").length > 0) ? dataFiles.get("recheioPriceLabel") : null;
        String[] priceRecheio = (dataFiles.get("priceRecheio") != null && dataFiles.get("priceRecheio").length > 0) ? dataFiles.get("priceRecheio") : null;

        String discountType = (dataFiles.get("discountType") != null && dataFiles.get("discountType").length > 0) ? dataFiles.get("discountType")[0] : null;
        String quantity = (dataFiles.get("quantity") != null && dataFiles.get("quantity").length > 0) ? dataFiles.get("quantity")[0] : null;
        String valueOF = (dataFiles.get("valueOF") != null && dataFiles.get("valueOF").length > 0) ? dataFiles.get("valueOF")[0] : null;


        List<String> recheioList =  (recheio!=null)?Arrays.asList(recheio):new ArrayList<>();
        List<String> productTypeList =  (productType!=null)?Arrays.asList(productType):new ArrayList<>();

        boolean availableBool = (available!=null)?true:false;
        boolean hasDiscountBool = (hasDiscount!=null)?true:false;

        //build EncomendaProperties object
        EncomendaProperties encomendaprop = MongoService.getEncomendaProperties();
        if(encomendaprop==null)
            encomendaprop = new EncomendaProperties();
        HashSet<Estrutura> estruturas = new HashSet();

        if(productEstrutura!=null&&productEstrutura.length>0){
            for(int i=0;i<productEstrutura.length;i++){
                if(!color[i].equals("")){
                    Estrutura estrutura = new Estrutura();
                    estrutura.setEstrutura(productEstrutura[i]);
                    estrutura.setColor(color[i]);
                    estruturas.add(estrutura);
                }
            }
        }

        HashSet<EstruturaPreco> recheioPrice = new HashSet();

        if(recheioPriceLabel!=null&&recheioPriceLabel.length>0){
            for(int i=0;i<recheioPriceLabel.length;i++){
                double value = 0;
                if(priceRecheio!=null&&i<priceRecheio.length&&!priceRecheio[i].equals("")){
                   value = Double.parseDouble(priceRecheio[i].replace(",","."));
                }
                EstruturaPreco estrutura = new EstruturaPreco();
                estrutura.setName(recheioPriceLabel[i]);
                estrutura.setPrice(value);
                recheioPrice.add(estrutura);
            }
        }

        HashSet<EstruturaPreco> florPrice = new HashSet();

        if(florPriceLabel!=null&&florPriceLabel.length>0){
            for(int i=0;i<florPriceLabel.length;i++){
                double value = 0;
                if(priceFlor!=null&&i<priceFlor.length&&!priceFlor[i].equals("")){
                   value = Double.parseDouble(priceFlor[i].replace(",","."));
                }
                EstruturaPreco estrutura = new EstruturaPreco();
                estrutura.setName(florPriceLabel[i]);
                estrutura.setPrice(value);
                florPrice.add(estrutura);
            }
        }

        if(discountType!=null){
            encomendaprop.getDiscount().setType(discountType);
        }

        if(quantity!=null){
            encomendaprop.getDiscount().setQuantity(Double.parseDouble(quantity));
        }

        if(valueOF!=null){
            encomendaprop.getDiscount().setValue(Double.parseDouble(valueOF.replace(",",".")));
        }

        encomendaprop.setEstruturas(estruturas);
        encomendaprop.setFlowerPrice(florPrice);
        encomendaprop.setRecheiosPrice(recheioPrice);
        encomendaprop.setAvailableEncomenda(availableBool);
        encomendaprop.setRecheios(recheioList);
        encomendaprop.setTipoDeFlor(productTypeList);
        
        //save EncomendaProperties
        MongoService.saveEncomendaProperties(encomendaprop);
        

        return redirect(routes.EncomendaController.index());
    }


}
