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

public class InventoryController extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result listInventory() {
        List<Inventory> inventories = MongoService.getAllInventories();

        return ok(listInventory.render(inventories));
    }

    @AddCSRFToken
    @Security.Authenticated(Secured.class)
    public static Result inventory(String id){
        Inventory inventory = MongoService.findInventoryById(id);
        inventory= (inventory!=null?inventory:new Inventory());
        List<Product> products = MongoService.getAllProducts();
        List<Collection> genders = MongoService.findCollectionOnline();
        return ok(newInventory.render(inventory,products,genders));
    }
    
    
    @Security.Authenticated(Secured.class)
    public static Result updateInventoryQuantity(String id,String quantity){
        if(id!=null && quantity!=null){
            Inventory inventory = MongoService.findInventoryById(id);
            if(inventory!=null){
                try {
                    int oldQuantity = inventory.getQuantity();
                    int quantityInt = Integer.parseInt(quantity.trim());

                    boolean updatedQuantity = ((oldQuantity - quantityInt)==0)?false:true;
        
                    if(updatedQuantity){
                        InventoryEntry entry = new InventoryEntry();
                        entry.setInventory(inventory);
                        entry.setQuantity(quantityInt - oldQuantity);

                        // Save Inventory
                        inventory.setQuantity(quantityInt);
                        MongoService.saveInventory(inventory);
                        // Save Inventory Entry
                        MongoService.saveInventoryEntry(entry);
                    }


                    return ok();
                }catch (Exception e) {
                    return internalServerError();
                }
            }
        }
        return internalServerError();
    }

    @RequireCSRFCheck
    @Security.Authenticated(Secured.class)
    public static Result saveInventory(String id){

        //Http.MultipartFormData dataFiles = request().body().asMultipartFormData();
        Map<String, String[]> dataFiles = request().body().asFormUrlEncoded();

        String productId = (dataFiles.get("product") != null && dataFiles.get("product").length > 0) ? dataFiles.get("product")[0] : null;
        String outOfStock = (dataFiles.get("outOfStock") != null && dataFiles.get("outOfStock").length > 0) ? dataFiles.get("outOfStock")[0] : null;
        String temDoce = (dataFiles.get("temDoce") != null && dataFiles.get("temDoce").length > 0) ? dataFiles.get("temDoce")[0] : null;
        String quantity = (dataFiles.get("quantity") != null && dataFiles.get("quantity").length > 0) ? dataFiles.get("quantity")[0] : null;
        String sellInOutOfStock = (dataFiles.get("sellInOutOfStock") != null && dataFiles.get("sellInOutOfStock").length > 0) ? dataFiles.get("sellInOutOfStock")[0] : null;
        String recheio = (dataFiles.get("recheio") != null && dataFiles.get("recheio").length > 0) ? dataFiles.get("recheio")[0] : null;
        String productType = (dataFiles.get("productType") != null && dataFiles.get("productType").length > 0) ? dataFiles.get("productType")[0] : null;
        String[] color = (dataFiles.get("color") != null && dataFiles.get("color").length > 0) ? dataFiles.get("color") : null;
        String[] productEstrutura = (dataFiles.get("productEstrutura") != null && dataFiles.get("productEstrutura").length > 0) ? dataFiles.get("productEstrutura") : null;
        List<String> colorsList =  (color!=null)?Arrays.asList(color):new ArrayList<>();
        List<String> productEstruturaList =  (productEstrutura!=null)?Arrays.asList(productEstrutura):new ArrayList<>();

        boolean outOfStockBool = (outOfStock!=null)?true:false;
        boolean sellInOutOfStockBool = (sellInOutOfStock!=null)?true:false;
        boolean temDoceBool = (temDoce!=null)?true:false;


        int quantityInt = Integer.parseInt(quantity.trim().replace(".",""));

        //Validation

        if(id!=null&&!MongoService.hasInventoryById(id)) {
            flash("inventory","Inventory not in base");
            return redirect(routes.InventoryController.inventory(null));
        }

        if(productId==null||productId.equals("")){
            flash("inventory","Insert A product");
            return redirect(routes.InventoryController.inventory(id));
        }

        if(productId!=null&&!MongoService.hasProductById(productId)){
            flash("inventory","Product does not Exist");
            return redirect(routes.InventoryController.inventory(id));
        }
        
        if(productType==null||productType.equals("")){
            flash("inventory","Coloque um modelo ou tipo");
            return redirect(routes.InventoryController.inventory(id));
        }


        //build inventory object
        Inventory inventory = null;
        if(id!=null) {
            inventory = MongoService.findInventoryById(id);
        }
        Product product = MongoService.findProductById(productId);

        if(inventory==null){
            inventory = new Inventory();
            inventory.setSku("sku"+(MongoService.countAllInventories()+1));
        } 

        if(productEstrutura.length>0){
            for(int i=0;i<productEstrutura.length;i++){
                if(!color[i].equals("")){
                    Estrutura estrutura = new Estrutura();
                    estrutura.setEstrutura(productEstrutura[i]);
                    estrutura.setColor(color[i]);
                    inventory.getEstruturas().add(estrutura);
                }
                
            }
        }

        inventory.setOrderOutOfStock(outOfStockBool);
        inventory.setProduct(product);
        int oldQuantity = inventory.getQuantity();
        inventory.setQuantity(quantityInt);
        inventory.setSellInOutOfStock(sellInOutOfStockBool);
        inventory.setType(productType);
        inventory.setTemDoce(temDoceBool);

        
        //save Inventory
        MongoService.saveInventory(inventory);
        // Save Inventory Entry
        
        boolean updatedQuantity = ((oldQuantity - quantityInt)==0)?false:true;

        if(updatedQuantity){
            InventoryEntry entry = new InventoryEntry();
            entry.setInventory(inventory);
            if(id!=null){
                entry.setQuantity(quantityInt - oldQuantity);
            }else{
                entry.setQuantity(quantityInt);
            }
            MongoService.saveInventoryEntry(entry);
        }

        //product update
        product.getInventories().add(inventory);
        MongoService.saveProduct(product);

        return redirect(routes.InventoryController.inventory(id));
    }


    @Security.Authenticated(Secured.class)
    public static Result deleteInventory(String id){
        Inventory inventory = MongoService.findInventoryById(id);
        if(inventory!=null){
            MongoService.deleteInventory(inventory);
            flash("listInventory","Removed with success");
        }
        return redirect(routes.InventoryController.listInventory());
    }


}
