package controllers;

import models.*;
import models.Collection;
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

public class Application extends Controller {

    /**
     * Create
     * */

    @AddCSRFToken
    public static Result product(String id){
        Product product = MongoService.findProductById(id);
        product= (product!=null?product:new Product());
        List<Collection> collections = MongoService.getAllCollections();
        return ok(newProduct.render(product,collections));
    }
    @AddCSRFToken
    public static Result inventory(String id){
        Inventory inventory = MongoService.findInventoryById(id);
        inventory= (inventory!=null?inventory:new Inventory());
        List<Product> products = MongoService.getAllProducts();
        return ok(newInventory.render(inventory,products));
    }
    @AddCSRFToken
    public static Result costumer(String id){
        User user = MongoService.findUserById(id);
        user= (user!=null?user:new User());
        return ok(newCostumer.render(user, Utils.UserTags.getList()));
    }
    @AddCSRFToken
    public static Result collection(String id){
        Collection collection = MongoService.findCollectionById(id);
        collection= (collection!=null?collection:new Collection());

        List<String> collectionsSlug = new ArrayList<>();
        collectionsSlug.add(collection.getSlug());
        List<Product> productsByCollections = MongoService.findProductByCollectionSlug(collectionsSlug);
        List<Product> products = MongoService.getAllProducts();

        return ok(newCollection.render(collection,productsByCollections,products));
    }
    @AddCSRFToken
    public static Result discountCode(String id){
        DiscountCode discountCode = MongoService.findDiscountCodeById(id);
        discountCode= (discountCode!=null?discountCode:new DiscountCode());
        List<Collection> collections =  MongoService.getAllCollections();
        List<Product> products = MongoService.getAllProducts();

        return ok(newDiscountCode.render(discountCode,collections,products));
    }
    @AddCSRFToken
    public static Result giftCard(String id){
        GiftCard giftCard = MongoService.findGiftCardById(id);
        giftCard= (giftCard!=null?giftCard:new GiftCard());
        List<User> users = MongoService.getAllUsers();

        return ok(newGiftCode.render(giftCard,users));
    }
    @AddCSRFToken
    public static Result order(String id){
        Order order = MongoService.findOrdersById(id);
        order= (order!=null?order:new Order());
        List<User> users = MongoService.getAllUsers();

        return ok(detailOrder.render(order));
    }


    /**
     * List
     * */

    public static Result listProduct() {
        List<Product> products = MongoService.getAllProducts();
        return ok(listProduct.render(products));
    }
    public static Result listInventory() {
        List<Inventory> inventories = MongoService.getAllInventories();

        return ok(listInventory.render(inventories));
    }
    public static Result listCollections() {
        List<Collection> collections = MongoService.getAllCollections();

        return ok(listCollections.render(collections));
    }
    public static Result listGiftCard() {
        List<GiftCard> giftCards = MongoService.getAllGiftCards();
        return ok(listGiftCard.render(giftCards));
    }
    public static Result listDiscount() {
        List<DiscountCode> discounts = MongoService.getAllDiscountCodes();

        return ok(listDiscountCode.render(discounts));
    }
    public static Result listCostumers() {
        List<User> users = MongoService.getAllUsers();
        return ok(listCostumer.render(users));
    }
    public static Result listOrders() {
        List<Order> orders = MongoService.getAllOrders();
        return ok(listOrders.render(orders));
    }


    /**
     * save or Edit From Form TODO
     * */

    @RequireCSRFCheck
    public static Result saveProduct(String id){

        Http.MultipartFormData dataFiles = request().body().asMultipartFormData();
        String title = (dataFiles.asFormUrlEncoded().get("title") != null && dataFiles.asFormUrlEncoded().get("title").length > 0) ? dataFiles.asFormUrlEncoded().get("title")[0] : null;
        String description = (dataFiles.asFormUrlEncoded().get("description") != null && dataFiles.asFormUrlEncoded().get("description").length > 0) ? dataFiles.asFormUrlEncoded().get("description")[0] : null;
        String price = (dataFiles.asFormUrlEncoded().get("price") != null && dataFiles.asFormUrlEncoded().get("price").length > 0) ? dataFiles.asFormUrlEncoded().get("price")[0] : null;
        String priceCompareWith = (dataFiles.asFormUrlEncoded().get("priceCompareWith") != null && dataFiles.asFormUrlEncoded().get("priceCompareWith").length > 0) ? dataFiles.asFormUrlEncoded().get("priceCompareWith")[0] : null;
        String online = (dataFiles.asFormUrlEncoded().get("online") != null && dataFiles.asFormUrlEncoded().get("online").length > 0) ? dataFiles.asFormUrlEncoded().get("online")[0] : null;
        String store = (dataFiles.asFormUrlEncoded().get("store") != null && dataFiles.asFormUrlEncoded().get("store").length > 0) ? dataFiles.asFormUrlEncoded().get("store")[0] : null;
        String newProduct = (dataFiles.asFormUrlEncoded().get("newProduct") != null && dataFiles.asFormUrlEncoded().get("newProduct").length > 0) ? dataFiles.asFormUrlEncoded().get("newProduct")[0] : null;
        String weight = (dataFiles.asFormUrlEncoded().get("weight") != null && dataFiles.asFormUrlEncoded().get("weight").length > 0) ? dataFiles.asFormUrlEncoded().get("weight")[0] : null;
        String mail = (dataFiles.asFormUrlEncoded().get("mail") != null && dataFiles.asFormUrlEncoded().get("mail").length > 0) ? dataFiles.asFormUrlEncoded().get("mail")[0] : null;
        String[] tags = (dataFiles.asFormUrlEncoded().get("tags") != null && dataFiles.asFormUrlEncoded().get("tags").length > 0) ? dataFiles.asFormUrlEncoded().get("tags") : null;
        String productType = (dataFiles.asFormUrlEncoded().get("productType") != null && dataFiles.asFormUrlEncoded().get("productType").length > 0) ? dataFiles.asFormUrlEncoded().get("productType")[0] : null;
        String[] collections = (dataFiles.asFormUrlEncoded().get("collections") != null && dataFiles.asFormUrlEncoded().get("collections").length > 0) ? dataFiles.asFormUrlEncoded().get("collections") : null;

        boolean onlineBool = (online!=null)?true:false;
        boolean storeBool = (store!=null)?true:false;
        boolean newProductBool = (newProduct!=null)?true:false;
        boolean mailBool = (mail!=null)?true:false;

        double priceDouble = Double.parseDouble(price);
        double priceCompareWithDouble = Double.parseDouble(priceCompareWith);
        double weightDouble = Double.parseDouble(weight);

        List<String> tagsList =  (tags!=null)?Arrays.asList(tags):new ArrayList<>();
        Set<String> collectionsList =  (collections!=null)?new HashSet<>(Arrays.asList(collections)):new HashSet<>();


        List<Http.MultipartFormData.FilePart> fileImages = dataFiles.getFiles();



        //Validation

        if(id!=null&&!MongoService.hasProductById(id)) {
            flash("product","Procut not in base");
            return redirect(routes.Application.product(null));
        }

        if(title==null||title.equals("")){
            flash("product","Insert Title at least");
            return redirect(routes.Application.product(null));
        }

        //build product object
        Product product = null;
        if(id!=null) {
            product = MongoService.findProductById(id);
        }

        product = (product!=null)?product:new Product();

        product.setTitle((title != null && !title.equals("")) ? title : product.getTitle());
        product.setDescription((description != null && !description.equals("")) ? title : product.getDescription());
        product.setNewProduct(newProductBool);
        product.setOnLineVisible(onlineBool);
        product.setPrice(priceDouble);
        product.setPriceCompareWith(priceCompareWithDouble);
        product.setSendMail(mailBool);
        product.setStoreVisible(storeBool);
        product.setType((productType != null && !productType.equals("")) ? productType : product.getType());
        product.setUserTags(tagsList);
        product.setWeight(weightDouble);
        product.setCollectionsSlugs(collectionsList);


        List<Image> imagesNew = new ArrayList<>();
        for(Http.MultipartFormData.FilePart file : fileImages){
            String  imageName;
            if (file != null) {
                Image image = new Image();
                imageName = file.getFilename();
                File fileSave = file.getFile();
                double bytes = fileSave.length();
                double kilobytes = (bytes / 1024);
                double megabytes = (kilobytes / 1024);

                image.setSize(megabytes+" mb");
                image.setName(imageName);
                image.setImageFile(fileSave);
                image.saveFile();
                imagesNew.add(image);
            }
        }

        product.getImages().addAll(imagesNew);
        MongoService.saveProduct(product);

        return redirect(routes.Application.listProduct());
    }
    @RequireCSRFCheck
    public static Result saveInventory(String id){

        //Http.MultipartFormData dataFiles = request().body().asMultipartFormData();
        Map<String, String[]> dataFiles = request().body().asFormUrlEncoded();

        String productId = (dataFiles.get("product") != null && dataFiles.get("product").length > 0) ? dataFiles.get("product")[0] : null;
        String outOfStock = (dataFiles.get("outOfStock") != null && dataFiles.get("outOfStock").length > 0) ? dataFiles.get("outOfStock")[0] : null;
        String productSize = (dataFiles.get("productSize") != null && dataFiles.get("productSize").length > 0) ? dataFiles.get("productSize")[0] : null;
        String quantity = (dataFiles.get("quantity") != null && dataFiles.get("quantity").length > 0) ? dataFiles.get("quantity")[0] : null;
        String sellInOutOfStock = (dataFiles.get("sellInOutOfStock") != null && dataFiles.get("sellInOutOfStock").length > 0) ? dataFiles.get("sellInOutOfStock")[0] : null;
        boolean outOfStockBool = (outOfStock!=null)?true:false;
        boolean sellInOutOfStockBool = (sellInOutOfStock!=null)?true:false;

        int quantityInt = Integer.parseInt(quantity.trim().replace(".",""));

        //Validation

        if(id!=null&&!MongoService.hasInventoryById(id)) {
            flash("inventory","Inventory not in base");
            return redirect(routes.Application.inventory(null));
        }

        if(productId==null||productId.equals("")){
            flash("inventory","Insert A product");
            return redirect(routes.Application.inventory(null));
        }

        if(productId!=null&&!MongoService.hasProductById(productId)){
            flash("inventory","Product does not Exist");
            return redirect(routes.Application.inventory(null));
        }

        if(MongoService.hasInventoryByProductAndSize(productId,productSize)){
            flash("inventory","Inventory with same Size");
            return redirect(routes.Application.inventory(null));
        }



        //build inventory object
        Inventory inventory = null;
        if(id!=null) {
            inventory = MongoService.findInventoryById(id);
        }
        Product product = MongoService.findProductById(productId);

        inventory = (inventory!=null)?inventory:new Inventory();
        inventory.setOrderOutOfStock(outOfStockBool);
        inventory.setProduct(product);
        inventory.setQuantity(quantityInt);
        inventory.setSize(productSize);
        inventory.setSellInOutOfStock(sellInOutOfStockBool);

        //save Inventory
        MongoService.saveInventory(inventory);
        //product update
        product.getInventories().add(inventory);
        MongoService.saveProduct(product);


        return redirect(routes.Application.listInventory());
    }
    @RequireCSRFCheck
    public static Result saveCollection(String id){

        Http.MultipartFormData dataFiles = request().body().asMultipartFormData();
        String title = (dataFiles.asFormUrlEncoded().get("title") != null && dataFiles.asFormUrlEncoded().get("title").length > 0) ? dataFiles.asFormUrlEncoded().get("title")[0] : null;
        String description = (dataFiles.asFormUrlEncoded().get("description") != null && dataFiles.asFormUrlEncoded().get("description").length > 0) ? dataFiles.asFormUrlEncoded().get("description")[0] : null;
        String visibleOnline = (dataFiles.asFormUrlEncoded().get("visibleOnline") != null && dataFiles.asFormUrlEncoded().get("visibleOnline").length > 0) ? dataFiles.asFormUrlEncoded().get("visibleOnline")[0] : null;
        String hasOnLocalStore = (dataFiles.asFormUrlEncoded().get("hasOnLocalStore") != null && dataFiles.asFormUrlEncoded().get("hasOnLocalStore").length > 0) ? dataFiles.asFormUrlEncoded().get("hasOnLocalStore")[0] : null;
        String[] products = (dataFiles.asFormUrlEncoded().get("products") != null && dataFiles.asFormUrlEncoded().get("products").length > 0) ? dataFiles.asFormUrlEncoded().get("products") : null;

        boolean onlineBool = (visibleOnline!=null)?true:false;
        boolean storeBool = (hasOnLocalStore!=null)?true:false;

        List<String> productsList =  (products!=null)?Arrays.asList(products):new ArrayList<>();

        List<Http.MultipartFormData.FilePart> fileImages = dataFiles.getFiles();



        //Validation

        if(id!=null&&!MongoService.hasCollectionById(id)) {
            flash("collection","Procut not in base");
            return redirect(routes.Application.product(null));
        }

        if(title==null||title.equals("")){
            flash("collection","Insert Title at least");
            return redirect(routes.Application.product(null));
        }

        //build product object
        Collection collection = null;
        if(id!=null) {
            collection = MongoService.findCollectionById(id);
        }

        collection = (collection!=null)?collection:new Collection();

        collection.setTitle((title != null && !title.equals("")) ? title : collection.getTitle());
        collection.setDescription(description);
        collection.setOnLineVisible(onlineBool);
        collection.setOnLocalStore(storeBool);


        List<String> collectionsSlug = new ArrayList<>();
        collectionsSlug.add(collection.getSlug());
        final String slugFinal = collection.getSlug();

        List<Product> productsToAddOrRemoveCollection = MongoService.findProductByCollectionSlugOrListId(collectionsSlug, productsList);
        productsToAddOrRemoveCollection.stream().forEach(p->{
            if(productsList.contains(p.getId())){
                p.getCollectionsSlugs().add(slugFinal);
            }else{
                p.getCollectionsSlugs().remove(slugFinal);}
        });
        MongoService.saveProducts(productsToAddOrRemoveCollection);
        List<Image> imagesNew = new ArrayList<>();
        for(Http.MultipartFormData.FilePart file : fileImages){
            String  imageName;
            if (file != null) {
                Image image = new Image();
                imageName = file.getFilename();
                File fileSave = file.getFile();
                double bytes = fileSave.length();
                double kilobytes = (bytes / 1024);
                double megabytes = (kilobytes / 1024);

                image.setSize(megabytes+" mb");
                image.setName(imageName);
                image.setImageFile(fileSave);
                image.saveFile();
                imagesNew.add(image);
            }
        }

        if(imagesNew.size() > 0&&collection.getImage()!=null) {
            collection.getImage().deleteFile();
        }
        collection.setImage((imagesNew.size() > 0) ? imagesNew.get(0) : collection.getImage());

        MongoService.saveCollection(collection);

        return redirect(routes.Application.listCollections());
    }
    @RequireCSRFCheck
    public static Result saveGiftCard(String id){

        //Http.MultipartFormData dataFiles = request().body().asMultipartFormData();
        Map<String, String[]> dataFiles = request().body().asFormUrlEncoded();

        String code = (dataFiles.get("code") != null && dataFiles.get("code").length > 0) ? dataFiles.get("code")[0] : null;
        String price = (dataFiles.get("price") != null && dataFiles.get("price").length > 0) ? dataFiles.get("price")[0] : null;
        String userFrom = (dataFiles.get("userFrom") != null && dataFiles.get("userFrom").length > 0) ? dataFiles.get("userFrom")[0] : null;
        String userTo = (dataFiles.get("userTo") != null && dataFiles.get("userTo").length > 0) ? dataFiles.get("userTo")[0] : null;
        String active = (dataFiles.get("active") != null && dataFiles.get("active").length > 0) ? dataFiles.get("active")[0] : null;
        String used = (dataFiles.get("used") != null && dataFiles.get("used").length > 0) ? dataFiles.get("used")[0] : null;

        boolean usedBool = (used!=null)?true:false;
        boolean activeBool = (active!=null)?true:false;

        double priceDouble = Double.parseDouble(price);




        //Validation

        if(id!=null&&!MongoService.hasGiftCardById(id)) {
            flash("giftCard","Gift Card not in base");
            return redirect(routes.Application.giftCard(null));
        }

        if((code==null||code.equals(""))&&(id==null&&!MongoService.hasGiftCardById(code))){
            flash("giftCard","Insert a Valid Code");
            return redirect(routes.Application.giftCard(null));
        }


        //build inventory object
        GiftCard giftCard = null;
        if(id!=null) {
            giftCard = MongoService.findGiftCardById(id);
        }

        giftCard = (giftCard!=null)?giftCard:new GiftCard();
        giftCard.setActive(activeBool);
        giftCard.setCode((giftCard.getCode() == null) ? code : giftCard.getCode());
        giftCard.setPrice(priceDouble);
        giftCard.setUsed((giftCard.isUsed())?giftCard.isUsed():usedBool);
        giftCard.setUserIdFrom(userFrom);
        giftCard.setUserIdTo(userTo);

        //save Inventory
        MongoService.saveGiftCard(giftCard);
        return redirect(routes.Application.listGiftCard());
    }
    @RequireCSRFCheck
    public static Result saveDiscountCode(String id){

        Map<String, String[]> dataFiles = request().body().asFormUrlEncoded();

        String code = (dataFiles.get("code") != null && dataFiles.get("code").length > 0) ? dataFiles.get("code")[0] : null;
        String timesLeft = (dataFiles.get("timesLeft") != null && dataFiles.get("timesLeft").length > 0) ? dataFiles.get("timesLeft")[0] : null;
        String noLimits = (dataFiles.get("noLimits") != null && dataFiles.get("noLimits").length > 0) ? dataFiles.get("noLimits")[0] : null;
        String discountType = (dataFiles.get("discountType") != null && dataFiles.get("discountType").length > 0) ? dataFiles.get("discountType")[0] : null;
        String valueOf = (dataFiles.get("valueOF") != null && dataFiles.get("valueOF").length > 0) ? dataFiles.get("valueOF")[0] : null;
        String validationType = (dataFiles.get("validationType") != null && dataFiles.get("validationType").length > 0) ? dataFiles.get("validationType")[0] : null;
        String applyCondition = (dataFiles.get("applyCondition") != null && dataFiles.get("applyCondition").length > 0) ? dataFiles.get("applyCondition")[0] : null;
        String noDateLimite = (dataFiles.get("noDateLimite") != null && dataFiles.get("noDateLimite").length > 0) ? dataFiles.get("noDateLimite")[0] : null;
        String dateRange = (dataFiles.get("dateRange") != null && dataFiles.get("dateRange").length > 0) ? dataFiles.get("dateRange")[0] : null;
        String overValueInput = (dataFiles.get("overValue") != null && dataFiles.get("overValue").length > 0) ? dataFiles.get("overValue")[0] : null;

        String active = (dataFiles.get("active") != null && dataFiles.get("active").length > 0) ? dataFiles.get("active")[0] : null;

        String[] collections = (dataFiles.get("collections") != null && dataFiles.get("collections").length > 0) ? dataFiles.get("collections") : null;
        String[] products = (dataFiles.get("products") != null && dataFiles.get("products").length > 0) ? dataFiles.get("products") : null;
        List<String> collectionList =  (collections!=null)?Arrays.asList(collections):new ArrayList<>();
        List<String> productsnList =  (products!=null)?Arrays.asList(products):new ArrayList<>();

        boolean noLimitsBool = (noLimits!=null)?true:false;
        boolean noDateLimiteBool = (noDateLimite!=null)?true:false;
        boolean activeBool = (active!=null)?true:false;


        Utils.DiscountValidation validation = Utils.DiscountValidation.valueOf(validationType);
        Utils.DiscountPaymentAdjust paymentAdjust = Utils.DiscountPaymentAdjust.valueOf(applyCondition);
        Utils.DiscountType typeDiscount = Utils.DiscountType.valueOf(discountType);

        double value = 0;
        try {
            value = Double.parseDouble((valueOf != null && !valueOf.equals("")) ? valueOf : "0");
        }catch (Exception e){

        }
        double overValueInputDouble = 0;
        try {

            overValueInputDouble = Double.parseDouble((overValueInput!=null&&!overValueInput.equals(""))?overValueInput:"0");
        }catch (Exception e){

        }
        int timesLeftInt = 0;
        try {

            timesLeftInt = Integer.parseInt((timesLeft!=null&&!timesLeft.equals(""))?timesLeft:"0");
        }catch (Exception e){
            timesLeftInt = 0;
        }

        Date dateStart = null;
        Date dateEnd = null;

        if(dateRange!=null&&!dateRange.equals("")) {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy H:mm", Locale.ENGLISH);
            try {
                dateStart = format.parse(dateRange.split(" - ")[0]);
                dateEnd = format.parse(dateRange.split(" - ")[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //Validation

        if(id!=null&&!MongoService.hasDiscountCodeByCode(code)){
            flash("discountCode","Code not found");
        }

        if(id==null&&code!=null&&MongoService.hasDiscountCodeByCode(code)) {
            flash("discountCode","Code already in use");
            return redirect(routes.Application.discountCode(null));
        }

        if(validation.equals(Utils.DiscountValidation.collections)&&collectionList.size()==0){
            flash("discountCode","Need Some Collections for this validation");
            return redirect(routes.Application.discountCode(null));
        }


        //build inventory object
        DiscountCode discount = null;
        if(id!=null) {
            discount = MongoService.findDiscountCodeById(id);
        }

        discount = (discount!=null)?discount:new DiscountCode();
        discount.setCode((discount.getCode() == null) ? code : discount.getCode());

        discount.setType(typeDiscount);
        discount.setValueOf(value);
        discount.setActive(activeBool);

        discount.setNoDateLimits(noDateLimiteBool);
        discount.setStartDate(dateStart);
        discount.setEndDate(dateEnd);

        discount.setNoTimesLimits(noLimitsBool);
        discount.setTimesLeft(timesLeftInt);

        discount.setOrdersValidation(validation);
        discount.setWhereApply(paymentAdjust);

        switch (validation) {

            case collections: {
                discount.setCollectionsId(collectionList);
                discount.setOverValueOf(0);
                discount.setProducts(new ArrayList<>());
                break;
            }
            case overValue: {
                discount.setCollectionsId(new ArrayList<>());
                discount.setOverValueOf(overValueInputDouble);
                discount.setProducts(new ArrayList<>());
                break;

            }
            case specificProduct:{
                discount.setCollectionsId(new ArrayList<>());
                discount.setOverValueOf(0);
                discount.setProducts(productsnList);
                break;

            }
            default:{
                discount.setCollectionsId(new ArrayList<>());
                discount.setOverValueOf(0);
                discount.setProducts(new ArrayList<>());
                break;

            }

        }

        //save Discount Code
        MongoService.saveDiscountCode(discount);





        return redirect(routes.Application.listDiscount());

    }
    @RequireCSRFCheck
    public static Result saveCostumer(String id){

        //Http.MultipartFormData dataFiles = request().body().asMultipartFormData();
        Map<String, String[]> dataFiles = request().body().asFormUrlEncoded();

        String firstname = (dataFiles.get("firstname") != null && dataFiles.get("firstname").length > 0) ? dataFiles.get("firstname")[0] : null;
        String lastname = (dataFiles.get("lastname") != null && dataFiles.get("lastname").length > 0) ? dataFiles.get("lastname")[0] : null;
        String email = (dataFiles.get("email") != null && dataFiles.get("email").length > 0) ? dataFiles.get("email")[0] : null;
        String mktAccept = (dataFiles.get("mktAccept") != null && dataFiles.get("mktAccept").length > 0) ? dataFiles.get("mktAccept")[0] : null;
        String notes = (dataFiles.get("notes") != null && dataFiles.get("notes").length > 0) ? dataFiles.get("notes")[0] : null;
        String[] tags = (dataFiles.get("tags") != null && dataFiles.get("tags").length > 0) ? dataFiles.get("tags") : null;
        List<String> tagsList =  (tags!=null)?Arrays.asList(tags):new ArrayList<>();
        boolean mktAcceptBool = (mktAccept!=null)?true:false;

        //Validation

        if(id!=null&&!MongoService.hasUserByEmail(id)) {
            flash("costumer","User not in base");
            return redirect(routes.Application.costumer(null));
        }

        if(firstname==null||firstname.equals("")){
            flash("costumer","Insert First name");
            return redirect(routes.Application.costumer(null));
        }
        if(email==null||email.equals("")||!isValidEmailAddress(email)){
            flash("costumer","Isert Email correctly");
            return redirect(routes.Application.costumer(null));
        }
        if (id==null&&MongoService.hasUserByEmail(email)){
            flash("costumer","Email already in use");
            return redirect(routes.Application.costumer(null));
        }
        if (id!=null&&!(id.equals(email))&&MongoService.hasUserByEmail(email)){
            flash("costumer","Changing the Email with other already in use");

            return redirect(routes.Application.costumer(id));
        }

        //build object User
        User  user = null;

        if(id==null){
            user = new User(firstname,email,generatePassword());
            user.setLastName(lastname);
            user.setMkt(mktAcceptBool);
            user.setNotes(notes);
            user.setTags(tagsList);
        }else{
            user = MongoService.findUserByEmail(id);
            user.setFirstName((firstname != null && !firstname.equals("")) ? firstname : user.getFirstName());
            user.setLastName((lastname != null && !lastname.equals("")) ? lastname : user.getLastName());
            user.setEmail((email != null && !email.equals("")) ? email : user.getEmail());
            user.setMkt(mktAcceptBool);
            user.setNotes((notes != null && !notes.equals("")) ? notes : user.getNotes());
            user.setTags(tagsList);
        }

        int count = dataFiles.get("address") != null ? dataFiles.get("address").length : 0;
        List<Address> addressesList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Address addressObj = new Address();
            String name = (dataFiles.get("name") != null && dataFiles.get("name").length > i) ? dataFiles.get("name")[i] : null;
            String address = (dataFiles.get("address") != null && dataFiles.get("address").length > i) ? dataFiles.get("address")[i] : null;
            String number = (dataFiles.get("number") != null && dataFiles.get("number").length > i) ? dataFiles.get("number")[i] : null;
            String city = (dataFiles.get("city") != null && dataFiles.get("city").length > i) ? dataFiles.get("city")[i] : null;
            String state = (dataFiles.get("state") != null && dataFiles.get("state").length > i) ? dataFiles.get("state")[i] : null;
            String country = (dataFiles.get("country") != null && dataFiles.get("country").length > i) ? dataFiles.get("country")[i] : null;
            String zipcode = (dataFiles.get("zipcode") != null && dataFiles.get("zipcode").length > i) ? dataFiles.get("zipcode")[i] : null;
            addressObj.setName(name);
            addressObj.setAddress(address);
            addressObj.setCity(city);
            addressObj.setState(state);
            addressObj.setNumber(number);
            addressObj.setCountry(country);
            addressObj.setCep(zipcode);
            addressesList.add(addressObj);
        }

        user.setAddress(addressesList);

        //save user or update
        MongoService.saveUser(user);

        return redirect(routes.Application.listCostumers());
    }


    /**
     * save From List TODO
     * */

    public static Result deleteProduct(String id){
        Product product = MongoService.findProductById(id);
        if(product!=null){
            MongoService.deleteProduct(product);
            flash("listProduct","Removed with success");
        }
        return redirect(routes.Application.listProduct());
    }
    public static Result deleteInventory(String id){
        Inventory inventory = MongoService.findInventoryById(id);
        if(inventory!=null){
            MongoService.deleteInventory(inventory);
            flash("listInventory","Removed with success");
        }
        return redirect(routes.Application.listInventory());
    }
    public static Result deleteCollection(String id){
        return ok();
    }
    public static Result deleteGiftCard(String id){
        GiftCard giftCard = MongoService.findGiftCardById(id);
        if(giftCard!=null){
            MongoService.deleteGiftCard(giftCard);
            flash("listGiftCard","Removed with success");
        }
        return redirect(routes.Application.listGiftCard());
    }
    public static Result deleteDiscountCode(String id){
        DiscountCode discountCode = MongoService.findDiscountCodeById(id);
        if(discountCode!=null){
            MongoService.deleteDiscountCode(discountCode);
            flash("listDiscountCode","Removed with success");
        }
        return redirect(routes.Application.listDiscount());
    }
    public static Result deleteCostumer(String id){
        User user = MongoService.findUserByEmail(id);
        if(user!=null){
            MongoService.deleteUser(user);
            flash("listCostumers","Removed with success");
        }
        return redirect(routes.Application.listCostumers());

    }

    public static Result deleteProductImage(String productId,String imageName){
        if(productId!=null && imageName!=null){
            Product product = MongoService.findProductById(productId);
            if(product!=null){
                try {
                    Image image = product.getImages().stream().filter(i -> i.getName().equals(imageName)).findFirst().get();
                    product.getImages().remove(image);
                    MongoService.saveProduct(product);
                    image.deleteFile();
                    return ok();
                }catch (Exception e) {
                    return internalServerError();
                }
            }
        }
        return internalServerError();
    }
    public static Result deleteCollectionImage(){
        Map<String, String[]> dataFiles = request().body().asFormUrlEncoded();

        String collectionId = (dataFiles.get("collectionId") != null && dataFiles.get("collectionId").length > 0) ? dataFiles.get("collectionId")[0] : null;
        String imageName = (dataFiles.get("imageName") != null && dataFiles.get("imageName").length > 0) ? dataFiles.get("imageName")[0] : null;


        if(collectionId!=null && imageName!=null){
            Collection collection = MongoService.findCollectionById(collectionId);
            if(collection!=null){
                try {
                    Image image = collection.getImage();
                    image.deleteFile();
                    collection.setImage(null);
                    MongoService.saveCollection(collection);

                    return ok();
                }catch (Exception e) {
                    return internalServerError();
                }
            }
        }
        return internalServerError();
    }

    public static Result updateInventoryQuantity(String id,String quantity){
        if(id!=null && quantity!=null){
            Inventory inventory = MongoService.findInventoryById(id);
            if(inventory!=null){
                try {
                    inventory.setQuantity(Integer.parseInt(quantity.trim()));
                    MongoService.saveInventory(inventory);
                    return ok();
                }catch (Exception e) {
                    return internalServerError();
                }
            }
        }
        return internalServerError();
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public static String generatePassword() {

        return new BigInteger(130, new SecureRandom()).toString(32);
    }

    public static String generateCode(){
        return "";
    }

}